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
import java.util.Collection;
import java.util.regex.Pattern;
import bio.gcat.Documented;
import bio.gcat.Parameter;
import bio.gcat.Parameter.Type;
import bio.gcat.nucleic.Tuple;
import bio.gcat.operation.Cataloged;
import bio.gcat.operation.Named;

@Named(name="n-shift all tuples", icon="text_kerning") @Cataloged(group="Transformations")
@Parameter.Annotation(key="shift",label="Shift",type=Type.NUMBER,value="1,10")
@Documented(title="Shift Tuples", category={OPERATIONS,TRANSFORMATIONS}, resource="help/operation/transformation/shift_tuples.html")
public class ShiftTuples implements Transformation {
	private static final Pattern PATTERN_ROTATE = Pattern.compile("(\\S)(\\S*)");
	private static final String REPLACE_ROTATE = "$2$1";
	
	@Override public Collection<Tuple> transform(Collection<Tuple> tuples,Object... values) { return transform(tuples,(Integer)values[0]); }
	public Collection<Tuple> transform(Collection<Tuple> tuples,int shift) {
		String string = Tuple.joinTuples(tuples);
		while(shift-->0) string = PATTERN_ROTATE.matcher(string).replaceAll(REPLACE_ROTATE);
		return Tuple.splitTuples(string);
	}
}