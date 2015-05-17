package de.hsma.gentool.operation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;
import org.reflections.Reflections;
import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;
import de.hsma.gentool.Parameter;
import de.hsma.gentool.log.InjectionLogger;
import de.hsma.gentool.log.Logger;

public interface Operation extends InjectionLogger.Injectable {
	public static String GET_PARAMETERS = "getParameters";
	
	public default String getName() {
		Named displayed = this.getClass().getAnnotation(Named.class);
		return displayed!=null?displayed.name():null;
	}
	public default String getGroup() {
		Cataloged cataloged = this.getClass().getAnnotation(Cataloged.class);
		return cataloged!=null?cataloged.group():null;
	}
	
	public static String getName(Class<? extends Operation> operation) { return operation.isAnnotationPresent(Named.class)?operation.getAnnotation(Named.class).name():operation.getSimpleName(); }
	public static Parameter[] getParameters(Class<? extends Operation> operation) {
		try {
			Object parameters = operation.getMethod(Operation.GET_PARAMETERS).invoke(null);
			return parameters instanceof Parameter[]?(Parameter[])parameters:null;
		} catch(NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | SecurityException e) { return null; }
	}
	
	public static <T extends Operation> T newInstance(Class<T> operation) throws InstantiationException, IllegalAccessException { return newInstance(operation,null); }
	public static <T extends Operation> T newInstance(Class<T> operation, Logger logger) throws InstantiationException, IllegalAccessException {
		return InjectionLogger.injectLogger(logger, operation.newInstance());
	}
	
	public static Set<Class<? extends Operation>> getOperations() { return getOperations(false); }
	public static Set<Class<? extends Operation>> getOperations(boolean onlyCataloged) {
		Set<Class<? extends Operation>> operations = new TreeSet<>(new Comparator<Class<? extends Operation>>() {
			@Override public int compare(Class<? extends Operation> operationA,Class<? extends Operation> operationB) {
				return Operation.getName(operationA).compareTo(Operation.getName(operationB));
			}
		});
		operations.addAll(new Reflections(Operation.class.getPackage().getName()).getSubTypesOf(Operation.class));
		operations.removeIf(new Predicate<Class<? extends Operation>>() {
			@Override public boolean test(Class<? extends Operation> operation) {
				if(onlyCataloged&&!operation.isAnnotationPresent(Cataloged.class))
					return true;
				return operation.isInterface()||Modifier.isAbstract(operation.getModifiers());
			}
		});
		return operations;
	}
	
	public static Multimap<String,Class<? extends Operation>> getGroups() {
		Multimap<String,Class<? extends Operation>> groups = TreeMultimap.create(Comparator.reverseOrder(),new Comparator<Class<? extends Operation>>() {
			@Override public int compare(Class<? extends Operation> operationA,Class<? extends Operation> operationB) {
				Cataloged catalogedA = operationA.getAnnotation(Cataloged.class), catalogedB = operationB.getAnnotation(Cataloged.class);
				int orderA = catalogedA!=null?catalogedA.order():Short.MAX_VALUE, orderB = catalogedB!=null?catalogedB.order():Short.MAX_VALUE;
				return orderA!=orderB?Integer.compare(orderA,orderB):operationA.getSimpleName().compareTo(operationB.getSimpleName());
			}
		});
		for(Class<? extends Operation> operation:getOperations(true))
			groups.put(operation.getAnnotation(Cataloged.class).group(),operation);
		return groups;
	}
	
	public static class Exception extends java.lang.Exception {
		private static final long serialVersionUID = 1l;
		
		private Operation operation;
		
	  public Exception(Operation operation) { super(); this.operation = operation; }
	  public Exception(Operation operation, String message) { super(message); this.operation = operation; }
	  public Exception(Operation operation, String message, Throwable cause) { super(message, cause); this.operation = operation; }
	  public Exception(Operation operation, Throwable cause) { super(cause); this.operation = operation; }
	  
	  public Operation getOperation() { return operation; }
	}
}