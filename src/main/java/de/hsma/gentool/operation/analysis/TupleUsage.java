package de.hsma.gentool.operation.analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import de.hsma.gentool.Parameter;
import de.hsma.gentool.Parameter.Type;
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
	
	@Override public Result analyse(Collection<Tuple> tuples,Object... values) { return analyse(tuples,(File)values[0]); }
	public Result analyse(Collection<Tuple> tuples,File file) {
		if(file==null||!file.exists())
			return new SimpleResult(this,"Please choose an existing file to count tuple usage in.");
		
		Multiset<Tuple> tupleCount = HashMultiset.create();
		try(BufferedReader reader = new BufferedReader(new FileReader(file))) {
			String line; while((line=reader.readLine())!=null)
				tupleCount.addAll(Tuple.splitTuples(Tuple.tupleString(line).trim()));
		} catch(IOException e) { return new SimpleResult(this,"Error while reading file."); }
		
		StringBuilder builder = new StringBuilder();
		for(Tuple tuple:tuples) builder.append(DELIMITER).append(tupleCount.count(tuple)).append(TIMES).append(tuple);
		return new SimpleResult(this,builder.substring(DELIMITER.length()).toString());
	}
}