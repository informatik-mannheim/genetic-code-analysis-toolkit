package bio.gcat.batch;

import static bio.gcat.batch.Action.TaskAttribute.SPLIT_PICK;
import static bio.gcat.batch.Action.TaskAttribute.SPLIT_PICK_ANY;
import static bio.gcat.batch.Action.TaskAttribute.SPLIT_PICK_FIRST;
import static bio.gcat.batch.Action.TaskAttribute.SPLIT_PICK_LAST;
import static bio.gcat.batch.Action.TaskAttribute.TEST_CRITERIA;
import static bio.gcat.batch.Action.TaskAttribute.TEST_CRITERIA_BREAK_IF_FALSE;
import static bio.gcat.batch.Action.TaskAttribute.TEST_CRITERIA_BREAK_IF_TRUE;
import static bio.gcat.batch.Action.TaskAttribute.TEST_CRITERIA_NEVER_BREAK;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableBiMap;

import bio.gcat.Parameter;
import bio.gcat.batch.Action.TaskAttribute;
import bio.gcat.operation.Operation;
import bio.gcat.operation.split.Split;
import bio.gcat.operation.test.Test;

public class Script {
	public static final String
		SCRIPT_IDENTIFIER = "-- GENETIC CODE ANALYSIS TOOLKIT SCRIPT --"; 
		
	protected static final String
		ATTRIBUTE_FORMAT = "%s=%s,",
		VALUE_FORMAT = "%s,";

	protected static final String
		ATTRIBUTE_TEST_CRITERIA = "testCriteria",
		ATTRIBUTE_SPLIT_PICK = "splitPick";
	
	private static final Pattern
		SCRIPT_IDENTIFIER_PATTERN = Pattern.compile("-+ ?GENETIC CODE ANALYSIS TOOLKIT SCRIPT ?-+"),
		ACTION_PATTERN = Pattern.compile("((?:[A-Za-z_$][a-zA-Z0-9_$]*\\.)*[A-Za-z_$][a-zA-Z0-9_$]*)[^\\S\r\n]*(?:\\[([^\\]]*)\\])?[^\\S\r\n]*\\(([^)]*)\\)"), //((?:[A-Za-z_$][a-zA-Z0-9_$]*\.)*[A-Za-z_$][a-zA-Z0-9_$]*)[^\S\r\n]*(?:\[([^\]]*)\])?[^\S\r\n]*\(([^)]*)\) 
		COMMA_PATTERN = Pattern.compile("(?<!(?<![^\\\\]\\\\(?:\\\\{2}){0,10})\\\\),"); //(?<!(?<![^\\]\\(?:\\{2}){0,10})\\),
		
	private List<Action> actions = new ArrayList<>();
	
	private final BiMap<TaskAttribute,String> attributeMap;
	private final Map<TaskAttribute,BiMap<Object,String>> attributeValueMap;
	
	public Script() {
		// build the attribute / attribute value maps
		attributeMap = buildAttributeMap();
		attributeValueMap = buildAttributeValueMap();
	}
	public Script(Collection<Action> actions) {
		this(); this.actions.addAll(actions); }
	public Script(File file) throws IOException {
		this(); readFrom(file); }
	public Script(Reader reader) throws IOException {
		this(); readFrom(reader); }
	
	public List<Action> getActions() { return Collections.unmodifiableList(actions); }
	public void addAction(Action action) { actions.add(action); }
	public void removeAction(Action action) { actions.remove(action); }
	
	public Batch createBatch() { return new Batch(actions); }
	
	public void readFrom(File file) throws IOException {
		try(FileReader reader=new FileReader(file)) {
			readFrom(reader); }
	}
	public void readFrom(Reader reader) throws IOException {
		boolean identified = false; String line; long count = 0;
		Matcher matcher; Class<? extends Operation> operation;
		BufferedReader bufferedReader = new BufferedReader(reader);
		while((line=bufferedReader.readLine())!=null) {
			count++; line = line.trim(); // trim line and check if empty / comment
			if(line.isEmpty()||line.charAt(0)=='#')
				continue;
			else if(!identified) {
				if(!(identified=SCRIPT_IDENTIFIER_PATTERN.matcher(line).matches()))
					throw new IOException("Script identifier missing. The given resource is not a valid script file.");
				continue; // we have a script here
			} else if(!(matcher=ACTION_PATTERN.matcher(line)).matches())
				throw new IOException(String.format("Action in line %d does not match the required format action[attribute1=value,...](value1,...). The given resource is not a valid script file.",count));
			
			// parse the action string and create a action
			addAction(new Action(operation=parseOperation(matcher.group(1)),
				parseAttributes(unescapeValues(matcher.group(2))),
				parseValues(operation,unescapeValues(matcher.group(3)))));
		}
	}
	
