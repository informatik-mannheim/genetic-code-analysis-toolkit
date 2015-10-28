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
import bio.gcat.operation.test.CommaFree;

public class CommaFreeTest {
	private static final CommaFree COMMA_FREE = new CommaFree();
	
	@Test public void test() {
		Collection<Tuple> tuples = Collections.emptyList();
		assertTrue(tuples+" is empty",COMMA_FREE.test(tuples,1));
		
		tuples = Tuple.splitTuples("AAA, AAA");
		assertFalse(tuples+" contains duplicate tuples",COMMA_FREE.test(tuples,1));
		
		tuples = Tuple.splitTuples("AAA, AAAA");
		assertFalse(tuples+" contains tuples of variable length",COMMA_FREE.test(tuples,1));
		
		tuples = Tuple.splitTuples("GGC, GCC");
		assertTrue(tuples+" is comma-free",COMMA_FREE.test(tuples));
		
		tuples = Tuple.splitTuples("ATC, TCC, CAA");
		assertFalse(tuples+" is not comma-free",COMMA_FREE.test(tuples));
	}
}
