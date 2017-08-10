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

import bio.gcat.nucleic.Tuple;
import bio.gcat.operation.test.Test.Expression;

import org.junit.Test;

public class ExpressionTest {
	private static final Expression EXPRESSION = new Expression();
	
	@Test public void test() {
		Collection<Tuple> tuples = Collections.emptyList();
		assertTrue(tuples+" is empty",EXPRESSION.test(tuples,"",false));
		assertFalse(tuples+" is empty",EXPRESSION.test(tuples,".",true));
		assertFalse(tuples+" has wrong pattern",EXPRESSION.test(tuples,"{}",true));
		
		tuples = Tuple.splitTuples("AAA");
		assertTrue(tuples+" is equal AAA",EXPRESSION.test(tuples,"AAA",false));
		
		tuples = Tuple.splitTuples("AAA, CCC, GGG");
		assertTrue(tuples+" is equal AAA CCC GGG",EXPRESSION.test(tuples,"AAA CCC GGG",false));
		
		tuples = Tuple.splitTuples("AAA, CCC, GGG");
		assertTrue(tuples+" matches (\\b(A|C|G){3} ?)+",EXPRESSION.test(tuples,"(\\b(A|C|G){3} ?)+",true));
		
		tuples = Tuple.splitTuples("AAA, CCC, GGG, UUU");
		assertFalse(tuples+" not matches (\\b(A|C|G){3} ?)+",EXPRESSION.test(tuples,"(\\b(A|C|G){3} ?)+",true));
	}
}