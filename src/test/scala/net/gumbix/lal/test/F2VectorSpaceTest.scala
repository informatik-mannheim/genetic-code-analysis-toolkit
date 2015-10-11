package net.gumbix.lal.test

import org.junit.Test
import junit.framework.Assert._
import net.gumbix.lal.{F2Vector => F, F2VectorSpace => F2}

/**
 * @author Markus Gumbel (m.gumbel@hs-mannheim.de)
 *         (c) 2012 Markus Gumbel
 */
class F2VectorSpaceTest {

  @Test
  def testSubSpace() {
    val c = List(F(0, 0, 0), F(0, 1, 0), F(1, 0, 0), F(1, 1, 0))
    val vs = new F2(3)
    assertEquals(None, vs.subSpaceExceptions(c))
    assertTrue(vs.isSubSpace(c))
    assertTrue(vs.isMovedSubSpace(c))
  }

  @Test
  def testNoSubSpace() {
    val c = List(F(0, 0, 0), F(0, 1, 0), F(1, 0, 0))
    val vs = new F2(3)
    assertEquals(Some(List((F(0, 1, 0), F(1, 0, 0)))), vs.subSpaceExceptions(c))
    assertTrue(!vs.isSubSpace(c))
    assertTrue(!vs.isMovedSubSpace(c))
  }

  @Test
  def testMovedSubSpace() {
    val c = List(F(0, 0, 1), F(0, 1, 1), F(1, 0, 1), F(1, 1, 1))
    val vs = new F2(3)
    assertEquals(Some(Nil), vs.subSpaceExceptions(c))
    assertTrue(!vs.isSubSpace(c))
    assertTrue(vs.isMovedSubSpace(c))
    val l = vs.movedSubSpaceExceptions(c)
    println(l.mkString("\n"))
  }
}
