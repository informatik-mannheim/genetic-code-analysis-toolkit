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
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import bio.gcat.Documented;
import bio.gcat.Parameter;
import bio.gcat.Utilities;
import bio.gcat.Parameter.Type;
import bio.gcat.nucleic.Tuple;
import bio.gcat.operation.Named;
import bio.gcat.operation.Operation;

public interface Test extends Operation {
	public boolean test(Collection<Tuple> tuples, Object... values);
	
	public static class Failed extends Exception {
		private static final long serialVersionUID = 1l;
		
	  public Failed(Test test) { super(test,"Test Failed"); }
	  
	  public Test getTest() { return (Test)getOperation(); }
	}
	
	@Named(name="match", icon="magnifier_zoom_in")
	@Parameter.Annotation(key="pattern",label="Term",type=Type.TEXT)
	@Parameter.Annotation(key="regex",label="Regex",type=Type.BOOLEAN,value=Utilities.TRUE)
	@Documented(title="Find", category={OPERATIONS,GENERAL}, resource="help/operation/test/find.html")
	public class Expression implements Test {
   	@Override public boolean test(Collection<Tuple> tuples,Object... values) { return test(tuples, (String)values[0], (Boolean)values[1]); }
  	public boolean test(Collection<Tuple> tuples,String pattern,boolean regex) {
  		try {
  			return Pattern.compile(regex?pattern:Pattern.quote(pattern),Pattern.CASE_INSENSITIVE).matcher(Tuple.joinTuples(tuples)).matches();
			}	catch(PatternSyntaxException e) { return false; }
		}
	}
}