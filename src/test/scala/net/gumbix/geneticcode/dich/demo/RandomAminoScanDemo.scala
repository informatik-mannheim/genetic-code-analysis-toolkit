package net.gumbix.geneticcode.dich.demo

import org.junit.{Ignore, Test}
import net.gumbix.geneticcode.dich._
import net.gumbix.geneticcode.dich.{BinaryDichotomicAlgorithm => BDA}
import net.gumbix.geneticcode.dich.{Adenine => A, Uracil => U, Cytosine => C, Guanine => G}
import net.gumbix.geneticcode.dich.scan.RandomScan

/**
 * See also net.gumbix.geneticcode.dich.demo.Paper.
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
