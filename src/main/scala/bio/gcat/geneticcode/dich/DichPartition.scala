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

import scala.collection.mutable
import Nucleotides._
import scala.collection.mutable.ArrayBuffer

/**
 * @author Markus Gumbel (m.gumbel@hs-mannheim.de)
 *         (c) 2016 Markus Gumbel
 */
trait DichPartition {

  def dich0(c: Compound): Compound

  def dich1(c: Compound): Compound

  def dich2(c: Compound): Compound

  def dichC(c: Codon) = Codon(Array(dich0(c(0)), dich1(c(1)), dich2(c(2))))

  val partitions = {
    val h1 = new ArrayBuffer[Codon]()
    val h2 = new ArrayBuffer[Codon]()
    for (i <- 0 until Codon.codons.size) {
      val c = Codon.codons(i)
      if (!h2.contains(c)) {
        val cCompl = dichC(c)
        h1 += c
        h2 += cCompl
      }
    }
    (h1, h2)
  }

  def h1 = partitions._1

  def h2 = partitions._2

  def isDichPartition = {
    h1.size == h2.size &&
      h1.intersect(h2).isEmpty &&
      h1.union(h2).size == Codon.codons.size
  }

  def mkString = {
    "|h1| = " + h1.size + ", |h2| = " + h2.size + "\n" +
    "h1        = " + h1.mkString("{", ", ", "}") + "\n" +
      "h2        = " + h2.mkString("{", ", ", "}") + "\n" +
      "h1 and h2 = " + h1.intersect(h2).mkString("{", ", ", "}") + "\n"
  }

  override def toString = "Dich. partition"
}

object IdMapping {
  def dich(c: Compound) = c match {
    case Adenine => Adenine
    case Cytosine => Cytosine
    case Guanine => Guanine
    case Uracil => Uracil
  }
}

object RumerMapping {
  def dich(c: Compound) = c match {
    case Adenine => Cytosine
    case Cytosine => Adenine
    case Guanine => Uracil
    case Uracil => Guanine
  }
}

object ParityMapping {
  def dich(c: Compound) = c match {
    case Uracil => Cytosine
    case Cytosine => Uracil
    case Adenine => Guanine
    case Guanine => Adenine
  }
}

object DichPartitionRumer extends DichPartition {
  def dich0(c: Compound) = RumerMapping.dich(c)

  def dich1(c: Compound) = RumerMapping.dich(c)

  def dich2(c: Compound) = RumerMapping.dich(c)
}
