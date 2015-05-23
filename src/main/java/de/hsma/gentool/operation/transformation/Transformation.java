package de.hsma.gentool.operation.transformation;

import java.util.Collection;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import de.hsma.gentool.Parameter;
import de.hsma.gentool.Parameter.Type;
import de.hsma.gentool.Utilities;
import de.hsma.gentool.nucleic.Tuple;
import de.hsma.gentool.operation.Named;
import de.hsma.gentool.operation.Operation;

public interface Transformation extends Operation {
	public default Collection<Tuple> transform(Collection<Tuple> tuples) { return transform(tuples, Parameter.getValues(Operation.getParameters(this.getClass())));  }
	public Collection<Tuple> transform(Collection<Tuple> tuples, Object... values);
	
	@Named(name="find & replace")
	@Parameter.Annotation(key="pattern",label="Term",type=Type.TEXT)
	@Parameter.Annotation(key="replace",label="Replace",type=Type.TEXT)
	@Parameter.Annotation(key="regex",label="Regex",type=Type.BOOLEAN,value=Utilities.TRUE)
	
	public class Expression implements Transformation {  	
  	@Override public Collection<Tuple> transform(Collection<Tuple> tuples,Object... values) { return transform(tuples, (String)values[0], (String)values[1], (Boolean)values[2]); }
  	public Collection<Tuple> transform(Collection<Tuple> tuples,String pattern,String replace,boolean regex) {
			try {
				return Tuple.splitTuples(Pattern.compile(regex?pattern:Pattern.quote(pattern),Pattern.CASE_INSENSITIVE).matcher(Tuple.joinTuples(tuples)).replaceAll(replace));
			}	catch(PatternSyntaxException e) { return tuples; }
		}
	}
}