package net.gumbix.geneticcode.model

import scala.collection.JavaConversions._
import net.gumbix.geneticcode._
import core.{RNASequence, tRNASequence}
import org.biojava3.core.sequence.ProteinSequence
import org.biojava3.core.sequence.compound.AminoAcidCompound
import util.Random

/**
 * (c) 2012 by Markus Gumbel (m.gumbel@hs-mannheim.de)
 * @author Markus Gumbel
 */
class HydroClassAA(thres: Double, initTable: feedback.MutuableCodeTable, randomGen: Random)
  extends feedback.EvolvedGeneticCode(thres, initTable, randomGen) with feedback.HydroMapper {

  val m = 5

  def recognize(tRNA: tRNASequence, aaRS: ProteinSequence, aa: AminoAcidCompound) = {
    val tRNA1 = tRNA.getAsList.take(m)
    val rna = aaRS.getAsList.take(m-1).map(s => convert(s)).toList.mkString("")
    val aaRNA = RNASequence(rna + convert(aa))
    val count = tRNA1.zip(aaRNA).filter(e => (e._1 != e._2)).size
    // val c = aa.hashCode()
    val u = (count / (1.0 * m)) <= thres
    u
  }

}
