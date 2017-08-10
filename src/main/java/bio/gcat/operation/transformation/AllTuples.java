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

import static bio.gcat.Help.OPERATIONS;
import static bio.gcat.Help.TRANSFORMATIONS;
import static bio.gcat.nucleic.Acid.RNA;
import static bio.gcat.nucleic.Tuple.allTuples;
import static bio.gcat.nucleic.Tuple.tuplesAcid;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import bio.gcat.Documented;
import bio.gcat.Parameter;
import bio.gcat.Parameter.Type;
import bio.gcat.nucleic.Tuple;
import bio.gcat.operation.Cataloged;
import bio.gcat.operation.Named;

@Named(name="add all (missing) tuples", icon="color_swatch") @Cataloged(group="Add / Remove Tuples")
@Parameter.Annotation(key="length",label="Length",type=Type.NUMBER,value="1,3,10")
@Documented(title="All (Missing) Tuples", category={OPERATIONS,TRANSFORMATIONS}, resource="help/operation/transformation/all_tuples.html")
public class AllTuples implements Transformation {	
	@Override public Collection<Tuple> transform(Collection<Tuple> tuples,Object... values) {
		List<Tuple> newTuples = new ArrayList<>(tuples);
		for(Tuple tuple:allTuples(Optional.ofNullable(tuplesAcid(tuples)).orElse(RNA), (Integer)values[0]))
			if(!tuples.contains(tuple)) newTuples.add(tuple);
		return newTuples;
	}
}