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

import static bio.gcat.Help.*;
import java.util.Collection;
import bio.gcat.Documented;
import bio.gcat.log.Logger;
import bio.gcat.nucleic.Tuple;
import bio.gcat.operation.Cataloged;
import bio.gcat.operation.Named;
import bio.gcat.operation.transformation.ShiftTuples;
import bio.gcat.operation.transformation.Transformation;

@Named(name="c3", icon="c3") @Cataloged(group="Tests")
@Documented(title="C3", category={OPERATIONS,TESTS}, resource="help/operation/test/c3.html")
public class C3 implements Test {
	private static final Test
		CIRCULAR = new Circular();
	private static final Transformation
		SHIFT = new ShiftTuples();
	
	@Override public boolean test(Collection<Tuple> tuples,Object... values) {
		Logger logger = getLogger();
		
		if(tuples.isEmpty())
			return true; //an empty set of tuples is comma-free
		
		int length;
		if((length=Tuple.tuplesLength(tuples))==0) {
			logger.log("Tuples of variable length, can't check for c3-code.");
			return false; //tuples not all of same length
		}
		
		Collection<Tuple> shifted = tuples;
		for(int shift=0;shift<length;shift++)
			if(!CIRCULAR.test(shifted=SHIFT.transform(shifted,shift),length+1))
				return false;
		
		return true;
	}
}
