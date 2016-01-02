package bio.gcat.util.test

import bio.gcat.util.Combine._
import junit.framework.Assert._
import org.junit.Test

/**
 * @author Markus Gumbel (m.gumbel@hs-mannheim.de)
 *         (c) 2012 Markus Gumbel
 */
class CombineTest {
  @Test
  def test123() {
    val c = combine(List(1, 2, 3))
    assertEquals(List((1,2), (1,3), (2,3)), c)
  }

  @Test
  def testEmpty() {
    val c = combine(List())
    assertEquals(List(), c)
  }

  @Test
  def test1() {
    val c = combine(List(1))
    assertEquals(List(), c)
  }
}
