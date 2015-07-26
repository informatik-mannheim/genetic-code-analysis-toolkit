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

import java.util.Collection;
import java.util.Optional;
import com.google.common.collect.EnumMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multiset.Entry;
import de.hsma.gentool.nucleic.Compound;
import de.hsma.gentool.nucleic.Tuple;
import de.hsma.gentool.operation.Cataloged;
import de.hsma.gentool.operation.Named;

@Named(name="amino acids") @Cataloged(group="Analyses")
public class AminoAcids implements Analysis {
	private static final String DELIMITER = ", ", TIMES = "x ";
	
	@Override public Result analyse(Collection<Tuple> tuples,Object... values) {
		Multiset<Compound> compounds = EnumMultiset.create(Compound.class);
		for(Tuple tuple:tuples) compounds.add(Optional.ofNullable(tuple.getCompound()).orElse(Compound.UNKNOWN));
		
		StringBuilder builder = new StringBuilder();
		for(Entry<Compound> compound:compounds.entrySet())
			builder.append(DELIMITER).append(compound.getCount()).append(TIMES).append(compound.getElement());
		
		return new SimpleResult(this,builder.substring(DELIMITER.length()).toString());
	}
}