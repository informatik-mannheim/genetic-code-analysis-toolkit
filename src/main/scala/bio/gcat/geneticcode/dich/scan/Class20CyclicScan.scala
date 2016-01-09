package bio.gcat.geneticcode.dich.scan

import bio.gcat.geneticcode.dich._
import bio.gcat.geneticcode.dich.ct.ClassTable
import scala.collection.JavaConversions._

/**
 * Used for cyclic codes.
 * @version not completed
 * @author Markus Gumbel (m.gumbel@hs-mannheim.de)
 *         (c) 2012 Markus Gumbel
 */
class Class20CyclicTable(bdas: List[Classifier[Int]], iupacNumber: Int)
  extends ClassTable(bdas, iupacNumber) {

  override lazy val codons = {
    //val c = "AAC GUU AAG CUU AAU AUU ACC GGU ACG CGU ACU AGU AGC GCU AGG CCU CCG CGG UCA UGA"
     val c = "AAC GUU AAU AUU ACC GGU ACG CGU ACU AGU AGA UCU AGC GCU GCC GGC GGA UCC UCA UGA"
    c.split(" ").map {
      codon => CodonImplicitDefs.string2Codon(codon)
    }.toList
  }
}

class Class20CyclicScan(bdas: List[Classifier[Int]], size: Int)
  extends Scan[ClassTable](bdas, size) {

  override def newClassTable(bda: List[Classifier[Int]]) =
    new Class20CyclicTable(bda, IUPAC.EUPLOTID_NUCLEAR)

  def this(bdas: Array[String], size: Int) =
    this(BdaImplicitDefs.toBda(bdas), size)

  override def isValidConfig(t: ClassTable, w: Int) = t.classes.size <= 20

  override def isSolution(t: ClassTable) = t.classes.size == 20
}

object Class20CyclicScan {

  def main(args: Array[String]) {
    new Class20CyclicScan(List(ParityBDA, RumerBDA, AntiCodonBDA), 5).run()
  }
}
