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
package bio.gcat.operation.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Collections;

import org.junit.Test;

import bio.gcat.nucleic.Tuple;

public class SelfComplementaryTest {
	private static final SelfComplementary SELF_COMPLEMENTARY = new SelfComplementary();
	
	@Test public void test() {
		Collection<Tuple> tuples = Collections.emptyList();
		assertTrue(tuples+" is empty",SELF_COMPLEMENTARY.test(tuples));
		
		tuples = Tuple.splitTuples("AAA");
		assertFalse(tuples+" contains only one tuple",SELF_COMPLEMENTARY.test(tuples));
		
		tuples = Tuple.splitTuples("AAA, AAAA");
		assertFalse(tuples+" contains tuples of variable length",SELF_COMPLEMENTARY.test(tuples));
		
		tuples = Tuple.splitTuples("TTT, UUU");
		assertFalse(tuples+" contains tuples of different acids",SELF_COMPLEMENTARY.test(tuples));
		
		tuples = Tuple.splitTuples("AAA, UUU");
		assertTrue(tuples+" is self complementary",SELF_COMPLEMENTARY.test(tuples));
		
		tuples = Tuple.splitTuples("AGC, UCG");
		assertFalse(tuples+" is not self complementary",SELF_COMPLEMENTARY.test(tuples));
		
		tuples = Tuple.splitTuples("AGC, GCU");
		assertTrue(tuples+" is self complementary",SELF_COMPLEMENTARY.test(tuples));
		
		tuples = Tuple.splitTuples("AGC, GCU, AAG");
		assertFalse(tuples+" is not self complementary",SELF_COMPLEMENTARY.test(tuples));
		
		tuples = Tuple.splitTuples("CUU, AGC, GCU, AAG");
		assertTrue(tuples+" is self complementary",SELF_COMPLEMENTARY.test(tuples));
	}
}