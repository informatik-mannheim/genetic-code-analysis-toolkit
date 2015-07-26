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

import static de.hsma.gentool.Utilities.*;
import static de.hsma.gentool.nucleic.Tuple.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import de.hsma.gentool.Parameter;
import de.hsma.gentool.Parameter.Type;
import de.hsma.gentool.log.Logger;
import de.hsma.gentool.nucleic.Acid;
import de.hsma.gentool.nucleic.Tuple;
import de.hsma.gentool.operation.Cataloged;
import de.hsma.gentool.operation.Named;

@Named(name="tuple usage", icon="report") @Cataloged(group="Analyses")
@Parameter.Annotation(key="file",label="File",type=Type.FILE)
public class TupleUsage implements Analysis {	
	private static final String DELIMITER = ", ", TIMES = "x ";
	private static final Tuple EMPTY_TUPLE = new Tuple();
	
	@Override public Result analyse(Collection<Tuple> tuples,Object... values) { return analyse(tuples,(File)values[0]); }
	public Result analyse(Collection<Tuple> tuples,File file) {
		Logger logger = getLogger();
		
		if(file==null||!file.exists()) {
			logger.log("Choose an existing file to count tuple usage in.");
			return null;
		}

		Acid acid;
		if((acid=Tuple.tuplesAcid(tuples))==null) {
			logger.log("Tuples with variable acids, can't analyse tuple usage.");
			return null; //tuples not all in same acid
		}
		
		Multiset<Tuple> tupleCount = HashMultiset.create();
		try(BufferedReader reader = new BufferedReader(new FileReader(file))) {
			String line; while((line=reader.readLine())!=null)
				tupleCount.addAll(normalizeTuples(splitTuples(tupleString(line).trim()),acid));
		} catch(IOException e) { logger.log("Error while reading file.",e); return null; }
		
		StringBuilder builder = new StringBuilder();
		for(Tuple tuple:(!tuples.isEmpty()&&!containsOnly(tuples,EMPTY_TUPLE)?
				normalizeTuples(tuples,acid):tupleCount.elementSet()))
			builder.append(DELIMITER).append(tupleCount.count(tuple)).append(TIMES).append(tuple);
		return new SimpleResult(this,builder.length()!=0?builder.substring(DELIMITER.length()).toString():"no tuples");
	}
}