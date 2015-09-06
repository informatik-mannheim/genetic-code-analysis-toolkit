package de.hsma.gentool.operation.split;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.paukov.combinatorics.Factory;
import org.paukov.combinatorics.ICombinatoricsVector;
import de.hsma.gentool.Parameter;
import de.hsma.gentool.Parameter.Type;
import de.hsma.gentool.nucleic.Tuple;
import de.hsma.gentool.operation.Cataloged;
import de.hsma.gentool.operation.Named;
import de.hsma.gentool.operation.test.Test;

@Named(name="comma-free", icon="comma_free") @Cataloged(group="Splits")
@Parameter.Annotation(key="parts",label="Parts",type=Type.NUMBER,value="2,32767")
@Parameter.Annotation(key="equal-sized",label="Equal Sized",type=Type.BOOLEAN)
public class CommaFree implements Split {
	private static final Test
		COMMA_FREE = new de.hsma.gentool.operation.test.CommaFree();
	
	@Override public List<Collection<Tuple>> split(Collection<Tuple> tuples,Object... values) { return split(tuples,(int)values[0], (boolean)values[1]); }
	public List<Collection<Tuple>> split(Collection<Tuple> tuples,int parts,boolean equalSized) {
		if(parts==1) return COMMA_FREE.test(tuples)?new ArrayList<Collection<Tuple>>() {
			private static final long serialVersionUID = 1l; { add(tuples); }}:null;

		if(equalSized&&tuples.size()%parts!=0)
			return null;
		
		for(ICombinatoricsVector<Tuple> subset:Factory.createSubSetGenerator(Factory.createVector(tuples))) {
			if(subset.getSize()==0||(equalSized&&tuples.size()/parts!=subset.getSize())) continue;
			List<Tuple> vector = subset.getVector();
			if(COMMA_FREE.test(vector)) {
				List<Collection<Tuple>> split = split(new ArrayList<Tuple>(tuples) { private static final long serialVersionUID = 1l;
					{ removeAll(vector); }}, parts-1, equalSized);
				if(split!=null) {
					split.add(0,vector);
					return split;
				}
			}
		}
		return null;
		
		/*for(ICombinatoricsVector<ICombinatoricsVector<Tuple>> combination:new ComplexCombinationGenerator<Tuple>(Factory.createVector(tuples), parts)) {
			boolean commaFree = true;
			for(ICombinatoricsVector<Tuple> partition:combination)
				if(!(commaFree=COMMA_FREE.test(partition.getVector())))
						break;
			if(commaFree)
				return combination.getVector().stream().map(partition->partition.getVector()).collect(Collectors.toList());
		} return null;*/
	}
	
	
}
