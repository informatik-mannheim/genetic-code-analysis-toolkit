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

import org.biojava3.core.sequence.compound.AminoAcidCompoundSet

/**
 * (c) 2012 by Markus Gumbel (m.gumbel@hs-mannheim.de)
 * @author Markus Gumbel
 */

object ProteinSequence {
  def apply(s: String) = new org.biojava3.core.sequence.ProteinSequence(s)

  // def apply(seq: List[AminoAcid]) = new Peptide(seq)
}

object AminoAcid20CompoundSet extends AminoAcidCompoundSet {

  /**
   * Sorted by three character name (Ala, Arg, ...)
   * @return
   */
  def allUniqueCompounds = {
    List(getCompoundForString("A"),
      getCompoundForString("R"),
      getCompoundForString("N"),
      getCompoundForString("D"),
      getCompoundForString("C"),
      getCompoundForString("E"),
      getCompoundForString("Q"),
      getCompoundForString("G"),
      getCompoundForString("H"),
      getCompoundForString("I"),
      getCompoundForString("L"),
      getCompoundForString("K"),
      getCompoundForString("M"),
      getCompoundForString("F"),
      getCompoundForString("P"),
      getCompoundForString("S"),
      getCompoundForString("T"),
      getCompoundForString("W"),
      getCompoundForString("Y"),
      getCompoundForString("V")
    )
  }
}
