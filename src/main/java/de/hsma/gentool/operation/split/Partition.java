package de.hsma.gentool.operation.split;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import com.google.common.collect.Lists;
import de.hsma.gentool.Parameter;
import de.hsma.gentool.nucleic.Tuple;
import de.hsma.gentool.operation.Cataloged;
import de.hsma.gentool.operation.Named;

@Named(name="n-partition") @Cataloged(group="Splits")
public class Partition implements Split {
	private static final Parameter[] PARAMETERS = new Parameter[] {
		new Parameter("parts", "Parts", 2, 2, Short.MAX_VALUE, 2),
	};
	public static Parameter[] getParameters() { return PARAMETERS; }
	
	@SuppressWarnings("unchecked") @Override public List<Collection<Tuple>> split(Collection<Tuple> tuples,Object... values) {
		return (List<Collection<Tuple>>)(List<?>)Lists.partition((tuples instanceof List)?(List<Tuple>)tuples:new ArrayList<>(tuples),(int)Math.ceil((double)tuples.size()/(Integer)values[0]));
	}
}