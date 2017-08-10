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

import bio.gcat.geneticcode.core.CodonMapping
import bio.gcat.geneticcode.dich.{CodonProperty, Codon, Classifier}
import java.util.{HashSet, HashMap}
import scala.collection.JavaConversions._

/**
 * TODO: Move to abstract class as these are properties of a class table.
 * @author Markus Gumbel (m.gumbel@hs-mannheim.de)
 *         (c) 2016 Markus Gumbel
 */
trait ClassTableFormatter extends GeneticCodeTableFormatter {
  val codons: List[Codon]
  val bdas: List[Classifier[Int]]
  val codonProperty: CodonProperty
  val class2codonList: HashMap[List[Int], List[Codon]]
  val codon2class: HashMap[Codon, List[Int]]
  val aa2classes: HashMap[CodonMapping, HashSet[List[Int]]]

  def colorRGB(codon: Codon) = {
    val list = codon2class(codon) // List of Int
    val m = list.size / 3
    val rList = list.take(m)
    val gList = list.drop(m).take(m)
    val bList = list.drop(2 * m)

    def rgbValue(l: List[Int]) = l match {
      case Nil => 255 // treat as white
      case _ => {
        val n = l.size
        val k = (0 until n).map(i => math.pow(2, i))
        val z = (k zip l).map(e => e._1 * e._2).sum
        val p = z / (math.pow(2, n) - 1) * 100 // 0 .. 100  or 000 to 111
        (255 - p).asInstanceOf[Int] // 155 .. 255 or 111 to 000
      }
    }

    val r = rgbValue(rList)
    val g = rgbValue(gList)
    val b = rgbValue(bList)
    (r, g, b)
  }
}
