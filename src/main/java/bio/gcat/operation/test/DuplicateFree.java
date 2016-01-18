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
import java.util.HashSet;
import bio.gcat.Documented;
import bio.gcat.nucleic.Tuple;
import bio.gcat.operation.Cataloged;
import bio.gcat.operation.Named;

@Named(name="duplicate free", icon="style_delete") @Cataloged(group="Test Sequence", order=60)
@Documented(title="Duplicate Free", category={OPERATIONS,TESTS}, resource="help/operation/test/duplicate_free.html")
public class DuplicateFree implements Test {
	@Override public boolean test(Collection<Tuple> tuples,Object... values) {
		return tuples.size()==new HashSet<>(tuples).size();
	}
}