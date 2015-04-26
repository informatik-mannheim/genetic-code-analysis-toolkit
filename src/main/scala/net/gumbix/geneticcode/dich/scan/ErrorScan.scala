package net.gumbix.geneticcode.dich.scan

import java.io.{PrintWriter, File}
import java.util.Date

import net.gumbix.geneticcode.dich._
import net.gumbix.util.{Version, ArgsParser}
import net.gumbix.geneticcode.dich.conc.ConcScan
import net.gumbix.geneticcode.dich.ct.ClassTable

/**
 * @author Markus Gumbel (m.gumbel@hs-mannheim.de)
 *         (c) 2013 Markus Gumbel
 */
trait ErrorConstraints[T <: ClassTable] extends ScanConstraint[T] {

  val minClassSize: Int
  val maxClassSize: Int
  val errorD: Double

  /**
   * @param t
   * @param w
   * @return
   */
  override def isValidConfig(t: T, w: Int) = {
    t.classes.size > w &&
      t.classes.size <= maxClassSize &&
      t.errorC <= errorD
  }

  override def isSolution(t: T) =
    minClassSize <= t.classes.size && t.classes.size <= maxClassSize
}

/**
 * @param bdas BDAs that are always included.
 * @param size The size of the (full) BDA set.
 * @author Markus Gumbel (m.gumbel@hs-mannheim.de)
 *         (c) 2013 Markus Gumbel
 */
class ErrorScan(bdas: List[Classifier[Int]] = List(),
                val errorD: Double = 0.2,
                val size: Int = 6, val classSize: Int = 21,
                val iupacNumber: Int = IUPAC.STANDARD,
                val codonProperty: CodonProperty = IdStandardAminoAcidProperty,
                allClassifiers: ClassifierSet = BinaryDichotomicAlgorithm.bdas216)
  extends Scan[ClassTable](bdas, size, allClassifiers) with ClassTableScan
  with ErrorConstraints[ClassTable] {

  override def newClassTable(bda: List[Classifier[Int]]) = {
    new ClassTable(bda, iupacNumber, codonProperty)
  }

  val minClassSize = classSize
  val maxClassSize = classSize + 4

  override def startMessage = "Error sequentially with E_d = " + errorD +
    ", k_d = " + size + ", |M| = " + classSize + ", code table = " + iupacNumber +
    ", codonProp = " + codonProperty
}

object ErrorScan extends ArgsParser {
  def main(args: Array[String]) {
    new ErrorScan(
      toBDAList(args, 0),
      toDouble(args, 1), toInt(args, 2), toInt(args, 3), // E, k_d, |M|
      toInt(args, 4), toProp(args, 5) // code table, aaProps
    ).run()
  }
}

class ConcErrorScan(bdas: List[Classifier[Int]] = List(),
                    val errorD: Double = 0.2,
                    val size: Int = 6, val classSize: Int = 21,
                    val iupacNumber: Int = IUPAC.STANDARD,
                    val codonProperty: CodonProperty = new IdAminoAcidProperty(1),
                    noActors: Int = 3,
                    allClassifiers: ClassifierSet = BinaryDichotomicAlgorithm.bdas216)
  extends ConcScan[ClassTable](bdas, size, noActors, allClassifiers)
  with ClassTableScan with ErrorConstraints[ClassTable] {

  val minClassSize = classSize
  val maxClassSize = classSize + 4

  def cloneScan() = new ConcErrorScan(bdas, errorD, size, classSize,
    iupacNumber, codonProperty, noActors)

  override def startMessage = "Error concurrently with E_d = " + errorD +
    ", k_d = " + size + ", |M| = " + classSize + ", code table = " + iupacNumber +
    ", codonProp = " + codonProperty

}

object ConcErrorScan extends ArgsParser {
  def main(args: Array[String]) {
    new ConcErrorScan(
      toBDAList(args, 0),
      toDouble(args, 1), toInt(args, 2), toInt(args, 3), // E, k_d, |M|
      toInt(args, 4), toProp(args, 5), // code table
      toInt(args, 6) // no. of actors
    ).run()
  }
}
