package net.gumbix.geneticcode.core

import org.biojava3.core.sequence.{DNASequence, RNASequence}
import org.biojava3.core.sequence.compound.RNACompoundSet
import net.gumbix.geneticcode.feedback
import feedback.Codon

/**
 * tRNA with some required properties.
 * (c) 2012 by Markus Gumbel (m.gumbel@hs-mannheim.de)
 * @author Markus Gumbel
 */
case class tRNASequence(s: String) extends RNASequence(s) {

  def antiCodon = Codon(toString.take(3).toString)
}

object DNASequence {
  def apply(s: String) = new DNASequence(s)
}

object RNASequence {
  def apply(s: String) = new RNASequence(s)
}

object RNA4CompoundSet extends RNACompoundSet {

  def allUniqueCompounds = {
    List(getCompoundForString("U"), getCompoundForString("C"),
      getCompoundForString("A"), getCompoundForString("G"))
  }
}