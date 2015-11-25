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
package bio.gcat.gui.editor;

import static bio.gcat.Utilities.*;
import static bio.gcat.nucleic.Acid.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import bio.gcat.Utilities.Characters;
import bio.gcat.nucleic.Acid;
import bio.gcat.nucleic.Base;
import bio.gcat.nucleic.Tuple;

public class NucleicDocument extends DefaultStyledDocument {
	private static final long serialVersionUID = 1l;

	private static Pattern patternTupleLength;
	
	private int tupleLength;
	private Acid defaultAcid = RNA;
	
	@Override public void insertString(int offset, String text, AttributeSet attributes) throws BadLocationException {
		// only allow input of bases & with maximum one whitespace
		if((text=Tuple.tupleString(text)).isEmpty())
			return;
		
		// simplify input if only DNA or RNA input is chosen
		     if(DNA.equals(defaultAcid)) text = text.replace(Base.URACIL.letter, Base.THYMINE.letter);
		else if(RNA.equals(defaultAcid)) text = text.replace(Base.THYMINE.letter, Base.URACIL.letter);
		
		// determine position and white spaces in document / text
		boolean startOfText = offset==0, endOfText = offset==getLength(), nearEndOfText = endOfText||offset+1==getLength(),
			afterSpace = !startOfText&&SPACE.equals(getText(offset-1,1)), beforeSpace = !endOfText&&SPACE.equalsIgnoreCase(getText(offset,1)), beforeTwoSpace = beforeSpace&&!nearEndOfText&&SPACE.equalsIgnoreCase(getText(offset+1,1)),
			containsSpace = text.contains(SPACE), startsWithSpace = text.startsWith(SPACE), endsWithSpace = text.endsWith(SPACE);

		// white space handling
		if((startOfText||afterSpace)&&startsWithSpace)
			if((text = Characters.SPACE.trim(text,Characters.Trim.LEFT)).isEmpty())
				return;
			else startsWithSpace = false;
		if(((startOfText&&endOfText&&startsWithSpace)||beforeTwoSpace)&&endsWithSpace)
			if((text = Characters.SPACE.trim(text,Characters.Trim.RIGHT)).isEmpty())
				return;
			else endsWithSpace = false;
		
		// tuple length input (di-nucleoide, codon, ...)
		if(tupleLength>0) {
			StringBuilder builder = new StringBuilder();
			
			if(text.length()!=1) {
				Matcher matcher = patternTupleLength.matcher(text);
				while(matcher.find()) {
					if(builder.length()!=0)
						builder.append(SPACE);
					builder.append(matcher.group(1));
				}
				if(startsWithSpace) builder.insert(0,SPACE);
				if(endsWithSpace) builder.append(SPACE);
			} else builder.append(text);
			
			if(endOfText&&!endsWithSpace) {
				int currentTupleLength = builder.length()-builder.lastIndexOf(SPACE)-1;
				if(!containsSpace)
					for(int lookbackOffset=offset-1; lookbackOffset>=0; lookbackOffset--)
						if(!SPACE.equals(getText(lookbackOffset, 1)))
							currentTupleLength++;
						else break;
				if(currentTupleLength%tupleLength==0)
					builder.append(SPACE);
			}
			
			text = builder.toString();
		}
		
		// this document works transparently with tabs instead of spaces
		super.insertString(offset,text,attributes);
	}
	
	public int getTupleLength() { return tupleLength; }
	public void setTupleLength(int tupleLength) {
		if(tupleLength>0)
			patternTupleLength = Pattern.compile(" ?(\\S{1,"+(this.tupleLength=tupleLength)+"}) *");
		else { this.tupleLength = 0; patternTupleLength = null; }
	}
	
	public Acid getDefaultAcid() { return defaultAcid; }
	public void setDefaultAcid(Acid defaultAcid) {
		this.defaultAcid = defaultAcid;
	}
	
	public void adaptText() {
		try { replace(0,getLength(),getText(0,getLength()).replace(SPACE,EMPTY),null); }
		catch(BadLocationException e) { /* nothing to do here */ }
	}
}