package net.gumbix.geneticcode.dich.demo

import org.junit.{Ignore, Test}
import net.gumbix.geneticcode.dich._
import net.gumbix.geneticcode.dich.scan.{ConcClassPower2Scan, IncreaseScan, ClassPower2Scan}
import net.gumbix.geneticcode.dich.{BinaryDichotomicAlgorithm => BDA}
import net.gumbix.geneticcode.dich.{Adenine => A, Uracil => U, Cytosine => C, Guanine => G}
import net.gumbix.geneticcode.dich.ct.ClassTable

/**
 * @author Markus Gumbel (m.gumbel@hs-mannheim.de)
 *         (c) 2012 Markus Gumbel
 */
class Class64ScanDemo {

  @Test
  @Ignore
  def scanFor64Classes() {
    new ClassPower2Scan(List(), 6).run()
  }

  /**
   * Show that there are 1496 solutions when Rumer is included.
   */
  @Test
  @Ignore
  def classes64withRumerScan() {
    val bdas = List(RumerBDA.complement)
    new ClassPower2Scan(bdas, 6).run()
  }

  /**
   * TODO: consolidate with scan above.
   */
  @Test
  @Ignore
  def exampleWithRumer() {
    new ClassPower2Scan(List(RumerBDA), 6).run()
  }

  @Test
  @Ignore
  def exampleWithRumerParity() {
    new ClassPower2Scan(List(RumerBDA, ParityBDA), 6).run()
  }


  @Test
  @Ignore
  def exampleWithParity() {
    new ClassPower2Scan(List(ParityBDA.complement), 6).run() // TODO: why complement?
  }

  @Test
  @Ignore
  def exampleWithRumerParityAntiCodon() {
    new ClassPower2Scan(List(RumerBDA, ParityBDA, AntiCodonBDA), 6).run()
  }

  @Test
  @Ignore
  def exampleWithRumerParityAntiCodonRedundant() {
    new IncreaseScan(64, List(RumerBDA, ParityBDA, AntiCodonBDA), 7).run()
  }

  @Test
  @Ignore
  def exampleWithSomethingToScan() {
    val bdas = List(
      new BDA(0, 1, (A, U), Set(A, U)),
      new BDA(0, 1, (A, U), Set(C, G))
      // new BDA(0, 1, (C, G), Set(A, U))
      // new BDA(2, 1, (A, U), Set(A, C))
      // new BDA(2, 1, (A, U), Set(U, G))
    )
    new ClassPower2Scan(bdas, 6).run()
  }

  @Test
  @Ignore
  def exampleWithSomethingToConcScan() {
    val bdas = List(
      new BDA(0, 1, (A, U), Set(A, U)),
      new BDA(0, 1, (A, U), Set(C, G))
    )
    // new ClassPower2Scan(bdas, 6).run()
    new ConcClassPower2Scan(bdas, 6, 4).run()
  }

  @Test
  @Ignore
  def exampleWithNothingToScan() {
    val bdas = List(
      new BDA(0, 1, (A, U), Set(A, U)),
      new BDA(0, 1, (A, U), Set(C, G)),
      new BDA(0, 1, (C, G), Set(A, U)),
      new BDA(2, 1, (A, U), Set(A, C)),
      new BDA(2, 1, (A, U), Set(U, G)),
      new BDA(2, 1, (C, G), Set(A, C))
    )
    new ClassPower2Scan(bdas, 6).run()
  }

  @Test
  @Ignore
  def scanLinear() {
    val bdas = BDA.bdas.classifiers.toList
    val t0 = new ClassTable(List(RumerBDA), IUPAC.STANDARD)
    println(". " + ". " + ". " + ". " + t0.mkCodonsLinearString)
    bdas.foreach {
      bda =>
        val t = new ClassTable(List(bda), IUPAC.STANDARD)
        println(bda.toString + " " + t.mkClassesLinearString)
    }
  }
}
