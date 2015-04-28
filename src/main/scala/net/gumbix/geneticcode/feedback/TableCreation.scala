package net.gumbix.geneticcode.feedback

import scala.util.Random

import net.gumbix.geneticcode.core

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
        table.map(codon) = core.AminoAcidMapping(aa.toString)
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
        table.map(codon) = core.AminoAcidMapping(aa.toString)
    }
    table
  }

}
