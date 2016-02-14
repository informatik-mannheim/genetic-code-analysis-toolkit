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

public class C3Test {
	private static final C3 C3 = new C3();
	
	@Test public void test() {
		Collection<Tuple> tuples = Collections.emptyList();
		assertTrue(tuples+" is empty",C3.test(tuples));
		
		tuples = Tuple.splitTuples("AAA");
		assertFalse(tuples+" contains one of AAA,GGG,etc.",C3.test(tuples));
		
		tuples = Tuple.splitTuples("AAA, AAA");
		assertFalse(tuples+" contains duplicate tuples",C3.test(tuples));
		
		tuples = Tuple.splitTuples("AAA, AAAA");
		assertFalse(tuples+" contains tuples of variable length",C3.test(tuples));
		
		tuples = Tuple.splitTuples("AAC, GTT");
		assertTrue(tuples+" is c3",C3.test(tuples));
		
		tuples = Tuple.splitTuples("TGG, GTG");
		assertFalse(tuples+" isn't c3",C3.test(tuples));
		
		tuples = Tuple.splitTuples("CCT, CCG, TCA, TGA");
		assertTrue(tuples+" is c3",C3.test(tuples));
		
		tuples = Tuple.splitTuples("CGT, ACG, TAC, GTA");
		assertFalse(tuples+" isn't c3",C3.test(tuples));
		
		tuples = Tuple.splitTuples("AAC,GTT,AAG,CTT,AAT,ATT,ACC,GGT,ACG,CGT,ACT,AGT,AGC,GCT,AGG,CCT,CCG,CGG,TCA,TGA");
		assertTrue(tuples+" is c3",C3.test(tuples));
	}
}