	public void writeTo(File file) throws IOException {
		try(FileWriter writer=new FileWriter(file)) {
			writeTo(writer); }
	}
	public void writeTo(Writer writer) throws IOException {
		CommaPrintWriter printWriter = new CommaPrintWriter(writer);
		printWriter.append(SCRIPT_IDENTIFIER).println();
		
		for(Action action:actions) {
			// write one line per action: <NAME>[<TASK_ATTRIBUTE1>=<VALUE>,...](<ACTION_VALUE1>,...)
			writeOperationTo(printWriter,action);
			
			// write task attributes in square brackets [<TASK_ATTRIBUTE1>=<VALUE>,...]
			printWriter.write('[');
			writeAttributesTo(printWriter,action);
			printWriter.stripComma().write(']');
			// write values in brackets (<ACTION_PARAMETER1>,...)
			printWriter.write('(');
			writeValuesTo(printWriter,action);
			printWriter.stripComma().write(')');
			
			// line separator to separate actions
			printWriter.println();
		}
	}
	
	/**
	 * append operation name
	 */
	protected void writeOperationTo(PrintWriter writer, Action action) throws IOException {
		writer.write(action.getOperation().getName()); }
	/**
	 * append attributes (if present, some attributes can't be stored)
	 */
	protected void writeAttributesTo(PrintWriter writer, Action action) throws IOException {
		Class<? extends Operation> operation = action.getOperation();
		
		for(Entry<TaskAttribute,Object> attribute:action.getAttributes().entrySet()) {			
			String name = attributeMap.get(attribute.getKey());
			Map<Object,String> values = attributeValueMap.get(attribute.getKey());
			if(name==null||values==null||!values.containsKey(attribute.getValue())) 
				continue; // if the attribute map doesn't contain the attribute it can't be stored
			
			// check for the default conditions, as they are added to any operation
			if(attribute.getKey()==TEST_CRITERIA&&!Test.class.isAssignableFrom(operation)
			|| attribute.getKey()==SPLIT_PICK&&!Split.class.isAssignableFrom(operation))
				continue;
			
			writer.format(ATTRIBUTE_FORMAT,name,escapeValue(values.get(attribute.getValue())));
		}
	}
	/**
	 * append values (in sequence, if present)
	 */
	protected void writeValuesTo(PrintWriter writer, Action action) throws IOException {
		for(Object value:action.getValues())
			writer.format(VALUE_FORMAT,escapeValue(value.toString()));
	}
	
	/**
	 * parse operation and return the operation class to instantiate
	 */
	@SuppressWarnings("unchecked") protected Class<? extends Operation> parseOperation(String operationString) throws IOException {
		try {
			Class<?> likelyOperation = Class.forName(operationString);
			if(!Operation.class.isAssignableFrom(likelyOperation))
				throw new IOException(String.format("The operations %s was found, but is not a operations subtype.",operationString));
			return (Class<? extends Operation>)likelyOperation;
		} catch (ClassNotFoundException e) { throw new IOException(String.format("Operation %s not found.",operationString),e); }
	}
	
	/**
	 * parse attributes and return a list of task attributes
	 */
	protected Map<TaskAttribute,Object> parseAttributes(String[] attributeStrings) throws IOException {
		Map<TaskAttribute,Object> attributes = new HashMap<>();
		for(String attributeString:attributeStrings) {
			String[] likelyAttribute = attributeString.split("=",2);
			if(likelyAttribute.length<=1)
				continue; // attribute has no value skip
				
			TaskAttribute attribute = Optional.ofNullable(attributeMap.inverse().get(likelyAttribute[0]))
				.orElseThrow(()->new IOException(String.format("Unknown task attribute %s.",likelyAttribute[0])));
			
			Map<String,Object> values = attributeValueMap.getOrDefault(attribute,ImmutableBiMap.of()).inverse();
			if(!values.containsKey(likelyAttribute[1])) // use contains, as attribute value may be null deliberately
				throw new IOException(String.format("Unknown value %s for task attribute %s.",likelyAttribute[1],attribute.getName()));
			
			attributes.put(attribute,values.get(likelyAttribute[1]));
		} return attributes;
	}
	
