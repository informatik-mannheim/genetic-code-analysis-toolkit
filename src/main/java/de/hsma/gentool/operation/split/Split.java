package de.hsma.gentool.operation.split;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import de.hsma.gentool.Parameter;
import de.hsma.gentool.Parameter.Type;
import de.hsma.gentool.Utilities;
import de.hsma.gentool.nucleic.Tuple;
import de.hsma.gentool.operation.Named;
import de.hsma.gentool.operation.Operation;

public interface Split extends Operation {
	public List<Collection<Tuple>> split(Collection<Tuple> tuples, Object... values);
	
	public interface Pick {
		public Collection<Tuple> pick(List<Collection<Tuple>> split);
	}
	
	@SafeVarargs public static List<Collection<Tuple>> asList(Collection<Tuple>... tuples) { return Arrays.asList(tuples); }
	
	@Named(name="split")
	@Parameter.Annotation(key="pattern",label="Split",type=Type.TEXT)
	@Parameter.Annotation(key="regex",label="Regex",type=Type.BOOLEAN,value=Utilities.TRUE)
	@Parameter.Annotation(key="delimiter",label="Remove Delimiter",type=Type.BOOLEAN,value=Utilities.FALSE)
	public class Expression implements Split {   	
   	@Override public List<Collection<Tuple>> split(Collection<Tuple> tuples,Object... values) { return split(tuples, (String)values[0], (Boolean)values[1], (Boolean)values[2]); }
  	public List<Collection<Tuple>> split(Collection<Tuple> tuples,String pattern,boolean regex,boolean delimiter) {
  		try {
  			String[] strings = Pattern.compile(String.format(delimiter?"%s":"(?<=%s)",regex?pattern:Pattern.quote(pattern)),Pattern.CASE_INSENSITIVE).split(Tuple.joinTuples(tuples));
  			List<Collection<Tuple>> split = new ArrayList<>(strings.length);
  			for(String string:strings) split.add(Tuple.splitTuples(string));
  			return split;
			}	catch(PatternSyntaxException e) { return null; }
		}
	}
}