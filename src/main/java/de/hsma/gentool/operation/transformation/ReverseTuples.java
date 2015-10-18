package de.hsma.gentool.operation.transformation;

import static de.hsma.gentool.Help.*;
import static de.hsma.gentool.Utilities.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import de.hsma.gentool.Documented;
import de.hsma.gentool.nucleic.Tuple;
import de.hsma.gentool.operation.Cataloged;
import de.hsma.gentool.operation.Named;

@Named(name="reverse tuples", icon="text_letterspacing") @Cataloged(group="Transformations")
@Documented(title="Reverse Tuples", category={OPERATIONS,TRANSFORMATIONS}, resource="help/operation/transformation/reverse_tuples.html")
public class ReverseTuples implements Transformation {
	@Override public Collection<Tuple> transform(Collection<Tuple> tuples,Object... values) {
		List<Tuple> reverse = new ArrayList<>(tuples);
		reverse.replaceAll(tuple->new Tuple(reverse(tuple.getBases())));
		return reverse;
	}
}