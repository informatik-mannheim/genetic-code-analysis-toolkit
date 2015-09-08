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
package de.hsma.gentool;

import static java.lang.Math.*;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.Position;
import sun.reflect.ConstructorAccessor;
import sun.reflect.ReflectionFactory;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.google.common.base.CaseFormat;
import com.google.common.util.concurrent.ListenableFuture;
import de.hsma.gentool.gui.GenTool;

public final class Utilities {	
	public static final String EMPTY = "", SPACE = " ", NEW_LINE = "\n", WHITESPACE = " \t\n\r\f", TRUE = "true", FALSE = "false", CHARSET = "UTF-8";
	public static final double TWO_PI = PI*2, HALF_PI = PI/2, QUARTER_PI = PI/4, EIGHTH_PI = PI/8, SIXTEENTH_PI = PI/16;
	
  private static final int BUFFER_SIZE = 8192;
  private static final int TEMP_DIRECTORY_ATTEMPTS = 10000;

  private static Properties configuration = new Properties();
  
  public static URL getResource(String name) {
  	if(!name.startsWith("/"))
  		name = "/de/hsma/gentool/"+name;
  	return Utilities.class.getResource(name);
  }
  
  private static String tempDirectory() {
  	return System.getProperty("java.io.tmpdir");
  }
  
	private static boolean loadConfiguration() {
		if(!configuration.isEmpty()) return true;
		File file = new File(tempDirectory()+GenTool.class.getSimpleName()+".properties");
		if(!file.exists()) return false;
		try {
			configuration.load(new FileInputStream(file));
		}	catch (Exception e) {	return false;	}
		return true;
	}
	private static boolean writeConfiguration() {
		if(configuration.isEmpty()) return true;
		File file = new File(tempDirectory()+GenTool.class.getSimpleName()+".properties");
		try {
			if(! file.exists()) file.createNewFile();
			configuration.store(new BufferedOutputStream(new FileOutputStream(file)),new String());
		}	catch (IOException e) { return false;	}
		return true;		
	}	
	public static boolean hasConfiguration(String key) {
		return (loadConfiguration()&&configuration.containsKey(key));
	}
	public static String getConfiguration(String key) { return getConfiguration(key,null); }
	public static String getConfiguration(String key,String dfault) {
		if(!loadConfiguration()||!configuration.containsKey(key)) return dfault;
		return configuration.getProperty(key);
	}
	public static boolean setConfiguration(String key,String value) {
		configuration.setProperty(key,value);
		return writeConfiguration();
	}
	
	public static URL getLocalPath() {
		URL path = Utilities.class.getProtectionDomain().getCodeSource().getLocation();
		try { return new URL(URLDecoder.decode(path.toString(),"UTF-8")); }	
		catch(Exception e) { return path; }
	}

	public static File getFile(URL ressource) {
		StringBuilder file = new StringBuilder();
		String host = ressource.getHost(),path = ressource.getPath();
		if(host!=null&&!host.isEmpty())
			file.append(File.separatorChar).append(File.separatorChar).append(host);
		return new File(file.append(path.split("!",2)[0]).toString());
	}
	
	public static boolean isWindows() { return System.getProperty("os.name").startsWith("Windows"); }

	public static int indexOf(Object[] array,Object object) {
		for(int index=0;index<array.length;index++)
			if(array[index]==object) return index;
		return -1;
	}
	
	public static <T> T[] reverse(T[] originalArray) {
		T[] array = Arrays.copyOf(originalArray,originalArray.length);
		int indexA = 0, indexB = array.length-1; T temp;
		while(indexB>indexA) {
			temp = array[indexB];
			array[indexB] = array[indexA];
			array[indexA] = temp;
			indexA++; indexB--;
		} return array;
	}
	
	public static <T> T[] substitute(T[] originalArray,Map<T,T> substitution) {
		@SuppressWarnings("unchecked") T[] array = (T[])Array.newInstance(originalArray.getClass().getComponentType(),originalArray.length);
		for(int element=0;element<originalArray.length;element++)
			array[element] = substitution.getOrDefault(
				originalArray[element],originalArray[element]);
		return array;
	}
	
	public static <T> boolean contains(T[] array, T search) {
		for(T element:array)
			if (element==search||search!=null&&search.equals(element))
				return true;
		return false;
	}
	public static <T> boolean contains(Iterable<T> iterable, T search) {
		for(T element:iterable)
			if (element==search||search!=null&&search.equals(element))
				return true;
		return false;
	}

