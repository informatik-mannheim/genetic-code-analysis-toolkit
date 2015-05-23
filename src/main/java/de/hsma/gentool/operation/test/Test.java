package de.hsma.gentool.operation.test;

import java.util.Collection;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import de.hsma.gentool.Parameter;
import de.hsma.gentool.Utilities;
import de.hsma.gentool.Parameter.Type;
import de.hsma.gentool.nucleic.Tuple;
import de.hsma.gentool.operation.Named;
import de.hsma.gentool.operation.Operation;

public interface Test extends Operation {
	public boolean test(Collection<Tuple> tuples, Object... values);
	
	public static class Failed extends Exception {
		private static final long serialVersionUID = 1l;
		
	  public Failed(Test test) { super(test,"Test Failed"); }
	  
	  public Test getTest() { return (Test)getOperation(); }
	}
	
	@Named(name="match")
	@Parameter.Annotation(key="pattern",label="Term",type=Type.TEXT)
	@Parameter.Annotation(key="regex",label="Regex",type=Type.BOOLEAN,value=Utilities.TRUE)
	public class Expression implements Test {
   	@Override public boolean test(Collection<Tuple> tuples,Object... values) { return test(tuples, (String)values[0], (Boolean)values[1]); }
  	public boolean test(Collection<Tuple> tuples,String pattern,boolean regex) {
  		try {
  			return Pattern.compile(regex?pattern:Pattern.quote(pattern),Pattern.CASE_INSENSITIVE).matcher(Tuple.joinTuples(tuples)).matches();
			}	catch(PatternSyntaxException e) { return false; }
		}
	}
}