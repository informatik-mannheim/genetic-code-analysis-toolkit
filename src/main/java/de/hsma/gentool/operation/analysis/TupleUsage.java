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

@Named(name="tuple usage") @Cataloged(group="Analyses")
public class TupleUsage implements Analysis {
	private static final Parameter[] PARAMETERS = new Parameter[] {
		new Parameter("file", "File", Type.FILE),
	};
	public static Parameter[] getParameters() { return PARAMETERS; }
	
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