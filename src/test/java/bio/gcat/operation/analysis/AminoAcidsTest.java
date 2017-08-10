package bio.gcat.operation.analysis;

import static bio.gcat.nucleic.Tuple.splitTuples;
import static org.junit.Assert.assertThat;

import static org.hamcrest.Matchers.*;
import org.junit.Test;

public class AminoAcidsTest {
	private static final AminoAcids AMINO_ACIDS = new AminoAcids();
	
	@Test public void test() {
		String result = AMINO_ACIDS.analyse(splitTuples("CCU CUG CUA CUC UGA")).toString();
		assertThat(result, containsString("3x Leu"));
		assertThat(result, containsString("1x Pro"));
		assertThat(result, containsString("1x Stop"));
		
		result = AMINO_ACIDS.analyse(splitTuples("GCGC UCAG UCGA AGAC ")).toString();
		assertThat(result, containsString("3x Tsr"));
		assertThat(result, containsString("1x Unknown"));
	}
}
