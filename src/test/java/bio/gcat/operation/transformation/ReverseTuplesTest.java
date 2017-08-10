/*
 * Copyright [2016] [Mannheim University of Applied Sciences]
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package bio.gcat.operation.transformation;

import static bio.gcat.nucleic.Tuple.splitTuples;
import static bio.gcat.operation.transformation.helper.TuplesAssert.assertTuplesContains;
import static org.junit.Assert.assertTrue;

import java.util.Collections;

import org.junit.Test;

public class ReverseTuplesTest {
	private static final ReverseTuples REVERSE_TUPLES = new ReverseTuples();
	
	@Test public void test() {
		assertTrue("Empty list expeceted", REVERSE_TUPLES.transform(Collections.emptyList()).isEmpty());
		assertTuplesContains(splitTuples("G, U, A, C"), REVERSE_TUPLES.transform(splitTuples("G, U, A, C")));
		assertTuplesContains(splitTuples("GG, UU, AA, CC"), REVERSE_TUPLES.transform(splitTuples("GG, UU, AA, CC")));
		assertTuplesContains(splitTuples("UGA, GUG, GTG, CAG, UTG"), REVERSE_TUPLES.transform(splitTuples("AGU, GUG, GTG, GAC, GTU")));
		assertTuplesContains(splitTuples("A, AG, GUA, AUCG, CCUGA"), REVERSE_TUPLES.transform(splitTuples("A, GA, AUG, GCUA, AGUCC")));
	}
}