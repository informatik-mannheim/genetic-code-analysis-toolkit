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
package de.hsma.gentool.operation.split;

import static de.hsma.gentool.Help.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import com.google.common.collect.Lists;
import de.hsma.gentool.Documented;
import de.hsma.gentool.Parameter;
import de.hsma.gentool.Parameter.Type;
import de.hsma.gentool.nucleic.Tuple;
import de.hsma.gentool.operation.Cataloged;
import de.hsma.gentool.operation.Named;

@Named(name="n-partition", icon="timeline_marker") @Cataloged(group="Splits")
@Parameter.Annotation(key="parts",label="Parts",type=Type.NUMBER,value="2,32767")
@Documented(title="Partition", category={OPERATIONS,SPLITS}, resource="help/operation/split/partition.html")
public class Partition implements Split {
	@SuppressWarnings("unchecked") @Override public List<Collection<Tuple>> split(Collection<Tuple> tuples,Object... values) {
		return (List<Collection<Tuple>>)(List<?>)Lists.partition((tuples instanceof List)?(List<Tuple>)tuples:new ArrayList<>(tuples),(int)Math.ceil((double)tuples.size()/(Integer)values[0]));
	}
}