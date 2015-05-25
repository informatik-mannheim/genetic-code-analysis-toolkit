package de.hsma.gentool.operation.split;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import de.hsma.gentool.Parameter;
import de.hsma.gentool.Parameter.Type;
import de.hsma.gentool.gui.GenBDA;
import de.hsma.gentool.nucleic.Tuple;
import de.hsma.gentool.operation.Cataloged;
import de.hsma.gentool.operation.Named;

@Named(name="binary dichotomic algorithm") @Cataloged(group="Splits")
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
    for(scala.collection.immutable.List<net.gumbix.geneticcode.dich.Codon> codons:classTable.class2codons().values())
    	split.add(new ArrayList<Tuple>(tuples) { private static final long serialVersionUID = 1l; {
    		retainAll(Collections2.transform(scala.collection.JavaConversions.asJavaCollection(codons.toList()),new Function<net.gumbix.geneticcode.dich.Codon,Tuple>() {
  				@Override public Tuple apply(net.gumbix.geneticcode.dich.Codon codon) { return new Tuple(codon.toString()); }
      	}));
    	}});
    
    split.removeIf(splitCandidate->splitCandidate.isEmpty());
		return split;
	}
}
