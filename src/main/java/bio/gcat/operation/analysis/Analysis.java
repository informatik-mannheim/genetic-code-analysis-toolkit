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
package bio.gcat.operation.analysis;

import java.util.Collection;
import bio.gcat.nucleic.Tuple;
import bio.gcat.operation.Operation;

public interface Analysis extends Operation {
	public Result analyse(Collection<Tuple> tuples, Object... values);
	
	public class Result {
		private Analysis analysis;
		public Result(Analysis analysis) { this.analysis = analysis; }
		public Analysis getAnalysis() { return analysis; }
		@Override public String toString() { return analysis!=null?analysis.getName():null; }
	}
	public class SimpleResult extends Result {
		private String result;
		public SimpleResult(Analysis analysis, String result) { super(analysis); this.result = result; }
		@Override public String toString() { return result; }
	}
	
	public interface Handler {
		public void handle(Result result);
	}
}