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

import bio.gcat.geneticcode.dich.{AAImplicitDefs, Codon}
import scala.collection.JavaConversions._

/**
 * Outputs a class table in several plain text formats.
 * @author Markus Gumbel (m.gumbel@hs-mannheim.de)
 *         (c) 2016 Markus Gumbel
 */
trait PlainTextFormatter extends ClassTableFormatter with AAImplicitDefs {

  /**
   * A genetic code table.
   * @return
   */
  def mkString() = {
    var linebreak = 0
    var rowbreak = 0
    codons.sortBy(c => (order(c(0)), order(c(2)), order(c(1)))).map {
      codon =>
        val prefix = mkCellString(codon)
        linebreak += 1
        val eol = if (linebreak == 4) {
          linebreak = 0
          rowbreak += 1
          "\n"
        } else {
          "    "
        }
        if (rowbreak == 4) {
          rowbreak = 0
          prefix + eol + "\n"
        } else {
          prefix + eol
        }
    }.mkString
  }

  /**
   * A list of bdas separated by newline.
   * @return
   */
  def bdaMkString = {
    val numbers = 1 to bdas.size
    val l = numbers zip bdas
    l.map(e => e._1 + ": " + e._2).mkString("\n")
  }

  /**
   * All classes sorted by its binary string.
   * @return
   */
  def classesMkString = {
    val sorted = class2codonList.toList.sortBy(x => x._1.toString)
    sorted.map {
      e =>
        val o = e._2.map {
          e => e.toString + "|" + codon2AA(e).toString
        }
        e._1.mkString("") + " -> " + o.mkString(", ")
    }.mkString("\n")
  }

  /**
   * List all amino acids and their classes they belong to.
   * @return
   */
  def aaMkString() = {
    aa2classes.map {
      e =>
        val (aa, classes) = e
        val o = classes.map(e => e.mkString)
        aa.toString + " -> " + o.mkString(";")
    }.toList.sortBy(x => x.toString).mkString("\n")
  }


  /**
   * All classes sorted by the number of codons in it.
   * @return
   */
  def classesMkStringSize = {
    val sorted = class2codonList.toList.sortBy(x => x._2.size)
    sorted.map {
      e =>
        val o = e._2.map(c => c.toString + "|" + codon2AA(c).toString)
        e._1.mkString("") + " -> " + o.mkString(", ")
    }.mkString("\n")
  }

  def mkCellString(codon: Codon) = {
    codon.toString + "|" + codon2AA(codon).toString +
      "|" + codonProperty.property(codon).toString +
      ": " + codon2class(codon).mkString("")
  }

  /**
   * All codons as strings sorted alphabetically.
   * @return
   */
  def mkCodonsLinearString() = {
    val l = codon2class.entrySet.toList.sortBy(x => x.getKey.toString)
    l.map(e => e.getKey.toString).mkString(" ")
  }

  /**
   * All classes as strings sorted alphabetically by its codon.
   * @return
   */
  def mkClassesLinearString() = {
    val l = codon2class.entrySet.toList.sortBy(x => x.getKey.toString)
    l.map(e => e.getValue.mkString).mkString(" ")
  }
}
