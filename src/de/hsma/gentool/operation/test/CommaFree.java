package de.hsma.gentool.operation.test;

import java.util.Collection;
import java.util.Collections;
import de.hsma.gentool.log.Logger;
import de.hsma.gentool.nucleic.Tuple;
import de.hsma.gentool.operation.Cataloged;
import de.hsma.gentool.operation.Named;
import de.hsma.gentool.operation.transformation.ShiftTuples;
import de.hsma.gentool.operation.transformation.ShiftSequence;

@Named(name="comma-free?") @Cataloged(group="Tests")
public class CommaFree implements Test {
	@Override public boolean test(Collection<Tuple> tuples,Object... values) {
		Logger logger = getLogger();
		
		int length = tuples.iterator().next().length();
		for(Tuple tuple:tuples)
			if(tuple.length()!=length) {
				logger.log("Tuples of variable length, can't check for comma-free.");
				return false; //tuples not all of same length
			}
		
		if(!new DuplicateFree().test(tuples)) {
			logger.log("Duplicate tuples in sequence, code not comma-free.");
			return false; //duplicate tuples
		}
		
		Collection<Tuple> shifted = tuples;
		for(int shift=1;shift<length;shift++)
			if(!Collections.disjoint(tuples,shifted = new ShiftSequence().transform(shifted))) {
				logger.log("Shifted tuple is contained in sequence, code not comma-free.");
				return false; //contains shifted tuple
			}
		
		Collection<Tuple> rotated = tuples;
		for(int rotate=1;rotate<length;rotate++)
			if(!Collections.disjoint(tuples,rotated = new ShiftTuples().transform(rotated))) {
				logger.log("Rotated tuple is contained in sequence, code not comma-free.");
				return false; //contains rotated tuple
			}
		
		return true;
	}
}