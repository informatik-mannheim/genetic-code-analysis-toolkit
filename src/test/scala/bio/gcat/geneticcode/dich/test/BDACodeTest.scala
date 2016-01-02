package bio.gcat.geneticcode.dich.test

import bio.gcat.geneticcode.dich.ct.{MinMax, CodingClassTable}
import bio.gcat.geneticcode.dich.{Codon, RumerBDA}
import org.junit.Test
import junit.framework.Assert._
import bio.gcat.geneticcode.dich.CodonImplicitDefs._

/**
 * @author Markus Gumbel (m.gumbel@hs-mannheim.de)
 *         (c) 2012 Markus Gumbel
 */
class BDACodeTest {

  @Test
  def testOne() {
    val ct = new CodingClassTable(RumerBDA :: Nil)
    assertEquals(2, ct.classes.size)
    assertEquals(1, ct.bdas.size)
    assertEquals(MinMax(0, 1), ct.hammingDistMinMax)
    assertEquals(0, ct.hammingCDist(Codon("AAA"), Codon("UUU")))
    assertEquals(1, ct.hammingCDist(Codon("AAA"), Codon("GGG")))
  }

  @Test
  def testHamming() {
    val ct = new CodingClassTable(RumerBDA :: Nil)
    assertEquals(0, ct.hammingLDist(List(1, 1, 1), List(1, 1, 1)))
    assertEquals(1, ct.hammingLDist(List(1, 1, 1), List(1, 1, 0)))
    assertEquals(3, ct.hammingLDist(List(1, 1, 1), List(0, 0, 0)))
    assertEquals(0, ct.hammingLDist(List(), List()))
    assertEquals(1, ct.hammingLDist(List(1), List(0)))
  }

  @Test(expected = classOf[java.lang.IllegalArgumentException])
  def testHammingDiffSize() {
    val ct = new CodingClassTable(RumerBDA :: Nil)
    assertEquals(0, ct.hammingLDist(List(1, 1, 1), List(1, 1)))
  }
}
