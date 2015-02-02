package de.hsma.gentool;

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
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JFileChooser;
import javax.swing.text.Position;
import com.google.common.util.concurrent.ListenableFuture;
import de.hsma.gentool.gui.GenTool;

public final class Utilities {
	private static Properties configuration = new Properties();
	public static final String EMPTY = "", SPACE = " ", NEW_LINE = "\n";

  private static final int BUFFER_SIZE = 8192;
  private static final int TEMP_DIRECTORY_ATTEMPTS = 10000;

  public static URL getResource(String name) {
  	if(!name.startsWith("/"))
  		name = "/resources/de/hsma/gentool/"+name;
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
			super.approveSelection();
			File selected = getSelectedFile();
			if(!selected.isDirectory())
				setConfiguration("directory",selected.getParentFile().toString());
		}
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

	public static enum Characters {
		SPACE(' ',"[ \t\\f]"),
		NEW_LINE('\n',"\r\n|\n\r|[\n\r]","\n?\r?"),
		WHITESPACE(' ',"\r\n|\n\r| \t\n\r\f");
		
		public final char character;
		private final String string;
		private Pattern match,start,end,last,split;
		
		private Characters(char character,String pattern) {
			string = String.valueOf(this.character = character);
			match = Pattern.compile(pattern);
			start = Pattern.compile("\\A"+pattern);
			end = Pattern.compile(pattern+"\\z");
			last = Pattern.compile(".*("+pattern+")",Pattern.DOTALL);
			split = match;
		}
		private Characters(char character,String pattern,String split) {
			this(character,pattern); this.split = Pattern.compile(split);
		}
		
		public boolean equals(String input) { return match.matcher(input).matches(); }
		public boolean contains(String input) { return match.matcher(input).find(); }
		public boolean startsWith(String input) { return start.matcher(input).find(); }
		public boolean endsWith(String input) { return end.matcher(input).find(); }
		public int indexOf(String input) { Matcher matcher = match.matcher(input); return matcher.find()?matcher.start():-1; }
		public int lastIndexOf(String input) { Matcher matcher = last.matcher(input); return matcher.find()?matcher.start(1):-1; }
		public String replace(String input,String replacement) { return match.matcher(input).replaceAll(replacement); }
		public String[] split(String input) { return split.split(input); }
		
		@Override public String toString() { return string; }
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