	public static <T> boolean containsOnly(T[] array, T search) {
		for(T element:array)
			if(element!=search&&(search==null||!search.equals(element)))
				return false;
		return true;
	}
	public static <T> boolean containsOnly(Iterable<T> iterable, T search) {
		for(T element:iterable)
			if(element!=search&&(search==null||!search.equals(element)))
				return false;
		return true;
	}

	public static String cropString(String text,int length) {
		if(text.length()<=length)
			return text;
		return text.substring(0,length)+"...";
	}
  
	public static class RememberFileChooser extends JFileChooser {
		private static final long serialVersionUID = 1l;
		public RememberFileChooser() { super(getLastDirectory()); }
		private static File getLastDirectory() {
			String directory = getConfiguration("directory");
			if(directory!=null) {
				File file = new File(directory);
				if(file.exists()&&file.isDirectory())
					return file;
				else return null;
			} else return null;
		}
		@Override public void approveSelection() {
			File selected = getSelectedFile();
			if(!selected.isDirectory())
				setConfiguration("directory",selected.getParentFile().toString());
			super.approveSelection();
		}
	}
	public static class FileNameExtensionFileChooser extends RememberFileChooser {
		private static final long serialVersionUID = 1l;
		
		private final FileFilter supportedFileFilter;
		
		public FileNameExtensionFileChooser(FileNameExtensionFilter... filters) { this(filters.length>1,filters); }
		public FileNameExtensionFileChooser(boolean addSupportedFileFilter, FileNameExtensionFilter... filters) {
			supportedFileFilter = supportedFileFilter(filters);
			if(addSupportedFileFilter)
				addChoosableFileFilter(supportedFileFilter);
			for(FileNameExtensionFilter filter:filters)
				addChoosableFileFilter(filter);
			if(addSupportedFileFilter)
				setFileFilter(supportedFileFilter);
			else if(filters.length!=0)
				setFileFilter(filters[0]);
		}
		
		public FileFilter getSupportedFileFilter() { return supportedFileFilter; }
		private static FileFilter supportedFileFilter(FileNameExtensionFilter... filters) {
			StringBuilder description = new StringBuilder("All Supported Files (");
			List<String> extensions = new ArrayList<>();
			for(FileNameExtensionFilter filter:filters)
				for(String extension:filter.getExtensions()) {
					description.append("*.").append(extension).append(';');
					extensions.add(extension);
				}
			description.deleteCharAt(description.length()-1);
			if(filters.length!=0) description.append(')');
			return new FileNameExtensionFilter(description.toString(),extensions.toArray(new String[0]));
		}

		public FileNameExtensionFilter getFileNameExtensionFilter() {
			FileFilter filter; return (filter=getFileFilter()) instanceof FileNameExtensionFilter?
				(FileNameExtensionFilter)filter:null;
		}
		
		@Override public void approveSelection() {
  		File file = getSelectedFile(); FileNameExtensionFilter filter = getFileNameExtensionFilter();
  		if(filter!=null&&!supportedFileFilter.accept(file)&&!file.exists())
				setSelectedFile(file=new File(file.getAbsolutePath()+'.'+filter.getExtensions()[0]));
  		if(getDialogType()==SAVE_DIALOG&&file!=null&&file.exists())
  			if(JOptionPane.showOptionDialog(getParent(),String.format("%s already exists.\nDo you want to replace it?",file.getName()),"Confirm Overwrite",JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE,null,null,null)==JOptionPane.NO_OPTION)
  				return;
  		super.approveSelection();
		}
	}
	
	public static Object jsonValueAsObject(JsonValue value) {
		if(value.isBoolean())
			return value.asBoolean();
		else if(value.isNumber())
			return value.asInt();
		else if(value.isString())
			return value.asString();
		else if(value.isArray())
			return jsonArrayAsList(value.asArray());
		else if(value.isObject())
			return jsonObjectAsMap(value.asObject());
		else return null;
	}
	public static Set<Object> jsonArrayAsSet(JsonArray array) {
		Set<Object> set = new HashSet<>();
		for(JsonValue value:array)
			set.add(jsonValueAsObject(value));
		return set;
	}
	public static List<Object> jsonArrayAsList(JsonArray array) {
		List<Object> list = new ArrayList<>();
		for(JsonValue value:array)
			list.add(jsonValueAsObject(value));
		return list;
	}
	public static Map<String,Object> jsonObjectAsMap(JsonObject object) {
		Map<String,Object> map = new HashMap<>();
		for(JsonObject.Member member:object)
			map.put(member.getName(), jsonValueAsObject(member.getValue()));
		return map;		
	}
	
