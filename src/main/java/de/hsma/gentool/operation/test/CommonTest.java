package de.hsma.gentool.operation.test;

import java.util.Collection;
import de.hsma.gentool.nucleic.Tuple;
import de.hsma.gentool.operation.Cataloged;
import de.hsma.gentool.operation.Named;
import de.hsma.gentool.operation.transformation.CommonSubstitution;

@Named(name="common test", icon="book_next") @Cataloged(group="Tests")
public class CommonTest extends CommonSubstitution implements Test {
	@Override public boolean test(Collection<Tuple> tuples,Object... values) { return test(tuples,(String)values[0]); }
	public boolean test(Collection<Tuple> tuples,String name) {
		Collection<Tuple> transformed = transform(tuples,name);
		return tuples.containsAll(transformed)&&transformed.containsAll(tuples);
	}
}