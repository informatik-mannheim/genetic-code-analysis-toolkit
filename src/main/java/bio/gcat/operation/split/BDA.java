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
package bio.gcat.operation.split;

import static bio.gcat.Help.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import bio.gcat.Documented;
import bio.gcat.gui.AnalysisTool;
import bio.gcat.log.Logger;
import bio.gcat.nucleic.Tuple;
import bio.gcat.operation.Cataloged;
import bio.gcat.operation.Named;

@Named(name="binary dichotomic algorithm", icon="bda") @Cataloged(group="Splits")
@Documented(title="BDA", category={OPERATIONS,SPLITS}, resource="help/operation/split/bda.html")
public class BDA implements Split {
	@Override public List<Collection<Tuple>> split(Collection<Tuple> tuples,Object... values) {
		Logger logger = getLogger();
		
    net.gumbix.geneticcode.dich.ct.ClassTable classTable = AnalysisTool.getBDA().getClassTable();
    if(classTable==null) {
			logger.log("No Binary Dichotomic Algorithm. Use BDA Tool to open / create BDAs.");
			return null; //no BDA
    }
    
    List<Collection<Tuple>> split = new ArrayList<>();
    for(scala.collection.immutable.List<net.gumbix.geneticcode.dich.Codon> codons:classTable.class2codonList().values())
    	split.add(new ArrayList<Tuple>(tuples) { private static final long serialVersionUID = 1l; {
    		retainAll(scala.collection.JavaConversions.asJavaCollection(codons.toList()).stream().map(codon->new Tuple(codon.toString())).collect(Collectors.toList()));
    	}});
    
    split.removeIf(splitCandidate->splitCandidate.isEmpty());
		return split;
	}
}