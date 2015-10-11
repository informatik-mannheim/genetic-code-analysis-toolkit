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
package de.hsma.gentool.operation.analysis;

import static de.hsma.gentool.nucleic.Tuple.*;
import static de.hsma.gentool.nucleic.helper.SequenceUtilities.*;
import static java.util.stream.Collectors.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.IntSummaryStatistics;
import java.util.List;
import net.gumbix.geneticcode.dich.Codon;
import de.hsma.gentool.gui.GenTool;
import de.hsma.gentool.nucleic.Tuple;
import de.hsma.gentool.nucleic.helper.SequenceUtilities.Pair;
import de.hsma.gentool.operation.Cataloged;
import de.hsma.gentool.operation.Named;

@Named(name = "BDA run lengths", icon = "bda") @Cataloged(group = "Analyses")
public class BDARunLength implements Analysis {
	@Override public Result analyse(Collection<Tuple> tuples, Object... values) {
		if(condenseTuples(tuples).isEmpty())
			return new SimpleResult(this, "No tuples.");
		else if(Tuple.tuplesLength(tuples)!=3)
			return new SimpleResult(this, "Only sequences with codons (tuples of length 3) are allowed.");
		
    net.gumbix.geneticcode.dich.ct.ClassTable classTable = GenTool.getBDA().getClassTable();
    if(classTable==null)
    	return new SimpleResult(this, "No Binary Dichotomic Algorithm. Use BDA Editor to open / create BDAs.");
    
		HashMap<Codon, scala.collection.immutable.List<Object>> m = classTable.codon2class();
		List<Pair> nl = splitByRuns(new ArrayList<>(tuples),
				(t1, t2) -> {
					Codon codon1 = new Codon(t1.toString());
					Codon codon2 = new Codon(t2.toString());
					Object clazz1 = m.get(codon1);
					Object clazz2 = m.get(codon2);
					return clazz1.equals(clazz2);
				},
				t -> {
					Codon codon = new Codon(t.toString());
					return classTable.codon2class().get(codon);
				}
				);

		// TODO requires unit tests...
		StringBuilder builder = new StringBuilder();

		// Iterate over all classes...
		for (scala.collection.immutable.List<Object> clazz : classTable.classes()) {
			// Get run lengths for this class:
			List<List<Tuple>> lp = nl.stream().filter(p -> p.id.equals(clazz))
					.map(p -> p.list).collect(toList());
			List<Integer> li = lp.stream().map(it -> it.size()).collect(toList());

			IntSummaryStatistics stats = li.stream().mapToInt(it -> it).summaryStatistics();
			builder.append(clazz.mkString() + ": ").append(stats.getMin() + ", ").
			append(stats.getAverage() + ", ").
			append(stats.getMax()).append("<br/>");
		}
		
		return new SimpleResult(this, builder.toString());
	}
}