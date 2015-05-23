package de.hsma.gentool.operation.transformation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import de.hsma.gentool.Parameter;
import de.hsma.gentool.Parameter.Type;
import de.hsma.gentool.nucleic.Tuple;
import de.hsma.gentool.operation.Cataloged;
import de.hsma.gentool.operation.Named;

@Named(name="add all (missing) tuples") @Cataloged(group="Transformations")
@Parameter.Annotation(key="length",label="Length",type=Type.NUMBER,value="1,3,10")
public class AllTuples implements Transformation {	
	@Override public Collection<Tuple> transform(Collection<Tuple> tuples,Object... values) { return transform(tuples,(Integer)values[0]); }
	public Collection<Tuple> transform(Collection<Tuple> tuples,int length) {
		List<Tuple> newTuples = new ArrayList<>(tuples);
		for(Tuple tuple:Tuple.allTuples(length))
			if(!tuples.contains(tuple)) newTuples.add(tuple);
		return newTuples;
	}
}