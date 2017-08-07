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
package bio.gcat.geneticcode.dich.ct

import bio.gcat.geneticcode.dich._

/**
 * Formats a genetic code table.
 * @author Markus Gumbel (m.gumbel@hs-mannheim.de)
 *         (c) 2016 Markus Gumbel
 */
trait GeneticCodeTableFormatter {
  /**
   * Order of nucleotides in mkString.
   * @param c Nucleotide
   * @return A number between 1 to 4 representing the order.
   */
  def order = UcagOrder(_)

  def UcagOrder(c: Compound) = c match {
    case Adenine => 3
    case Cytosine => 2
    case Guanine => 4
    case Uracil => 1
  }

  def CguaOrder(c: Compound) = c match {
    case Adenine => 4
    case Cytosine => 1
    case Guanine => 2
    case Uracil => 3
  }
}
