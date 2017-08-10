package bio.gcat.gui.helper;

import static bio.gcat.Utilities.SPACE;

import java.util.Collection;

import bio.gcat.nucleic.Tuple;

public class TuplesHelper {
	public static String joinTuples(Collection<Tuple> tuples) { return joinTuples(tuples, 100); }
	public static String joinTuples(Collection<Tuple> tuples, int maxTuples) {
		return tuples.size()>100?Tuple.joinTuples(tuples.stream().limit(100), SPACE, false)+" ...":
			Tuple.joinTuples(tuples);
	}
	
	public static String alignTuples(String tuples, int length) {
		for(String space=SPACE;space.length()<length;)
			tuples = tuples.replaceAll("(?<=(?:\\s|^)\\S{"+(length-space.length())+"})\\s", space+=SPACE);
		return tuples;
	}
}