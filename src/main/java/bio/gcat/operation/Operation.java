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
package bio.gcat.operation;

import java.lang.reflect.Modifier;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;

import bio.gcat.Parameter;
import bio.gcat.log.InjectionLogger;
import bio.gcat.log.Logger;

public interface Operation extends InjectionLogger.Injectable {
	default public String getName() { return getName(this.getClass()); }
	default public String getIcon() { return getIcon(this.getClass()); }
	default public String getGroup() { return getGroup(this.getClass()); }
	
	public static String getName(Class<? extends Operation> operation) {
		if(operation.isAnnotationPresent(Named.class))
		     return operation.getAnnotation(Named.class).name();
		else return operation.getSimpleName();
	}
	public static String getIcon(Class<? extends Operation> operation) {
		if(operation.isAnnotationPresent(Named.class))
		     return operation.getAnnotation(Named.class).icon();
		else return null;
	}
	public static String getGroup(Class<? extends Operation> operation) {
		if(operation.isAnnotationPresent(Cataloged.class))
		     return operation.getAnnotation(Cataloged.class).group();
		else return null;
	}
	public static Parameter[] getParameters(Class<? extends Operation> operation) {
		return Parameter.getParameters(operation);
	}
	
	public static <T extends Operation> T newInstance(Class<T> operation) throws InstantiationException, IllegalAccessException { return newInstance(operation,null); }
	public static <T extends Operation> T newInstance(Class<T> operation, Logger logger) throws InstantiationException, IllegalAccessException {
		return InjectionLogger.injectLogger(logger, operation.newInstance());
	}
	
	public static Set<Class<? extends Operation>> getOperations() { return getOperations(false); }
	public static Set<Class<? extends Operation>> getOperations(boolean onlyCataloged) {
		Set<Class<? extends Operation>> operations = new TreeSet<>(new Comparator<Class<? extends Operation>>() {
			@Override public int compare(Class<? extends Operation> operationA,Class<? extends Operation> operationB) {
				String nameA = Operation.getName(operationA), nameB = Operation.getName(operationB);
				if(nameA.equals(nameB)) { nameA = operationA.getName(); nameB = operationB.getName(); }
				return nameA.compareTo(nameB);
			}
		});
		Reflections operationsReflections = new Reflections(new ConfigurationBuilder()
			.addClassLoaders(ClasspathHelper.staticClassLoader(),ClasspathHelper.contextClassLoader()/*,ClassLoader.getSystemClassLoader()*/)
			.setUrls(ClasspathHelper.forPackage(Operation.class.getPackage().getName()))
			.setScanners(new SubTypesScanner()));
		operations.addAll(operationsReflections.getSubTypesOf(Operation.class));
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