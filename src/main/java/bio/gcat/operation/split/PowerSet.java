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
package bio.gcat.operation.split;

import static bio.gcat.Help.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import bio.gcat.Documented;
import bio.gcat.nucleic.Tuple;
import bio.gcat.operation.Cataloged;
import bio.gcat.operation.Named;

@Named(name="power set", icon="chart_organisation") @Cataloged(group="Split Sequence")
@Documented(title="Power Set", category={OPERATIONS,SPLITS}, resource="help/operation/split/power_set.html")
public class PowerSet implements Split {
 	@Override public List<Collection<Tuple>> split(Collection<Tuple> tuples,Object... values) { return new ArrayList<>(powerSet(new HashSet<>(tuples))); }
	@SuppressWarnings("serial") private static <T> Set<Set<T>> powerSet(Set<T> originalSet) {
		Set<Set<T>> sets = new HashSet<Set<T>>();
	    if(!originalSet.isEmpty()) {
	      List<T> list = new ArrayList<T>(originalSet);
	      T head = list.get(0);
	      Set<T> restSet = new HashSet<T>(list.subList(1, list.size())); 
	      for(Set<T> set:powerSet(restSet)) {
	      	sets.add(new HashSet<T>(set) {{ add(head); }});
	      	sets.add(set);
	      }
	    } else sets.add(Collections.emptySet());
	    return sets;
	}
}