	public static byte[] readFile(File file) throws IOException { return readStream(new FileInputStream(file)); }
  public static byte[] readStream(InputStream input) throws IOException { return readStream(input, -1, true); }
  public static byte[] readStream(InputStream input, int length, boolean readAll) throws IOException {
  	byte[] output = {}; int position = 0;
  	if(length==-1) length = Integer.MAX_VALUE;
  	while(position<length) {
  		int bytesToRead;
  		if(position>=output.length) { // Only expand when there's no room
  			bytesToRead = Math.min(length - position, output.length + 1024);
  			if(output.length < position + bytesToRead)
  				output = Arrays.copyOf(output, position + bytesToRead);
  		} else bytesToRead = output.length - position;
  		int bytesRead = input.read(output, position, bytesToRead);
  		if(bytesRead<0) {
  			if(!readAll||length==Integer.MAX_VALUE) {
  				if(output.length!=position)
  					output = Arrays.copyOf(output, position);
  				break;
  			} else throw new EOFException("Detect premature EOF");
  		}
  		position += bytesRead;
  	}
  	return output;
  }

  public static URLConnection writeString(URLConnection connection, String string) throws IOException {
  	writeString(connection.getOutputStream(), string);
  	return connection;	
  }
  public static void writeString(OutputStream output, String string) throws IOException {
  	BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output));
  	writer.write(string); writer.flush();
  }

  public static File createTempDirectory(String prefix) {
  	String baseName = prefix+Long.toString(System.currentTimeMillis())+"-";
  	File directory = new File(tempDirectory()), tempDirectory;
  	for (int counter=0;counter<TEMP_DIRECTORY_ATTEMPTS;counter++)
  		if((tempDirectory=new File(directory, baseName+counter)).mkdir())
  			return tempDirectory;
  	throw new IllegalStateException("Failed to create directory within "
  			+ TEMP_DIRECTORY_ATTEMPTS + " attempts (tried "
  			+ baseName + "0 to " + baseName + (TEMP_DIRECTORY_ATTEMPTS - 1) + ')');
  }

  public static void writeFile(String text, File file) throws IOException {
  	Writer writer = new BufferedWriter(new FileWriter(file));
  	try { writer.write(text); }
  	finally { writer.close(); }
  }
  public static void writeFile(InputStream input, File file) throws IOException {
  	OutputStream output = null;
  	try {
  		output = new FileOutputStream(file);
  		int read; byte[] buffer = new byte[BUFFER_SIZE];
  		while((read=input.read(buffer))>0)
  			output.write(buffer, 0, read);
  	} finally { if(output!=null) output.close(); }
  }

  public static boolean deleteDirectory(File directory) { return deleteDirectory(directory, false); }
  public static boolean deleteDirectory(File directory, boolean preserveGit) {
  	if(directory.exists()&&directory.isDirectory()&&!(preserveGit&&directory.getName().equals(".git"))) {
  		boolean deleteDirectory = true;
  		for(File file:directory.listFiles())
  			if(file.isDirectory()) {
  				if(!deleteDirectory(file, preserveGit))
  					deleteDirectory = false;
  			} else file.delete();
  		return deleteDirectory?directory.delete():false;
  	} else return false;
  }
  
  public static boolean isEmpty(String text) {
  	return text==null||text.isEmpty();
  }
	public static boolean isNumeric(String number) {
		try {
			Long.parseLong(number);
			return true;
		}	catch(NumberFormatException e) {
			return false;
		}
	}
	public static boolean isDecimal(String number) {
		try {
			Double.parseDouble(number);
			return true;
		}	catch(NumberFormatException e) {
			return false;
		}
	}
	 
	public static int countOccurences(String text, char find) {
		if(isEmpty(text)) return 0;
		int count = 0, offset = 0;
		while((offset=text.indexOf(find, offset))!=-1)
			{ count++; offset++; }
		return count;
	}
		
	public static int countOccurences(String text, String find) {
		if(isEmpty(text)) return 0;
		int count = 0, offset = 0, length = find.length();
		while((offset=text.indexOf(find, offset))!=-1)
			{ count++; offset += length; }
		return count;
	}
	
	public static Position fixPosition(final int position) {
		return new Position() {
			@Override public int getOffset() {
				return position;
			}
		};
	}
	
	public static int pow(int base, int exponent) {
		int result = 1;
		while(exponent!=0) {
			if((exponent&1)!=0)
				result *= base;
			exponent >>= 1;
			base *= base;
		}
		return result;
	}
	
	public static String firstToUpper(String text) {
		if(text==null||text.isEmpty())
			return text;
		else if(text.length()==1)
			return text.toUpperCase();
		else return Character.toUpperCase(text.charAt(0))+camelCase(text.substring(1).toLowerCase()); 
	}
	public static String camelCase(String text) {
		return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL,text);
	}

	public static enum Characters {
		SPACE(' ',"[ \t\\f]"),
		NEW_LINE('\n',"\r\n|\n\r|[\n\r]","\n?\r?"),
		WHITESPACE(' ',"\r\n|\n\r|[ \t\n\r\\f]");
		public static enum Trim { LEFT,RIGHT,BOTH; }
		
		public final char character;
		private final String string;
		private Pattern match,start,end,last,left,right,both,condense,split;
		
		private Characters(char character) { this(character,Character.toString(character)); }
		private Characters(char character,String pattern) {
			string = String.valueOf(this.character = character);
			split = match = Pattern.compile(pattern);
		}
		private Characters(char character,String pattern,String split) {
			this(character,pattern); this.split = Pattern.compile(split);
		}
		
		public boolean equals(String input) { return match.matcher(input).matches(); }
		public boolean contains(String input) { return match.matcher(input).find(); }
		public boolean startsWith(String input) { return (start!=null?start:(start=Pattern.compile("\\A"+match.pattern()))).matcher(input).find(); }
		public boolean endsWith(String input) { return (end!=null?end:(end=Pattern.compile(match.pattern()+"\\z"))).matcher(input).find(); }
		public int indexOf(String input) { Matcher matcher = match.matcher(input); return matcher.find()?matcher.start():-1; }
		public int lastIndexOf(String input) { Matcher matcher = (last!=null?last:(last=Pattern.compile(".*("+match.pattern()+")",Pattern.DOTALL))).matcher(input); return matcher.find()?matcher.start(1):-1; }
		public String replace(String input,String replacement) { return match.matcher(input).replaceAll(replacement); }
		public String trim(String input) { return trim(input,Trim.BOTH); }
		public String trim(String input, Trim trim) { return (Trim.BOTH.equals(trim)?(both!=null?both:(both=Pattern.compile("\\A(?:"+match.pattern()+")+|(?:"+match.pattern()+")+\\z"))):Trim.LEFT.equals(trim)?(left!=null?left:(left=Pattern.compile("\\A(?:"+match.pattern()+")+"))):(right!=null?right:(right=Pattern.compile("(?:"+match.pattern()+")+\\z")))).matcher(input).replaceAll(EMPTY); }
		public String condense(String input) { return (condense!=null?condense:(condense=Pattern.compile("(?:"+match.pattern()+")+"))).matcher(input).replaceAll(string); }
		public String[] split(String input) { return split.split(input); }
		
		@Override public String toString() { return string; }
		
		public static Characters valueOf(char character) {
			String string = Character.toString(character);
			try { return Characters.valueOf(string); }
			catch(IllegalArgumentException e) {
				return Enums.addEnum(Characters.class,string,
					new Class<?>[]{char.class},new Object[]{character});
			}
		}
	}
	
	public static class Enums {
		private static ReflectionFactory reflectionFactory = ReflectionFactory.getReflectionFactory();

		private static void setFailsafeFieldValue(Field field, Object target, Object value) throws NoSuchFieldException,IllegalAccessException {
			field.setAccessible(true);

			// change the modifier in the Field instance to
			// not be final anymore, thus tricking reflection into
			// letting us modify the static final field
			Field modifiersField = Field.class.getDeclaredField("modifiers");
			modifiersField.setAccessible(true);
			int modifiers = modifiersField.getInt(field);

			// blank out the final bit in the modifiers int
			modifiers &= ~Modifier.FINAL;
			modifiersField.setInt(field, modifiers);

			reflectionFactory.newFieldAccessor(field, false).set(target, value);
		}

		private static void blankField(Class<?> enumClass, String fieldName) throws NoSuchFieldException,IllegalAccessException {
			for(Field field : Class.class.getDeclaredFields()) {
				if(field.getName().contains(fieldName)) {
					AccessibleObject.setAccessible(new Field[] { field }, true);
					setFailsafeFieldValue(field, enumClass, null);
					break;
				}
			}
		}

		private static void cleanEnumCache(Class<?> enumClass) throws NoSuchFieldException, IllegalAccessException {
			blankField(enumClass, "enumConstantDirectory"); // Sun (Oracle?!?) JDK 1.5/6
			blankField(enumClass, "enumConstants"); // IBM JDK
		}

		private static ConstructorAccessor getConstructorAccessor(Class<?> enumClass, Class<?>[] additionalParameterTypes) throws NoSuchMethodException {
			Class<?>[] parameterTypes = new Class[additionalParameterTypes.length + 2];
			parameterTypes[0] = String.class;
			parameterTypes[1] = int.class;
			System.arraycopy(additionalParameterTypes, 0, parameterTypes, 2, additionalParameterTypes.length);
			return reflectionFactory.newConstructorAccessor(enumClass.getDeclaredConstructor(parameterTypes));
		}

		private static Object makeEnum(Class<?> enumClass, String value, int ordinal, Class<?>[] additionalTypes,
				Object[] additionalValues) throws Exception {
			Object[] parms = new Object[additionalValues.length + 2];
			parms[0] = value;
			parms[1] = Integer.valueOf(ordinal);
			System.arraycopy(additionalValues, 0, parms, 2, additionalValues.length);
			return enumClass.cast(getConstructorAccessor(enumClass, additionalTypes).newInstance(parms));
		}

		
		public static <T extends Enum<?>> void addEnum(Class<T> enumType, String enumName) {
			addEnum(enumType, enumName, new Class<?>[] {}, new Object[] {});
		}
		
		/**
		 * Add an enum instance to the enum class given as argument
		 *
		 * @param <T> the type of the enum (implicit)
		 * @param enumType the class of the enum to be modified
		 * @param enumName the name of the new enum instance to be added to the class.
		 * @return 
		 */
		@SuppressWarnings("unchecked")
		public static <T extends Enum<?>> T addEnum(Class<T> enumType, String enumName, Class<?>[] additionalTypes, Object[] additionalValues) {
			// 0. Sanity checks
			if(!Enum.class.isAssignableFrom(enumType))
				throw new RuntimeException("class "+enumType+" is not an instance of Enum");
			
			// 1. Lookup "$VALUES" holder in enum class and get previous enum instances
			Field valuesField = null, fields[] = enumType.getDeclaredFields();
			for(Field field:fields)
				if(field.getName().contains("$VALUES")) {
					valuesField = field;
					break;
				}
			AccessibleObject.setAccessible(new Field[]{valuesField},true);

			try {
				// 2. Copy it
				T[] previousValues = (T[])valuesField.get(enumType),
					newValues = Arrays.copyOf(previousValues,previousValues.length+1);
				
				// 3. build new enum
				newValues[previousValues.length] = (T) makeEnum(enumType, // The target enum class
					enumName, // THE NEW ENUM INSTANCE TO BE DYNAMICALLY ADDED
					previousValues.length,
					additionalTypes, // could be used to pass values to the enum constuctor if needed
					additionalValues); // could be used to pass values to the enum constuctor if needed
				
				// 5. Set new values field
				setFailsafeFieldValue(valuesField, null, newValues);

				// 6. Clean enum cache
				cleanEnumCache(enumType);
				
				return newValues[previousValues.length];
			} catch (Exception e) { throw new RuntimeException(e.getMessage(), e); }
		}
	}
	
	public static class Range {
		public final Position start, end;
		public Range(Position start, Position end) {
			if(start.getOffset()<=end.getOffset()) {
				this.start = start; this.end = end;
			} else {
				this.start = end; this.end = start;
			}
		}
	}
	
	public static enum OperatingSystem {
		WINDOWS,MAC,UNIX,SOLARIS;
		private static final OperatingSystem OPERATING_SYSTEM;
		static {
			String name = System.getProperty("os.name").toLowerCase();
			if(name.contains("win"))
				OPERATING_SYSTEM = WINDOWS;
			else if(name.contains("mac"))
				OPERATING_SYSTEM = MAC;
			else if(name.contains("nix")||name.contains("nux")||name.contains("aix"))
				OPERATING_SYSTEM = UNIX;
			else if(name.contains("sunos"))
				OPERATING_SYSTEM = SOLARIS;
			else OPERATING_SYSTEM = null;
		}
		public static OperatingSystem currentOperatingSystem() { return OPERATING_SYSTEM; }
	}
	
	public static class DefiniteFuture<T> implements Future<T> {
		private T result;
		public DefiniteFuture() {}
		public DefiniteFuture(T result) { set(result); }
		@Override public boolean cancel(boolean mayInterruptIfRunning) { return true; }
		@Override public boolean isCancelled() { return true; }
		@Override public boolean isDone() { return true; }
		@Override public T get() { return result; }
		@Override public T get(long timeout,TimeUnit unit) { return get(); }
		public void set(T result) { this.result = result; }
	}
	
	public static class DefiniteListenableFuture<T> extends DefiniteFuture<T> implements ListenableFuture<T> {
		public DefiniteListenableFuture(T result) { super(result); }
		@Override public void addListener(Runnable runnable, Executor executor) { runnable.run(); }
	}
}