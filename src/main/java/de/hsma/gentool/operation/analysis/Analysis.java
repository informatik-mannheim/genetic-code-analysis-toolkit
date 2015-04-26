package de.hsma.gentool.operation.analysis;

import java.util.Collection;
import de.hsma.gentool.nucleic.Tuple;
import de.hsma.gentool.operation.Operation;

public interface Analysis extends Operation {
	public Result analyse(Collection<Tuple> tuples, Object... values);
	
	public class Result {
		private Analysis analysis;
		public Result(Analysis analysis) { this.analysis = analysis; }
		public Analysis getAnalysis() { return analysis; }
		@Override public String toString() { return analysis!=null?analysis.getName():null; }
	}
	public class SimpleResult extends Result {
		private String result;
		public SimpleResult(Analysis analysis, String result) { super(analysis); this.result = result; }
		@Override public String toString() { return result; }
	}
	
	public interface Handler {
		public void handle(Result result);
	}
}