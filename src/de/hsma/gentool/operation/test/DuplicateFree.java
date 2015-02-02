package de.hsma.gentool.operation.test;

import java.util.Collection;
import java.util.HashSet;
import de.hsma.gentool.nucleic.Tuple;
import de.hsma.gentool.operation.Cataloged;
import de.hsma.gentool.operation.Named;

@Named(name="duplicate free?") @Cataloged(group="Tests")
public class DuplicateFree implements Test {
	@Override public boolean test(Collection<Tuple> tuples,Object... values) {
		return tuples.size()==new HashSet<>(tuples).size();
	}
}