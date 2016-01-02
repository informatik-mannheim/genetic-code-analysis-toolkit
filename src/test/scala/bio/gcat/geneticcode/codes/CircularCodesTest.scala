package bio.gcat.geneticcode.codes

import bio.gcat.geneticcode.dich.Codon
import org.junit.Test
import junit.framework.Assert._
import bio.gcat.geneticcode.dich.CodonImplicitDefs._

/**
 * @author Markus Gumbel (m.gumbel@hs-mannheim.de)
 *         (c) 2012 Markus Gumbel
 */
class CircularCodesTest {

  @Test
  def testConcat1() {
    val a = Codon("AAA")
    val code = new Code(Set(a))

    assertEquals(List(List(a)), code.concats(1))
    assertEquals(List(List(a, a)), code.concats(2))
    assertEquals(List(List(a, a, a)), code.concats(3))
  }

  @Test
  def testConcat2() {
    val a = Codon("AAA")
    val u = Codon("UUU")
    val code = new Code(Set(a, u))

    assertEquals(List(List(a), List(u)), code.concats(1))
    assertEquals(List(List(a, a), List(a, u), List(u, a), List(u, u)), code.concats(2))
    assertEquals(List(
      List(a, a, a), List(a, a, u),
      List(a, u, a), List(a, u, u),
      List(u, a, a), List(u, a, u),
      List(u, u, a), List(u, u, u)
    ), code.concats(3))
  }

  @Test
  def testConcat3() {
    val X = Set("ACG", "GUA", "CGU", "CGG", "UAC")
    val code = new Code(X)

    val n = X.size
    assertEquals(n, code.concats(1).size)
    assertEquals(n * n, code.concats(2).size)
    assertEquals(n * n * n, code.concats(3).size)

    // println(code.concats(2).mkString("\n"))
  }

  @Test
  def testShift1() {
    val X = Set("UGG", "GUG")
    val code = new Code(X)
    val l = code.codonsL
    val s1 = code.shift(l, 1)
    val s2 = code.shift(l, 2)
    println(code.codonsL + "->" + s1 + "->" + s2)
    assertEquals(List(Codon("GGG"), Codon("UGU")), s1)
    assertEquals(List(Codon("GGU"), Codon("GUG")), s2)
  }

  @Test
  def testShift2() {
    val X = Set("ACG", "GUA", "CGU", "CGG", "UAC")
    val code = new Code(X)
    val l = code.codonsL
    val s1 = code.shift(l, 1)
    val s2 = code.shift(l, 2)
    println(code.codonsL + "->" + s1 + "->" + s2)
  }

  @Test
  def test1() {
    val X = Set("UGG", "GUG")
    val code = new Code(X)
    assertTrue(!code.isNCircularM(1))
    assertTrue(!code.isNCircular)
  }

  @Test
  def test1b() {
    val code = Code.apply(Array("UGG", "GUG"))
    assertTrue(!code.isNCircularM(1))
    assertTrue(!code.isNCircular)
  }

  @Test
  def test2() {
    val X = Set("UGG", "CUG", "GGC", "UGU")
    val code = new Code(X)
    assertTrue(code.isNCircularM(1))
    assertTrue(!code.isNCircular)
  }

  @Test
  def test3() {
    val X = Set("ACG", "GUA", "CGU", "CGG", "UAC")
    val code = new Code(X)
    assertTrue(code.isNCircularM(1))
    assertTrue(code.isNCircularM(2))
    assertTrue(!code.isNCircularM(3))
    assertTrue(!code.isNCircular)
  }

  @Test
  def test4() {
    val X = Set("CGU", "ACG", "UAC", "GUA")
    val code = new Code(X)
    assertTrue(code.isNCircularM(1))
    assertTrue(code.isNCircularM(2))
    assertTrue(code.isNCircularM(3))
    assertTrue(!code.isNCircularM(4))
    assertTrue(!code.isNCircular)
  }

  @Test
  def test5() {
    val X = Set("AUG", "GCC", "UUU")
    val code = new Code(X)
    assertTrue(!code.isNCircularM(1))
    assertTrue(!code.isNCircularM(2))
    assertTrue(!code.isNCircularM(3))
    assertTrue(!code.isNCircular)
  }

  @Test
  def testCodici1() {
    val X = Set("AAC", "GUU", "AAG", "CUU", "AAU", "AUU", "ACC", "GGU",
      "ACG", "CGU", "ACU", "AGU", "AGC", "GCU", "AGG", "CCU", "CCG",
      "CGG", "UCA", "UGA")
    val code = new Code(X)
    assertTrue(code.isNCircular)
  }

  @Test
  def testCodici1b() {
    val X = Set("CAC", "GUU", "AAG", "CUU", "AAU", "AUU", "ACC", "GGU",
      "ACG", "CGU", "ACU", "AGU", "AGC", "GCU", "AGG", "CCU", "CCG",
      "CGG", "UCA", "UGA")
    val code = new Code(X)
    assertTrue(!code.isNCircular)
  }

  @Test
  def testElena13() {
    val X = Set("UGG", "AUG", "UUC", "AAG", "GAG", "GAC",
      "GGC", "CAG", "GUA", "CGU", "UGU", "AAC", "GCC")
    val code = new Code(X)
    assertTrue(!code.isNCircular)
  }

  def isCircular(X: Set[String]) = {
    val code = new Code(X)
    code.isNCircular
  }
}
