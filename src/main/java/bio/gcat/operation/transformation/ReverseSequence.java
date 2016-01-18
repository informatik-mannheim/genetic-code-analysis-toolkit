package bio.gcat.operation.transformation;

import static bio.gcat.Help.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import bio.gcat.Documented;
import bio.gcat.nucleic.Tuple;
import bio.gcat.operation.Cataloged;
import bio.gcat.operation.Named;

@Named(name="reverse sequence", icon="arrow_rotate_clockwise") @Cataloged(group="Permute Tuple Position")
@Documented(title="Reverse Sequence", category={OPERATIONS,TRANSFORMATIONS}, resource="help/operation/transformation/reverse_sequence.html")
public class ReverseSequence implements Transformation {
	@Override public Collection<Tuple> transform(Collection<Tuple> tuples,Object... values) {
		List<Tuple> reverse = new ArrayList<>(tuples);
		Collections.reverse(reverse);
		return reverse;
	}
}