package de.hsma.gentool.batch;

import static de.hsma.gentool.batch.Action.TaskAttribute.*;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import de.hsma.gentool.Parameter;
import de.hsma.gentool.log.InjectionLogger;
import de.hsma.gentool.nucleic.Tuple;
import de.hsma.gentool.operation.Operation;
import de.hsma.gentool.operation.analysis.Analysis;
import de.hsma.gentool.operation.analysis.Analysis.Result;
import de.hsma.gentool.operation.split.Split;
import de.hsma.gentool.operation.test.Test;
import de.hsma.gentool.operation.transformation.Transformation;

public class Action {
	private final Class<? extends Operation> operation;
	public Map<TaskAttribute,Object> attributes;
	public Object[] values;
	
	public Action(Class<? extends Operation> operation) { this(operation, Parameter.getValues(Operation.getParameters(operation))); }
	public Action(Class<? extends Operation> operation, Object... values) {
		this(operation,new HashMap<>(DEFAULT_ATTRIBUTES),values); //default behaviour (e.g. break if test is false)
	}
	public Action(Class<? extends Operation> operation, Map<TaskAttribute,Object> attributes, Object... values) {
		this.operation = operation;
		this.values = values;
		this.attributes = attributes;
	}
	
	public Class<? extends Operation> getOperation() { return operation; }
	
	public class Task implements Callable<Collection<Tuple>>, InjectionLogger.Injectable {
		private Collection<Tuple> tuples;
		private Future<Collection<Tuple>> future;
		private Callable<Collection<Tuple>> task;
		
		public Task(Collection<Tuple> tuples) { this.tuples = tuples; }
		public Task(Future<Collection<Tuple>> future) { this.future = future; }
		public Task(Callable<Collection<Tuple>> task) { this.task = task; }
		
		public Collection<Tuple> getTuples() { return tuples; }
		
		@Override public Collection<Tuple> call() throws Exception {
			   if(future!=null) tuples = future.get(); //could be a ExecutionException with cause Test.Failed (which is fine)
			else if(task!=null) tuples = task.call(); //could be a Exception which cause Test.Failed (which is fine)
			Operation instance = Operation.newInstance(operation,getLogger());
			if(instance instanceof Transformation)
				return ((Transformation)instance).transform(tuples,values);
			else if(instance instanceof Test) {
				if(Boolean.valueOf(((Test)instance).test(tuples,values)).equals(valueOrDefault(attributes,TEST_CRITERIA)))
					throw new Test.Failed((Test)instance);
			} else if(instance instanceof Analysis)
				((Analysis.Handler)valueOrDefault(attributes,ANALYSIS_HANDLER)).handle(((Analysis)instance).analyse(tuples,values));
			else if(instance instanceof Split)
				return Optional.ofNullable(valueOrDefault(attributes,SPLIT_PICK,Split.Pick.class).pick(((Split)instance).split(tuples,values))).orElseThrow(()->new Operation.Exception(instance,"Split failed"));
			return tuples;
		}
	}
	
	public static final class TaskAttribute { // implements Serializable {
		private static final long serialVersionUID = -6267992889104134263L;

		public static final TaskAttribute TEST_CRITERIA = new TaskAttribute("Test Creteria");
		public static final Boolean
			TEST_CRITERIA_NEVER_BREAK = null,
			TEST_CRITERIA_BREAK_IF_TRUE = true,
			TEST_CRITERIA_BREAK_IF_FALSE = false;		
		
		public static final TaskAttribute ANALYSIS_HANDLER = new TaskAttribute("Analysis Handler");
		public static final Analysis.Handler
			ANALYSIS_HANDLER_DEFAULT = new Analysis.Handler() { @Override public void handle(Result result) {} };
		
		public static final TaskAttribute SPLIT_PICK = new TaskAttribute("Split Pick");
		public static final Split.Pick
			SPLIT_PICK_FIRST = new Split.Pick() { @Override public Collection<Tuple> pick(List<Collection<Tuple>> split) { return split!=null&&!split.isEmpty()?split.get(0):null; } },
			SPLIT_PICK_LAST = new Split.Pick() { @Override public Collection<Tuple> pick(List<Collection<Tuple>> split) { return split!=null&&!split.isEmpty()?split.get(split.size()-1):null; } },
			SPLIT_PICK_ANY = new Split.Pick() { private final Random RANDOM = new Random(); @Override public Collection<Tuple> pick(List<Collection<Tuple>> split) { return split!=null&&!split.isEmpty()?split.get(RANDOM.nextInt(split.size())):null; } };
		
		public static final Map<TaskAttribute,Object> DEFAULT_ATTRIBUTES;
		static {
			Map<TaskAttribute,Object> defaultAttributes = new HashMap<>();
			defaultAttributes.put(TEST_CRITERIA,TEST_CRITERIA_BREAK_IF_FALSE);
			defaultAttributes.put(ANALYSIS_HANDLER,ANALYSIS_HANDLER_DEFAULT);
			defaultAttributes.put(SPLIT_PICK,SPLIT_PICK_FIRST);
			DEFAULT_ATTRIBUTES = Collections.unmodifiableMap(defaultAttributes);
		}
		
		private String name;
		protected TaskAttribute(String name) { this.name = name; }
		public String getName() { return name; }
		public String toString() { return getClass().getName() + "(" + name + ")"; }
		
		public static Object valueOrDefault(Map<TaskAttribute,Object> attributes,TaskAttribute attribute) {
			if(attributes==null||!attributes.containsKey(attribute))
				return DEFAULT_ATTRIBUTES.get(attribute);
			return attributes.get(attribute);
		}
		@SuppressWarnings("unchecked") static <T> T valueOrDefault(Map<TaskAttribute,Object> attributes,TaskAttribute attribute,Class<T> type) {
			Object value = valueOrDefault(attributes,attribute);
			if(type==null||!type.isInstance(value))
				return (T)DEFAULT_ATTRIBUTES.get(attribute);
			return (T)value;
		}
  }
}