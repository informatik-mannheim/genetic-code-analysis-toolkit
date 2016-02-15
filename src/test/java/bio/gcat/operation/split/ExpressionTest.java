package bio.gcat.operation.split;

import static bio.gcat.nucleic.Tuple.splitTuples;
import static bio.gcat.operation.split.helper.SplitAssert.assertSplitContains;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;

import bio.gcat.operation.split.Split.Expression;

public class ExpressionTest {
	private static final Expression EXPRESSION = new Expression();
	
	@Test public void test() {
		assertSplitContains(Arrays.asList(
			Collections.emptyList()
		), EXPRESSION.split(Collections.emptyList(), ".+", true, false));
	
		assertSplitContains(Arrays.asList(
			Collections.emptyList()
		), EXPRESSION.split(Collections.emptyList(), ".+", true, true));
		
		assertSplitContains(Arrays.asList(
			splitTuples("AAA"),
			splitTuples("BBB, CCC")
		), EXPRESSION.split(splitTuples("AAA, BBB, CCC"), "AAA", false, false));
	
		assertSplitContains(Arrays.asList(
			Collections.emptyList(),
			splitTuples("BBB, CCC")
		), EXPRESSION.split(splitTuples("AAA, BBB, CCC"), "AAA", false, true));
		
		assertSplitContains(Arrays.asList(
			splitTuples("AAA"),
			splitTuples("BBB, AAA")
		), EXPRESSION.split(splitTuples("AAA, BBB, AAA"), "AAA", false, false));

		assertSplitContains(Arrays.asList(
			splitTuples("AAA"),
			splitTuples("AAA")
		), EXPRESSION.split(splitTuples("AAA, BBB, AAA"), "BBB", false, true));
		
		assertSplitContains(Arrays.asList(
			splitTuples("AAA"),
			splitTuples("BBB"),
			splitTuples("CCC")
		), EXPRESSION.split(splitTuples("AAA, BBB, CCC"), "AAA|BBB", true, false));

		assertSplitContains(Arrays.asList(
			Collections.emptyList(),
			splitTuples("BBB")
		), EXPRESSION.split(splitTuples("AAA, BBB, CCC"), "AAA|CCC", true, true));
		
		assertNull("Expected null on wrong pattern", EXPRESSION.split(Collections.emptyList(), new Object[] {"{}", Boolean.TRUE, Boolean.FALSE}));
	}
}
