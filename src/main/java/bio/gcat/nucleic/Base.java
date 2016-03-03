/*
 * Copyright [2016] [Mannheim University of Applied Sciences]
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
package bio.gcat.nucleic;

import static bio.gcat.nucleic.Acid.DNA;
import static bio.gcat.nucleic.Acid.RNA;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public enum Base {
	// IUPAC nucleotide code (http://www.bioinformatics.org/sms/iupac.html)
	ADENINE('A'), GUANINE('G'), CYTOSINE('C'), THYMINE('T'), URACIL('U'),
	
	ADENINE_OR_GUANINE('R',new Base[]{ADENINE,GUANINE}),
	CYTOSINE_OR_THYMINE('Y',new Base[]{CYTOSINE,THYMINE,URACIL}),
	GUANINE_OR_CYTOSINE('S',new Base[]{GUANINE,CYTOSINE}),
	ADENINE_OR_THYMINE('W',new Base[]{ADENINE,THYMINE,URACIL}),
	GUANINE_OR_THYMINE('K',new Base[]{GUANINE,THYMINE,URACIL}),
	ADENINE_OR_CYTOSINE('M',new Base[]{ADENINE,CYTOSINE}),
	
	CYTOSINE_GUANINE_OR_THYMINE('B',new Base[]{CYTOSINE,GUANINE,THYMINE,URACIL}),
	ADENINE_GUANINE_OR_THYMINE('D',new Base[]{ADENINE,GUANINE,THYMINE,URACIL}),
	ADENINE_CYTOSINE_OR_THYMINE('H',new Base[]{ADENINE,CYTOSINE,THYMINE,URACIL}),
	ADENINE_CYTOSINE_OR_GUANINE('V',new Base[]{ADENINE,CYTOSINE,GUANINE}),
	
	ANY('N',new Base[]{ADENINE,GUANINE,CYTOSINE,THYMINE,URACIL});
	
	private static final Map<Character,Base> letterMap = new HashMap<>();
	static {
		// it's very slow to call tuple.toUpperCase, therefore putting both lower and upper case char's to the letterMap, so we can spare this from the Tuple implementation
		Arrays.stream(values()).forEach(base->{
			letterMap.put(Character.toUpperCase(base.letter),base);
			letterMap.put(Character.toLowerCase(base.letter),base);
		});
	}
	
	public final char letter;
	public final Base[] compound;
	
	private Base(char letter) { this(letter,null); }
	private Base(char letter, Base[] compound) {
		this.letter = letter; this.compound = compound; }
	
	public boolean isCompound() { return compound!=null&&compound.length!=0; }
	
	public boolean inDNA() { return Arrays.asList(DNA.bases).contains(this); }
	public boolean inRNA() { return Arrays.asList(RNA.bases).contains(this); }
	
	public static Base valueOf(char letter) {
		return Optional.ofNullable(letterMap.get(letter)).orElseThrow(()->
			new IllegalArgumentException("'"+letter+"' is not a valid base."));
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