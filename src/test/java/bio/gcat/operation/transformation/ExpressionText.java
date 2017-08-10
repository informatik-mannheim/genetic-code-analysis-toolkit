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
import static bio.gcat.operation.transformation.helper.TuplesAssert.assertTuplesContains;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collections;

import org.junit.Test;

import bio.gcat.operation.transformation.Transformation.Expression;

public class ExpressionText {
	private static final Expression EXPRESSION = new Expression();
	
	@Test public void test() {
		assertTrue("Empty list expeceted", EXPRESSION.transform(Collections.emptyList(), ".", "A", true).isEmpty());
		
		assertTuplesContains(splitTuples("AB, BA"), EXPRESSION.transform(splitTuples("AB, BAA"), "AA", "A", false));
		assertTuplesContains(splitTuples("BBB, BBB"), EXPRESSION.transform(splitTuples("AAA, BBB"), "A", "B", false));
		assertTuplesContains(splitTuples("D, BB, CCC"), EXPRESSION.transform(splitTuples("A, BB, CCC"), "A", "D", false));
		
		assertTuplesContains(splitTuples("AB, BA"), EXPRESSION.transform(splitTuples("AB, BAAAAA"), "A+", "A", true));
		assertTuplesContains(splitTuples("CCC, CCC"), EXPRESSION.transform(splitTuples("AAA, BBB"), "A|B", "C", true));
		assertTuplesContains(splitTuples("C"), EXPRESSION.transform(splitTuples("AAA, BBB"), "A+\\sB+", "C", true));
		
		assertEquals(Collections.emptyList(), EXPRESSION.transform(Collections.emptyList(), "{}", "", true));
	}
}