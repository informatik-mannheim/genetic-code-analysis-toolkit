package bio.gcat.geneticcode.feedback.model.aa11aaRS

import bio.gcat.geneticcode.core.{RNASequence, tRNASequence}
import bio.gcat.geneticcode.feedback.{HydroMapper, MutuableCodeTable}

import scala.collection.JavaConversions._
import org.biojava3.core.sequence.ProteinSequence
import org.biojava3.core.sequence.compound.AminoAcidCompound
import util.Random

/**
 * (c) 2012 by Markus Gumbel (m.gumbel@hs-mannheim.de)
 * @author Markus Gumbel
 */
class HydroClassAA11(thres: Double, initTable: MutuableCodeTable, randomGen: Random)
  extends GeneticCode1(thres, initTable, randomGen) with HydroMapper {

  val m = 20

  def recognize(tRNA: tRNASequence, aaRS: ProteinSequence, aa: AminoAcidCompound) = {
    val tRNARec = tRNA.getAsList.take(m)
    val aaRSnetto = aaRS.getAsList.take(m + 1).drop(1)
    val aaRSRNA = aaRSnetto.map(s => convert(s)).toList.mkString("")
    val aaRSRec = RNASequence(aaRSRNA)
    val count = tRNARec.zip(aaRSRec).filter(e => (e._1 != e._2)).size
    val u = (count / (1.0 * m)) <= thres
    u
  }
}
