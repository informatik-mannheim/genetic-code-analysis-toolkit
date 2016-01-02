package bio.gcat.geneticcode.dich

import bio.gcat.geneticcode.core.{StopCodon, CodonMapping}
import scala.collection.mutable
import bio.gcat.geneticcode.dich.CodonImplicitDefs._

/**
 * @author Markus Gumbel (m.gumbel@hs-mannheim.de)
 *         (c) 2013 Markus Gumbel
 */
abstract class C3Property(val label: String,
                          val codonsStringList: String) extends CodonProperty {
  override def toString = "C3 code table classifier (" + label + ")"
}

class C3PropertyCodon(label: String, codonsStringList: String)
  extends C3Property(label, codonsStringList) {

  val c3 = {
    val cds = codonsStringList.split(" ")
    val map = new mutable.HashMap[Codon, String]()
    cds.foreach {
      cs =>
        map(Codon(cs)) = cs
    }
    map
  }

  // TODO not String!!!
  def property(c: Codon) =
    if (c3.contains(c)) c3(c).toString else "!  "

}

class C3PropertyAA(label: String, codonsStringList: String, val iupacNumber: Int)
  extends C3Property(label, codonsStringList) with AAImplicitDefs {

  val c3 = {
    val cds = codonsStringList.split(" ")
    val map = new mutable.HashMap[Codon, CodonMapping]()
    cds.foreach {
      cs =>
        map(Codon(cs)) = codon2AA(Codon(cs)) //  or amino acid
    }
    map
  }

  // TODO not String!!!
  def property(c: Codon) =
    if (c3.contains(c)) c3(c).toString else StopCodon.toString

  /*
  def aaSize = {
    c3.values.toSet.size
  }
  */
}