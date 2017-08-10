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

import static bio.gcat.Help.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import bio.gcat.Documented;
import bio.gcat.nucleic.Tuple;
import bio.gcat.operation.Cataloged;
import bio.gcat.operation.Named;

@Named(name="remove unknown compounds", icon="bug_delete") @Cataloged(group="Add / Remove Tuples")
@Documented(title="Remove Unknown Compounds", category={OPERATIONS,TRANSFORMATIONS}, resource="help/operation/transformation/remove_unknown_compounds.html")
public class RemoveUnknownCompounds implements Transformation {
	@Override public Collection<Tuple> transform(Collection<Tuple> tuples,Object... values) {
		List<Tuple> remove = new ArrayList<>(tuples);
		remove.removeIf(tuple->tuple==null||tuple.getCompound()==null);
		return remove;
	}
}