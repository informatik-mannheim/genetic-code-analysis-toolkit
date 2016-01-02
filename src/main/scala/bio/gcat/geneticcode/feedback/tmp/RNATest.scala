package bio.gcat.geneticcode.feedback.tmp

import org.biojava3.core.sequence._
import org.biojava3.core.sequence.compound.RNACompoundSet
import org.biojava3.core.sequence.io.IUPACParser
import org.biojava3.core.sequence.transcription.{Table, TranscriptionEngine}

import scala.collection.JavaConversions._

/**
 * (c) 2012 by Markus Gumbel (m.gumbel@hs-mannheim.de)
 * @author Markus Gumbel
 */
object RNATest {

  def main(args: Array[String]) {
    val rna = new RNASequence("AGUC")
    println(rna)
    // rna.getAsList.foreach(base => println(base.getMolecularWeight))

    val protein = new DNASequence("AATGATCG").getRNASequence.getProteinSequence
    println(protein)

    val table: Table = IUPACParser.getInstance().getTable(1)

    val teb: TranscriptionEngine.Builder = new TranscriptionEngine.Builder
    val te: TranscriptionEngine = teb.table(3).build
    val table2 = te.getTable()
    val codons = table2.getCodons(te.getRnaCompounds, te.getAminoAcidCompounds)
    println(te.getRnaCompounds.getAllCompounds)
    println(codons.map{
      codon =>
        codon.getTriplet + " -> " + codon.getAminoAcid
    }.mkString(", "))

    // RNA compound set contains all RNA nucleotides:
    val rnaCompounds2 = new RNACompoundSet()
    println("My RNA compounds: " + rnaCompounds2.getAllCompounds)

    // TODO Apparently the arguments are not used and all codons are returned, though:
    //val codons2 = table2.getCodons(rnaCompounds2, te.getAminoAcidCompounds)
    val codons2 = table2.getCodons(null, null)
    println(codons2.map{
      codon =>
        codon.getTriplet + " -> " + codon.getAminoAcid
    }.mkString(", "))

    val aa = te.translate(new DNASequence("GGG"))
    println("GGG -> " + aa)

    /*
    val seq1 = new ChromosomeSequence("AATGATCG")
    val seq2 = new GeneSequence(seq1, 0, seq1.size-1, Strand.POSITIVE)
    val seq3 = new TranscriptSequence(seq2, 0, seq2.size-1)
    seq3.addCDS(new AccessionID(), 1, seq2.size-1, 0)
    println(seq3)
    println(seq3.getStartCodonSequence)
    val prot1 = seq3.getProteinSequence
    println(prot1)
    */
  }

}
