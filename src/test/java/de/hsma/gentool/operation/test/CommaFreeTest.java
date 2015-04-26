package de.hsma.gentool.operation.test;

import static org.junit.Assert.*;
import java.util.Collection;
import java.util.Collections;
import org.junit.Test;
import de.hsma.gentool.nucleic.Tuple;

public class CommaFreeTest {
	private static final CommaFree COMMA_FREE = new CommaFree();
	
	@Test public void test() {
		Collection<Tuple> tuples = Collections.emptyList();
		assertTrue(tuples+" is empty",COMMA_FREE.test(tuples,1));
		
		tuples = Tuple.splitTuples("AAA, AAA");
		assertFalse(tuples+" contains duplicate tuples",COMMA_FREE.test(tuples,1));
		
		tuples = Tuple.splitTuples("AAA, AAAA");
		assertFalse(tuples+" contains tuples of variable length",COMMA_FREE.test(tuples,1));
		
		tuples = Tuple.splitTuples("GGC, GCC");
		assertTrue(tuples+" is comma-free",COMMA_FREE.test(tuples));
		
		tuples = Tuple.splitTuples("ATC, TCC, CAA");
		assertFalse(tuples+" is not comma-free",COMMA_FREE.test(tuples));
	}
}
