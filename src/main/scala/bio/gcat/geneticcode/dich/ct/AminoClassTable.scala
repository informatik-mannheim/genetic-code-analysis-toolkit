package bio.gcat.geneticcode.dich.ct

import bio.gcat.geneticcode.core.{TRNAStat, AminoAcidMapping}
import bio.gcat.geneticcode.dich.{Classifier, AaRSClassifier}
import scala.collection.JavaConversions._

/**
 * Class table that considers missing tRNAs.
 * @author Markus Gumbel (m.gumbel@hs-mannheim.de)
 *         (c) 2013 Markus Gumbel
 */
class AminoClassTable(bdas: List[Classifier[Int]], iupacNumber: Int)
  extends ClassTable(bdas, iupacNumber) {

  import bio.gcat.geneticcode.dich.Codon
  import bio.gcat.geneticcode.dich.CodonImplicitDefs._

  val aa2RS = new AaRSClassifier(iupacNumber)

  /**
   * Amino acids that have a tRNA. Data is read from a file that was
   * the result of a DB query.
   */
  val aatRNA = {
    val hs = TRNAStat.tRNAfromFile(".\\resources\\homo-sapiens-trna.txt",
      3, 361, 4)
    //val ecoli = TRNAStat.readFile("c:\\Users\\Markus\\Local-Docs\\src\\jvm\\GeneticCode\\resources\\e.coli-trna-all.txt",
    val ecoli = TRNAStat.tRNAfromFile(".\\resources\\e.coli-trna-all.txt",
      3, 102, 3)
    hs
  }

  val codonsWithtRNA = aatRNA.map(t => t._2.map(c => Codon(c))).flatten

  /**
   * Go through all amino acids that have tRNA and check
   * if the class assignment is compatible.
   */
  override def isAACompatible = {
    // List for all amino acids, indicating whether each amino acid
    // has belongs to exactly one class (true) or not (false):
    val list = aatRNA.map {
      t =>
        val codonsPerAA = t._2
        // Create the codon-classes per amino acids for those who have a tRNA:
        val classes = codonsPerAA.map(c => codon2class(Codon(c))).toList
        // println(classes)
        // Add them to set set to remove duplicates:
        val set = scala.collection.immutable.HashSet() ++ classes
        set.size == 1 // Check if there is exactly one class
    }
    list.forall(b => b) // Condition must be true for all amino acids.
  }

  override def mkCellString(codon: Codon) = {
    val tRNA = if (codonsWithtRNA contains codon) " " else "-"
    codon.toString + "|" + codon2AA(codon).toString + "|" + tRNA +
      ": " + codon2class(codon).mkString("")
  }

  /**
   * All classes sorted by the number of codons in it.
   * @return
   */
  override def classesMkStringSize = {
    val sorted = class2codonList.toList.sortBy(x => x._2.size)
    sorted.map {
      e =>
        val o = e._2.toList.sortBy(c => codon2AA(c).toString).map {
          e =>
            val u = codon2AA(e) match {
              case w: AminoAcidMapping => aa2RS.aa2aaRS(w)
              case _ => "!"
            }
            codon2AA(e).toString + " (" + u + ", " + e.toString + ")"
        }
        e._1.mkString("") + " -> " + o.mkString(", ")
    }.mkString("\n")
  }
}
