package de.hsma.gentool.operation.test;

import java.util.Collection;
import de.hsma.gentool.log.Logger;
import de.hsma.gentool.nucleic.Acid;
import de.hsma.gentool.nucleic.Tuple;
import de.hsma.gentool.operation.Cataloged;
import de.hsma.gentool.operation.Named;

@Named(name="self-complementary?") @Cataloged(group="Tests")
public class SelfComplementary implements Test {
	@Override public boolean test(Collection<Tuple> tuples,Object... values) {
		Logger logger = getLogger();
		
		if(tuples.isEmpty())
			return true; //an empty set of tuples is self complementary
		
		Acid acid;
		if((acid=Tuple.tuplesAcid(tuples))==null) {
			logger.log("Tuples with variable acids, can't check for self complementary.");
			return false; //tuples not all in same acid
		}
		
		Tuple complement;
		for(Tuple tuple:tuples)
			if(!tuples.contains(complement=tuple.getComplement(acid))) {
				logger.log("Complement tuple "+complement+" is not contained in sequence, code not self complementary.");
				return false; //contains a complement, not self complementary
			}
		
		return true;
	}
}