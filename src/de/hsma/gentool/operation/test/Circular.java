package de.hsma.gentool.operation.test;

import java.util.Collection;
import de.hsma.gentool.Parameter;
import de.hsma.gentool.nucleic.Tuple;
import de.hsma.gentool.operation.Cataloged;
import de.hsma.gentool.operation.Named;

@Named(name="n-circular?") @Cataloged(group="Tests")
public class Circular implements Test {
	private static final Parameter[] PARAMETERS = new Parameter[] {
 		new Parameter("n", "n-Circular", 1, 1, Short.MAX_VALUE, 1),
 	};
	public static Parameter[] getParameters() { return PARAMETERS; }
	
	@Override public boolean test(Collection<Tuple> tuples,Object... values) {
		//Logger logger = getLogger();
		
		int circular = (Integer)values[0];
		if(circular>1) {
			if(!test(tuples,circular-1))
				return false;
		} else return true;
		
		/*int length = tuples.iterator().next().length();
		for(Tuple tuple:tuples)
			if(tuple.length()!=length) {
				logger.log("Tuples of variable length, can't check for circular.");
				return false; //tuples not all of same length
			}
		
		if(!new DuplicateFree().test(tuples)) {
			logger.log("Duplicate tuples in sequence, code not circular.");
			return false; //duplicate tuples
		}
		
		Collection<Tuple> shifted = tuples;
		for(int shift=1;shift<length;shift++)
			if(!Collections.disjoint(tuples,shifted = new ShiftTuples().transform(shifted))) {
				logger.log("Shifted tuple is contained in sequence, code not circular.");
				return false; //contains shifted tuple
			}
		
		Collection<Tuple> rotated = tuples;
		for(int rotate=1;rotate<length;rotate++)
			if(!Collections.disjoint(set,rotated = new RotateTuples().transform(rotated))) {
				logger.log("Rotated tuple is contained in sequence, code not comma-free.");
				return false; //contains rotated tuple
			}*/
		
		return true;
	}
}