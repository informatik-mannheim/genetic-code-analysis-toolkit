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

public class DuplicateFreeTest {
	private static final DuplicateFree DUPLICATE_FREE = new DuplicateFree();
	
	@Test public void test() {
		Collection<Tuple> tuples = Collections.emptyList();
		assertTrue(tuples+" is empty",DUPLICATE_FREE.test(tuples));
		
		tuples = Tuple.splitTuples("AAA");
		assertTrue(tuples+" contains only one tuple",DUPLICATE_FREE.test(tuples));
		
		tuples = Tuple.splitTuples("AAA, AAA");
		assertFalse(tuples+" contains duplicate tuples",DUPLICATE_FREE.test(tuples));
		
		tuples = Tuple.splitTuples("AAA, AA");
		assertTrue(tuples+" contains tuples of different length",DUPLICATE_FREE.test(tuples));
		
		tuples = Tuple.splitTuples("AAA, GGG, UUU, CCC");
		assertTrue(tuples+" contains no duplicate tuples",DUPLICATE_FREE.test(tuples));
		
		tuples = Tuple.splitTuples("AAA, GGG, AAA, UUU, CCC");
		assertFalse(tuples+" contains duplicate tuples",DUPLICATE_FREE.test(tuples));
		
		tuples = Tuple.splitTuples("AAA, GGG, AAA, UUU, CCC, GGG");
		assertFalse(tuples+" contains duplicate tuples",DUPLICATE_FREE.test(tuples));
	}
}