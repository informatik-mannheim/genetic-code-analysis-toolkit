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
package bio.gcat.nucleic;

import static bio.gcat.Utilities.EMPTY;
import static bio.gcat.Utilities.SPACE;
import static bio.gcat.Utilities.contains;
import static bio.gcat.Utilities.pow;
import static bio.gcat.Utilities.reverse;
import static bio.gcat.Utilities.substitute;
import static bio.gcat.nucleic.Acid.DNA;
import static bio.gcat.nucleic.Acid.RNA;
import static bio.gcat.nucleic.Base.ADENINE;
import static bio.gcat.nucleic.Base.CYTOSINE;
import static bio.gcat.nucleic.Base.GUANINE;
import static bio.gcat.nucleic.Base.THYMINE;
import static bio.gcat.nucleic.Base.URACIL;
import static bio.gcat.nucleic.Compound.START;
import static bio.gcat.nucleic.Compound.STOP;
import static bio.gcat.nucleic.Compound.isStart;
import static bio.gcat.nucleic.Compound.isStop;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableMap;

import bio.gcat.Utilities;
import lc.kra.Characters;

public class Tuple implements Comparable<Tuple> {
	private static final Map<Base,Base> COMPLEMENT_SUBSTITUTION = ImmutableMap.of(
		ADENINE,URACIL, URACIL,ADENINE, THYMINE,ADENINE, GUANINE,CYTOSINE, CYTOSINE,GUANINE);
	private static final Map<Acid,Map<Base,Base>> ACID_SUBSTITUTIONS = ImmutableMap.of(
		RNA,ImmutableMap.of(THYMINE,URACIL), DNA,ImmutableMap.of(URACIL,THYMINE));
	
	private static final Pattern PATTERN_NO_WORD = Pattern.compile("\\W"),PATTERN_NO_BASE;
	static {
		StringBuilder bases = new StringBuilder();
		for(Base base:Base.values())
			bases.append(base.letter);
		PATTERN_NO_BASE = Pattern.compile(bases.insert(0,"[^").append(" ]").toString());
	}
	
	private static final String TUPLE_DELIMITERS = Utilities.WHITESPACE+",;";
	private static final Map<String,Base[]> baseBuffer = new IdentityHashMap<>(); //using an Identity (!) HashMap for the baseBuffer, for all keys placed in the intern() must be called!
	
	private Base[] bases;
	private String string;
	
	public Tuple() { this(EMPTY); }
	public Tuple(String string) { this(Base.parseBase(string)); }
	public Tuple(Base... bases) {
		this.string = Base.toString(bases).intern(); // same as for the arrays below, calling intern reduced heap size and is required to use an IdentityHashMap
		// putting arrays of bases to the heap costs a lot of memory, buffer the arrays up to size 5
		if(bases.length>5||(this.bases=baseBuffer.putIfAbsent(string,bases))==null)
			this.bases = bases;
	}
	
	public Base[] getBases() { return bases; }
	public boolean hasBase(Base base) { return contains(bases,base); }
	
	public Compound getCompound() { return Compound.forTuple(this); }
	
	public int length() { return string.length(); }
	
	public Tuple toAcid(Acid acid) {
		if(RNA.equals(acid)&&contains(bases,THYMINE)||DNA.equals(acid)&&contains(bases,URACIL))
			return new Tuple(substitute(bases,ACID_SUBSTITUTIONS.get(acid)));
		else return this;
	}
	
	public Tuple getComplement() { return getComplement(RNA); }
	public Tuple getComplement(Acid acid) {
		return new Tuple(reverse(substitute(bases,COMPLEMENT_SUBSTITUTION))).toAcid(acid);
	}
	
	@Override public int compareTo(Tuple tuple) {
		int lengthA = bases.length, lengthB = tuple.bases.length;
		for(int limit = Math.min(lengthA,lengthB),index=0;index<limit;index++) {			
			int baseA = bases[index].ordinal(), baseB = tuple.bases[index].ordinal();
			if(baseA!=baseB) return baseA-baseB;
		} return lengthA-lengthB;
	}
	
	@Override public int hashCode() { return string.hashCode(); }
	@Override public boolean equals(Object anObject) {
		if(anObject==this)
			return true;
		if(!(anObject instanceof Tuple))
			return false;
		return string.equals(((Tuple)anObject).string);
	}
	
	@Override public String toString() { return toString(false); }
	public String toString(boolean appendCompound) {
		if(appendCompound) {
			StringBuilder builder = new StringBuilder(toString()).append(SPACE).append('(');
			Compound compound = getCompound();
			if(compound==null) {
				if(isStart(this)) builder.append(START);
				else if(isStop(this)) builder.append(STOP);
				else builder.append("Unknown");
			} else builder.append(compound);
			return builder.append(')').toString();
		} return string;
	}
	
	public static String tupleString(String string) {
		string = Characters.NEW_LINE.replace(string,EMPTY).toUpperCase();
		string = PATTERN_NO_WORD.matcher(string).replaceAll(SPACE);
		string = PATTERN_NO_BASE.matcher(string).replaceAll(EMPTY);
		return Characters.WHITESPACE.condense(string);
	}
	
