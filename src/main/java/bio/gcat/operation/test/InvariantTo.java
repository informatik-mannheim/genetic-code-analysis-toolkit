package bio.gcat.operation.test;

import static bio.gcat.Help.*;
import java.util.Collection;
import bio.gcat.Documented;
import bio.gcat.Parameter;
import bio.gcat.nucleic.Tuple;
import bio.gcat.operation.Cataloged;
import bio.gcat.operation.Named;
import bio.gcat.operation.transformation.CommonSubstitution;
import bio.gcat.operation.transformation.Transformation;

@Named(name="invariant to", icon="book_next") @Cataloged(group="Tests", order=50)
@Documented(title="Invariant To", category={OPERATIONS,TESTS}, resource="help/operation/test/invariant_to.html")
public class InvariantTo implements Test {
	private static final Transformation
		COMMON = new CommonSubstitution();
	
	public static Parameter[] getParameters() { return CommonSubstitution.getParameters(); }
	
	@Override public boolean test(Collection<Tuple> tuples,Object... values) { return test(tuples,(String)values[0]); }
	public boolean test(Collection<Tuple> tuples,String name) {
		Collection<Tuple> transformed = COMMON.transform(tuples,name);
		return tuples.containsAll(transformed)&&transformed.containsAll(tuples);
	}
}