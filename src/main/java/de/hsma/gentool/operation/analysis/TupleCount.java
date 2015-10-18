package de.hsma.gentool.operation.analysis;

import static de.hsma.gentool.Help.*;
import static de.hsma.gentool.nucleic.Tuple.*;
import java.util.Collection;
import java.util.stream.Collectors;
import com.google.common.collect.HashMultiset;
import de.hsma.gentool.Documented;
import de.hsma.gentool.nucleic.Tuple;
import de.hsma.gentool.operation.Cataloged;
import de.hsma.gentool.operation.Named;

@Named(name="tuple count", icon="chart_bar") @Cataloged(group="Analyses")
@Documented(title="Tuple Count", category={OPERATIONS,ANALYSES}, resource="help/operation/analysis/tuple_count.html")
public class TupleCount implements Analysis {
	private static final String DELIMITER = ": ", NEW_LINE = "<br/>";
	
	public Result analyse(Collection<Tuple> tuples, Object... values) {
		String result = HashMultiset.create(condenseTuples(tuples)).entrySet().stream().map(
			tuple->tuple.getElement()+DELIMITER+tuple.getCount()).sorted().collect(Collectors.joining(NEW_LINE));
		return new SimpleResult(this,result.isEmpty()?"no tuples":NEW_LINE+result);
	}
}