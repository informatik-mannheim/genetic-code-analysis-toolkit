package net.gumbix.geneticcode.core

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
