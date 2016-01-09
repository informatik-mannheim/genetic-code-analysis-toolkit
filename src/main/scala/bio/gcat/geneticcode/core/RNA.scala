package bio.gcat.geneticcode.core

import bio.gcat.geneticcode.feedback.Codon
import org.biojava3.core.sequence.compound.RNACompoundSet

/**
 * tRNA with some required properties.
 * (c) 2012 by Markus Gumbel (m.gumbel@hs-mannheim.de)
 * @author Markus Gumbel
 */
case class tRNASequence(s: String) extends org.biojava3.core.sequence.RNASequence(s) {
  def antiCodon = Codon(toString.take(3).toString)
}

object DNASequence {
  def apply(s: String) = new org.biojava3.core.sequence.DNASequence(s)
}

object RNASequence {
  def apply(s: String) = new org.biojava3.core.sequence.RNASequence(s)
}

object RNA4CompoundSet extends RNACompoundSet {

  def allUniqueCompounds = {
    List(getCompoundForString("U"), getCompoundForString("C"),
      getCompoundForString("A"), getCompoundForString("G"))
  }
}