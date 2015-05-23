package de.hsma.gentool.operation.split;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import de.hsma.gentool.Parameter;
import de.hsma.gentool.Parameter.Type;
import de.hsma.gentool.Utilities;
import de.hsma.gentool.nucleic.Tuple;
import de.hsma.gentool.operation.Cataloged;
import de.hsma.gentool.operation.Named;

@Named(name="pick") @Cataloged(group="Splits")
@Parameter.Annotation(key="pattern",label="Split",type=Type.TEXT)
@Parameter.Annotation(key="regex",label="Regex",type=Type.BOOLEAN,value=Utilities.TRUE)
public class Pick implements Split {	
 	@Override public List<Collection<Tuple>> split(Collection<Tuple> tuples,Object... values) { return split(tuples, (String)values[0], (Boolean)values[1]); }
	public List<Collection<Tuple>> split(Collection<Tuple> tuples,String pattern,boolean regex) {
		try {
			String string = Tuple.joinTuples(tuples);
			Matcher matcher = Pattern.compile(regex?pattern:Pattern.quote(pattern),Pattern.CASE_INSENSITIVE).matcher(string);
			Collection<Tuple> set = new ArrayList<>(), complement = new ArrayList<>(); int lastEnd = 0;
			while(matcher.find()) {
				set.addAll(Tuple.splitTuples(matcher.group()));
				complement.addAll(Tuple.splitTuples(string.substring(lastEnd,matcher.start())));
				lastEnd = matcher.end();
			}
			complement.addAll(Tuple.splitTuples(string.substring(lastEnd)));
			return Split.asList(set,complement);
		}	catch(PatternSyntaxException e) { return null; }
		/*Pattern patternObject = Pattern.compile(regex?pattern:Pattern.quote(pattern),Pattern.CASE_INSENSITIVE);
		return tuples.stream().collect(Collectors.partitioningBy(tuple->patternObject.matcher(tuple.toString()).matches())).values();*/
	}
}