package bio.gcat.geneticcode.dich.demo

import bio.gcat.geneticcode.dich._
import bio.gcat.geneticcode.dich.ct.ClassTable
import bio.gcat.geneticcode.dich.scan.ErrorScan
import org.junit.{Ignore, Test}

/**
 * @author Markus Gumbel (m.gumbel@hs-mannheim.de)
 *         (c) 2014 Markus Gumbel
 */
class C3ScanDemo {

  @Test
  @Ignore
  def scanC3Codon {
    for (i <- 0 to 10) {
      scanC3Codon(i, C3Codes.codes(i))
    }
  }

  def scanC3Codon(id: Int, code: String) {
    new ErrorScan(
      List(),
      .3, 6, 21, // E, k_d, |M|
      IUPAC.STANDARD, // code table
      new C3PropertyCodon(id.toString, code) // C3
    ).run()
  }

  @Test
  @Ignore
  def scanC3AA {
    for (i <- 0 to 10) {
      scanC3AA(i, C3Codes.codes(i))
    }
  }

  def scanC3AA(id: Int, code: String) {
    val p = new C3PropertyAA(id.toString, code, IUPAC.STANDARD)
    val size = p.size
    new ErrorScan(
      List(),
      .4, 6, p.size + 1, // E, k_d, |M|
      IUPAC.STANDARD, // code table
      p // C3
    ).run()
  }

  // Print

  @Test
  @Ignore
  def printC3Codon {
    for (i <- 0 to 10) {
      printC3Codon(i, C3Codes.codes(i))
    }
  }

  def printC3Codon(i: Int, codons: String) {
    val t = new ClassTable(List(RumerBDA), IUPAC.EUPLOTID_NUCLEAR,
      new C3PropertyCodon(i.toString, codons))
    println(t.mkString)
    println("--------------------------------------")
  }

  @Test
  @Ignore
  def printC3AA {
    for (i <- 0 to 10) {
      printC3AA(i, C3Codes.codes(i))
    }
  }

  def printC3AA(i: Int, codons: String) {
    val t = new ClassTable(List(RumerBDA), IUPAC.STANDARD,
      new C3PropertyAA(i.toString, codons, IUPAC.STANDARD))
    println(t.mkString)
    println("--------------------------------------")
  }
}
