package de.hsma.gentool.operation.transformation;

import java.util.Collection;
import java.util.regex.Pattern;
import de.hsma.gentool.Parameter;
import de.hsma.gentool.nucleic.Tuple;
import de.hsma.gentool.operation.Cataloged;
import de.hsma.gentool.operation.Named;

@Named(name="n-shift all tuples") @Cataloged(group="Transformations")
public class ShiftTuples implements Transformation {
	private static final Pattern PATTERN_ROTATE = Pattern.compile("(\\S)(\\S*)");
	private static final String REPLACE_ROTATE = "$2$1";
		
	private static final Parameter[] PARAMETERS = new Parameter[] {
		new Parameter("shift", "Shift", 1, 1, Byte.MAX_VALUE, 1),
	};
	public static Parameter[] getParameters() { return PARAMETERS; }
	
	@Override public Collection<Tuple> transform(Collection<Tuple> tuples,Object... values) { return transform(tuples,(Integer)values[0]); }
	public Collection<Tuple> transform(Collection<Tuple> tuples,int shift) {
		String string = Tuple.joinTuples(tuples);
		while(shift-->0) string = PATTERN_ROTATE.matcher(string).replaceAll(REPLACE_ROTATE);
		return Tuple.splitTuples(string);
	}
}