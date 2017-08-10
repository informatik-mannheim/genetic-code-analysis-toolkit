package bio.gcat.operation.analysis;

import static bio.gcat.nucleic.Tuple.splitTuples;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.*;

import org.junit.Test;

public class TupleCountTest {
	private static final TupleCount TUPLE_COUNT = new TupleCount();
	
	@Test public void test() {
		String result = TUPLE_COUNT.analyse(splitTuples("CCU CUG CCU CUC CUG AGAC")).toString();
		assertThat(result, containsString("CCU: 2"));
		assertThat(result, containsString("CUG: 2"));
		assertThat(result, containsString("CUC: 1"));
		assertThat(result, containsString("AGAC: 1"));
	}
}
