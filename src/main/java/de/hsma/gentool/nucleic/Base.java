/*
 * Copyright [2014] [Mannheim University of Applied Sciences]
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package de.hsma.gentool.nucleic;

import static de.hsma.gentool.nucleic.Acid.*;
import java.util.Arrays;

public enum Base {
	ADENINE('A'), GUANINE('G'), CYTOSINE('C'), THYMINE('T'), URACIL('U'), HYPOXANTHINE('H'), XANTHINE('X');
	
	public final char letter;
	
	private Base(char letter) {
		this.letter = letter;
	}
	
	public boolean inDNA() { return Arrays.asList(DNA.bases).contains(this); }
	public boolean inRNA() { return Arrays.asList(RNA.bases).contains(this); }
	
	public static Base valueOf(char letter) {
		switch(letter) {
		case 'A': return ADENINE;
		case 'G': return GUANINE;
		case 'C': return CYTOSINE;
		case 'T': return THYMINE;
		case 'U': return URACIL;
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
}