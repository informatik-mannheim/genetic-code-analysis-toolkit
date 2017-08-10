package bio.gcat.operation.split;

import static bio.gcat.nucleic.Tuple.splitTuples;
import static bio.gcat.operation.split.helper.SplitAssert.assertSplitContains;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;

public class CommaFreeTest {
	private static final CommaFree COMMA_FREE = new CommaFree();
	
	@Test public void test() {
		assertNull("An empty list can't be splitted into comma free parts", COMMA_FREE.split(Collections.emptyList(), new Object[]{2, false}));

		assertSplitContains(Arrays.asList(
			splitTuples("AAA")
		), COMMA_FREE.split(splitTuples("AAA"), 1, false));

		assertNull("Can't be splitted into one comma free parts",
			COMMA_FREE.split(splitTuples("AAA, AAA"), 1, false));

		assertNull("Can't be splitted into one comma free parts",
			COMMA_FREE.split(splitTuples("AAA, AAAA"), 1, false));

		assertNull("With duplicates, can't be splitted into comma free parts",
			COMMA_FREE.split(splitTuples("AAA, AAA"), 2, false));

		assertSplitContains(Arrays.asList(
			splitTuples("AAA"),
			splitTuples("AAAA")
		), COMMA_FREE.split(splitTuples("AAA, AAAA"), 2, false));
	
		assertSplitContains(Arrays.asList(
			splitTuples("GGC"),
			splitTuples("GCC")
		), COMMA_FREE.split(splitTuples("GGC, GCC"), 2, false));
		
		assertSplitContains(Arrays.asList(
			splitTuples("ATC"),
			splitTuples("CAA, TCC")
		), COMMA_FREE.split(splitTuples("ATC, TCC, CAA"), 2, false));
		
		assertNull("Can't be splitted into equal sized comma free parts",
			COMMA_FREE.split(splitTuples("ATC, TCC, CAA"), 2, true));
		assertSplitContains(Arrays.asList(
			splitTuples("AAA, GGG"),
			splitTuples("AAT, GGT")
		), COMMA_FREE.split(splitTuples("AAA, AAT, GGG, GGT"), 2, true));
	}
}
