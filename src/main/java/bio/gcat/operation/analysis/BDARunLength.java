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
import static bio.gcat.nucleic.helper.SequenceUtilities.*;
import static java.util.stream.Collectors.*;

import java.util.*;

import bio.gcat.Documented;
import bio.gcat.gui.AnalysisTool;
import bio.gcat.nucleic.Tuple;
import bio.gcat.nucleic.helper.SequenceUtilities.Pair;
import bio.gcat.operation.Cataloged;
import bio.gcat.operation.Named;
import bio.gcat.geneticcode.dich.Codon;

@Named(name = "BDA run lengths", icon = "bda")
@Cataloged(group = "Analyse Sequence")
@Documented(title = "BDA Run Length", category = {OPERATIONS, ANALYSES}, resource = "help/operation/analysis/bda_run_length.html")
public class BDARunLength implements Analysis {
  @Override
  public Result analyse(Collection<Tuple> tuples, Object... values) {
    if (condenseTuples(tuples).isEmpty())
      return new SimpleResult(this, "No tuples.");
    else if (Tuple.tuplesLength(tuples) != 3)
      return new SimpleResult(this, "Only sequences with codons (tuples of length 3) are allowed.");

    bio.gcat.geneticcode.dich.ct.ClassTable classTable = AnalysisTool.getBDATool().getClassTable();
    if (classTable == null)
      return new SimpleResult(this, "No Binary Dichotomic Algorithm. Use BDA Tool to open / create BDAs.");

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
    StringBuilder sb = new StringBuilder();
    try(Formatter formatter = new Formatter(sb, Locale.US)) {
	    // Iterate over all classes...
	    for (scala.collection.immutable.List<Object> clazz : classTable.classes()) {
	      // Get run lengths for this class:
	      List<List<Tuple>> lp = nl.stream().filter(p -> p.id.equals(clazz))
	              .map(p -> p.list).collect(toList());
	      List<Integer> li = lp.stream().map(it -> it.size()).collect(toList());
	
	      IntSummaryStatistics stats = li.stream().mapToInt(it -> it).summaryStatistics();
	      sb.append(clazz.mkString() + ": ");
	      formatter.format("min = %5d", stats.getMin());
	      formatter.format(", avg = %5.2f, ", stats.getAverage());
	      formatter.format("max = %5d <br/>", stats.getMax());
	    }
    }

    return new SimpleResult(this, sb.toString());
  }
}