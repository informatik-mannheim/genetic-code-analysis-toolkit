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
import java.util.regex.PatternSyntaxException;
import de.hsma.gentool.Parameter;
import de.hsma.gentool.Parameter.Type;
import de.hsma.gentool.Utilities;
import de.hsma.gentool.nucleic.Tuple;
import de.hsma.gentool.operation.Named;
import de.hsma.gentool.operation.Operation;

public interface Transformation extends Operation {
	public default Collection<Tuple> transform(Collection<Tuple> tuples) { return transform(tuples, Parameter.getValues(Operation.getParameters(this.getClass())));  }
	public Collection<Tuple> transform(Collection<Tuple> tuples, Object... values);
	
	@Named(name="find & replace", icon="find")
	@Parameter.Annotation(key="pattern",label="Term",type=Type.TEXT)
	@Parameter.Annotation(key="replace",label="Replace",type=Type.TEXT)
	@Parameter.Annotation(key="regex",label="Regex",type=Type.BOOLEAN,value=Utilities.TRUE)
	public class Expression implements Transformation {  	
  	@Override public Collection<Tuple> transform(Collection<Tuple> tuples,Object... values) { return transform(tuples, (String)values[0], (String)values[1], (Boolean)values[2]); }
  	public Collection<Tuple> transform(Collection<Tuple> tuples,String pattern,String replace,boolean regex) {
			try {
				return Tuple.splitTuples(Pattern.compile(regex?pattern:Pattern.quote(pattern),Pattern.CASE_INSENSITIVE).matcher(Tuple.joinTuples(tuples)).replaceAll(replace));
			}	catch(PatternSyntaxException e) { return tuples; }
		}
	}
}