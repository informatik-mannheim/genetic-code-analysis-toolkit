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
package bio.gcat.geneticcode.core

import org.biojava.nbio.core.sequence.compound.RNACompoundSet

/**
 * tRNA with some required properties.
 * (c) 2012 by Markus Gumbel (m.gumbel@hs-mannheim.de)
 * @author Markus Gumbel
 */
case class tRNASequence(s: String) extends org.biojava.nbio.core.sequence.RNASequence(s) {
  def antiCodon = Codon(toString.take(3).toString)
}

object DNASequence {
  def apply(s: String) = new org.biojava.nbio.core.sequence.DNASequence(s)
}

object RNASequence {
  def apply(s: String) = new org.biojava.nbio.core.sequence.RNASequence(s)
}

object RNA4CompoundSet extends RNACompoundSet {

  def allUniqueCompounds = {
    List(getCompoundForString("U"), getCompoundForString("C"),
      getCompoundForString("A"), getCompoundForString("G"))
  }
}