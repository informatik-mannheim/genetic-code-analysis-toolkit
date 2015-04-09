package net.gumbix.geneticcode.feedback

import collection.mutable.HashMap
import net.gumbix.geneticcode.core
import core.UnknownCodonMapping

/**
 * (c) 2012 by Markus Gumbel (m.gumbel@hs-mannheim.de)
 * @author Markus Gumbel
 */
class MutuableCodeTable extends AbstractBioJavaTable {

  val map = new HashMap[Codon, core.AminoAcidMapping]

  val counter = new HashMap[Codon, Int]

  def add(codon: Codon, mapping: core.AminoAcidMapping) {
    if (map.get(codon) == None) {
      counter(codon) = 1
      map(codon) = mapping
    } else {
      counter(codon) = counter(codon) + 1
      // println("Ambigous for " + tRNA.antiCodon)
    }
  }

  def numberAminoAcids = {
    degeneracyByAminoAcids.filter(e => e._2 != 0).size
  }

  def translate(codon: Codon) = {
    if (map.contains(codon)) {
      map(codon)
    } else {
      UnknownCodonMapping
    }
  }

  /**
   * Count the number of differences in the code table.
   * @param table
   * @return
   */
  def equalCodes(table: MutuableCodeTable) = {
    codons.map(codon => map.get(codon) != table.map.get(codon)).filter(b => b).size
  }

  def mkCounterString() = {
    var linebreak = 0
    codons.map {
      codon =>
        val c = if (map.get(codon) == None) "-" else counter(codon).toString
        val prefix = codon.toString + ": " + c
        linebreak += 1
        if (linebreak == 4) {
          linebreak = 0
          prefix + "\n"
        } else {
          prefix + "\t\t"
        }
    }.mkString
  }
}
