package de.hsma.gentool.nucleic;

public class Triplet extends Tuple {
	public Triplet(String string) {
		super(string);
		if(string.length()!=3)
			throw new IllegalArgumentException("A base triplet must consist of 3 nuclebases.");
	}
	public Triplet(Base[] bases) {
		super(bases);
		if(bases.length!=3)
			throw new IllegalArgumentException("A base triplet must consist of 3 nuclebases.");
	}
}
