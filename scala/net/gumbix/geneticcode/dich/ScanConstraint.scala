package net.gumbix.geneticcode.dich

import net.gumbix.util.Loggable
import net.gumbix.geneticcode.dich.ct.ClassTable

/**
 * @author Markus Gumbel (m.gumbel@hs-mannheim.de)
 *         (c) 2013 Markus Gumbel
 */
trait ScanConstraint[T <: ClassTable] extends Loggable {

  /**
   * All classifiers, i.e. BDAs, used in the scan.
   */
  val allClassifiers: ClassifierSet

  /**
   * Indicates whether a class table is valid configuration.
   * If not the recursive scan is terminated here and
   * the last BDA of the BDA-list is skipped.
   * @param classTable
   * @param w
   * @return
   */
  def isValidConfig(classTable: T, w: Int) = true

  /**
   * Indicates whether the class table is a solution or not.
   * @param classTable
   * @return
   */
  def isSolution(classTable: T) = false

  def scanParameter(classTable: T) = classTable.classes.size
}
