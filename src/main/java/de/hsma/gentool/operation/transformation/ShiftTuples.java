package de.hsma.gentool.operation.transformation;

import java.util.Collection;
import java.util.regex.Pattern;
import de.hsma.gentool.Parameter;
import de.hsma.gentool.Parameter.Type;
import de.hsma.gentool.nucleic.Tuple;
import de.hsma.gentool.operation.Cataloged;
import de.hsma.gentool.operation.Named;

@Named(name="n-shift all tuples") @Cataloged(group="Transformations")
@Parameter.Annotation(key="shift",label="Shift",type=Type.NUMBER,value="1,10")
public class ShiftTuples implements Transformation {
	private static final Pattern PATTERN_ROTATE = Pattern.compile("(\\S)(\\S*)");
	private static final String REPLACE_ROTATE = "$2$1";
	
	@Override public Collection<Tuple> transform(Collection<Tuple> tuples,Object... values) { return transform(tuples,(Integer)values[0]); }
	public Collection<Tuple> transform(Collection<Tuple> tuples,int shift) {
		String string = Tuple.joinTuples(tuples);
		while(shift-->0) string = PATTERN_ROTATE.matcher(string).replaceAll(REPLACE_ROTATE);
		return Tuple.splitTuples(string);
	}
}