package bio.gcat.geneticcode.feedback

import org.biojava3.core.sequence.compound.AminoAcidCompound
import org.biojava3.core.sequence.RNASequence
import bio.gcat.geneticcode.core

/**
 * (c) 2012 by Markus Gumbel (m.gumbel@hs-mannheim.de)
 * @author Markus Gumbel
 */
trait AA2RNAMapper {
  def convert(aa: AminoAcidCompound): RNASequence
}

trait HydroMapper extends AA2RNAMapper {

  def convert(aa: AminoAcidCompound) = aa.getShortName match {
    case "A" => core.RNASequence("A")
    case "V" => core.RNASequence("A")
    case "M" => core.RNASequence("A")
    case "L" => core.RNASequence("A")
    case "I" => core.RNASequence("A")
    case "W" => core.RNASequence("A")
    case "P" => core.RNASequence("A")
    case "F" => core.RNASequence("A")
    case "Y" => core.RNASequence("U")
    case "T" => core.RNASequence("U")
    case "Q" => core.RNASequence("U")
    case "G" => core.RNASequence("U")
    case "S" => core.RNASequence("U")
    case "C" => core.RNASequence("U")
    case "N" => core.RNASequence("U")
    case "K" => core.RNASequence("C")
    case "R" => core.RNASequence("C")
    case "H" => core.RNASequence("C")
    case "E" => core.RNASequence("G")
    case "D" => core.RNASequence("G")
    case _ => core.RNASequence("G") // TODO
  }

}
