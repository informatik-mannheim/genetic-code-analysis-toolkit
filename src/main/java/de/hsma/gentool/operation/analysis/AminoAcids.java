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