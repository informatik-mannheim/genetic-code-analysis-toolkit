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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public enum Compound {
	START('\u25B6', "Start", null, new String[]{"AUG", "ATG"}),
	STOP('\u25CF', "Stop", null, new String[]{"UAA", "TAA", "UGA", "TGA", "UAG", "TAG"}),
	
	// IUPAC amino acid code (http://www.bioinformatics.org/sms/iupac.html)
	ALANINE('A', "Ala", Property.NONPOLAR, new String[]{"GCU", "GCT", "GCC", "GCA", "GCG"}),
	CYSTEINE('C', "Cys", Property.POLAR, new String[]{"UGU", "TGT", "UGC", "TGC"}),
	ASPARTIC_ACID('D', "Asp", Property.ACIDIC, new String[]{"GAU", "GAT", "GAC"}),
	GLUTAMIC_ACID('E', "Glu", Property.ACIDIC, new String[]{"GAA", "GAG"}),
	PHENYLALANINE('F', "Phe", Property.NONPOLAR, new String[]{"UUU", "TTT", "UUC", "TTC"}),
	GLYCINE('G', "Gly", Property.NONPOLAR, new String[]{"GGU", "GGT", "GGC", "GGA", "GGG"}),
	HISTIDINE('H', "His", Property.BASIC, new String[]{"CAU", "CAT", "CAC"}),
	ISOLEUCINE('I', "Ile", Property.NONPOLAR, new String[]{"AUU", "ATT", "AUC", "ATC", "AUA", "ATA"}),
	LYSINE('K', "Lys", Property.BASIC, new String[]{"AAA", "AAG"}),
	LEUCINE('L', "Leu", Property.NONPOLAR, new String[]{"UUA", "TTA", "UUG", "TTG", "CUU", "CTT", "CUC", "CTC", "CUA", "CTA", "CUG", "CTG"}),
	METHIONINE('M', "Met", Property.NONPOLAR, new String[]{"AUG", "ATG"}),
	ASPARAGINE('N', "Asn", Property.POLAR, new String[]{"AAU", "AAT", "AAC"}),
	PROLINE('P', "Pro", Property.NONPOLAR, new String[]{"CCU", "CCT", "CCC", "CCA", "CCG"}),
	GLUTAMINE('Q', "Gln", Property.POLAR, new String[]{"CAA", "CAG"}),
	ARGININE('R', "Arg", Property.BASIC, new String[]{"CGU", "CGT", "CGC", "CGA", "CGG", "AGA", "AGG"}),
	SERINE('S', "Ser", Property.POLAR, new String[]{"UCU", "TCT", "UCC", "TCC", "UCA", "TCA", "UCG", "TCG", "AGU", "AGT", "AGC"}),
	THREONINE('T', "Thr", Property.POLAR, new String[]{"ACU", "ACT", "ACC", "ACA", "ACG"}),
	VALINE('V', "Val", Property.NONPOLAR, new String[]{"GUU", "GTT", "GUC", "GTC", "GUA", "GTA", "GUG", "GTG"}),
	TRYPTOPHAN('W', "Trp", Property.NONPOLAR, new String[]{"UGG", "TGG"}),
	TYROSINE('Y', "Tyr", Property.POLAR, new String[]{"UAU", "TAT", "UAC", "TAC"}),
	
	TESSERA('4', "Tsr", Property.TESSERA, new String[]{"AAAA", "AAUU", "AAGG", "AACC", "UUUU", "UUAA", "UUCC", "UUGG", "CCCC", "CCGG", "CCUU", "CCAA", "GGGG", "GGCC", "GGAA", "GGUU", "AUUA", "AUAU", "AUCG", "AUGC", "UAAU", "UAUA", "UAGC", "UACG", "CGGC", "CGCG", "CGAU", "CGUA", "GCCG", "GCGC", "GCUA", "GCAU", "AGGA", "AGCU", "AGAG", "AGUC", "UCCU", "UCGA", "UCUC", "UCAG", "CUUC", "CUAG", "CUCU", "CUGA", "GAAG", "GAUC", "GAGA", "GACU", "ACCA", "ACGU", "ACUG", "ACAC", "UGGU", "UGCA", "UGAC", "UGUG", "CAAC", "CAUG", "CAGU", "CACA", "GUUG", "GUAC", "GUCA", "GUGU"}),
	UNKNOWN('?', "Unknown", null, (Collection<Tuple>)null);
	
	public enum Property {
		NONPOLAR, POLAR, BASIC, ACIDIC, TESSERA;
	}
	
	public final char letter;
	public final String abbreviation;
	public final Property property;
	public final Set<Tuple> tuples;
	
	private static Map<Tuple,Compound> tupleCompound;
	static {
		tupleCompound = new HashMap<Tuple,Compound>();
		for(Compound compound:Compound.values())
			if(!(compound.equals(START)||compound.equals(STOP)||compound.equals(UNKNOWN)))
				for(Tuple tuple:compound.tuples)
					tupleCompound.put(tuple,compound);
	}
	
	private Compound(char letter, String abbreviation, Property property, String[] tuples) {
		this(letter, abbreviation, property, Arrays.stream(tuples).map(tuple->new Tuple(tuple)).collect(Collectors.toList()));
	}
	private Compound(char letter, String abbreviation, Property property, Collection<Tuple> tuples) {
		this.letter = letter; this.abbreviation = abbreviation; this.property = property;
		this.tuples = tuples!=null?Collections.unmodifiableSet(new HashSet<Tuple>(tuples)):null;
	}
	
	public static boolean isStart(Tuple tuple) { return START.tuples.contains(tuple); }
	public static boolean isStop(Tuple tuple) { return STOP.tuples.contains(tuple); }
	public static Compound forTuple(Tuple tuple) {
		return tupleCompound.get(tuple);
	}
	
	@Override public String toString() {
		return abbreviation;
	}
}