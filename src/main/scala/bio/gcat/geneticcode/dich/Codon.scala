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

import bio.gcat.geneticcode.dich.Nucleotides._
import bio.gcat.geneticcode.dich.CodonImplicitDefs._

/**
 * @author Markus Gumbel (m.gumbel@hs-mannheim.de)
 *         (c) 2012 Markus Gumbel
 */
case class Codon(c: Array[Compound]) {

  def this(s: String) = this(string2Compounds(s))

  def apply(i: Int) = c(i)

  override def toString = c.mkString("")

  /**
   * Required for managing objects in HashMap etc.
   * @return
   */
  override def hashCode() = c.mkString("").hashCode

  override def equals(that: Any) = that match {
    case c: Codon => {
      // TODO: == on ArrayBuffer does not work!
      c.toString == toString
    }
    case _ => false
  }
}

object Codon {
  val codons = {
    for (c1 <- nucleotides; c2 <- nucleotides; c3 <- nucleotides) yield {
      Codon(Array(c1, c2, c3))
    }
  }
}

class Compound extends Serializable {
  override def equals(that: Any) = that match {
    case c: Compound => c.toString == toString
    case _ => false
  }
}

object Adenine extends Compound {
  override def toString = "A"
}

object Uracil extends Compound {
  override def toString = "U"
}

object Cytosine extends Compound {
  override def toString = "C"
}

object Guanine extends Compound {
  override def toString = "G"
}

object Nucleotides {
  def nucleotides = Uracil :: Cytosine :: Adenine :: Guanine :: Nil
}