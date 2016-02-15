package bio.gcat.operation.split;

import static bio.gcat.nucleic.Tuple.splitTuples;
import static bio.gcat.operation.split.helper.SplitAssert.assertSplitContains;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.Test;

public class PartitionTest {
	private static final Partition PARTITION = new Partition();
	
	@Test public void test() {
		assertSplitContains(Arrays.asList(
			splitTuples("AAA")
		), PARTITION.split(new HashSet<>(splitTuples("AAA")), 1));
	
		assertSplitContains(Arrays.asList(
			splitTuples("AAA")
		), PARTITION.split(splitTuples("AAA"), 1));

		assertSplitContains(Arrays.asList(
			splitTuples("AAA")
		), PARTITION.split(splitTuples("AAA"), 2));

		assertSplitContains(Arrays.asList(
			splitTuples("AAA"),
			splitTuples("BBB")
		), PARTITION.split(splitTuples("AAA, BBB"), 2));
		
		assertSplitContains(Arrays.asList(
			splitTuples("AAA"),
			splitTuples("BBB")
		), PARTITION.split(splitTuples("AAA, BBB"), 3));
	
		assertSplitContains(Arrays.asList(
			splitTuples("AAA, BBB"),
			splitTuples("CCC")
		), PARTITION.split(splitTuples("AAA, BBB, CCC"), 2));
		
		assertSplitContains(Arrays.asList(
			splitTuples("AAA, BBB"),
			splitTuples("CCC, DDD")
		), PARTITION.split(splitTuples("AAA, BBB, CCC, DDD"), 2));
		
		assertSplitContains(Arrays.asList(
			splitTuples("AAA, BBB"),
			splitTuples("CCC, DDD"),
			splitTuples("EEE")
		), PARTITION.split(splitTuples("AAA, BBB, CCC, DDD, EEE"), 3));
	}
}
