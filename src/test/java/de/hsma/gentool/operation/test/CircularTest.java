package de.hsma.gentool.operation.test;

import static org.junit.Assert.*;
import java.util.Collection;
import java.util.Collections;
import org.junit.Test;
import de.hsma.gentool.nucleic.Tuple;

public class CircularTest {
	private static final Circular CIRCULAR = new Circular();
	
	@Test public void test() {
		Collection<Tuple> tuples = Collections.emptyList();
		assertTrue(tuples+" is empty",CIRCULAR.test(tuples,1));
		
		tuples = Tuple.splitTuples("AAA");
		assertFalse(tuples+" contains one of AAA, GGG, etc.",CIRCULAR.test(tuples,1));
		
		tuples = Tuple.splitTuples("AAA, AAA");
		assertFalse(tuples+" contains duplicate tuples",CIRCULAR.test(tuples,1));
		
		tuples = Tuple.splitTuples("AAA, AAAA");
		assertFalse(tuples+" contains tuples of variable length",CIRCULAR.test(tuples,1));
		
		tuples = Tuple.splitTuples("TGG, GTG");
		assertTrue(tuples+" is 0-circular",CIRCULAR.test(tuples,0));
		assertFalse(tuples+" isn't 1-circular",CIRCULAR.test(tuples,1));
		assertFalse(tuples+" isn't 2-circular",CIRCULAR.test(tuples,2));
		
		tuples = Tuple.splitTuples("TGG, CTG, GGC, TGT");
		assertTrue(tuples+" is 1-circular",CIRCULAR.test(tuples,1));
		assertFalse(tuples+" isn't 2-circular",CIRCULAR.test(tuples,2));
		
		tuples = Tuple.splitTuples("ACG, GTA, CGT, CGG, TAC");
		assertTrue(tuples+" is 2-circular",CIRCULAR.test(tuples,2));
		assertFalse(tuples+" isn't 3-circular",CIRCULAR.test(tuples,3));
		
		tuples = Tuple.splitTuples("CGT, ACG, TAC, GTA");
		assertTrue(tuples+" is 3-circular",CIRCULAR.test(tuples,3));
		assertFalse(tuples+" isn't 4-circular",CIRCULAR.test(tuples,4));
	}
}
