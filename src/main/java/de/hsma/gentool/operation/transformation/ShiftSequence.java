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

import java.util.Collection;
import java.util.regex.Pattern;
import de.hsma.gentool.Parameter;
import de.hsma.gentool.Parameter.Type;
import de.hsma.gentool.nucleic.Tuple;
import de.hsma.gentool.operation.Cataloged;
import de.hsma.gentool.operation.Named;

@Named(name="n-shift sequence") @Cataloged(group="Transformations")
@Parameter.Annotation(key="shift",label="Shift",type=Type.NUMBER,value="1,10")
public class ShiftSequence implements Transformation {
	private static final Pattern
		PATTERN_SHIFT_A = Pattern.compile("(\\S)\\s+(\\S)"),
		PATTERN_SHIFT_B = Pattern.compile("^\\s*(\\S)(.*?)\\s*$");
	private static final String
		REPLACE_SHIFT_A = "$1$2 ",
		REPLACE_SHIFT_B = "$2$1";
 	
	@Override public Collection<Tuple> transform(Collection<Tuple> tuples,Object... values) { return transform(tuples,(Integer)values[0]); }
	public Collection<Tuple> transform(Collection<Tuple> tuples,int shift) {
		String string = Tuple.joinTuples(tuples);
		while(shift-->0) string = PATTERN_SHIFT_B.matcher(PATTERN_SHIFT_A.matcher(string).replaceAll(REPLACE_SHIFT_A)).replaceAll(REPLACE_SHIFT_B);
		return Tuple.splitTuples(string);
	}
}