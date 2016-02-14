/*
 * Copyright [2014] [Mannheim University of Applied Sciences]
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
package bio.gcat.operation.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Collections;

import org.junit.Test;

import bio.gcat.nucleic.Tuple;
import bio.gcat.operation.test.Circular.CommonCircular;

public class CommonCircularTest {
	private static final CommonCircular COMMON_CIRCULAR = new CommonCircular();
	
	@Test public void test() {
		Collection<Tuple> tuples = Collections.emptyList();
		assertTrue(tuples+" is empty",COMMON_CIRCULAR.test(tuples));
		
		tuples = Tuple.splitTuples("AAA");
		assertFalse(tuples+" contains one of AAA, GGG, etc.",COMMON_CIRCULAR.test(tuples));
		
		tuples = Tuple.splitTuples("AAA, AAA");
		assertFalse(tuples+" contains duplicate tuples",COMMON_CIRCULAR.test(tuples));
		
		tuples = Tuple.splitTuples("AAA, AAAA");
		assertFalse(tuples+" contains tuples of variable length",COMMON_CIRCULAR.test(tuples));
		
		tuples = Tuple.splitTuples("AAAAA");
		assertFalse(tuples+" contains tuples of length > 4",COMMON_CIRCULAR.test(tuples));

		tuples = Tuple.splitTuples("G, T, G");
		assertFalse(tuples+" is not 1-circular",COMMON_CIRCULAR.test(tuples));

		tuples = Tuple.splitTuples("G, T, C");
		assertTrue(tuples+" is 1-circular",COMMON_CIRCULAR.test(tuples));
		
		tuples = Tuple.splitTuples("GT, GG");
		assertFalse(tuples+" is not 3-circular",COMMON_CIRCULAR.test(tuples));

		tuples = Tuple.splitTuples("AG, TC");
		assertTrue(tuples+" is 3-circular",COMMON_CIRCULAR.test(tuples));
		
		tuples = Tuple.splitTuples("TGG, GTG");
		assertFalse(tuples+" is not 4-circular",COMMON_CIRCULAR.test(tuples));
		
		tuples = Tuple.splitTuples("TGG, CTG, GGC, TGT");
		assertFalse(tuples+" is not 4-circular",COMMON_CIRCULAR.test(tuples));
		
		tuples = Tuple.splitTuples("ACG, GTA, CGT, CGG, TAC");
		assertFalse(tuples+" is not 4-circular",COMMON_CIRCULAR.test(tuples));
		
		tuples = Tuple.splitTuples("CGT, ACG, TAC, GTA");
		assertFalse(tuples+" is not 4-circular",COMMON_CIRCULAR.test(tuples));
		
		tuples = Tuple.splitTuples("CGT, ACG, TAC");
		assertTrue(tuples+" is 4-circular",COMMON_CIRCULAR.test(tuples));
		
		tuples = Tuple.splitTuples("TCCC, CTCC");
		assertFalse(tuples+" is not 3-circular",COMMON_CIRCULAR.test(tuples));

		tuples = Tuple.splitTuples("TACC, CTCC");
		assertTrue(tuples+" is 3-circular",COMMON_CIRCULAR.test(tuples));
	}
}
