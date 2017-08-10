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
package bio.gcat.operation.analysis;

import static bio.gcat.Help.*;
import static bio.gcat.nucleic.Tuple.*;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import bio.gcat.Documented;
import bio.gcat.Parameter;
import bio.gcat.nucleic.Tuple;
import bio.gcat.nucleic.helper.C3Code;
import bio.gcat.operation.Cataloged;
import bio.gcat.operation.Named;

@Named(name = "C3 sequence", icon = "c3") @Cataloged(group="Analyse Sequence")
@Parameter.Annotation(key = "codeNumber", label = "Code Number", type = Parameter.Type.NUMBER, value = "1,1,216")
@Documented(title="C3 Sequence", category={OPERATIONS,ANALYSES}, resource="help/operation/analysis/c3_sequence.html")
public class C3Sequence implements Analysis {
	@Override public Result analyse(Collection<Tuple> tuples, Object... values) { return analyse(tuples, ((int)values[0])-1); }
	public Result analyse(Collection<Tuple> tuples, int codeNumber) {
		if(condenseTuples(tuples).isEmpty())
			return new SimpleResult(this, "No tuples.");
		else if(Tuple.tuplesLength(tuples)!=3)
			return new SimpleResult(this, "Only sequences with codons (tuples of length 3) are allowed.");
		
		List<Tuple> code = C3Code.CODES.get(codeNumber);
		return new SimpleResult(this, tuples.stream().map(tuple->(
			"<span style=\"background: "+(code.contains(tuple)?"orange":"white")+"\"> "+tuple.toString()+"</span> "))
			.collect(Collectors.joining()));
	}
}