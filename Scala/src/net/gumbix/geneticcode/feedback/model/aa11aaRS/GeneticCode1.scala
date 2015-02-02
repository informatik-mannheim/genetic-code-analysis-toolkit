package net.gumbix.geneticcode.model.aa11aaRS

import net.gumbix.geneticcode._
import core.{AminoAcidMapping, ProteinSequence}
import util.Random
import org.biojava3.core.sequence.transcription.TranscriptionEngine
import collection.mutable.HashMap
import org.biojava3.core.sequence.compound.AminoAcidCompound

/**
 * (c) 2012 by Markus Gumbel (m.gumbel@hs-mannheim.de)
 * @author Markus Gumbel
 */
abstract class GeneticCode1(thres: Double, initTable: feedback.MutuableCodeTable,
                   randomGen: Random)
  extends feedback.EvolvedGeneticCode(thres, initTable, randomGen) {

  override lazy val numberOfaaRSGenes = 20

  override def convertTable() {

    val teb: TranscriptionEngine.Builder = new TranscriptionEngine.Builder
    val te = teb.table(codeTables.last).build

    // Calculate new translations of aaRNs
    aaRSproteins = aaRSgenes.map(rna => ProteinSequence(te.translate(rna).toString))

    val prevTable = codeTables.last
    // Create translation table
    codeTables += new feedback.MutuableCodeTable


    import org.biojava3.core.sequence.ProteinSequence
    val aaRS2aa = new HashMap[ProteinSequence, AminoAcidCompound]
    val pairs = aaRSproteins.zip(codeTables.last.aminoAcids)
    pairs.foreach {
      p =>
        aaRS2aa(p._1) = p._2
    }

    for (tRNA <- tRNAs; aaRS <- aaRSproteins) {
      val aa = aaRS2aa(aaRS)
      if (recognize(tRNA, aaRS, aa)) {
        codeTables.last.add(tRNA.antiCodon, AminoAcidMapping(aa.toString))
      }
    }
  }

}
