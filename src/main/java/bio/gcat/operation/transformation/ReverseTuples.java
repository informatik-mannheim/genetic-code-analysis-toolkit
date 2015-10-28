package bio.gcat.operation.transformation;

import static bio.gcat.Help.*;
import static bio.gcat.Utilities.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import bio.gcat.Documented;
import bio.gcat.nucleic.Tuple;
import bio.gcat.operation.Cataloged;
import bio.gcat.operation.Named;

@Named(name="reverse tuples", icon="text_letterspacing") @Cataloged(group="Transformations")
@Documented(title="Reverse Tuples", category={OPERATIONS,TRANSFORMATIONS}, resource="help/operation/transformation/reverse_tuples.html")
public class ReverseTuples implements Transformation {
	@Override public Collection<Tuple> transform(Collection<Tuple> tuples,Object... values) {
		List<Tuple> reverse = new ArrayList<>(tuples);
		reverse.replaceAll(tuple->new Tuple(reverse(tuple.getBases())));
		return reverse;
	}
}