package net.gumbix.geneticcode.dich.demo

import org.junit.{Ignore, Test}
import net.gumbix.geneticcode.dich.{BinaryDichotomicAlgorithm => BDA, Adenine => A, Uracil => U, Cytosine => C, Guanine => G, _}
import net.gumbix.geneticcode.dich.scan.ScanNumberOfClassesScan

/**
 * @author Markus Gumbel (m.gumbel@hs-mannheim.de)
 *         (c) 2013 Markus Gumbel
 */
class NumberOfClassesScanDemo {

  /**
   * Taken from Paper class. Maybe used in slides (Ulm?).
   */
  @Test
  @Ignore
  def ClassScanWithFixedBDAs() {
    val bdas = List(
      RumerBDA, ParityBDA
    )
    new ScanNumberOfClassesScan(bdas).run()
  }

  @Test
  @Ignore
  def ClassScanWithFixedBDAs2() {
    val bdas = List(
      RumerBDA, ParityBDA, AntiCodonBDA
    )
    new ScanNumberOfClassesScan(bdas).run()
  }

  @Test
  @Ignore
  def ClassScanWithFixedBDAs2b() {
    val bdas = List(
      RumerBDA, ParityBDA, AntiCodonBDA
    )
    new ScanNumberOfClassesScan(bdas, 7).run()
  }

}
