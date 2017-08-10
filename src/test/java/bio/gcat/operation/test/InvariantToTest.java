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

public class InvariantToTest {
	private static final InvariantTo INVARIANT_TO = new InvariantTo();
	
	@Test public void test() {
		Collection<Tuple> tuples = Collections.emptyList();
		assertTrue(tuples+" is empty",INVARIANT_TO.test(tuples,"id"));
		
		tuples = Tuple.splitTuples("AAA");
		assertTrue(tuples+" contains only one tuple",INVARIANT_TO.test(tuples,"id"));
		
		tuples = Tuple.splitTuples("AAA, AAA");
		assertTrue(tuples+" contains duplicate tuples",INVARIANT_TO.test(tuples,"id"));
		
		tuples = Tuple.splitTuples("AAA, AAAA");
		assertTrue(tuples+" contains tuples of variable length",INVARIANT_TO.test(tuples,"id"));
		
		tuples = Tuple.splitTuples("AAA, CCC");
		assertFalse(tuples+" is not invariant for c (GGG, UUU missing)",INVARIANT_TO.test(tuples,"c"));
		
		tuples = Tuple.splitTuples("GGG, UUU");
		assertFalse(tuples+" is not invariant for c (AAA, CCC missing)",INVARIANT_TO.test(tuples,"c"));
		
		tuples = Tuple.splitTuples("AAA, CCC, GGG");
		assertFalse(tuples+" is not invariant for c (UUU missing)",INVARIANT_TO.test(tuples,"c"));
		
		tuples = Tuple.splitTuples("AAA, CCC, GGG, UUU");
		assertTrue(tuples+" is invariant for c",INVARIANT_TO.test(tuples,"c"));
		
		tuples = Tuple.splitTuples("AGC, AGU");
		assertFalse(tuples+" is not invariant for g",INVARIANT_TO.test(tuples,"p"));
		
		tuples = Tuple.splitTuples("AGC, GAU");
		assertTrue(tuples+" is invariant for g",INVARIANT_TO.test(tuples,"p"));

		tuples = Tuple.splitTuples("AA, GC, UU");
		assertFalse(tuples+" is not invariant for \u03c0CG",INVARIANT_TO.test(tuples,"\u03c0CG"));
		
		tuples = Tuple.splitTuples("AA, GC, UU, CG");
		assertTrue(tuples+" is invariant for \u03c0CG",INVARIANT_TO.test(tuples,"\u03c0CG"));
	}
}