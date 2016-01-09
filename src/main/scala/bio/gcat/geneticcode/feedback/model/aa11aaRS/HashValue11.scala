package bio.gcat.geneticcode.feedback.model.aa11aaRS

import bio.gcat.geneticcode.core.tRNASequence
import bio.gcat.geneticcode.feedback.{MutuableCodeTable, HydroMapper}

import scala.collection.JavaConversions._
import org.biojava3.core.sequence.ProteinSequence
import org.biojava3.core.sequence.compound.AminoAcidCompound
import util.Random

/**
 * (c) 2012 by Markus Gumbel (m.gumbel@hs-mannheim.de)
 * @author Markus Gumbel
 */
class HashValue11(thres: Double, initTable: MutuableCodeTable, randomGen: Random)
  extends GeneticCode1(thres, initTable, randomGen) with HydroMapper {

  val hashRange = 25

  /**
   * This is invariant (physical property)
   * @param tRNA
   * @param aaRS
   */
  def recognize(tRNA: tRNASequence, aaRS: ProteinSequence, aa: AminoAcidCompound) = {
    val a = tRNA.getAsList.take(6).map(s => s.hashCode).reduceLeft(_ + _)
    val b = aaRS.getAsList.take(6).map(s => s.hashCode).reduceLeft(_ + _)
    val u = ((a + b) % hashRange) / (hashRange * 1.0) < thres
    u
  }
}
