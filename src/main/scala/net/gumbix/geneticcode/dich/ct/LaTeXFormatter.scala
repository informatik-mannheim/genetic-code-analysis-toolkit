package net.gumbix.geneticcode.dich.ct

import scala.collection.JavaConversions.mapAsScalaMap

import net.gumbix.geneticcode.dich.AAImplicitDefs
import net.gumbix.geneticcode.dich.BinaryDichotomicAlgorithm
import net.gumbix.geneticcode.dich.Codon
import net.gumbix.geneticcode.dich.Compound

/**
 * Methods that outputs a class table in LaTeX format.
 * @author Markus Gumbel (m.gumbel@hs-mannheim.de)
 *         (c) 2014 Markus Gumbel
 */
trait LaTeXFormatter extends ClassTableFormatter with AAImplicitDefs {

  /**
   * Questions (BDAs) in LaTex format.
   * @return
   */
  def laTeXBdaMkString() = {
    val numbers = 1 to bdas.size
    val list = bdas match {
      case rbda: List[BinaryDichotomicAlgorithm] => {
        // TODO really sort it?
        val sortedBdas = rbda // .sortBy(x => (x.i1, x.i2))
        val l = numbers zip sortedBdas
        l.map {
          e =>
            "$\\A_{" + e._1 + "}$ & (" + (e._2.i1 + 1) + ", " + (e._2.i2 + 1) + ") & $" +
              e._2.q1.toString + "$ & $" + e._2.q2.mkString("\\{", ",", "\\}") + "$"
        }
      }
      case _ => bdas
    }
    list.mkString("\\\\\\hline \n")
  }

  def laTeXMkString(): String = laTeXMkString(UcagOrder, true)

  /**
   * A genetic code table suitable for LaTeX.
   * @param ord Order of the codons, default UCAG.
   * @param useColor true: cells are colored, default true.
   * @return
   */
  def laTeXMkString(ord: (Compound => Int) = order, useColor: Boolean = true) = {

    def colorList(codon: Codon) = {
      val (r, g, b) = colorRGB(codon)
      "\\cellcolor[RGB]{" + r + "," + g + "," + b + "}"
    }

    val sList = codons.sortBy(c => (ord(c(0)), ord(c(2)), ord(c(1))))
    val header = " & " + sList.take(4).map {
      codon =>
        "\\multicolumn{2}{c|}{ " + codon.toString()(1) + "}" + " & "
    }.mkString + "\\\\\\hline \n"
    var linebreak = 0
    var groupBreak = 0
    val table = sList.map {
      codon =>
        val binS = codon2class(codon).mkString("")
        // val color = if (binS(col) == '1') "\\cellcolor[gray]{0.8}" else ""
        val color = if (useColor) colorList(codon) else ""

        val prefix = color + codon2AA(codon).toFullString + " & " +
          codon2class(codon).mkString("")
        linebreak += 1
        linebreak match {
          case 1 => {
            codon.toString()(0) + " & " + prefix + " & "
          }
          case 4 => {
            linebreak = 0
            groupBreak += 1
            if (groupBreak == 4) {
              groupBreak = 0
              prefix + " & " + codon.toString()(2) + "\\\\\\hline \\hline \n"
            } else {
              prefix + " & " + codon.toString()(2) + "\\\\\\hline \n"
            }
          }
          case _ => prefix + " & "
        }
    }.mkString
    header + table
  }

}
