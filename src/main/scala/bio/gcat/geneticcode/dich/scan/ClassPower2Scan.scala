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
import scala.collection.JavaConversions._

/**
 * Scan that looks for 2^size classes where size is the size of the full BDA list.
 * E.g. size = 6 implies 64 classes.
 * The search is truncated when a new BDA does double the class size.
 * @author Markus Gumbel (m.gumbel@hs-mannheim.de)
 *         (c) 2012 Markus Gumbel
 */
trait ClassPower2Constraints[T <: ClassTable] extends ScanConstraint[T] {

  val size: Int

  val classSize = math.pow(2, size).asInstanceOf[Int]

  /**
   * If the class size of t is 2^size (with size as the BDA list size)
   * the search is continued.
   * @param t
   * @param w
   * @return
   */
  override def isValidConfig(t: T, w: Int) = {
    val noClasses = math.pow(2, t.bdas.size).asInstanceOf[Int]
    t.classes.size == noClasses
  }

  override def isSolution(t: T) = t.classes.size == classSize
}

/**
 * @param bdas BDAs that are always included.
 * @param size The size of the (full) BDA set.
 * @author Markus Gumbel (m.gumbel@hs-mannheim.de)
 *         (c) 2013 Markus Gumbel
 */
class ClassPower2Scan(bdas: List[Classifier[Int]] = List(),
                      val size: Int = 6,
                      allClassifiers: ClassifierSet = BinaryDichotomicAlgorithm.bdas216)
  extends Scan[ClassTable](bdas, size, allClassifiers) with ClassTableScan
  with ClassPower2Constraints[ClassTable] {

  override def startMessage = classSize +
    " classes (via class size doubling per classifier/BDA) sequentially"
}

object ClassPower2Scan {
  def main(args: Array[String]) {
    new ClassPower2Scan().run()
  }
}

class ConcClassPower2Scan(bdas: List[Classifier[Int]] = List(),
                          val size: Int = 6,
                          noActors: Int = 3,
                          allClassifiers: ClassifierSet = BinaryDichotomicAlgorithm.bdas216)
  extends ConcScan[ClassTable](bdas, size, noActors, allClassifiers)
  with ClassTableScan with ClassPower2Constraints[ClassTable] {

  def cloneScan() = new ConcClassPower2Scan(bdas, size, noActors)

  override def startMessage = classSize +
    " classes (via class size doubling per classifier/BDA) concurrently"

}

object ConcClassPower2Scan {
  def main(args: Array[String]) {
    new ConcClassPower2Scan().run()
  }
}