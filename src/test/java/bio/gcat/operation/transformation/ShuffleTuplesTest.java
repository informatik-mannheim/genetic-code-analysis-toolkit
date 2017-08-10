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
import static bio.gcat.operation.transformation.helper.TuplesAssert.assertTuplesContainsInAnyOrder;
import static org.junit.Assert.assertTrue;

import java.util.Collections;

import org.junit.Test;

public class ShuffleTuplesTest {
	private static final ShuffleTuples SHUFFLE_TUPLES = new ShuffleTuples();
	
	@Test public void test() {
		assertTrue("Empty list expeceted", SHUFFLE_TUPLES.transform(Collections.emptyList()).isEmpty());
		assertTuplesContainsInAnyOrder(splitTuples("G, U, A, C"), SHUFFLE_TUPLES.transform(splitTuples("G, U, A, C")));
		assertTuplesContainsInAnyOrder(splitTuples("A, AA, AAA, AAAA"), SHUFFLE_TUPLES.transform(splitTuples("A, AA, AAA, AAAA")));
	}
}