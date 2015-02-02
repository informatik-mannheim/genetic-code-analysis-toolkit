package de.hsma.gentool.operation.transformation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import de.hsma.gentool.nucleic.Tuple;
import de.hsma.gentool.operation.Cataloged;
import de.hsma.gentool.operation.Named;

@Named(name="remove unknown compounds") @Cataloged(group="Transformations")
public class RemoveUnknownCompounds implements Transformation {
	@Override public Collection<Tuple> transform(Collection<Tuple> tuples,Object... values) {
		List<Tuple> remove = new ArrayList<>(tuples);
		remove.removeIf(tuple->tuple.getCompound()==null);
		return remove;
	}
}