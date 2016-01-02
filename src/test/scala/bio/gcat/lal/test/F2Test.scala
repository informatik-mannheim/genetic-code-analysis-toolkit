package bio.gcat.lal.test

import bio.gcat.lal.F2Vector
import junit.framework.Assert._
import org.junit.Test

/**
 * @author Markus Gumbel (m.gumbel@hs-mannheim.de)
 *         (c) 2012 Markus Gumbel
 */
class F2Test {

  @Test(expected = classOf[java.lang.IllegalArgumentException])
  def testDifferentSize() {
    val x1 = F2Vector(0)
    val x2 = F2Vector(0, 1)
    val s = x1 + x2
  }

  @Test
  def test1011() {
    val x1 = F2Vector(1, 0)
    val x2 = F2Vector(1, 1)
    val s = x1 + x2
    assertEquals(F2Vector(0, 1), s)
  }

  @Test
  def test0000() {
    val x1 = F2Vector(0, 0)
    val x2 = F2Vector(0, 0)
    val s = x1 + x2
    assertEquals(F2Vector(0, 0), s)
  }

  @Test
  def test1111() {
    val x1 = F2Vector(1, 1)
    val x2 = F2Vector(1, 1)
    val s = x1 + x2
    assertEquals(F2Vector(0, 0), s)
  }
}
