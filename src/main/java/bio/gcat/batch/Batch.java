/*
 * Copyright [2014] [Mannheim University of Applied Sciences]
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
		}
	}
}
