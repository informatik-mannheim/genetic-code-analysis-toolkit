package net.gumbix.geneticcode.model

import scala.collection.JavaConversions._
import org.biojava3.core.sequence.ProteinSequence
import org.biojava3.core.sequence.compound.AminoAcidCompound
import util.Random
import net.gumbix.geneticcode.feedback.{MutuableCodeTable, EvolvedGeneticCode, HydroMapper}
import net.gumbix.geneticcode.core.tRNASequence

/**
 * (c) 2012 by Markus Gumbel (m.gumbel@hs-mannheim.de)
 * @author Markus Gumbel
 */
class HashValue(thres: Double, initTable: MutuableCodeTable, randomGen: Random)
  extends EvolvedGeneticCode(thres, initTable, randomGen)
with HydroMapper {

  val hashRange = 25

  /**
   * This is invariant (physical property)
   * @param tRNA
   * @param aaRS
   */
  def recognize(tRNA: tRNASequence, aaRS: ProteinSequence, aa: AminoAcidCompound) = {
    val a = tRNA.getAsList.take(6).map(s => s.hashCode()).reduceLeft(_ + _)
    val b = aaRS.getAsList.take(6).map(s => s.hashCode).reduceLeft(_ + _)
    val c = aa.hashCode()
    val u = ((a + b + c) % hashRange) / (hashRange * 1.0) < thres
    u
  }


  def recognizex(tRNA: tRNASequence, aaRS: ProteinSequence, aa: AminoAcidCompound) = {
    val a = tRNA.getAsList.take(6).map(s => s.hashCode()).reduceLeft(_ + _)
    val b = aaRS.getAsList.take(6).map(s => convert(s).hashCode).reduceLeft(_ + _)
    val c = aa.hashCode()
    val u = ((a + b + c) % hashRange) / (hashRange * 1.0) < thres
    u
  }
}
