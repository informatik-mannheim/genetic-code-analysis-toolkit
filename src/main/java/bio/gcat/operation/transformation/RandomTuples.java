package bio.gcat.operation.transformation;

import static bio.gcat.Help.*;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import bio.gcat.Documented;
import bio.gcat.Parameter;
import bio.gcat.Parameter.Type;
import bio.gcat.nucleic.Acid;
import bio.gcat.nucleic.Base;
import bio.gcat.nucleic.Tuple;
import bio.gcat.operation.Cataloged;
import bio.gcat.operation.Named;

@Named(name="random tuples", icon="arrow_switch") @Cataloged(group="Transformations")
@Parameter.Annotation(key="number",label="Number of Tuples",type=Type.NUMBER,value="1,32767")
@Documented(title="Random Tuples", category={OPERATIONS,TRANSFORMATIONS}, resource="help/operation/transformation/random_tuples.html")
public class RandomTuples implements Transformation {
	
	@Override public Collection<Tuple> transform(Collection<Tuple> tuples,Object... values) { return transform(tuples,(Integer)values[0]); }
	public Collection<Tuple> transform(Collection<Tuple> tuples, int number) {
		Acid acid = Optional.ofNullable(Tuple.tuplesAcid(tuples)).orElse(Acid.RNA);
		int length = (length=Tuple.tuplesLength(tuples))>0?length:3;
		
		List<Tuple> random = new ArrayList<>(tuples);
		for(int index=0;index<number;index++)
			random.add(RandomHolder.randomTuple(acid,length));
		return random;
	}
	
	/*
	 * The random generator used by this class to create random keys.
	 * In a holder class to defer initialization until needed.
	 */
	private static class RandomHolder {
		static final Random random = new SecureRandom();
		public static Base randomBase(Acid acid) {
			return acid.bases[random.nextInt(acid.bases.length)];
		}
		public static Tuple randomTuple(Acid acid, int length) {
			return new Tuple(Arrays.stream(new Object[length]).map(dummy->randomBase(acid)).toArray(Base[]::new));
		}
	}
}