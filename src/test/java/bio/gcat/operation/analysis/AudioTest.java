package bio.gcat.operation.analysis;

import static bio.gcat.nucleic.Tuple.splitTuples;
import static org.junit.Assert.*;

import org.junit.Test;

public class AudioTest {
	private static final Audio AUDIO = new Audio();
	
	@Test public void test() {
		assertNotNull(AUDIO.analyse(splitTuples("CCU CUG CUA CUC UGA")));
	}
}
