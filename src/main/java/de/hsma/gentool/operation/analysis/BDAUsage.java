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

import static de.hsma.gentool.Help.*;
import static de.hsma.gentool.nucleic.Tuple.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.stream.Collectors;
import net.gumbix.geneticcode.dich.Codon;
import scala.collection.immutable.List;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multiset.Entry;
import de.hsma.gentool.Documented;
import de.hsma.gentool.gui.GenTool;
import de.hsma.gentool.nucleic.Tuple;
import de.hsma.gentool.operation.Cataloged;
import de.hsma.gentool.operation.Named;

@Named(name = "BDA usage", icon = "bda") @Cataloged(group = "Analyses")
@Documented(title="BDA Usage", category={OPERATIONS,ANALYSES}, resource="help/operation/analysis/bda_usage.html")
public class BDAUsage implements Analysis {
	@Override public Result analyse(Collection<Tuple> tuples, Object... values) {
		if(condenseTuples(tuples).isEmpty())
			return new SimpleResult(this, "No tuples.");
		else if(Tuple.tuplesLength(tuples)!=3)
			return new SimpleResult(this, "Only sequences with codons (tuples of length 3) are allowed.");

		net.gumbix.geneticcode.dich.ct.ClassTable classTable = GenTool.getBDA().getClassTable();
		if(classTable==null)
			return new SimpleResult(this, "No Binary Dichotomic Algorithm. Use BDA Editor to open / create BDAs.");
		HashMap<Codon, List<Object>> codonToClass = classTable.codon2class();

		Multiset<List<Object>> classCount = HashMultiset.create(tuples.stream().map(
			tuple->codonToClass.get(new Codon(tuple.toString()))).collect(Collectors.toList()));

		StringBuilder builder = new StringBuilder();
		for(Entry<List<Object>> count:classCount.entrySet())
			builder.append(count.getElement().mkString("","","") + ": " + count.getCount()+ "<br/>");
		return new SimpleResult(this, builder.toString());
	}
}