package net.gumbix.geneticcode.dich.test

import net.gumbix.geneticcode.dich.NonPowerBinString._
import net.gumbix.geneticcode.dich.{IUPAC, NonPowerBinString, Codon}
import net.gumbix.geneticcode.dich.CodonImplicitDefs._
import junit.framework.Assert._
import org.junit.Test

/**
 * @author Markus Gumbel (m.gumbel@hs-mannheim.de)
 *         (c) 2012 Markus Gumbel
 */
class NonPowerBinStringTest {

  @Test
  def testConfig0000() {
    val npr = new NonPowerBinString(IUPAC.EUPLOTID_NUCLEAR, 1)
    assertEquals('W', npr.binString2AA(0)("000000"))
    assertEquals('M', npr.binString2AA(0)("111111"))

    assertEquals('S', npr.binString2AA(0)("000010"))
    assertEquals('F', npr.binString2AA(0)("111101"))
  }

  @Test
  def testConfig0001() {
    val npr = new NonPowerBinString(IUPAC.EUPLOTID_NUCLEAR, 1)
    assertEquals('M', npr.binString2AA(1)("000000"))
    assertEquals('W', npr.binString2AA(1)("111111"))

    assertEquals('S', npr.binString2AA(1)("000010"))
    assertEquals('F', npr.binString2AA(1)("111101"))
  }

  @Test
  def testOne() {
    val npr = new NonPowerBinString(IUPAC.EUPLOTID_NUCLEAR, 1)
    assertEquals(Codon("AGC"), npr.binString2Codon("000001"))
  }

  @Test
  def testNumber2String() {
    val npr = new NonPowerBinString(IUPAC.EUPLOTID_NUCLEAR, 1)
    assertEquals(Integer.toBinaryString(1), "1")
    assertEquals(Integer.toBinaryString(4096), "1000000000000")
    assertEquals(npr.number2List(0), List(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0))
    assertEquals(npr.number2List(1), List(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1))
    assertEquals(npr.number2List(4095), List(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1))
  }

  @Test(expected = classOf[IllegalArgumentException])
  def testNumber2StringFailure() {
    val npr = new NonPowerBinString(IUPAC.EUPLOTID_NUCLEAR, 1)
    npr.number2List(-1)
  }

  @Test(expected = classOf[IllegalArgumentException])
  def testNumber2StringFailure2() {
    val npr = new NonPowerBinString(IUPAC.EUPLOTID_NUCLEAR, 1)
    npr.number2List(4096)
  }

  @Test
  def testGetLine() {
    val npr = new NonPowerBinString(IUPAC.EUPLOTID_NUCLEAR, 1)
    assertEquals(List(("000000", 'W'), ("111111", 'M')),
      npr.getLine(npr.origAssignment.head, 0))

    assertEquals(List(("000000", 'M'), ("111111", 'W')),
      npr.getLine(npr.origAssignment.head, 1))

    assertEquals(List(("000010", 'S'), ("000001", 'S'),
      ("111110", 'F'), ("111101", 'F')),
      npr.getLine(npr.origAssignment.tail.head, 0))

    // Note that the elements are flipped due to the way
    // this list is constructed.
    assertEquals(List(("000010", 'F'), ("111101", 'S'),
      ("000001", 'F'), ("111110", 'S')),
      npr.getLine(npr.origAssignment.tail.head, 1))
  }

  @Test
  def testSubstring() {
    assertEquals("56", "123456".substring(4, 6))
  }

  @Test
  def testNumber() {
    val npr = new NonPowerBinString(IUPAC.EUPLOTID_NUCLEAR, 1)
    assertEquals(Set("000000"), npr.binStringByNumber(0))
    assertEquals(Set("111111"), npr.binStringByNumber(23))
    assertEquals(Set("000010", "000001"), npr.binStringByNumber(1))
    assertEquals(Set("111110", "111101"), npr.binStringByNumber(22))
    assertEquals(Set("100110", "100101", "011000", "010111"),
      npr.binStringByNumber(11))
    assertEquals(Set("101000", "100111", "011010", "011001"),
      npr.binStringByNumber(12))
  }

  @Test
  def printTable() {
    val npr = new NonPowerBinString(IUPAC.EUPLOTID_NUCLEAR, 1)
    for (i <- 0 to 4) {
      println("\nTable " + i)
      println(npr.table(i))
    }
  }
}
