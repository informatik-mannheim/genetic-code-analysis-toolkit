package bio.gcat.operation.analysis;

import static bio.gcat.nucleic.Tuple.splitTuples;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.apache.tools.ant.filters.StringInputStream;
import org.junit.Test;

public class TupleUsageTest {
	private static final TupleUsage TUPLE_USAGE = new TupleUsage();
	
	@Test public void test() throws Exception {
		String result = TUPLE_USAGE.analyse(splitTuples("AAA UUU"), new StringInputStream("AAA UUU GGG CCC AAA UUU")).toString();
		assertThat(result, both(containsString("2x AAA")).and(containsString("2x UUU")));
		assertTrue(result.replace("2x AAA","").replace("2x UUU","").replaceAll("\\W","").isEmpty());
	}
}
