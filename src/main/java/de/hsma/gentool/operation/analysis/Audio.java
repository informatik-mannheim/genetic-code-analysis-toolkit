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
package de.hsma.gentool.operation.analysis;

import static de.hsma.gentool.nucleic.Base.*;
import java.util.Collection;
import java.util.Map;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import com.google.common.collect.ImmutableMap;
import de.hsma.gentool.Documented;
import de.hsma.gentool.log.Logger;
import de.hsma.gentool.nucleic.Base;
import de.hsma.gentool.nucleic.Tuple;
import de.hsma.gentool.operation.Cataloged;
import de.hsma.gentool.operation.Named;
import static de.hsma.gentool.Help.*;

@Named(name="audio", icon="sound") @Cataloged(group="Analyses")
@Documented(title="Audio", category={OPERATIONS,ANALYSES}, resource="help/operation/analysis/audio.html")
public class Audio implements Analysis {
	private static Map<Base,Note> notes = ImmutableMap.of(
		ADENINE,Note.C4,
		CYTOSINE,Note.D4,
		GUANINE,Note.E4,
		URACIL,Note.F4,
		THYMINE,Note.F4);
	
	@Override public Result analyse(Collection<Tuple> tuples,Object... values) {
		Logger logger = getLogger();
		
		try {
			AudioFormat format = new AudioFormat(Note.SAMPLE_RATE,8,1,true,true);
			SourceDataLine line = AudioSystem.getSourceDataLine(format);
			line.open(format, Note.SAMPLE_RATE); line.start();
			
			for(Tuple tuple:tuples) for(Base base:tuple.getBases())
				play(line,notes.getOrDefault(base,Note.B4),450);
			
			line.drain(); line.close();
			return new Result(this); //neutral result
		} catch(LineUnavailableException e) {
			logger.log("Can't open audio line", e);
			return null;
		}
	}
	
	private static int play(SourceDataLine line, Note note, int ms) throws LineUnavailableException {
		return line.write(note.data(), 0, Note.SAMPLE_RATE*Math.min(ms,Note.SECONDS*1000)/1000);
	}

	private static enum Note {
		REST, A4, A4$, B4, C4, C4$, D4, D4$, E4, F4, F4$, G4, G4$, A5;
		public static final int SAMPLE_RATE = 16 * 1024, /* ~16KHz */ SECONDS = 2; 
		private byte[] sin = new byte[SECONDS*SAMPLE_RATE];
		private Note() {
			int n; if((n=this.ordinal())>0) {
				double exp = ((double)n-1)/ 12d, f = 330d * Math.pow(2d,exp);
				for(int i = 0;i<sin.length;i++)
					sin[i] = (byte)(Math.sin(2.0*Math.PI*i/((double)SAMPLE_RATE/f))*127f);
			}
		}

		public byte[] data() { return sin; }
	}
}