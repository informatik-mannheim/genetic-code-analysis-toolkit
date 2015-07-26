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
package de.hsma.gentool.operation.transformation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import de.hsma.gentool.Parameter;
import de.hsma.gentool.Parameter.Type;
import de.hsma.gentool.nucleic.Tuple;
import de.hsma.gentool.operation.Cataloged;
import de.hsma.gentool.operation.Named;

@Named(name="add all (missing) tuples", icon="color_swatch") @Cataloged(group="Transformations")
@Parameter.Annotation(key="length",label="Length",type=Type.NUMBER,value="1,3,10")
public class AllTuples implements Transformation {	
	@Override public Collection<Tuple> transform(Collection<Tuple> tuples,Object... values) { return transform(tuples,(Integer)values[0]); }
	public Collection<Tuple> transform(Collection<Tuple> tuples,int length) {
		List<Tuple> newTuples = new ArrayList<>(tuples);
		for(Tuple tuple:Tuple.allTuples(length))
			if(!tuples.contains(tuple)) newTuples.add(tuple);
		return newTuples;
	}
}