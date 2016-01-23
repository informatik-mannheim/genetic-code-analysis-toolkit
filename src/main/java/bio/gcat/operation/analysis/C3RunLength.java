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

import static bio.gcat.Help.*;
import static bio.gcat.nucleic.Tuple.*;
import static bio.gcat.nucleic.helper.SequenceUtilities.*;
import static java.util.stream.Collectors.*;

import java.util.*;

import bio.gcat.Documented;
import bio.gcat.Parameter;
import bio.gcat.nucleic.Tuple;
import bio.gcat.nucleic.helper.C3Code;
import bio.gcat.nucleic.helper.SequenceUtilities.Pair;
import bio.gcat.operation.Cataloged;
import bio.gcat.operation.Named;

@Named(name = "C3 run lengths", icon = "c3") @Cataloged(group="Analyse Sequence")
@Parameter.Annotation(key = "codeNumber", label = "Code Number", type = Parameter.Type.NUMBER, value = "1,1,216")
@Documented(title="C3 Code Run Lenghts", category={OPERATIONS,ANALYSES}, resource="help/operation/analysis/c3_run_length.html")
public class C3RunLength implements Analysis {
	@Override public Result analyse(Collection<Tuple> tuples, Object... values) { return analyse(tuples, ((int)values[0])-1); }

	public Result analyse(Collection<Tuple> tuples, int codeNumber) {
		if(condenseTuples(tuples).isEmpty())
			return new SimpleResult(this, "No tuples.");
		else if(Tuple.tuplesLength(tuples)!=3)
			return new SimpleResult(this, "Only sequences with codons (tuples of length 3) are allowed.");

		List<Tuple> code = C3Code.CODES.get(codeNumber);
		List<Pair> nl = splitByRuns(new ArrayList<>(tuples),
			(t1, t2) -> code.contains(t1) == code.contains(t2),
			t -> code.contains(t));

		StringBuffer sb = new StringBuffer();
		Formatter formatter = new Formatter(sb, Locale.US);

		for (boolean isInClass : Arrays.asList(true, false)) {
			List<List<Tuple>> lp = nl.stream().filter(p -> p.id.equals(isInClass))
					.map(p -> p.list).collect(toList());
			List<Integer> li = lp.stream().map(it -> it.size()).collect(toList());

			IntSummaryStatistics stats = li.stream().mapToInt(it -> it).summaryStatistics();

			sb.append((isInClass ? "&nbsp;&nbsp;&nbsp;&nbsp;C3" : "not C3") +
							"(" + (codeNumber + 1) + "): ");
			formatter.format("min = %5d", stats.getMin());
			formatter.format(", avg = %5.2f, ", stats.getAverage());
			formatter.format("max = %5d <br/>", stats.getMax());
		}
		String x = sb.toString();

		return new SimpleResult(this, sb.toString());
	}
}