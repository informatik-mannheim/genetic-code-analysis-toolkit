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
import java.util.Optional;
import bio.gcat.Documented;
import bio.gcat.nucleic.Compound;
import bio.gcat.nucleic.Tuple;
import bio.gcat.operation.Cataloged;
import bio.gcat.operation.Named;
import com.google.common.collect.EnumMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multiset.Entry;
import static bio.gcat.Help.*;

@Named(name="amino acids", icon="bricks") @Cataloged(group="Analyse Sequence")
@Documented(title="Amino Acids", category={OPERATIONS,ANALYSES}, resource="help/operation/analysis/amino_acids.html")
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