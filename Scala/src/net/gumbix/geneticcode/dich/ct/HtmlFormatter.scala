package net.gumbix.geneticcode.dich.ct

import net.gumbix.geneticcode.dich.{Codon, Compound, AAImplicitDefs}
import scala.collection.JavaConversions._

/**
 * Creates a HTML table from the class table.
 * @author Markus Gumbel (m.gumbel@hs-mannheim.de)
 *         (c) 2014 Markus Gumbel
 */
trait HtmlFormatter extends ClassTableFormatter with AAImplicitDefs {

  def htmlMkString(): String = htmlMkString(UcagOrder, true)

  /**
   * A genetic code table suitable for LaTeX.
   * @param ord Order of the codons, default UCAG.
   * @param useColor true: cells are colored, default true.
   * @return
   */
  def htmlMkString(ord: (Compound => Int) = order, useColor: Boolean = true) = {

    def colorList(codon: Codon) = {
      val (r, g, b) = colorRGB(codon)
      "bgcolor=\"rgb(" + r + "," + g + "," + b + ")\""
    }

    val sList = codons.sortBy(c => (ord(c(0)), ord(c(2)), ord(c(1))))
    val header = "<tr> <td/> " + sList.take(4).map {
      codon =>
        "<td>" + codon.toString()(1) + "</td><td/>"
    }.mkString + "<td/></tr>\n"
    var linebreak = 0
    var groupBreak = 0
    val table = sList.map {
      codon =>
        val binS = codon2class(codon).mkString("")
        // val color = if (binS(col) == '1') "\\cellcolor[gray]{0.8}" else ""
        val color = if (useColor) colorList(codon) else ""

        val prefix = "<td " + color + ">" + codon2AA(codon).toFullString + " </td> <td>" +
          codon2class(codon).mkString("") + "</td>"
        linebreak += 1
        linebreak match {
          case 1 => {
            "<tr> <td>" + codon.toString()(0) + " </td> " + prefix + " "
          }
          case 4 => {
            linebreak = 0
            groupBreak += 1
            if (groupBreak == 4) {
              groupBreak = 0
              prefix + " <td> " + codon.toString()(2) + " </td></tr>\n"
            } else {
              prefix + " <td> " + codon.toString()(2) + " </td></tr>\n"
            }
          }
          case _ => prefix
        }
    }.mkString
    """<style>
      table,th,td
      {
      border:1px solid black;
      }
    </style>""".stripMargin.toString +
      "\n<table border=\"1\" widht=\"300\" height=\"300\">\n" + header + table + "</table>"
  }
}
