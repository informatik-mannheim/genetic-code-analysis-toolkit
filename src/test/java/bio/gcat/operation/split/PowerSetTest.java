package bio.gcat.operation.split;

import static bio.gcat.nucleic.Tuple.splitTuples;
import static bio.gcat.operation.split.SplitAssert.assertSplitContains;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;

public class PowerSetTest {
	private static final PowerSet POWER_SET = new PowerSet();
	
	@Test public void test() {
		assertSplitContains(Arrays.asList(
			Collections.emptyList()
		), POWER_SET.split(Collections.emptyList()));
		
		assertSplitContains(Arrays.asList(
			Collections.emptyList(),
			splitTuples("AAA")
		), POWER_SET.split(splitTuples("AAA")));
	
		assertSplitContains(Arrays.asList(
			Collections.emptyList(),
			splitTuples("AAA"),
			splitTuples("BBB"),
			splitTuples("AAA, BBB")
		), POWER_SET.split(splitTuples("AAA, BBB")));
		
		assertSplitContains(Arrays.asList(
			Collections.emptyList(),
			splitTuples("AAA"),
			splitTuples("BBB"),
			splitTuples("CCC"),
			splitTuples("AAA, BBB"),
			splitTuples("AAA, CCC"),
			splitTuples("BBB, CCC"),
			splitTuples("AAA, BBB, CCC")
		), POWER_SET.split(splitTuples("AAA, BBB, CCC")));
	}
}
