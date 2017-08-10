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
package bio.gcat.geneticcode.dich

import bio.gcat.geneticcode.dich.ct.ClassTable
import bio.gcat.util.Loggable

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
