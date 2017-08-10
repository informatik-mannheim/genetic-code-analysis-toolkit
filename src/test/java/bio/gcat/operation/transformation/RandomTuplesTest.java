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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Collections;

import org.junit.Test;

import bio.gcat.nucleic.Tuple;

public class RandomTuplesTest {
	private static final RandomTuples RANDOM_TUPLES = new RandomTuples();
	
	@Test public void test() {
		assertTrue("Empty list expeceted", RANDOM_TUPLES.transform(Collections.emptyList(), 0).isEmpty());
		
		Collection<Tuple> tuples = RANDOM_TUPLES.transform(Collections.emptyList(), 1);
		assertEquals("Collection with 1 tuple expected", 1, tuples.size());
		assertEquals("One tuple of length 3 expected", 3, tuples.iterator().next().length());
		
		tuples = RANDOM_TUPLES.transform(splitTuples("U, C, A, G"), 4);
		assertEquals("Collection with 8 tuples expected", 8, tuples.size());
		for(Tuple tuple:tuples)	assertEquals("Collection with tuples of size 1 expected", 1, tuple.length());
	}
}