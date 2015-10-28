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

import static org.junit.Assert.*;
import java.util.Collection;
import java.util.Collections;
import org.junit.Test;
import bio.gcat.nucleic.Tuple;
import bio.gcat.operation.test.Circular;

public class CircularTest {
	private static final Circular CIRCULAR = new Circular();
	
	@Test public void test() {
		Collection<Tuple> tuples = Collections.emptyList();
		assertTrue(tuples+" is empty",CIRCULAR.test(tuples,1));
		
		tuples = Tuple.splitTuples("AAA");
		assertFalse(tuples+" contains one of AAA, GGG, etc.",CIRCULAR.test(tuples,1));
		
		tuples = Tuple.splitTuples("AAA, AAA");
		assertFalse(tuples+" contains duplicate tuples",CIRCULAR.test(tuples,1));
		
		tuples = Tuple.splitTuples("AAA, AAAA");
		assertFalse(tuples+" contains tuples of variable length",CIRCULAR.test(tuples,1));
		
		tuples = Tuple.splitTuples("TGG, GTG");
		assertTrue(tuples+" is 0-circular",CIRCULAR.test(tuples,0));
		assertFalse(tuples+" isn't 1-circular",CIRCULAR.test(tuples,1));
		assertFalse(tuples+" isn't 2-circular",CIRCULAR.test(tuples,2));
		
		tuples = Tuple.splitTuples("TGG, CTG, GGC, TGT");
		assertTrue(tuples+" is 1-circular",CIRCULAR.test(tuples,1));
		assertFalse(tuples+" isn't 2-circular",CIRCULAR.test(tuples,2));
		
		tuples = Tuple.splitTuples("ACG, GTA, CGT, CGG, TAC");
		assertTrue(tuples+" is 2-circular",CIRCULAR.test(tuples,2));
		assertFalse(tuples+" isn't 3-circular",CIRCULAR.test(tuples,3));
		
		tuples = Tuple.splitTuples("CGT, ACG, TAC, GTA");
		assertTrue(tuples+" is 3-circular",CIRCULAR.test(tuples,3));
		assertFalse(tuples+" isn't 4-circular",CIRCULAR.test(tuples,4));
	}
}
