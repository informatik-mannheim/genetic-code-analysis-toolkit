package bio.gcat.geneticcode.feedback

import bio.gcat.geneticcode
import bio.gcat.geneticcode.core.AminoAcidMapping

import scala.util.Random
import scala.collection.JavaConversions._

/**
 * (c) 2012 by Markus Gumbel (m.gumbel@hs-mannheim.de)
 * @author Markus Gumbel
 */
class TableCreation(randomGen: Random) {

  val randomTable = {
    val table = new MutuableCodeTable
    table.codons.foreach {
      codon =>
        val r = randomGen.nextInt(table.aminoAcids.size)
        val as = table.aminoAcids.toArray
        val aa = as(r)
        table.map(codon) = AminoAcidMapping(aa.toString)
    }
    table
  }

  val moduloTable = {
    val table = new MutuableCodeTable
    var i = 0
    table.codons.foreach {
      codon =>
        val as = table.aminoAcids.toArray
        val aa = as(i % as.size)
        i += 1
        table.map(codon) = geneticcode.core.AminoAcidMapping(aa.toString)
    }
    table
  }

}
