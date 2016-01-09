package bio.gcat.geneticcode.core

import org.biojava3.core.sequence.compound.{AminoAcidCompound, AminoAcidCompoundSet}

/**
 * (c) 2012 by Markus Gumbel (m.gumbel@hs-mannheim.de)
 * @author Markus Gumbel
 */

abstract class CodonMapping() extends Serializable {

  def aminoAcid: Option[AminoAcidCompound] = None
  def isStart = false
  def isStop = false

  def toFullString = toString
}

case class AminoAcidMapping(s: String) extends CodonMapping {
  override def aminoAcid = Some(new AminoAcidCompoundSet().getCompoundForString(s))

  override def toString = aminoAcid.get.toString

  override def toFullString = aminoAcid.get.getLongName
}

object StartCodon extends CodonMapping {
  override def isStart = true
  override def toString = ">"
}

object StopCodon extends CodonMapping {
  override def isStop = true
  override def toString = "!"
}

object UnknownCodonMapping extends CodonMapping {
  override def toString = "?"
}
