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

public class RemoveUnknownCompoundsTest {
	private static final RemoveUnknownCompounds REMOVE_UNKNOWN_COMPOUNDS = new RemoveUnknownCompounds();
	
	@Test public void test() {
		assertTrue("Empty list expeceted", REMOVE_UNKNOWN_COMPOUNDS.transform(Collections.emptyList()).isEmpty());
		assertTrue("Empty list expeceted", REMOVE_UNKNOWN_COMPOUNDS.transform(splitTuples("U, C, A, G")).isEmpty());
		
		assertTuplesContains(splitTuples("UUU"), REMOVE_UNKNOWN_COMPOUNDS.transform(splitTuples("U, U, UU, UU, UUU")));
		assertTuplesContains(splitTuples("AGU, GUG, GTG, GAC"), REMOVE_UNKNOWN_COMPOUNDS.transform(splitTuples("AGU, GUG, GTG, GAC, GTU")));
		assertTuplesContains(splitTuples("AAA"), REMOVE_UNKNOWN_COMPOUNDS.transform(splitTuples("AXU, GXG, AAA, XAC, GTX")));
		
		assertTuplesContains(splitTuples("GCGC, CGGC, UUGG, CUCU"), REMOVE_UNKNOWN_COMPOUNDS.transform(splitTuples("GCGC, CGGC, GUAG, UUGG, CUCU")));
		assertTuplesContains(splitTuples("AAA, AAAA"), REMOVE_UNKNOWN_COMPOUNDS.transform(splitTuples("A, AA, AAA, AAAA, AAAAA")));
	}
}