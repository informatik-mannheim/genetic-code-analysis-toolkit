package de.hsma.gentool.nucleic;

import static de.hsma.gentool.Utilities.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Pattern;
import de.hsma.gentool.Utilities.Characters;

public enum Base {
	ADENINE('A'), GUANINE('G'), CYTOSINE('C'), THYMINE('T'), URACILE('U'), HYPOXANTHINE('H'), XANTHINE('X');
	
	public static final Set<Base>
		DNA_BASES = Collections.unmodifiableSet(new LinkedHashSet<Base>(Arrays.asList(new Base[]{THYMINE,CYTOSINE,ADENINE,GUANINE}))),
		RNA_BASES = Collections.unmodifiableSet(new LinkedHashSet<Base>(Arrays.asList(new Base[]{URACILE,CYTOSINE,ADENINE,GUANINE})));
	
	private static final Pattern PATTERN_NO_BASE;
	static {
		StringBuilder bases = new StringBuilder();
		for(Base base:Base.values())
			bases.append(base.letter);
		PATTERN_NO_BASE = Pattern.compile(bases.insert(0, "[^").append(" ]").toString());
	}
	
	public final char letter;
	
	private Base(char letter) {
		this.letter = letter;
	}
	
	public boolean inDNA() { return DNA_BASES.contains(this); }
	public boolean inRNA() { return RNA_BASES.contains(this); }
	
	public static Base valueOf(char letter) {
		switch(letter) {
		case 'A': return ADENINE;
		case 'G': return GUANINE;
		case 'C': return CYTOSINE;
		case 'T': return THYMINE;
		case 'U': return URACILE;
		case 'H': return HYPOXANTHINE;
		case 'X': return XANTHINE;
		default: throw new IllegalArgumentException("'"+letter+"' is not a valid base."); }
	}
	
	@Override public String toString() { return Character.toString(letter); }
	public static String toString(Base[] bases) {
		char[] letters = new char[bases.length];
		for(int base=0;base<bases.length;base++)
			letters[base] = bases[base].letter;
		return new String(letters);
	}
	public static Base[] parseBase(String string) {
		char[] letters = string.toCharArray();
		Base[] bases = new Base[letters.length];
		for(int letter=0;letter<letters.length;letter++)
			bases[letter] = valueOf(letters[letter]);
		return bases;
	}
	
	public static String baseString(String string) {
		return PATTERN_NO_BASE.matcher(
		  Characters.WHITESPACE.replace(string,SPACE).toUpperCase()
		).replaceAll(EMPTY);
	}
}