package bio.gcat.operation.analysis;

import static org.junit.Assert.assertEquals;

import java.util.Collections;

import org.junit.Test;

public class CommentTest {
	private static final Comment COMMENT = new Comment();
	
	@Test public void test() throws Exception {		
		assertEquals("Test", COMMENT.analyse(Collections.emptyList(), "Test").toString());
	}
}
