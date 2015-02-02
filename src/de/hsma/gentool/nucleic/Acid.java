package de.hsma.gentool.nucleic;

import java.util.Set;
import static de.hsma.gentool.nucleic.Base.*;

public enum Acid {
	DNA, RNA;
	
	public Set<Base> getBases() {
		switch(this) {
		case DNA: return DNA_BASES;
		case RNA: return RNA_BASES;
		default: return null; }
	}
}
