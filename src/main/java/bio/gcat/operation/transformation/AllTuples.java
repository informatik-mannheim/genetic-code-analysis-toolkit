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
package bio.gcat.operation.transformation;

import static bio.gcat.Help.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import bio.gcat.Documented;
import bio.gcat.Parameter;
import bio.gcat.Parameter.Type;
import bio.gcat.nucleic.Tuple;
import bio.gcat.operation.Cataloged;
import bio.gcat.operation.Named;

@Named(name="add all (missing) tuples", icon="color_swatch") @Cataloged(group="Transformations")
@Parameter.Annotation(key="length",label="Length",type=Type.NUMBER,value="1,3,10")
@Documented(title="All (Missing) Tuples", category={OPERATIONS,TRANSFORMATIONS}, resource="help/operation/transformation/all_tuples.html")
public class AllTuples implements Transformation {	
	@Override public Collection<Tuple> transform(Collection<Tuple> tuples,Object... values) { return transform(tuples,(Integer)values[0]); }
	public Collection<Tuple> transform(Collection<Tuple> tuples,int length) {
		List<Tuple> newTuples = new ArrayList<>(tuples);
		for(Tuple tuple:Tuple.allTuples(length))
			if(!tuples.contains(tuple)) newTuples.add(tuple);
		return newTuples;
	}
}