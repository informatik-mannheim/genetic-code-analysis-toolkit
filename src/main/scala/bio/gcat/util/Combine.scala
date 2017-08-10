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
object Combine {

  /**
   * Combine each element in the list with each other. (i, j) == (j, i) and i <> j.
   * @param l
   * @return
   */
  def combine[A](l: List[A]): List[(A, A)] = l match {
    case Nil => List()
    case hd :: tl => {
      // Combine the first element (hd) with all others
      // in the remaining list (tl):
      val l = for (e <- tl) yield (hd, e)
      // Do this also for the remaining list (tl):
      val sl = combine(tl)
      l ::: sl
    }
  }
}
