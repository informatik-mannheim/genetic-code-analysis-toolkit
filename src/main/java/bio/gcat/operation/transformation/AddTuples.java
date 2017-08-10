package bio.gcat.operation.transformation;

import static bio.gcat.nucleic.Tuple.splitTuples;
import static bio.gcat.nucleic.Tuple.tupleString;

import java.util.ArrayList;
import java.util.Collection;

import bio.gcat.Parameter;
import bio.gcat.Parameter.Type;
import bio.gcat.nucleic.Tuple;
import bio.gcat.operation.Named;

@Named(name="add tuples", icon="add")
@Parameter.Annotation(key="tuples",label="Tuples",type=Type.TEXT)
public class AddTuples implements Transformation {
	@Override public Collection<Tuple> transform(Collection<Tuple> tuples,Object... values) {
		return new ArrayList<Tuple>(tuples) { private static final long serialVersionUID = 1l; {
			addAll(splitTuples(tupleString((String)values[0])));
		}};
	}
}
