package de.hsma.gentool.nucleic;

import static de.hsma.gentool.nucleic.Base.*;

public enum Acid {
	DNA(THYMINE,CYTOSINE,ADENINE,GUANINE),
	RNA(URACILE,CYTOSINE,ADENINE,GUANINE);
	
	public final Base[] bases;
	private Acid(Base... bases) { this.bases = bases; }
}
