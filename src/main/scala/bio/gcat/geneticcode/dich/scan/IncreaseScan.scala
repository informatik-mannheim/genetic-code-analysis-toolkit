package bio.gcat.geneticcode.dich.scan

import bio.gcat.geneticcode.dich.conc.ConcScan
import bio.gcat.geneticcode.dich.ct.{ClassTable, CodingClassTable}
import bio.gcat.geneticcode.dich.{Classifier, CodingClassTableScan, Scan, ScanConstraint}

/**
 * The search is continued as long as a new BDA that is added to the list
 * leads to an increase of the classes size.
 * @author Markus Gumbel (m.gumbel@hs-mannheim.de)
 *         (c) 2013 Markus Gumbel
 */
trait IncreaseConstraints[T <: ClassTable] extends ScanConstraint[T] {

  val classSize: Int

  /**
   * As long as the new code table has more classes we continue searching...
   * @param t
   * @param w
   * @return
   */
  override def isValidConfig(t: T, w: Int) = t.classes.size > w

  override def isSolution(t: T) = t.classes.size == classSize

  override def scanParameter(t: T) = t.classes.size
}

/**
 * @param classSize The intended class size (e.g. 64 or 20)
 * @param bdas BDAs that are always included.
 * @param size The maximum size of the BDA set.
 * @author Markus Gumbel (m.gumbel@hs-mannheim.de)
 *         (c) 2013 Markus Gumbel
 */
class IncreaseScan(val classSize: Int = 64,
                   bdas: List[Classifier[Int]] = List(), size: Int = 6)
  extends Scan[CodingClassTable](bdas, size) with CodingClassTableScan
  with IncreaseConstraints[CodingClassTable] {

  override def startMessage = "Increase sequentially"
}

object IncreaseScan {
  def main(args: Array[String]) {
    new IncreaseScan(20).run()
  }
}

class ConcIncreaseScan(val classSize: Int = 64,
                       bdas: List[Classifier[Int]] = List(),
                       size: Int = 6,
                       noActors: Int = 3)
  extends ConcScan[CodingClassTable](bdas, size, noActors)
  with CodingClassTableScan
  with IncreaseConstraints[CodingClassTable] {

  override def startMessage = "Increase concurrently"

  def cloneScan() = new ConcIncreaseScan(classSize, bdas, size)
}

object ConcIncreaseScan {
  def main(args: Array[String]) {
    new ConcIncreaseScan(20).run()
  }
}