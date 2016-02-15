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

@Named(name="n-shift sequence", icon="text_indent") @Cataloged(group="Permute Nucleotide Bases Positions")
@Parameter.Annotation(key="shift",label="Shift",type=Type.NUMBER,value="1,10")
@Documented(title="Shift Sequence", category={OPERATIONS,TRANSFORMATIONS}, resource="help/operation/transformation/shift_sequence.html")
public class ShiftSequence implements Transformation {
	private static final Pattern
		PATTERN_SHIFT_A = Pattern.compile("(\\s)(\\S)"),
		PATTERN_SHIFT_B = Pattern.compile("^(\\S)(.*)$");
	private static final String
		REPLACE_SHIFT_A = "$2$1",
		REPLACE_SHIFT_B = "$2$1";
 	
	@Override public Collection<Tuple> transform(Collection<Tuple> tuples,Object... values) {
		int shift = (Integer)values[0]; String string = Tuple.joinTuples(tuples);
		while(shift-->0) string = PATTERN_SHIFT_B.matcher(PATTERN_SHIFT_A.matcher(string).replaceAll(REPLACE_SHIFT_A)).replaceAll(REPLACE_SHIFT_B);
		return Tuple.splitTuples(string);
	}
}