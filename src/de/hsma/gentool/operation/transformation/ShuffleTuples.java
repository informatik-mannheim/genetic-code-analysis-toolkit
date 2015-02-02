package de.hsma.gentool.operation.transformation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import de.hsma.gentool.nucleic.Tuple;
import de.hsma.gentool.operation.Cataloged;
import de.hsma.gentool.operation.Named;

@Named(name="shuffle all tuples") @Cataloged(group="Transformations")
public class ShuffleTuples implements Transformation {		
	@Override public Collection<Tuple> transform(Collection<Tuple> tuples,Object... values) {
		List<Tuple> shuffle = new ArrayList<>(tuples);
		Collections.shuffle(shuffle);
		return shuffle;
	}
}