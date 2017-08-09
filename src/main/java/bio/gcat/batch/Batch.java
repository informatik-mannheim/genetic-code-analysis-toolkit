/*
 * Copyright [2016] [Mannheim University of Applied Sciences]
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package bio.gcat.batch;

import static bio.gcat.Utilities.CHARSET;
import static bio.gcat.Utilities.EMPTY;
import static bio.gcat.Utilities.SPACE;
import static bio.gcat.batch.Action.TaskAttribute.TEST_HANDLER;
import static bio.gcat.batch.Action.TaskAttribute.ANALYSIS_HANDLER;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.BooleanSupplier;
import java.util.stream.Collectors;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import bio.gcat.Utilities.DefiniteFuture;
import bio.gcat.Utilities.DefiniteListenableFuture;
import bio.gcat.log.InjectionLogger;
import bio.gcat.log.Logger;
import bio.gcat.nucleic.Tuple;
import bio.gcat.operation.Operation;
import bio.gcat.operation.analysis.Analysis;
import bio.gcat.operation.test.Test;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

public class Batch {
	private List<Action> actions = new LinkedList<>(), facade;
	
	public Batch() {}
	public Batch(Action... actions) { this(Arrays.asList(actions)); }
	public Batch(Enumeration<Action> actions) { this(Collections.list(actions)); }
	public Batch(Collection<Action> actions) { this.actions.addAll(actions); }
	
	public void addOperation(Class<? extends Operation> operation) { addAction(new Action(operation)); }
	public void addOperation(Class<? extends Operation> operation,Object... values) { addAction(new Action(operation,values)); }	
	
	public List<Action> getActions() { return facade!=null?facade:(facade=Collections.unmodifiableList(actions)); }
	public void addAction(Action action) { actions.add(action); }
	public void addActions(Action... actions) { this.actions.addAll(Arrays.asList(actions)); }
	public void removeAction(int index) { actions.remove(index); }
	public void removeAction(Action action) { actions.remove(action); }
	
	// build a queue of all the actions in this batch object (iterative)
	public Callable<Result> buildIterative(Collection<Tuple> tuples) { return buildIterative(new Result(tuples)); }
	public Callable<Result> buildIterative(final Result result) {
		Queue<Action> queue = new LinkedList<>(actions);
		return ()->{
			Action action;
			while((action=queue.poll())!=null)			
				result.setTuples(InjectionLogger.injectLogger(logAction(result,action),
					action.new Task(result.getTuples())).call());
			return result;
		};
	}
	
	// build a queue of all the actions in this batch object (recursive)
	public Callable<Result> buildRecursive(Collection<Tuple> tuples) { return buildRecursive(new Result(tuples)); }
	public Callable<Result> buildRecursive(final Result result) {
		Callable<Result> current = null;
		for(Action action:actions) {
			final Callable<Result> previous = current;
			current = ()->{
				Result localResult = previous!=null?previous.call():result;
				localResult.setTuples(InjectionLogger.injectLogger(logAction(localResult,action),
					action.new Task(localResult.getTuples())).call());
				return localResult;
			};
		} return current;
	}
	protected <T extends Logger> T logAction(T logger, Action action) {
		Object[] values = action.getValues();
		logger.log("Performing \"%s\". "+(values!=null&&values.length!=0?"Parameters: \"%s\".":"No parameters."),
			Operation.getName(action.getOperation()),values!=null?Arrays.stream(values).map(value->String.valueOf(value)).collect(Collectors.joining("\", \"")):null);
		return logger;
	}
	
	public ListenableFuture<Result> submit(Callable<Result> queue, ListeningExecutorService service) { return service.submit(queue); }
	public ListenableFuture<Result> submit(Callable<Result> queue, ListeningExecutorService service, BooleanSupplier start) {
		return service.submit(start!=null?new Callable<Result>() {
			@Override public Result call() throws Exception {
				if(start.getAsBoolean()) return queue.call(); else 
					throw new InterruptedException();
			}
		}:queue);
	}
	
	public ListenableFuture<Result> execute(Collection<Tuple> tuples) {
		final Result result = new Result(tuples);
		Queue<Action> queue = new LinkedList<>(actions);
		if(queue.isEmpty()) return new DefiniteListenableFuture<>(result);
		
		Action action; Future<Collection<Tuple>> future = new DefiniteFuture<>(tuples);
		ListeningExecutorService service = MoreExecutors.listeningDecorator(Executors.newSingleThreadExecutor());
		while((action=queue.poll())!=null)
			future = service.submit(InjectionLogger.injectLogger(result,action.new Task(future)));
		
		final ListenableFuture<Collection<Tuple>> lastFuture = (ListenableFuture<Collection<Tuple>>)future;
		return new ListenableFuture<Result>() {
			@Override public boolean isDone() { return lastFuture.isDone(); }
			@Override public boolean isCancelled() { return lastFuture.isCancelled(); }
			@Override public Result get(long timeout,TimeUnit unit) throws InterruptedException,ExecutionException,TimeoutException { result.setTuples(lastFuture.get(timeout,unit)); return result; }
			@Override public Result get() throws InterruptedException,ExecutionException { result.setTuples(lastFuture.get()); return result; }
			@Override public boolean cancel(boolean mayInterruptIfRunning) { return lastFuture.cancel(mayInterruptIfRunning); }
			@Override public void addListener(Runnable listener,Executor executor) { lastFuture.addListener(listener,executor); }
		};
	}
	
	public static class Result implements Logger {
		private Collection<Tuple> tuples;
		private List<Message> log = new LinkedList<Message>();
		
		public Result() { this(Collections.emptyList()); }
		public Result(Collection<Tuple> tuples) { this.tuples = tuples; }
		
		public Collection<Tuple> getTuples() { return tuples; }
		protected void setTuples(Collection<Tuple> tuples) { this.tuples = tuples; }
		
		public List<Message> getLog() { return log; }
		@Override public void log(String format,Object... arguments) { log.add(new Message(format,arguments)); }
		@Override public void log(String message,Throwable throwable) { log.add(new Message(message,throwable)); }
		
		public static class Message {
			public final String message;
			public final Throwable throwable;
			
			public Message(String format,Object... arguments) { this(String.format(format,arguments)); }
			public Message(String message) { this(message,(Throwable)null); }
			public Message(String message,Throwable throwable) {
				this.message = message;
				this.throwable = throwable;
			}
			
			@Override public String toString() {
				return message+(throwable!=null?" ("+throwable.getMessage()+")":EMPTY);
			}
		}
	}
	
	public static void main(String[] args) {
        ArgumentParser parser = ArgumentParsers.newArgumentParser(Batch.class.getName())
        	.defaultHelp(true).description("Executes a genetic code analysis script for each line of a file and prints the result to standard out.");
        parser.addArgument("-v", "--verbose").action(Arguments.storeTrue())
        	.help("verbose (prints source and target sequence [if any], seperates each result by a blank line)");
        parser.addArgument("-vv").action(Arguments.storeTrue())
        	.help("very verbose (same as -v, also prints the results of each test / analysis performed)");
        parser.addArgument("-vvv").action(Arguments.storeTrue())
        	.help("extremely verbose (same as -vv, also logs all performed operations incl. parameters and prints stack traces)");
        parser.addArgument("scriptfile")
        	.help("genetic code analysis toolkit script file (gcats)");
        parser.addArgument("sequencefile")
    		.help("list of sequences (one sequence per line)");

        Namespace ns = null;
        try { ns=parser.parseArgs(args); } 
        catch(ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        }
        
        final boolean extremelyVerbose = ns.getBoolean("vvv"), 
        	veryVerbose = extremelyVerbose||ns.getBoolean("vv"),
        	verbose = veryVerbose||ns.getBoolean("verbose");
		
		Script script = null;
		try { script = new Script(new File(ns.getString("scriptfile"))); }
		catch(IOException e) {
			System.err.printf("Could not read script file: %s", e.getMessage());
			if(extremelyVerbose) e.printStackTrace();
			System.exit(1);
		}
		
		final List<String> veryVerboseOutput = new ArrayList<>();
		if(veryVerbose) script.streamActions(Test.class).forEach(action->action.putAttribute(TEST_HANDLER, new Test.Handler() {
			@Override public void handle(Test test, boolean result) throws Test.Exception {
				String message = String.format("Test \"%s\": %b", Operation.getName(action.getOperation()), result);
				if(extremelyVerbose)
					 test.getLogger().log(message);
				else veryVerboseOutput.add(message);
				handleDefault(test, result);
			}
		}));
		if(!extremelyVerbose) script.streamActions(Analysis.class).forEach(action->action.putAttribute(ANALYSIS_HANDLER, new Analysis.Handler() {
			@Override public void handle(bio.gcat.operation.analysis.Analysis.Result result) {
				if(veryVerbose) veryVerboseOutput.add(result.toString());
			}
		}));
		
		Result result = null, temporaryResult = null;
		try(BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(ns.getString("sequencefile"))), CHARSET))) {
			String line; while((line=reader.readLine())!=null) {
				try {
					String tupleString = Tuple.tupleString(line);
					if(verbose) System.out.println(tupleString);
					temporaryResult = new Result(Tuple.sliceTuples(tupleString));
					
					result = script.createBatch().buildIterative(temporaryResult).call();
				} catch(Exception e) {
					if(!(e instanceof Test.Failed)) {
						System.err.println("Failed to execute operation.");
						if(extremelyVerbose) e.printStackTrace();
						System.exit(1);
					}
				} finally {
					if(extremelyVerbose)
						temporaryResult.getLog().forEach(message->{ System.out.println(message.message); });
					else if(veryVerbose) {
						veryVerboseOutput.forEach(System.out::println);
						veryVerboseOutput.clear();
					}
					
					if(result!=null) {
						result.getTuples().forEach(tuple->{
							System.out.print(tuple); System.out.print(SPACE);
						}); System.out.println();
					}
					
					if(verbose) System.out.println();
					result = temporaryResult = null;
				}
			}
		} catch(IOException e) {
			System.err.printf("Could not read sequence file: %s", e.getMessage());
			if(extremelyVerbose) e.printStackTrace();
			System.exit(1);
		}
	}
}
