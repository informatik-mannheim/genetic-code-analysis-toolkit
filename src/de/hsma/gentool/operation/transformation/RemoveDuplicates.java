package de.hsma.gentool.operation.transformation;

import java.util.Collection;
import java.util.LinkedHashSet;
import de.hsma.gentool.nucleic.Tuple;
import de.hsma.gentool.operation.Cataloged;
import de.hsma.gentool.operation.Named;

@Named(name="remove duplicates") @Cataloged(group="Transformations")
public class RemoveDuplicates implements Transformation { 	
	@Override public Collection<Tuple> transform(Collection<Tuple> tuples,Object... values) {
		return new LinkedHashSet<>(tuples); //removing duplicates by adding it to a linked (preserving order) hash (removing duplicates) set
	}
}