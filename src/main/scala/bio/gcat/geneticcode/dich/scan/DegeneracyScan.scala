package bio.gcat.geneticcode.dich.scan

/**
 * @author Markus Gumbel (m.gumbel@hs-mannheim.de)
 *         (c) 2013 Markus Gumbel
 */

import bio.gcat.geneticcode.core.StandardCodeDegeneracy
import bio.gcat.geneticcode.dich.ct.ClassTable
import bio.gcat.geneticcode.dich.{BinaryDichotomicAlgorithm, ClassTableScan, Scan, ScanConstraint}

/**
 * @author Markus Gumbel (m.gumbel@hs-mannheim.de)
 *         (c) 2012 Markus Gumbel
 */
trait DegeneracyConstraints[T <: ClassTable] extends ScanConstraint[T] {

  val classSize: Int

  /**
   * @param t
   * @param w
   * @return
   */
  override def isValidConfig(t: T, w: Int) = {
    t.classes.size > w &&
      t.classes.size <= classSize &&
      (t.degeneracy isCompatible StandardCodeDegeneracy)
  }

  override def isSolution(t: T) = t.classes.size == classSize && t.degeneracy == StandardCodeDegeneracy
}

/**
 * @param bdas BDAs that are always included.
 * @param size The size of the (full) BDA set.
 * @author Markus Gumbel (m.gumbel@hs-mannheim.de)
 *         (c) 2013 Markus Gumbel
 */
class DegeneracyScan(bdas: List[BinaryDichotomicAlgorithm] = List(), val size: Int = 6, val classSize: Int = 21)
  extends Scan[ClassTable](bdas, size) with ClassTableScan
  with DegeneracyConstraints[ClassTable] {

  override def startMessage = size + " degeneracy sequentially"
}

object DegeneracyScan {
  def main(args: Array[String]) {
    new DegeneracyScan(List(), 7, 21).run()
  }
}
