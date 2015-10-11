package de.hsma.gentool.nucleic.helper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import de.hsma.gentool.nucleic.Tuple;

/**
 * @author Markus Gumbel (m.gumbel@hs-mannheim.de)
 */
public class SequenceUtilities {

	public static List<Pair> splitByRuns(List<Tuple> tuples,
			RunDiscriminator f, RunClassifier c) {
		if (tuples.isEmpty()) {
			return new ArrayList<Pair>();
		}
		List<Pair> nl = new ArrayList<>();
		Iterator<Tuple> it = tuples.iterator();
		Tuple prevTuple = it.next(); // State of first element.
		Object clazz = c.classify(prevTuple);
		List<Tuple> l = new ArrayList<>();
		Pair p = new Pair(clazz, l);
		l.add(prevTuple); // could be asList() ?!
		nl.add(p);
		while (it.hasNext()) {
			Tuple tuple = it.next();
			if (f.sameRun(prevTuple, tuple)) {
				l.add(tuple);
			} else {
				// New run:
					clazz = c.classify(tuple);
					l = new ArrayList<>();
					l.add(tuple);
					p = new Pair(clazz, l);
					nl.add(p);
			}
			prevTuple = tuple;
		}

		return nl;
	}

	@FunctionalInterface
	public static interface RunDiscriminator {
		boolean sameRun(Tuple prevTuple, Tuple tuple);
	}

	@FunctionalInterface
	public static interface RunClassifier {
		Object classify(Tuple tuple);
	}

	public static class Pair {
		public Pair(Object id, List<Tuple> list) {
			this.id = id;
			this.list = list;
		}

		public Object id;
		public List<Tuple> list;

		@Override
		public boolean equals(Object o) {
			if (o == null || !(o instanceof Pair)) {
				return false;
			} else {
				Pair p2 = (Pair) o;
				return id == p2.id && list == p2.list;
			}
		}

		public String toString() {
			return id.toString() + ": " + list.toString();
		}
	}
}
