package de.hsma.gentool.operation.test;

import java.util.Collection;
import de.hsma.gentool.log.Logger;
import de.hsma.gentool.nucleic.Tuple;
import de.hsma.gentool.operation.Cataloged;
import de.hsma.gentool.operation.Named;
import de.hsma.gentool.operation.transformation.ShiftTuples;
import de.hsma.gentool.operation.transformation.Transformation;

@Named(name="c3-code?") @Cataloged(group="Tests")
public class C3 implements Test {
	private static final Test
		CIRCULAR = new Circular();
	private static final Transformation
		SHIFT = new ShiftTuples();
	
	@Override public boolean test(Collection<Tuple> tuples,Object... values) {
		Logger logger = getLogger();
		
		if(tuples.isEmpty())
			return true; //an empty set of tuples is comma-free
		
		int length;
		if((length=Tuple.tuplesLength(tuples))==0) {
			logger.log("Tuples of variable length, can't check for c3-code.");
			return false; //tuples not all of same length
		}
		
		Collection<Tuple> shifted = tuples;
		for(int shift=0;shift<length;shift++)
			if(!CIRCULAR.test(shifted=SHIFT.transform(shifted,shift),length+1))
				return false;
		
		return true;
	}
}
