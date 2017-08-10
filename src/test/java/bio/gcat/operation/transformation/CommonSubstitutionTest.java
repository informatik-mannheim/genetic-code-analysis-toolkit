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
import static org.junit.Assert.assertTrue;

import java.util.Collections;

import org.junit.Test;

public class CommonSubstitutionTest {
	private static final CommonSubstitution COMMON_SUBSTITUTION = new CommonSubstitution();
	
	@Test public void test() {
		assertTrue("Empty list expected", COMMON_SUBSTITUTION.transform(Collections.emptyList(), "id").isEmpty());
		
		assertTuplesContains(splitTuples("AAA"), COMMON_SUBSTITUTION.transform(splitTuples("AAA"), "id"));
		assertTuplesContains(splitTuples("AAA, AAA"), COMMON_SUBSTITUTION.transform(splitTuples("AAA, AAA"), "id"));
		assertTuplesContains(splitTuples("AAA, AAAA"), COMMON_SUBSTITUTION.transform(splitTuples("AAA, AAAA"), "id"));
		
		assertTuplesContains(splitTuples("UUU, GGG"), COMMON_SUBSTITUTION.transform(splitTuples("AAA, CCC"), "c"));
		assertTuplesContains(splitTuples("CCC, AAA"), COMMON_SUBSTITUTION.transform(splitTuples("GGG, UUU"), "c"));
		assertTuplesContains(splitTuples("UUU, GGG, CCC"), COMMON_SUBSTITUTION.transform(splitTuples("AAA, CCC, GGG"), "c"));
		assertTuplesContains(splitTuples("UUU, GGG, CCC, AAA"), COMMON_SUBSTITUTION.transform(splitTuples("AAA, CCC, GGG, UUU"), "c"));

		assertTuplesContains(splitTuples("GAU, GAC"), COMMON_SUBSTITUTION.transform(splitTuples("AGC, AGU"), "p"));
		assertTuplesContains(splitTuples("GAU, AGC"), COMMON_SUBSTITUTION.transform(splitTuples("AGC, GAU"), "p"));
		
		assertTuplesContains(splitTuples("AA, CG, UU"), COMMON_SUBSTITUTION.transform(splitTuples("AA, GC, UU"), "\u03c0CG"));
		assertTuplesContains(splitTuples("AA, CG, UU, GC"), COMMON_SUBSTITUTION.transform(splitTuples("AA, GC, UU, CG"), "\u03c0CG"));
	}
}