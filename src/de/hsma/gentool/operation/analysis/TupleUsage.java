package de.hsma.gentool.operation.analysis;

import java.util.Collection;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset.Entry;
import de.hsma.gentool.nucleic.Tuple;
import de.hsma.gentool.operation.Cataloged;
import de.hsma.gentool.operation.Named;

@Named(name="tuple usage") @Cataloged(group="Analyses")
public class TupleUsage implements Analysis {
	private static final String DELIMITER = ", ", TIMES = "x ";
	
	@Override public Result analyse(Collection<Tuple> tuples,Object... values) {
		StringBuilder builder = new StringBuilder();
		for(Entry<Tuple> tuple:HashMultiset.create(tuples).entrySet())
			builder.append(DELIMITER).append(tuple.getCount()).append(TIMES).append(tuple.getElement());
		return new SimpleResult(this,builder.substring(DELIMITER.length()).toString());
	}
}