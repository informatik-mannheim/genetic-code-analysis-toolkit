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

import static bio.gcat.Help.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import bio.gcat.Documented;
import bio.gcat.log.Logger;
import bio.gcat.nucleic.Tuple;
import bio.gcat.operation.Cataloged;
import bio.gcat.operation.Named;
import bio.gcat.operation.transformation.ShiftSequence;
import bio.gcat.operation.transformation.Transformation;

@Named(name="comma-free", icon="comma_free") @Cataloged(group="Test Sequence", order=10)
@Documented(title="Comma-Free", category={OPERATIONS,TESTS}, resource="help/operation/test/comma_free.html")
public class CommaFree implements Test {
	private static final Test
		DUPLICATE_FREE = new DuplicateFree();
	private static final Transformation
		SHIFT = new ShiftSequence();
	
	@Override public boolean test(Collection<Tuple> tuples,Object... values) {
		Logger logger = getLogger();
		
		if(tuples.isEmpty())
			return true; //an empty set of tuples is comma-free
		
		int length;
		if((length=Tuple.tuplesLength(tuples))==0) {
			logger.log("Tuples of variable length, can't check for comma-free.");
			return false; //tuples not all of same length
		}
		
		if(!DUPLICATE_FREE.test(tuples)) {
			logger.log("Duplicate tuples in sequence, code not comma-free.");
			return false; //duplicate tuples
		}
		
		int shift; Collection<Tuple> shifted;
		for(Tuple tupleA:tuples) for(Tuple tupleB:tuples)
			if(tupleA!=tupleB) for(shift=1,shifted=Arrays.asList(tupleA,tupleB);shift<length;shift++)
				if(!Collections.disjoint(tuples,shifted = SHIFT.transform(shifted))) {
					logger.log("Shifted tuple created from "+tupleA+" and "+tupleB+" is contained in sequence, code not comma-free.");
					return false; //contains shifted tuple
				}
		
		return true;
	}
}