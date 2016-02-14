package bio.gcat.operation.split;

import static bio.gcat.nucleic.Tuple.splitTuples;
import static bio.gcat.operation.split.SplitAssert.assertSplitContains;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;

public class PickTest {
	private static final Pick PICK = new Pick();
	
	@Test public void test() {
		assertSplitContains(Arrays.asList(
			Collections.emptyList(),
			Collections.emptyList()
		), PICK.split(Collections.emptyList(), ".+", true));
	
		assertSplitContains(Arrays.asList(
			splitTuples("AAA"),
			splitTuples("BBB, CCC")
		), PICK.split(splitTuples("AAA, BBB, CCC"), "AAA", false));
	
		assertSplitContains(Arrays.asList(
			splitTuples("AAA, AAA"),
			splitTuples("BBB")
		), PICK.split(splitTuples("AAA, BBB, AAA"), "AAA", false));
		
		assertSplitContains(Arrays.asList(
			splitTuples("AAA, BBB"),
			splitTuples("CCC")
		), PICK.split(splitTuples("AAA, BBB, CCC"), "AAA|BBB", true));
		
		assertNull("Expected null on wrong pattern", PICK.split(Collections.emptyList(), new Object[] {"{}", Boolean.TRUE}));
	}
}
