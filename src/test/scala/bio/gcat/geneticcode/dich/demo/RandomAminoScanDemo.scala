package bio.gcat.geneticcode.dich.demo

import bio.gcat.geneticcode.dich.RumerBDA
import bio.gcat.geneticcode.dich.scan.RandomScan
import org.junit.{Ignore, Test}

/**
 * See also bio.gcat.geneticcode.dich.demo.Paper.
 * As required by reviewer #2.
 * @author Markus Gumbel (m.gumbel@hs-mannheim.de)
 *         (c) 2012 Markus Gumbel
 */
class RandomAminoScanDemo {

  val N = 5000

  /**
   * Sample compatibility errors (E_c) for |M|=24 classes using exactly 7 bits.
   */
  @Test
  @Ignore
  def histClasses24k7() {
    new RandomScan(N, List(), 7, 7, 24, Some("-free"))
  }

  /**
   * Sample compatibility errors (E_c) for |M|=24 classes using 5 to 7 bits.
   */
  @Test
  @Ignore
  def histClasses24k567() {
    new RandomScan(N, List(), 5, 7, 24, Some("-free567"))
  }

  /**
   * Sample compatibility errors (E_c) for |M|=24 classes using 6 to 7 bits.
   */
  @Test
  @Ignore
  def histClasses24k67() {
    new RandomScan(N, List(), 6, 7, 24, Some("-free67"))
  }

  /**
   * Sample compatibility errors (E_c) for |M|=24 classes using 5 to 7 bits
   * and include Rumer.
   */
  @Test
  @Ignore
  def histClasses24withRumer567() {
    new RandomScan(N, List(RumerBDA), 5, 7, 24, Some("-Rumer567"))
  }

  /**
   * Sample compatibility errors (E_c) for |M|=24 classes using 6 to 7 bits
   * and include Rumer.
   */
  @Test
  @Ignore
  def histClasses24withRumer67() {
    // Allow any error using 6 to 7 bits.
    new RandomScan(N, List(RumerBDA), 6, 7, 24, Some("-Rumer67"))
  }
}
