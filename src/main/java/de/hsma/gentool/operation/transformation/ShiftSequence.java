package de.hsma.gentool.operation.transformation;

import java.util.Collection;
import java.util.regex.Pattern;
import de.hsma.gentool.Parameter;
import de.hsma.gentool.nucleic.Tuple;
import de.hsma.gentool.operation.Cataloged;
import de.hsma.gentool.operation.Named;

@Named(name="n-shift sequence") @Cataloged(group="Transformations")
public class ShiftSequence implements Transformation {
	private static final Pattern
		PATTERN_SHIFT_A = Pattern.compile("(\\S)\\s+(\\S)"),
		PATTERN_SHIFT_B = Pattern.compile("^\\s*(\\S)(.*?)\\s*$");
	private static final String
		REPLACE_SHIFT_A = "$1$2 ",
		REPLACE_SHIFT_B = "$2$1";
		
	private static final Parameter[] PARAMETERS = new Parameter[] {
 		new Parameter("shift", "Shift", 1, 1, Byte.MAX_VALUE, 1),
 	};
 	public static Parameter[] getParameters() { return PARAMETERS; }
 	
	@Override public Collection<Tuple> transform(Collection<Tuple> tuples,Object... values) { return transform(tuples,(Integer)values[0]); }
	public Collection<Tuple> transform(Collection<Tuple> tuples,int shift) {
		String string = Tuple.joinTuples(tuples);
		while(shift-->0) string = PATTERN_SHIFT_B.matcher(PATTERN_SHIFT_A.matcher(string).replaceAll(REPLACE_SHIFT_A)).replaceAll(REPLACE_SHIFT_B);
		return Tuple.splitTuples(string);
	}
}