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
package bio.gcat.operation.test;

import static bio.gcat.Help.GENERAL;
import static bio.gcat.Help.OPERATIONS;

import java.util.Collection;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import bio.gcat.Documented;
import bio.gcat.Parameter;
import bio.gcat.Parameter.Type;
import bio.gcat.Utilities;
import bio.gcat.nucleic.Tuple;
import bio.gcat.operation.Named;
import bio.gcat.operation.Operation;

public interface Test extends Operation {
	public boolean test(Collection<Tuple> tuples, Object... values);

	public interface Handler {
		public default void handle(Test test, boolean result) throws Exception { handleDefault(test, result); };
		
		public default void handleDefault(Test test, boolean result) throws Default { throw new Default(test, result); }
	}
	public static class Exception extends Operation.Exception {
		private static final long serialVersionUID = 1l;

		public Exception(Test test) { super(test); }
		public Exception(Test test, String message) { super(test, message); }
		public Exception(Test test, String message, Throwable cause) { super(test, message, cause); }
		public Exception(Test test, Throwable cause) { super(test, cause); }
		
		public Test getTest() { return (Test)getOperation(); }
	}
	public static class Failed extends Exception {
		private static final long serialVersionUID = 1l;
	
		public Failed(Test test) { super(test, "Test Failed"); }	  

	}
	public static class Default extends Exception {
		private static final long serialVersionUID = 1l;
		
		private final boolean result;
		
		public Default(Test test, boolean result) {
			super(test, "Default Test Handling");
			this.result = result;
		}
		
		public boolean getResult() { return result; }
	}
	
	@Named(name="match", icon="magnifier_zoom_in")
	@Parameter.Annotation(key="pattern",label="Term",type=Type.TEXT)
	@Parameter.Annotation(key="regex",label="Regex",type=Type.BOOLEAN,value=Utilities.TRUE)
	@Documented(title="Find", category={OPERATIONS,GENERAL}, resource="help/operation/test/find.html")
	public class Expression implements Test {
		@Override public boolean test(Collection<Tuple> tuples,Object... values) {
			try {
				return Pattern.compile((Boolean)values[1]?(String)values[0]:Pattern.quote((String)values[0]),
					Pattern.CASE_INSENSITIVE).matcher(Tuple.joinTuples(tuples)).matches();
			} catch(PatternSyntaxException e) { return false; }
		}
	}
}