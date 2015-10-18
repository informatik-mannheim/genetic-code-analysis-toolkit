package de.hsma.gentool.operation.analysis;

import static de.hsma.gentool.Help.*;
import static de.hsma.gentool.nucleic.Tuple.*;
import java.util.Collection;
import scala.Tuple3;
import net.gumbix.geneticcode.dich.Codon;
import de.hsma.gentool.Documented;
import de.hsma.gentool.gui.GenTool;
import de.hsma.gentool.nucleic.Tuple;
import de.hsma.gentool.operation.Cataloged;
import de.hsma.gentool.operation.Named;

@Named(name = "BDA sequence", icon = "bda") @Cataloged(group = "Analyses")
@Documented(title="BDA Sequence", category={OPERATIONS,ANALYSES}, resource="help/operation/analysis/bda_sequence.html")
public class BDASequence implements Analysis {
	@Override public Result analyse(Collection<Tuple> tuples, Object... values) {
		if(condenseTuples(tuples).isEmpty())
			return new SimpleResult(this, "No tuples.");
		else if(Tuple.tuplesLength(tuples)!=3)
			return new SimpleResult(this, "Only sequences with codons (tuples of length 3) are allowed.");
		
    net.gumbix.geneticcode.dich.ct.ClassTable classTable = GenTool.getBDA().getClassTable();
    if(classTable==null)
    	return new SimpleResult(this, "No Binary Dichotomic Algorithm. Use BDA Editor to open / create BDAs.");
    
		StringBuilder builder = new StringBuilder();
		for(Tuple tuple : tuples) {
			Tuple3<Object, Object, Object> tu = classTable.colorRGB(new Codon(tuple.toString()));
			builder.append("<span style=\"background-color:" + String.format("#%02x%02x%02x", tu._1(), tu._2(), tu._3()) + "\"> " + tuple + "</span> ");
		}
		return new SimpleResult(this, "\n" + builder.toString());
	}
}