	/**
	 * parse operation values
	 */
	protected Object[] parseValues(Class<? extends Operation> operation, String[] valueStrings) throws IOException {
		Parameter[] parameters = Operation.getParameters(operation);
		if(parameters==null) return new Object[0];
		Object[] values = Parameter.getValues(parameters); // get default values
		
		for(int parameter=0;parameter<parameters.length;parameter++) try {
			if(parameter>=valueStrings.length)
				break; // no more strings as parameter, stay on default
			else if(valueStrings[parameter].isEmpty())
				continue;
			
			switch(parameters[parameter].type) {
			case TEXT: case LIST:
				values[parameter] = valueStrings[parameter]; break;
			case BOOLEAN:
				values[parameter] = Boolean.parseBoolean(valueStrings[parameter]); break;
			case DECIMAL:
				values[parameter] = Double.parseDouble(valueStrings[parameter]); break;
			case NUMBER:
				values[parameter] = Long.parseLong(valueStrings[parameter]); break;
			case FILE:
				values[parameter] = new File(valueStrings[parameter]); break;
			}
		} catch(NumberFormatException e) { throw new IOException(String.format("Expected number for action value %d.",parameter)); }
		
		return values;
	}
	
	protected BiMap<TaskAttribute,String> buildAttributeMap() {
		BiMap<TaskAttribute,String> attributeMap = HashBiMap.create();
		attributeMap.put(TEST_CRITERIA,"testCriteria");
		attributeMap.put(SPLIT_PICK,"splitPick");
		return attributeMap;
	}
	protected Map<TaskAttribute,BiMap<Object,String>> buildAttributeValueMap() {
		Map<TaskAttribute,BiMap<Object,String>> attributeValueMap = new HashMap<>();
		
		BiMap<Object,String> testCriteriaMap = HashBiMap.create();
		testCriteriaMap.put(TEST_CRITERIA_NEVER_BREAK,String.valueOf((Object)null));
		testCriteriaMap.put(TEST_CRITERIA_BREAK_IF_TRUE,Boolean.TRUE.toString());
		testCriteriaMap.put(TEST_CRITERIA_BREAK_IF_FALSE,Boolean.FALSE.toString());
		attributeValueMap.put(TEST_CRITERIA,testCriteriaMap);
		
		BiMap<Object,String> splitPickMap = HashBiMap.create();
		splitPickMap.put(SPLIT_PICK_FIRST,"first");
		splitPickMap.put(SPLIT_PICK_LAST,"last");
		splitPickMap.put(SPLIT_PICK_ANY,"any");
		attributeValueMap.put(SPLIT_PICK,splitPickMap);
		
		return attributeValueMap;
	}
	
	protected String escapeValue(String value) {
		if(value.indexOf('\\')==-1&&value.indexOf(',')==-1)
			return value;
		StringBuilder stringBuilder = new StringBuilder();
		for(int index=0;index<value.length();index++) {
			char character = value.charAt(index);
			if(character=='\\'||character==',')
				stringBuilder.append('\\');
			stringBuilder.append(character);			
		} return stringBuilder.toString();
	}
	
	private String unescapeValue(String value) {
		return value.replace("\\\\","\\").replace("\\,",","); }
	private String[] unescapeValues(String values) {
		return COMMA_PATTERN.splitAsStream(values).map(value->unescapeValue(value)).toArray(String[]::new); }
	
	/**
	 * a special 'comma aware' print writer, able to dismisses the last comma written
	 */
	private static class CommaPrintWriter extends PrintWriter {	
		private boolean hadComma = false;
		public CommaPrintWriter(Writer out) { super(out); }
		@Override public void write(char[] buffer,int offset,int length) {
			if(length>0&&checkComma(buffer[offset+(length-1)])) {
				if(length>1) super.write(buffer,offset,length-1);
			} else super.write(buffer, offset, length);
		}
		@Override public void write(String string, int offset, int length) {
			if(string.length()>0&&checkComma(string.charAt(offset+(length-1)))) {
				if(string.length()>1) super.write(string,offset,length-1);
			} else super.write(string, offset, length);
		}
		@Override public void write(int character) {
			if(!checkComma(character))
				super.write(character);
		}
		@Override public void close() {
			writeComma();
			super.close();
		}
		
		public CommaPrintWriter stripComma() {
			hadComma = false;
			return this;
		}
		
		private boolean checkComma(int character) {
			writeComma();
			if(character==',') {
				hadComma = true;
				return true;
			} else return false;
		}
		private void writeComma() {
			if(hadComma) {
				super.write(',');
				hadComma = false;
			}
		}
	}
}
