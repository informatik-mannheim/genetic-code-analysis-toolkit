package bio.gcat.geneticcode.dich.demo

import bio.gcat.geneticcode.dich.ct.AminoClassTable
import bio.gcat.geneticcode.dich.scan.AminoMappingScan
import bio.gcat.geneticcode.dich.{Adenine => A, Uracil => U, Guanine => G, Cytosine => C, IUPAC, AntiCodonBDA, BinaryDichotomicAlgorithm, RumerBDA}
import org.junit.{Ignore, Test}

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
      new BinaryDichotomicAlgorithm(0, 1, (A, U), Set(C, G)),
      new BinaryDichotomicAlgorithm(1, 0, (A, U), Set(C, G)), // OK
      new BinaryDichotomicAlgorithm(2, 0, (U, C), Set(A, U))
    )
    new AminoMappingScan(bdas, 5)
  }

  @Test
  @Ignore
  def example2Alberto() {
    val bdas = List(
      new BinaryDichotomicAlgorithm(0, 1, (A, G), Set(C, G)),
      new BinaryDichotomicAlgorithm(1, 0, (A, G), Set(C, G)), // OK
      new BinaryDichotomicAlgorithm(0, 1, (U, C), Set(C, G)),
      new BinaryDichotomicAlgorithm(1, 0, (U, C), Set(C, G))
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
