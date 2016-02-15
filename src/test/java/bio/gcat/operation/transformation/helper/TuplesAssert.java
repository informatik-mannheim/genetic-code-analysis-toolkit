package bio.gcat.operation.transformation.helper;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;

import java.util.Collection;

import bio.gcat.nucleic.Tuple;

public class TuplesAssert {
	public static void assertTuplesContains(Collection<Tuple> expecteds, Collection<Tuple> actuals) {
		assertThat(actuals, contains(expecteds.toArray(new Tuple[0]))); }
	public static void assertTuplesContainsInAnyOrder(Collection<Tuple> expecteds, Collection<Tuple> actuals) {
		assertThat(actuals, containsInAnyOrder(expecteds.toArray(new Tuple[0]))); }
}