package net.gumbix.geneticcode.core

import io.Source.fromFile
import scala.collection.mutable

/**
 * Reads a tRNA CLUSTAL file from tRNA database.
 * The first column looks like this:
 * tdbD00000241_Homo_sapie_9606_Ala_TGC
 * Here, Ala is the amino acid and TGC the DNA sequence, thus GCU the codon.
 * @author Markus Gumbel (m.gumbel@hs-mannheim.de)
 *         (c) 2013 Markus Gumbel
 */
object TRNAStat {

  def main(args: Array[String]) {
    val hs = tRNAfromFile(".\\resources\\homo-sapiens-trna.txt",
      3, 361, 4)
    println(hs.mkString("\n"))

    println("-----")
    val ecoli = tRNAfromFile("\".\\\\resources\\e.coli-trna-all.txt",
      3, 102, 3)
    println(ecoli.mkString("\n"))

  }

  /**
   * Reads the file and return a tuple containing
   * the (amino acids, set of codons with tRNA)
   * @param filename
   * @param from Line number in raw text file where the data block
   *             begins (typically row 3)
   * @param to Line number in raw text file where block ends.
   */
  def tRNAfromFile(filename: String, from: Int, to: Int, colStart: Int) = {
    val aa2tRNACodon = new mutable.HashMap[String, mutable.Set[String]]

    // Get lines of interest:
    val lines = fromFile(filename).getLines.drop(from - 1).take(to - from)
    for (line <- lines) {
      val lineTokens = line.split(" ")
      val firstColumn = lineTokens(0).trim
      val tokens = firstColumn.split("_")
      val aa = tokens(colStart).trim
      // val codon = Codon(antiCodon(tokens(colStart + 1).trim)) // Codon
      val codon = antiCodon(tokens(colStart + 1).trim) // Codon
      // println(aa + " " + codon)
      val prev = if (aa2tRNACodon.contains(aa)) aa2tRNACodon(aa) else mutable.SortedSet[String]()
      prev += codon
      aa2tRNACodon(aa) = prev
    }
    aa2tRNACodon.toList.sortBy(x => x._1)
  }

  def antiCodon(codon: String) = codon.reverse.map(complement)

  def complement(b: Char) = b match {
    case 'A' => 'U'
    case 'T' => 'A'
    case 'C' => 'G'
    case 'G' => 'C'
  }
}
