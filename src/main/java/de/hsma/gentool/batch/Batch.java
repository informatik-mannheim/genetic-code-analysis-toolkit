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
package de.hsma.gentool.batch;

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
import java.util.function.Function;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import de.hsma.gentool.Utilities.DefiniteFuture;
import de.hsma.gentool.Utilities.DefiniteListenableFuture;
import de.hsma.gentool.log.InjectionLogger;
import de.hsma.gentool.log.Logger;
import de.hsma.gentool.nucleic.Tuple;
import de.hsma.gentool.operation.Operation;

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
	
	public Callable<Result> build(Collection<Tuple> tuples) {
		final Result result = new Result(tuples);
		Queue<Action> queue = new LinkedList<>(actions);
		return new Callable<Result>() {
			@Override public Result call() throws Exception {
				Action action;
				while((action=queue.poll())!=null)
					result.setTuples(InjectionLogger.injectLogger(result,action.new Task(result.getTuples())).call());
				return result;
			}
		};
	}
	
	public ListenableFuture<Result> submit(Collection<Tuple> tuples, ListeningExecutorService service) { return submit(tuples,service,null); }
	public ListenableFuture<Result> submit(Collection<Tuple> tuples, ListeningExecutorService service, Function<Collection<Tuple>,Boolean> start) {
		Callable<Result> result = build(tuples);
		return service.submit(start!=null?new Callable<Result>() {
			@Override public Result call() throws Exception {
				if(start.apply(tuples))
					return result.call();
				else throw new Exception();
			}
		}:result);
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
