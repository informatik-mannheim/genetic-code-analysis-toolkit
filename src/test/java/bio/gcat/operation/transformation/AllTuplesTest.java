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
import static bio.gcat.operation.transformation.helper.TuplesAssert.*;
import static org.junit.Assert.assertTrue;

import java.util.Collections;

import org.junit.Test;

public class AllTuplesTest {
	private static final AllTuples ALL_TUPLES = new AllTuples();
	
	@Test public void test() {
		assertTrue("Empty list expeceted", ALL_TUPLES.transform(Collections.emptyList(), 0).isEmpty());
		assertTuplesContainsInAnyOrder(splitTuples("AAA, BBB"), ALL_TUPLES.transform(splitTuples("AAA, BBB"), 0));
		
		assertTuplesContainsInAnyOrder(splitTuples("U, C, A, G"), ALL_TUPLES.transform(Collections.emptyList(), 1));
		assertTuplesContainsInAnyOrder(splitTuples("AAA, BBB, U, C, A, G"), ALL_TUPLES.transform(splitTuples("AAA, BBB"), 1));
		assertTuplesContainsInAnyOrder(splitTuples("AAA, U, C, A, G"), ALL_TUPLES.transform(splitTuples("AAA, C"), 1));

		assertTuplesContainsInAnyOrder(splitTuples("T, C, A, G"), ALL_TUPLES.transform(splitTuples("T, C"), 1));
		
		assertTuplesContainsInAnyOrder(
			splitTuples("UU, CU, AU, GU, UC, CC, AC, GC, UA, CA, AA, GA, UG, ,CG, AG, GG"),
		ALL_TUPLES.transform(Collections.emptyList(), 2));
	}
}