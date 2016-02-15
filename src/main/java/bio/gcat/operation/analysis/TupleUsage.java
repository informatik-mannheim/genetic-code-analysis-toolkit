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
package bio.gcat.operation.analysis;

import static bio.gcat.Help.ANALYSES;
import static bio.gcat.Help.OPERATIONS;
import static bio.gcat.Utilities.containsOnly;
import static bio.gcat.nucleic.Tuple.normalizeTuples;
import static bio.gcat.nucleic.Tuple.splitTuples;
import static bio.gcat.nucleic.Tuple.tupleString;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import bio.gcat.Documented;
import bio.gcat.Parameter;
import bio.gcat.Parameter.Type;
import bio.gcat.log.Logger;
import bio.gcat.nucleic.Acid;
import bio.gcat.nucleic.Tuple;
import bio.gcat.operation.Cataloged;
import bio.gcat.operation.Named;

@Named(name="tuple usage", icon="report") @Cataloged(group="Analyse Sequence")
@Parameter.Annotation(key="file",label="File",type=Type.FILE)
@Documented(title="Tuple Usage", category={OPERATIONS,ANALYSES}, resource="help/operation/analysis/tuple_usage.html")
public class TupleUsage implements Analysis {	
	private static final String DELIMITER = ", ", TIMES = "x ";
	private static final Tuple EMPTY_TUPLE = new Tuple();
	
	@Override public Result analyse(Collection<Tuple> tuples,Object... values) {
		Logger logger = getLogger();
		
		if(values[0]==null) {
			logger.log("Choose an existing file to count tuple usage in.");
			return null;
		}

		Acid acid;
		if((acid=Tuple.tuplesAcid(tuples))==null) {
			logger.log("Tuples with variable acids, can't analyse tuple usage.");
			return null; //tuples not all in same acid
		}
		
		Multiset<Tuple> tupleCount = HashMultiset.create();
		try(BufferedReader reader = new BufferedReader(new InputStreamReader((InputStream)values[0]))) {
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