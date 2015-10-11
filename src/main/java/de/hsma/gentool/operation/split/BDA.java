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
package de.hsma.gentool.operation.split;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import de.hsma.gentool.Parameter;
import de.hsma.gentool.Parameter.Type;
import de.hsma.gentool.gui.GenBDA;
import de.hsma.gentool.nucleic.Tuple;
import de.hsma.gentool.operation.Cataloged;
import de.hsma.gentool.operation.Named;

@Named(name="binary dichotomic algorithm", icon="bda") @Cataloged(group="Splits")
@Parameter.Annotation(key="bda-file",label="BDA-File",type=Type.FILE,value="Binary Dichotomic Algorithm (*.bda)")
public class BDA implements Split {
	@Override public List<Collection<Tuple>> split(Collection<Tuple> tuples,Object... values) { return split(tuples,(File)values[0]); }
	public List<Collection<Tuple>> split(Collection<Tuple> tuples,File file) {
		scala.collection.immutable.List<?> bdas;
		try {
			if((bdas=scala.collection.JavaConversions.collectionAsScalaIterable(GenBDA.Helper.readFile(file)).toList()).isEmpty())
				return new ArrayList<Collection<Tuple>>() { private static final long serialVersionUID = 1l; { add(tuples); }};
		} catch(IOException e) { return null; }
		
    @SuppressWarnings("unchecked") net.gumbix.geneticcode.dich.ct.ClassTable classTable =
    	new net.gumbix.geneticcode.dich.ct.ClassTable(
    		(scala.collection.immutable.List<net.gumbix.geneticcode.dich.Classifier<Object>>)bdas,
				net.gumbix.geneticcode.dich.IUPAC.STANDARD(),
				new net.gumbix.geneticcode.dich.IdAminoAcidProperty(1));

    List<Collection<Tuple>> split = new ArrayList<>();
    for(scala.collection.immutable.List<net.gumbix.geneticcode.dich.Codon> codons:classTable.class2codonList().values())
    	split.add(new ArrayList<Tuple>(tuples) { private static final long serialVersionUID = 1l; {
    		retainAll(scala.collection.JavaConversions.asJavaCollection(codons.toList()).stream().map(codon->new Tuple(codon.toString())).collect(Collectors.toList()));
    	}});
    
    split.removeIf(splitCandidate->splitCandidate.isEmpty());
		return split;
	}
}
