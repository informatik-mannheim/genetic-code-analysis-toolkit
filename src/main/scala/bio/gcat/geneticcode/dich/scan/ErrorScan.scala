/*
 * Copyright [2017] [Mannheim University of Applied Sciences]
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package bio.gcat.geneticcode.dich.scan

import bio.gcat.geneticcode.dich._
import bio.gcat.geneticcode.dich.conc.ConcScan
import bio.gcat.geneticcode.dich.ct.ClassTable
import bio.gcat.util.ArgsParser

/**
 * @author Markus Gumbel (m.gumbel@hs-mannheim.de)
 *         (c) 2013 Markus Gumbel
 */
trait ErrorConstraints[T <: ClassTable] extends ScanConstraint[T] {

  val classSize: Int
  val errorD: Double

  /**
   * @param t
   * @param w
   * @return
   */
  override def isValidConfig(t: T, w: Int) = {
    t.classes.size > w &&
      t.classes.size <= classSize + t.errorC &&
      t.relErrorC <= errorD
  }

  override def isSolution(t: T) =
    classSize <= t.classes.size && t.classes.size <=classSize + t.errorC
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
