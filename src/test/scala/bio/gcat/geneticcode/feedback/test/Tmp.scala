package bio.gcat.geneticcode.feedback.test

import bio.gcat.geneticcode.core.DNASequence
import org.biojava3.core.sequence.transcription.TranscriptionEngine
import junit.framework.TestCase

/**
 * (c) 2012 by Markus Gumbel (m.gumbel@hs-mannheim.de)
 * @author Markus Gumbel
 */
class Tmp extends TestCase {

  def testMain() {

    /*
    val codeTable = new MutuableCodeTable()
    codeTable.add(Codon("AAA"), AminoAcidMapping("F"))
    codeTable.add(Codon("AUG"), AminoAcidMapping("M"))
    println(codeTable.mkString())
    println(codeTable.reverseList.mkString(" "))
    println(codeTable.degeneracyByAminoAcids.mkString)
    println("degeneracy: " + codeTable.degeneracy.mkString(", "))
    */

    val teb = new TranscriptionEngine.Builder
    val te = teb.table(1).build
    val p = te.translate(DNASequence("TAA"))
    println(p)


    /*
    val codeTable2 = new StandardGeneticCodeTable()
    println(codeTable2.mkString())
    println(codeTable2.reverseList.mkString(" "))
    println(codeTable2.degeneracyByAminoAcids.mkString)
    println("degeneracy: " + codeTable2.degeneracy.mkString(", "))
    */
  }

}
