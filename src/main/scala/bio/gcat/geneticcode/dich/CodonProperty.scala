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

import Nucleotides._
import scala.Array

/**
 * @author Markus Gumbel (m.gumbel@hs-mannheim.de)
 *         (c) 2013 Markus Gumbel
 */
trait CodonProperty extends Serializable {
  val codons = {
    for (c1 <- nucleotides; c2 <- nucleotides; c3 <- nucleotides) yield {
      Codon(Array(c1, c2, c3))
    }
  }

  lazy val size: Int = {
    val l = codons.map(property)
    val s = Set() ++ l // create set
    s.size
  }

  def property(c: Codon): String

  override def toString = "CodonProperty (" + size + " classes)"
}

object IdCodonProperty extends CodonProperty {
  def property(c: Codon) = c.toString

  override def toString = "IdCodonProperty (" + size + " classes)"
}

class IdAminoAcidProperty(val iupacNumber: Int)
  extends CodonProperty with AAImplicitDefs {

  def property(c: Codon) = codon2AA(c).toString

  override def toString = "Id AA with IUPAC " + iupacNumber + " (" + size + " classes)"
}

object IdStandardAminoAcidProperty extends IdAminoAcidProperty(1)

class PolarityAminoAcidProperty(iupacNumber: Int)
  extends IdAminoAcidProperty(iupacNumber) {

  override def property(c: Codon) = {
    val cm = codon2AA(c)
    cm.aminoAcid match {
      case Some(aa) => aa.toString match {
        case "A" => "N"
        case "R" => "B"
        case "N" => "P"
        case "D" => "A"
        case "C" => "N"
        case "E" => "A"
        case "Q" => "P"
        case "G" => "N"
        case "H" => "B"
        case "I" => "N"
        case "L" => "N"
        case "K" => "B"
        case "M" => "N"
        case "F" => "N"
        case "P" => "N"
        case "S" => "P"
        case "T" => "P"
        case "W" => "N"
        case "Y" => "P"
        case "V" => "N"
      }
      case _ => "_" // Stop
    }
  }

  override def toString = "Polarity AA with IUPAC " + iupacNumber + " (" + size + " classes)"
}

class PolarityPAminoAcidProperty(iupacNumber: Int)
  extends IdAminoAcidProperty(iupacNumber) {

  override def property(c: Codon) = {
    val cm = codon2AA(c)
    cm.aminoAcid match {
      case Some(aa) => aa.toString match {
        case "A" => "NN"
        case "R" => "BP"
        case "N" => "PN"
        case "D" => "AN"
        case "C" => "NN"
        case "E" => "AN"
        case "Q" => "PN"
        case "G" => "NN"
        case "H" => "BN"
        case "I" => "NN"
        case "L" => "NN"
        case "K" => "BP"
        case "M" => "NN"
        case "F" => "NN"
        case "P" => "NN"
        case "S" => "PN"
        case "T" => "PN"
        case "W" => "NN"
        case "Y" => "PN"
        case "V" => "NN"
      }
      case _ => "__" // Stop
    }
  }

  override def toString = "Polarity Plus AA with IUPAC " + iupacNumber + " (" + size + " classes)"
}