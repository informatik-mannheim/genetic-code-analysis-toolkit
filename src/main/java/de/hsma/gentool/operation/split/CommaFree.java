package de.hsma.gentool.operation.split;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.paukov.combinatorics.Factory;
import org.paukov.combinatorics.ICombinatoricsVector;
import org.paukov.combinatorics.util.ComplexCombinationGenerator;
import de.hsma.gentool.Parameter;
import de.hsma.gentool.Parameter.Type;
import de.hsma.gentool.nucleic.Tuple;
import de.hsma.gentool.operation.Cataloged;
import de.hsma.gentool.operation.Named;
import de.hsma.gentool.operation.test.Test;

@Named(name="comma-free", icon="comma_free") @Cataloged(group="Splits")
@Parameter.Annotation(key="parts",label="Parts",type=Type.NUMBER,value="2,32767")
public class CommaFree implements Split {
	private static final Test
		COMMA_FREE = new de.hsma.gentool.operation.test.CommaFree();
	
	@Override public List<Collection<Tuple>> split(Collection<Tuple> tuples,Object... values) { return split(tuples,(int)values[0]); }
	public List<Collection<Tuple>> split(Collection<Tuple> tuples,int parts) {
		for(ICombinatoricsVector<ICombinatoricsVector<Tuple>> combination:new ComplexCombinationGenerator<Tuple>(Factory.createVector(tuples), parts)) {
			boolean commaFree = true;
			for(ICombinatoricsVector<Tuple> partition:combination)
				if(!(commaFree=COMMA_FREE.test(partition.getVector())))
						break;
			if(commaFree)
				return combination.getVector().stream().map(partition->partition.getVector()).collect(Collectors.toList());
		} return null;
	}
}
