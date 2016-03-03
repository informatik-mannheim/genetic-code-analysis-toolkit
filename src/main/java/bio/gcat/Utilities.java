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
package bio.gcat;

import static java.lang.Math.PI;

import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Array;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.Position;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.google.common.base.CaseFormat;
import com.google.common.util.concurrent.ListenableFuture;

import bio.gcat.gui.AnalysisTool;

public final class Utilities {	
	public static final String EMPTY = "", SPACE = " ", TAB = "\t", NEW_LINE = "\n", WHITESPACE = " \t\n\r\f", TRUE = "true", FALSE = "false", CHARSET = "UTF-8";
	public static final double TWO_PI = PI*2, HALF_PI = PI/2, QUARTER_PI = PI/4, EIGHTH_PI = PI/8, SIXTEENTH_PI = PI/16;

	private static final int BUFFER_SIZE = 8192;
	private static final int TEMP_DIRECTORY_ATTEMPTS = 10000;
	
	private static final String CONFIGURATION_NAME = AnalysisTool.class.getSimpleName()+".properties";
	
	private static Properties configuration;
	private static ClassLoader localClassLoader;
	static { localClassLoader = Utilities.class.getClassLoader(); }
	
	private static String tempDirectory() {
		return AccessController.doPrivileged(new PrivilegedAction<String>() {
			@Override public String run() { return System.getProperty("java.io.tmpdir"); }
		});
	}
	
	public static URL getResource(String name) {
		if(!name.startsWith("bio/gcat/"))
			name = "bio/gcat"+(name.startsWith("/")?EMPTY:"/")+name;
		return localClassLoader.getResource(name);
	}
	public static InputStream getResourceAsStream(String name) {
		if(!name.startsWith("bio/gcat/"))
			name = "bio/gcat"+(name.startsWith("/")?EMPTY:"/")+name;
		return localClassLoader.getResourceAsStream(name);
	}
	
	private static File configurationFile() { return new File(tempDirectory(),CONFIGURATION_NAME); }
	
	private static boolean loadConfiguration() {
		if(configuration!=null) return true;
		try {
			(configuration=new Properties()).load(new FileReader(configurationFile()));
			return true;
		} catch(FileNotFoundException e) {
			configuration = new Properties();
			return true;
		} catch(IOException e) { return false; }
	}
	private static boolean writeConfiguration() {
		if(configuration==null) return true;
		try {
			configuration.store(new FileOutputStream(configurationFile()),EMPTY);
			return true;
		} catch (IOException e) { return false; }
	}	
	public static boolean hasConfiguration(String key) {
		return (loadConfiguration()&&configuration.containsKey(key)); }
	public static String getConfiguration(String key) { return getConfiguration(key,null); }
	public static String getConfiguration(String key,String dfault) {
		if(!loadConfiguration()||!configuration.containsKey(key))
			return dfault;
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
	
	public static String ellipsisText(String text,int maxLength) {
		return text.length()<=maxLength?text:text.substring(0,maxLength-3)+"...";
	}
	
	public static boolean safeSetSystemProperty(String key, String value) {
		try {
			if(!value.equals(System.getProperty(key)))
				System.setProperty(key,value);
			return true;
		} catch(SecurityException e) { return false; }
	}

	public static int indexOf(Object[] array,Object object) {
		for(int index=0;index<array.length;index++)
			if(array[index]==object) return index;
		return -1;
	}
	
	public static <T> T[] add(T[] originalArray, T element) {
		T[] array = Arrays.copyOf(originalArray,originalArray.length+1);
		array[array.length-1] = element;
		return array;
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
  			if(JOptionPane.showOptionDialog(getParent(),String.format("%s already exists.\nDo you want to replace it?",file.getName()),"Confirm Overwrite",JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE,null,null,null)!=JOptionPane.YES_OPTION)
  				return;
  		super.approveSelection();
		}
	}

	public static String[] getFilterExtensions(FileNameExtensionFilter... filters) {
		return filters!=null?Arrays.stream(filters).map(filter->filter.getExtensions()).reduce((extensionsA,extensionsB)->
			Stream.concat(Arrays.stream(extensionsA),Arrays.stream(extensionsB)).toArray(String[]::new)).get():null;
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
	
	public static class ArrayComparator<T extends Comparable<T>> implements Comparator<T[]> {
		@Override public int compare(T[] arrayA, T[] arrayB) {
			if(arrayA==arrayB) return 0; int compare;
			for(int index=0;index<arrayA.length;index++)
				if(index<arrayB.length) {
					if((compare=arrayA[index].compareTo(arrayB[index]))!=0)
						return compare;
				} else return 1; //first array is longer
			if(arrayA.length==arrayB.length)
				   return 0; //arrays are equal
			else return -1; //first array is shorter 
		}
	}
}