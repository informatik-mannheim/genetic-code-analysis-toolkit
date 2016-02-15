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

public class ReverseSequenceTest {
	private static final ReverseSequence REVERSE_SEQUENCE = new ReverseSequence();
	
	@Test public void test() {
		assertTrue("Empty list expeceted", REVERSE_SEQUENCE.transform(Collections.emptyList()).isEmpty());
		assertTuplesContains(splitTuples("G"), REVERSE_SEQUENCE.transform(splitTuples("G")));
		assertTuplesContains(splitTuples("G, A, U"), REVERSE_SEQUENCE.transform(splitTuples("U, A, G")));
		assertTuplesContains(splitTuples("G, A, C, U"), REVERSE_SEQUENCE.transform(splitTuples("U, C, A, G")));
		assertTuplesContains(splitTuples("UUU, UU, UU, U, U"), REVERSE_SEQUENCE.transform(splitTuples("U, U, UU, UU, UUU")));
		assertTuplesContains(splitTuples("GTU, GAC, GTG, GUG, AGU"), REVERSE_SEQUENCE.transform(splitTuples("AGU, GUG, GTG, GAC, GTU")));
	}
}