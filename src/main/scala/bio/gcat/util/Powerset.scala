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
package bio.gcat.util

/**
 * @author Markus Gumbel (m.gumbel@hs-mannheim.de)
 *         (c) 2012 Markus Gumbel
 */
trait PowerSet {

  /**
   * Let the elements of a set be accessed by an index i (0 <= i < set.size).
   * Calculate the powerset of a maximum size of size for all indices in the range from to to.
   * Note: from := 0 and to := set.size-1 and size := set.size returns the powerset of set.
   * @param from
   * @param to
   * @param size
   * @return
   */
  def powerSetIndices(from: Int, to: Int, size: Int): List[List[Int]] = size match {
    case 0 => List(List())  // Just the empty set.
    case _ => {
      val nl = for (i <- from to to) yield {
        val subPowerSets = powerSetIndices(i + 1, to, size - 1)
        subPowerSets.map(subPowerSet => i :: subPowerSet)
      }
      List() :: nl.toList.flatten // Add the empty set again.
    }
  }
}
