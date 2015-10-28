package bio.gcat.operation.analysis;

import static bio.gcat.Help.*;
import static bio.gcat.nucleic.Tuple.*;
import java.util.Collection;
import java.util.stream.Collectors;
import bio.gcat.Documented;
import bio.gcat.nucleic.Tuple;
import bio.gcat.operation.Cataloged;
import bio.gcat.operation.Named;
import com.google.common.collect.HashMultiset;

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