package net.gumbix.geneticcode.dich.ct

import net.gumbix.geneticcode.dich.{IUPAC, NonPowerBinString, Classifier}
import scala.collection.JavaConversions._

/**
 * @author Markus Gumbel (m.gumbel@hs-mannheim.de)
 *         (c) 2013 Markus Gumbel
 */
class NonPowerClassTable(bdas: List[Classifier[Int]], iupacNumber: Int)
  extends ClassTable(bdas, iupacNumber) {

  val npbs = new NonPowerBinString(IUPAC.EUPLOTID_NUCLEAR, 1)

  def classesMkString(idx: Int) = {
    val sorted = class2codons.toList.sortBy(x => x._1.toString)
    sorted.map {
      e =>
        val npr = npbs.prefixL(idx)(e._1.mkString(""))
        val o = e._2.map(e => codon2AA(e).toString).toList.sortBy(x => x.toString)
        e._1.mkString("") + " -> \n" +
          "  is: " + o.mkString(",") + "\n" +
          "    : " + npr.mkString(",") + "\n"

    }.mkString("\n")
  }
}