	public static Acid tupleAcid(Tuple tuple) {
		boolean thymine = tuple.hasBase(THYMINE);
		if(thymine&&tuple.hasBase(URACIL))
		 	return null; //contains both acids!
		else if(thymine) return DNA;
		else return RNA;
	}
	public static Acid tuplesAcid(Collection<Tuple> tuples) {
		Acid acid = null;
		for(Tuple tuple:tuples) {
			if(tuple.hasBase(THYMINE))
				if(acid==null) acid = DNA;
				else if(!DNA.equals(acid))
					return null; //contains both acids!
			if(tuple.hasBase(URACIL))
				if(acid==null) acid = RNA;
				else if(!RNA.equals(acid))
					return null; //contains both acids!
		} return acid!=null?acid:RNA;
	}
	
	public static int tuplesLength(String string) {
		if(string.isEmpty()) return 0;
		StringTokenizer tuples = new StringTokenizer(string,TUPLE_DELIMITERS);
		String firstTuple = tuples.hasMoreTokens()?tuples.nextToken():null;
		int length = firstTuple!=null?firstTuple.length():0;
		if(!tuples.hasMoreTokens()&&length>5)
			return 3; //special case where all tuples are joined without any separation, assume codons
		while(tuples.hasMoreTokens()) if(tuples.nextToken().length()!=length)
			return 0;
		return length;
	}
	public static int tuplesLength(Collection<Tuple> tuples) {
		if(tuples.isEmpty()) return 0;
		Tuple firstTuple = tuples instanceof List?((List<Tuple>)tuples).get(0):tuples.iterator().next();
		int length = firstTuple!=null?firstTuple.length():0;
		if(tuples.size()==1&&length>5)
			return 3; //special case where all tuples are joined without any separation, assume codons
		for(Tuple tuple:tuples) if(tuple.length()!=length)
			return 0;
		return length;
	}
	
	public static List<Tuple> trimTuples(Collection<Tuple> tuples) {
		LinkedList<Tuple> trimmedTuples = new LinkedList<>(tuples);
		if(tuples.size()>0&&trimmedTuples.getFirst().length()==0) trimmedTuples.removeFirst();
		if(tuples.size()>1&&trimmedTuples.getLast().length()==0) trimmedTuples.removeLast();
		return trimmedTuples;
	}
	public static List<Tuple> condenseTuples(Collection<Tuple> tuples) {
		List<Tuple> condensedTuples = new ArrayList<>(tuples);
		condensedTuples.removeIf(tuple->tuple.length()==0);
		return condensedTuples;
	}
	
	public static List<Tuple> normalizeTuples(Collection<Tuple> tuples) { return normalizeTuples(tuples,RNA); }
	public static List<Tuple> normalizeTuples(Collection<Tuple> tuples, Acid acid) {
		List<Tuple> uniformTuples = new ArrayList<>(tuples);
		ListIterator<Tuple> tuple = uniformTuples.listIterator();
		while(tuple.hasNext()) tuple.set(tuple.next().toAcid(acid));
		return uniformTuples;
	}
	
	public static List<Tuple> splitTuples(String string) {
		List<Tuple> tuples = new ArrayList<Tuple>();
		StringTokenizer strings = new StringTokenizer(string,TUPLE_DELIMITERS);
		while(strings.hasMoreTokens())
			try { tuples.add(new Tuple(strings.nextToken())); }
			catch(IllegalArgumentException e) { tuples.add(null); }
		return tuples;
	}

	public static List<Tuple> sliceTuples(String string) {
		int tupleLength = tuplesLength(string);
		return tupleLength!=0?sliceTuples(string,tupleLength):
			splitTuples(string);
	}
	public static List<Tuple> sliceTuples(String string, int length) {
		try(Reader reader = new StringReader(string=Characters.WHITESPACE.replace(string,EMPTY))) {
			int read; char[] buffer = new char[length];
			List<Tuple> tuples = new ArrayList<>((string.length()/length)+1);
			while((read=reader.read(buffer))!=-1)
				tuples.add(new Tuple(new String(buffer,0,read)));
			return Collections.unmodifiableList(tuples);
		} catch (IOException e) { /* will not happen on StringReader */
			throw new IndexOutOfBoundsException(e.getMessage()); }
	}
	
	public static String joinTuples(Collection<Tuple> tuples) { return joinTuples(tuples, SPACE); }
	public static String joinTuples(Collection<Tuple> tuples, String glue) { return joinTuples(tuples, glue, false); }
	public static String joinTuples(Collection<Tuple> tuples, boolean appendCompounds) { return joinTuples(tuples, SPACE, appendCompounds); } 
	public static String joinTuples(Collection<Tuple> tuples, String glue, boolean appendCompounds) { return joinTuples(tuples.stream(), glue, appendCompounds); }
	public static String joinTuples(Stream<Tuple> tuples, String glue, boolean appendCompounds) {
		return tuples!=null?tuples.map(tuple->tuple.toString(appendCompounds))
			.collect(Collectors.joining(glue)):EMPTY;
	}
	
	public static List<Tuple> allTuples(int length) { return allTuples(RNA, length); }
	public static List<Tuple> allTuples(Acid acid) { return allTuples(acid, 3); }
	public static List<Tuple> allTuples(Acid acid, int length) {
		if(length==0) return Collections.emptyList();
		List<Tuple> tuples = new ArrayList<>(pow(length,acid.bases.length));
		allTuples(tuples, new Tuple(), acid.bases, length);
		return tuples;
	}
	protected static void allTuples(Collection<Tuple> tuples, Tuple tuple, Base[] bases, int length) {
		if(tuple.string.length()<length)
			for(Base base:bases)
				allTuples(tuples,new Tuple(tuple.string+base.letter),bases,length);
		else tuples.add(tuple);
	}
}