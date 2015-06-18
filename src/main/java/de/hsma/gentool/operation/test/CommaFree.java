package de.hsma.gentool.operation.test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import de.hsma.gentool.log.Logger;
import de.hsma.gentool.nucleic.Tuple;
import de.hsma.gentool.operation.Cataloged;
import de.hsma.gentool.operation.Named;
import de.hsma.gentool.operation.transformation.ShiftSequence;
import de.hsma.gentool.operation.transformation.Transformation;

@Named(name="comma-free?") @Cataloged(group="Tests")
public class CommaFree implements Test {
	private static final Test
		DUPLICATE_FREE = new DuplicateFree();
	private static final Transformation
		SHIFT = new ShiftSequence();
	
	@Override public boolean test(Collection<Tuple> tuples,Object... values) {
		Logger logger = getLogger();
		
		if(tuples.isEmpty())
			return true; //an empty set of tuples is comma-free
		
		int length;
		if((length=Tuple.tuplesLength(tuples))==0) {
			logger.log("Tuples of variable length, can't check for comma-free.");
			return false; //tuples not all of same length
		}
		
		if(!DUPLICATE_FREE.test(tuples)) {
			logger.log("Duplicate tuples in sequence, code not comma-free.");
			return false; //duplicate tuples
		}
		
		int shift; Collection<Tuple> shifted;
		for(Tuple tupleA:tuples) for(Tuple tupleB:tuples)
			if(tupleA!=tupleB) for(shift=1,shifted=Arrays.asList(tupleA,tupleB);shift<length;shift++)
				if(!Collections.disjoint(tuples,shifted = SHIFT.transform(shifted))) {
					logger.log("Shifted tuple created from "+tupleA+" and "+tupleB+" is contained in sequence, code not comma-free.");
					return false; //contains shifted tuple
				}
		
		return true;
	}
}