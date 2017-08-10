package bio.gcat.operation.split.helper;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

import bio.gcat.nucleic.Tuple;
import static org.hamcrest.Matchers.*;

public class SplitAssert {
	public static void assertSplitContains(List<Collection<Tuple>> expecteds, List<Collection<Tuple>> actuals) {
		List<Collection<Tuple>> remainingExpecteds = new ArrayList<>(expecteds);
		for(Collection<Tuple> actualTuples:actuals) {
			boolean found = false;
			ListIterator<Collection<Tuple>> remainingIterator = remainingExpecteds.listIterator();
			while(remainingIterator.hasNext()) {
				Collection<Tuple> remainingTuples=remainingIterator.next();
				if((actualTuples.isEmpty()&&remainingTuples.isEmpty())||containsInAnyOrder(actualTuples.toArray()).matches(remainingTuples)) {
					remainingIterator.remove();
					found = true; break;
				}
			}
			if(!found) fail(actualTuples+" not expected in split");
		}
		if(!remainingExpecteds.isEmpty())
			fail(remainingExpecteds.get(0)+" expected in split");
	}
}
