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
import org.paukov.combinatorics.Factory;
import org.paukov.combinatorics.ICombinatoricsVector;
import bio.gcat.Documented;
import bio.gcat.Parameter;
import bio.gcat.Parameter.Type;
import bio.gcat.log.Logger;
import bio.gcat.nucleic.Tuple;
import bio.gcat.operation.Cataloged;
import bio.gcat.operation.Named;
import bio.gcat.operation.transformation.ShiftSequence;
import bio.gcat.operation.transformation.Transformation;

@Named(name="n-circular", icon="arrow_rotate_clockwise") @Cataloged(group="Test Sequence", order=21)
@Parameter.Annotation(key="n",label="n-Circular",type=Type.NUMBER,value="1,10")
@Documented(title="Circular", category={OPERATIONS,TESTS}, resource="help/operation/test/circular.html")
public class Circular implements Test {	
	private static final Test
		DUPLICATE_FREE = new DuplicateFree();
	private static final Transformation
		SHIFT = new ShiftSequence();
	
	@Override public boolean test(Collection<Tuple> tuples,Object... values) { return test(tuples,(Integer)values[0]); }
	public boolean test(Collection<Tuple> tuples,int n) {
		Logger logger = getLogger();
		
		if(tuples.isEmpty())
			return true; //an empty set of tuples is circular
		
		int length;
		if((length=Tuple.tuplesLength(tuples))==0) {
			logger.log("Tuples of variable length, can't check for "+n+"-circular.");
			return false; //tuples not all of same length
		}
		
		if(!DUPLICATE_FREE.test(tuples)) {
			logger.log("Duplicate tuples in sequence, code not "+n+"-circular.");
			return false; //duplicate tuples
		}
		
		if(n<=0) return true;
		else if(n==1) {
			int shift; Tuple shifted;
			for(Tuple tuple:tuples) for(shift=1,shifted=tuple;shift<length;shift++)
				if(tuples.contains(shifted = SHIFT.transform(Arrays.asList(tuple)).iterator().next())) {
					logger.log((!tuple.equals(shifted)?"Tuples "+tuple+" and "+shifted+" belong to the same equivalence class":
						"Tuple "+tuple+" is contained in sequence")+", code not 1-circular.");
					return false; //lemma 3.2, is 1-circular if and only if X contains at most one codon from each complete conjugacy class
				}
		} else {
			if(!test(tuples,n-1))
				return false; //lemma 3.2, if X is a n-circularcode, then X is also m-circular for all m<n
			
			Collection<Tuple> shifted;
			for(ICombinatoricsVector<Tuple> combination:Factory.createSimpleCombinationGenerator(Factory.createVector(tuples),n))
				for(ICombinatoricsVector<Tuple> permutation:Factory.createPermutationGenerator(combination))
					for(int shift=1;shift<length;shift++)
						if(tuples.containsAll(shifted=SHIFT.transform(permutation.getVector(),shift))) {
							logger.log("Partition "+permutation.getVector()+" and shift "+shifted+" contained in sequence, code not "+n+"-circular.");
							return false;
						}
		}

		return true;
	}
	
	@Named(name="circular", icon="arrow_rotate_clockwise") @Cataloged(group="Test Sequence", order=20)
	@Documented(title="Circular", category={OPERATIONS,TESTS}, resource="help/operation/test/circular.html")
	public static class CommonCircular extends Circular {
		@Override public boolean test(Collection<Tuple> tuples,Object... values) {
			Logger logger = getLogger();
			
			if(tuples.isEmpty())
				return true; //an empty set of tuples is circular
			
			int length;
			if((length=Tuple.tuplesLength(tuples))==0) {
				logger.log("Tuples of variable length, can't check for circular.");
				return false; //tuples not all of same length
			}
			
			switch(length) {
			case 1: return super.test(tuples,1);
			case 2: return super.test(tuples,3);
			case 3: return super.test(tuples,4);
			case 4: return super.test(tuples,3);
			default:
				logger.log("Circular is not defined for length \u2265 4. Please use n-circular.");
				return false;
			}
		}
	}
}