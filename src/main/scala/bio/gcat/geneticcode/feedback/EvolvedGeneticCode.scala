package bio.gcat.geneticcode.feedback

import bio.gcat.geneticcode.core.AminoAcidMapping
import bio.gcat.geneticcode._
import org.biojava3.core.sequence.{DNASequence, ProteinSequence}
import org.biojava3.core.sequence.compound.AminoAcidCompound
import org.biojava3.core.sequence.transcription.TranscriptionEngine

import scala.collection.mutable.ArrayBuffer
import scala.util.Random
import scala.collection.JavaConversions._

/**
 * (c) 2012 by Markus Gumbel (m.gumbel@hs-mannheim.de)
 * @author Markus Gumbel
 */

abstract class EvolvedGeneticCode(thres: Double, initTable: feedback.MutuableCodeTable,
                                  randomGen: Random) extends feedback.AA2RNAMapper {

  val codeTables = new ArrayBuffer[feedback.MutuableCodeTable]()

  val seqSize = 20

  /**
   * How many genes for aaRS do we consider?
   */
  lazy val numberOfaaRSGenes = 30

  codeTables += initTable

  val tRNAs = {
    // Make sure we have anti-codons for each codon:
    codeTables.last.codons.map {
      codon => core.tRNASequence(codon.toString +
        core.DNASequence(randomSequence(seqSize-3).mkString("")).getRNASequence.toString)
    }
  }

  // TODO might contain stop codons!
  var aaRSgenes: List[DNASequence] = {
    (1 to numberOfaaRSGenes).map(z => core.DNASequence("ATG" + randomSequence(3 * seqSize).mkString)).toList
  }

  var aaRSproteins = {
    val teb: TranscriptionEngine.Builder = new TranscriptionEngine.Builder
    val te = teb.table(0).build
    aaRSgenes.map(rna => core.ProteinSequence(te.translate(rna).toString))
  }

  def convertTable() {

    val teb: TranscriptionEngine.Builder = new TranscriptionEngine.Builder
    val te = teb.table(codeTables.last).build

    // Calculate new translations of aaRNs
    aaRSproteins = aaRSgenes.map(rna => core.ProteinSequence(te.translate(rna).toString))

    val prevTable = codeTables.last
    // Create translation table
    codeTables += new feedback.MutuableCodeTable

    for (tRNA <- tRNAs; aaRS <- aaRSproteins; aa <- codeTables.last.aminoAcids) {
      if (recognize(tRNA, aaRS, aa)) {
        codeTables.last.add(tRNA.antiCodon, AminoAcidMapping(aa.toString))
      }
    }
  }

  def recognize(tRNA: core.tRNASequence, aaRS: ProteinSequence, aa: AminoAcidCompound): Boolean

  def randomSequence(size: Int) = {
    val nucleotides = "ATGC" // TODO performance
    (1 to size).map(o => randomGen.nextInt(nucleotides.size)).map(nucleotides(_)).toList
  }
}
