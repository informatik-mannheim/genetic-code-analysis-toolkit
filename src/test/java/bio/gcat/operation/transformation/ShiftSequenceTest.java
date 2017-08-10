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

public class ShiftSequenceTest {
	private static final ShiftSequence SHIFT_SEQUENCE = new ShiftSequence();
	
	@Test public void test() {
		assertTrue("Empty list expeceted", SHIFT_SEQUENCE.transform(Collections.emptyList()).isEmpty());
		assertTuplesContains(splitTuples("G"), SHIFT_SEQUENCE.transform(splitTuples("G"), 1));
		assertTuplesContains(splitTuples("A, G, U"), SHIFT_SEQUENCE.transform(splitTuples("U, A, G")));
		assertTuplesContains(splitTuples("C, A, G, U"), SHIFT_SEQUENCE.transform(splitTuples("U, C, A, G")));
		assertTuplesContains(splitTuples("U, U, UU, UU, UUU"), SHIFT_SEQUENCE.transform(splitTuples("U, U, UU, UU, UUU")));
		assertTuplesContains(splitTuples("U, C, CA, AA, AAG"), SHIFT_SEQUENCE.transform(splitTuples("G, U, CC, AA, AAA")));
		assertTuplesContains(splitTuples("GUG, UGG, TGG, ACG, TUA"), SHIFT_SEQUENCE.transform(splitTuples("AGU, GUG, GTG, GAC, GTU")));
	}
}