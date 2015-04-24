package net.gumbix.geneticcode.feedback

import org.biojava3.core.sequence.compound.{AminoAcidCompound, NucleotideCompound}
import org.biojava3.core.sequence.transcription.Table
import org.biojava3.core.sequence.template.{AbstractCompoundSet, CompoundSet}
import scala.collection.JavaConversions._
import java.util
import org.apache.commons.math.stat.Frequency
import net.gumbix.geneticcode._
import net.gumbix.geneticcode.core.{StopCodon, AminoAcid20CompoundSet, RNA4CompoundSet}

/**
 * A genetic code table.
 * (c) 2012 by Markus Gumbel (m.gumbel@hs-mannheim.de)
 * @author Markus Gumbel
 */
trait GeneticCodeTable {
  /**
   * Translate a codon or indicate that it is a stop codon.
   * @param codon
   * @return
   */
  def translate(codon: Codon): core.CodonMapping

  def reverseTranslate(m: core.CodonMapping) = {
    codons.filter {
      codon =>
        if (translate(codon) == m) true else false
    }
  }

  def numberAminoAcids: Int

  /**
   * The underlying rna nucleotides.
   */
  val rnaCompounds = RNA4CompoundSet.allUniqueCompounds

  /**
   * The underlying 20 amino acids.
   */
  val aminoAcids = AminoAcid20CompoundSet.allUniqueCompounds

  /**
   * A sorted list of all 64 codons.
   */
  def codons = {
    for (b1 <- rnaCompounds; b2 <- rnaCompounds; b3 <- rnaCompounds) yield {
      Codon(b1.toString + b2.toString + b3.toString)
    }
  }

  /**
   * A list of all amino acids and their codons.
   */
  def reverseList = {
    // Convert all amino acids into mappings, re-translate them and store
    // them in a tuple:
    aminoAcids.map(aa => core.AminoAcidMapping(aa.toString)).map(a => (a, reverseTranslate(a)))
  }

  def degeneracyByAminoAcids = reverseList.map(e => (e._1, e._2.size))

  /**
   * The degenercy of the genetic code (according to Gonzales et al.)
   * @return
   */
  def degeneracy = {
    val hist = new Frequency()
    degeneracyByAminoAcids.foreach(e => hist.addValue(e._2))
    (1 to 64).map(hist.getCount(_))
  }

  def mkString() = {
    var linebreak = 0
    codons.map {
      codon =>
        val prefix = codon.toString + ": " + translate(codon).toString
        linebreak += 1
        if (linebreak == 4) {
          linebreak = 0
          prefix + "\n"
        } else {
          prefix + "    "
        }
    }.mkString
  }
}

/**
 * Wrapper class for BioJava3.
 */
abstract class AbstractBioJavaTable extends Table with GeneticCodeTable {

  override def getCodonCompoundSet(rnaCompounds: CompoundSet[NucleotideCompound],
                                   aminoAcidCompounds: CompoundSet[AminoAcidCompound]): CompoundSet[Table.Codon] = {
    // TODO
    new AbstractCompoundSet[Table.Codon]() {
    }
  }

  def getCodons(nucelotides: CompoundSet[NucleotideCompound],
                aminoAcids: CompoundSet[AminoAcidCompound]) = {

    val codons = for (b1 <- rnaCompounds;
                      b2 <- rnaCompounds;
                      b3 <- rnaCompounds) yield {
      val seq = Codon(b1.toString + b2.toString + b3.toString)
      val aa = translate(seq)
      val triplet = new Table.CaseInsensitiveTriplet(b1, b2, b3)
      val aac = aa match {
        case core.UnknownCodonMapping => aminoAcids.getCompoundForString("*")
        case am: core.AminoAcidMapping => am.aminoAcid.get
      }
      val isStop = aa match {
        case core.UnknownCodonMapping => true
        case _ => false
      }
      // TODO always a start codon
      new Table.Codon(triplet, aac, true, isStop)
    }
    new util.ArrayList[Table.Codon](codons)
  }

  def isStart(compound: AminoAcidCompound) = true // TODO
}

/**
 * The standard genetic code.
 */
class StandardGeneticCodeTable extends AbstractBioJavaTable {

  def numberAminoAcids = 64

  def translate(codon: Codon) = {
    val aa = codon.getProteinSequence
    if (aa.getLength == 0) StopCodon
    else
    // TODO consider exceptions
      core.AminoAcidMapping(aa.toString)
  }
}