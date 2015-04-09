package net.gumbix.geneticcode.dich.scan

import net.gumbix.geneticcode.dich._
import net.gumbix.geneticcode.dich.{BinaryDichotomicAlgorithm => BDA, Adenine => A, Uracil => U, Cytosine => C, Guanine => G, _}
import net.gumbix.geneticcode.dich.conc.ConcScan
import net.gumbix.geneticcode.dich.ct.CodingClassTable

/**
 * Scans for 64 classes where the max. Hamming distance within
 * the same same amino acid group is less than the min. Hamming
 * distance to all other groups.
 * @author Markus Gumbel (m.gumbel@hs-mannheim.de)
 *         (c) 2013 Markus Gumbel
 */
trait HammingGroupConstraints extends ScanConstraint[CodingClassTable] {
  /**
   * As long as the new code table has more classes we continue searching...
   * @param t
   * @param w
   * @return
   */
  override def isValidConfig(t: CodingClassTable, w: Int) =
    t.classes.size > w // && t.minHammingDistInAA == 1

  /**
   * The max. hamming distance within the group of the same amino acid
   * must be less than the minimum distance within the group of different
   * amino acids.
   * @param t
   * @return
   */
  override def isSolution(t: CodingClassTable) =
    t.classes.size == 64 && t.hammingDistInAA.max < t.hammingDistBtwAA.min

  override def scanParameter(t: CodingClassTable) = t.classes.size
}

/**
 * @param bdas BDAs that are always included.
 * @param size The maximum size of the BDA set.
 */
class HammingGroupScan(bdas: List[BinaryDichotomicAlgorithm], size: Int)
  extends Scan[CodingClassTable](bdas, size) with CodingClassTableScan
  with HammingGroupConstraints {

  def this(bdas: Array[String], size: Int) =
    this(BdaImplicitDefs.toBda(bdas), size)

  override def startMessage = "Hamming distance grouped by aa (sequentially)"

}

object HammingGroupScan {

  def main(args: Array[String]) {
    val bdas = List(
      new BDA(0, 1, (A, U), Set(A, U)),
      new BDA(0, 1, (A, U), Set(C, G)),
      new BDA(0, 1, (C, G), Set(A, U)),

      new BDA(2, 0, (A, G), Set(A, U)),
      new BDA(2, 0, (C, U), Set(A, U)),
      new BDA(2, 0, (A, G), Set(C, G))
    )
    new HammingGroupScan(bdas, 12).run()
  }
}

/**
 * @param bdas BDAs that are always included.
 * @param size The maximum size of the BDA set.
 */
class ConcHammingGroupScan(bdas: List[BinaryDichotomicAlgorithm],
                           size: Int = 10, noActors: Int = 3)
  extends ConcScan[CodingClassTable](bdas, size, noActors) with CodingClassTableScan
  with HammingGroupConstraints {

  def this(bdas: Array[String], size: Int) =
    this(BdaImplicitDefs.toBda(bdas), size)

  override def startMessage = "Hamming distance grouped by aa (concurrently)"

  def cloneScan() = new ConcHammingGroupScan(bdas, size, noActors)
}

object ConcHammingGroupScan {

  def main(args: Array[String]) {
    val bdas = List(
      new BDA(0, 1, (A, U), Set(A, U)),
      new BDA(0, 1, (A, U), Set(C, G)),
      new BDA(0, 1, (C, G), Set(A, U)),

      new BDA(2, 0, (A, G), Set(A, U)),
      new BDA(2, 0, (C, U), Set(A, U)),
      new BDA(2, 0, (A, G), Set(C, G))
    )
    new ConcHammingGroupScan(bdas, 12, 4).run()
  }
}
