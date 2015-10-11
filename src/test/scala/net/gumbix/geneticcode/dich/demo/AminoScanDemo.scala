package net.gumbix.geneticcode.dich.demo

import org.junit.{Ignore, Test}
import net.gumbix.geneticcode.dich._
import scan.{AminoMappingScan, ClassPower2Scan}
import net.gumbix.geneticcode.dich.{BinaryDichotomicAlgorithm => BDA}
import net.gumbix.geneticcode.dich.{Adenine => A, Uracil => U, Cytosine => C, Guanine => G}
import net.gumbix.geneticcode.dich.ct.AminoClassTable

/**
 * @author Markus Gumbel (m.gumbel@hs-mannheim.de)
 *         (c) 2012 Markus Gumbel
 */
class AminoScanDemo {

  @Test
  @Ignore
  def scanAminoClasses() {
    new AminoMappingScan(List(), 5)
  }

  @Test
  @Ignore
  def example() {
    val bdas = List(
      RumerBDA, AntiCodonBDA
    )
    new AminoMappingScan(bdas, 5)
  }

  @Test
  @Ignore
  def exampleAlberto() {
    val bdas = List(
      new BDA(0, 1, (A, U), Set(C, G)),
      new BDA(1, 0, (A, U), Set(C, G)), // OK
      new BDA(2, 0, (U, C), Set(A, U))
    )
    new AminoMappingScan(bdas, 5)
  }

  @Test
  @Ignore
  def example2Alberto() {
    val bdas = List(
      new BDA(0, 1, (A, G), Set(C, G)),
      new BDA(1, 0, (A, G), Set(C, G)), // OK
      new BDA(0, 1, (U, C), Set(C, G)),
      new BDA(1, 0, (U, C), Set(C, G))
    )
    new AminoMappingScan(bdas, 12)
  }

  @Test
  @Ignore
  def printTable() {
    val bdas = List(
      RumerBDA
    )
    val t = new AminoClassTable(bdas, IUPAC.STANDARD)
    println(t.mkString)
  }
}
