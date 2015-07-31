package de.hsma.gentool.operation.transformation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import de.hsma.gentool.nucleic.Tuple;
import de.hsma.gentool.operation.Cataloged;
import de.hsma.gentool.operation.Named;

@Named(name="reverse sequence", icon="arrow_rotate_clockwise") @Cataloged(group="Transformations")
public class ReverseSequence implements Transformation {
	@Override public Collection<Tuple> transform(Collection<Tuple> tuples,Object... values) {
		List<Tuple> reverse = new ArrayList<>(tuples);
		Collections.reverse(reverse);
		return reverse;
	}
}