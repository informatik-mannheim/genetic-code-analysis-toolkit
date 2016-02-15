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
import java.util.regex.PatternSyntaxException;
import bio.gcat.Documented;
import bio.gcat.Parameter;
import bio.gcat.Utilities;
import bio.gcat.Parameter.Type;
import bio.gcat.nucleic.Tuple;
import bio.gcat.operation.Named;
import bio.gcat.operation.Operation;

public interface Transformation extends Operation {
	default public Collection<Tuple> transform(Collection<Tuple> tuples) { return transform(tuples, Parameter.getValues(Operation.getParameters(this.getClass())));  }
	public Collection<Tuple> transform(Collection<Tuple> tuples, Object... values);
	
	@Named(name="find & replace", icon="find")
	@Parameter.Annotation(key="pattern",label="Term",type=Type.TEXT)
	@Parameter.Annotation(key="replace",label="Replace",type=Type.TEXT)
	@Parameter.Annotation(key="regex",label="Regex",type=Type.BOOLEAN,value=Utilities.TRUE)
	@Documented(title="Replace", category={OPERATIONS,GENERAL}, resource="help/operation/transformation/replace.html")
	public class Expression implements Transformation {  	
		@Override public Collection<Tuple> transform(Collection<Tuple> tuples,Object... values) {
			try {
				return Tuple.splitTuples(Pattern.compile((Boolean)values[2]?(String)values[0]:Pattern.quote((String)values[0]),
					Pattern.CASE_INSENSITIVE).matcher(Tuple.joinTuples(tuples)).replaceAll((String)values[1]));
			} catch(PatternSyntaxException e) { return tuples; }
		}
	}
}