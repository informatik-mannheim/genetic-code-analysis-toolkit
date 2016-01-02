package bio.gcat.geneticcode.dich.test

import bio.gcat.geneticcode.dich.scan.MinBioBDASet
import bio.gcat.util.PowerSet
import org.junit.Test
import junit.framework.Assert._
import bio.gcat.geneticcode.dich.CodonImplicitDefs._

/**
 * @author Markus Gumbel (m.gumbel@hs-mannheim.de)
 *         (c) 2012 Markus Gumbel
 */
class MiniBioBDASetTest {

  object powerset extends PowerSet

  @Test
  def testThree0() {
    val x = powerset.powerSetIndices(0, 3, 0)
    assertEquals(List(List()), x)
  }

  @Test
  def testThree1() {
    val x = powerset.powerSetIndices(0, 2, 1)
    assertEquals(List(List(), List(0), List(1), List(2)), x)
  }

  @Test
  def testThree2() {
    val x = powerset.powerSetIndices(0, 2, 2)
    assertEquals(List(List(), List(0), List(0, 1), List(0, 2), List(1), List(1, 2), List(2)), x)
  }

  @Test
  def testThree3() {
    val x = powerset.powerSetIndices(0, 2, 3)
    assertEquals(List(List(), List(0), List(0, 1), List(0, 1, 2), List(0, 2),
      List(1), List(1, 2), List(2)), x)
  }

  @Test
  def testLeaveOutSize1() {
    val x = MinBioBDASet.shortenedBDAList(1)
    println(x.mkString("\n"))
    assertEquals(24, x.size)
  }
}
