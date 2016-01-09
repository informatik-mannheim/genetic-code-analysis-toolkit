package bio.gcat.geneticcode.dich

import bio.gcat.geneticcode.core.{StopCodon, AminoAcidMapping}
import org.biojava3.core.sequence.transcription.TranscriptionEngine
import bio.gcat.geneticcode.core.DNASequence
import scala.language.implicitConversions

/**
 * @author Markus Gumbel (m.gumbel@hs-mannheim.de)
 *         (c) 2012 Markus Gumbel
 */

object IntListImplicitDefs {
  implicit def string2List(s: String) = s.map {
    c =>
      c match {
        case '0' => 0
        case '1' => 1
        case _ => throw new RuntimeException("Only 0 and 1 allowed.")
      }
  }.toList

  implicit def list2String(l: List[Int]) = l.mkString("")
}

object CompoundImplicitDefs {
  implicit def char2Codon(c: Char) = c match {
    case 'A' => Adenine
    case 'C' => Cytosine
    case 'U' => Uracil
    case 'T' => Uracil // TODO: any side effects here?
    case 'G' => Guanine
  }
}

object CodonImplicitDefs {
  implicit def string2Compounds(s: String) = s.map(c => CompoundImplicitDefs.char2Codon(c)).toArray

  implicit def string2Codon(s: String) = new Codon(string2Compounds(s))
}

object IUPAC {

  import java.util.HashMap

  // see http://www.ncbi.nlm.nih.gov/Taxonomy/Utils/wprintgc.cgi?mode=c
  // or http://www.ncbi.nlm.nih.gov/Taxonomy/taxonomyhome.html/index.cgi?chapter=tgencodes#SG1
  // for table view
  val STANDARD = 1
  val EUPLOTID_NUCLEAR = 10
  val VERTEBRATE_MITOCHONDRIAL = 2

  val codeTable = {
    val m = new HashMap[Int, TranscriptionEngine]()
    m.put(STANDARD, new TranscriptionEngine.Builder().initMet(false).table(STANDARD).build)
    m.put(EUPLOTID_NUCLEAR, new TranscriptionEngine.Builder().initMet(false).table(EUPLOTID_NUCLEAR).build)
    m.put(VERTEBRATE_MITOCHONDRIAL, new TranscriptionEngine.Builder().
      initMet(false).table(VERTEBRATE_MITOCHONDRIAL).build)
    m
  }
}

trait AAImplicitDefs {

  def iupacNumber: Int

  implicit def codon2AA(c: Codon) = {
    import scala.collection.JavaConversions._
    val dnaString = c.toString.map(e => if (e == 'U') 'T' else e).toString
    val h = IUPAC.codeTable(iupacNumber).translate(DNASequence(dnaString)).toString
    if (h == "") StopCodon else new AminoAcidMapping(h)
  }

  // TODO: Do we really need this?
  implicit def codon2AAChar(c: Codon) = codon2AA(c).toString.charAt(0)
}

object BdaImplicitDefs {

  import CompoundImplicitDefs.char2Codon

  // TODO implicit not possible?!
  def toBda(bdas: Array[String]) = bdas match {
    // Matlab can have null arguments
    case null => List[BinaryDichotomicAlgorithm]()
    case _ => {
      val b = for (bda <- bdas) yield {
        val s = bda.split(" ")
        val i1 = s(0).toInt
        val i2 = s(1).toInt
        val q1 = (char2Codon(s(2)(0)), char2Codon(s(2)(1)))
        val q2 = Set(char2Codon(s(3)(0)), char2Codon(s(3)(1)))
        new BinaryDichotomicAlgorithm(i1, i2, q1, q2)
      }
      b.toList
    }
  }

}