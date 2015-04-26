package net.gumbix.geneticcode.dich.ct

/**
 * @author Markus Gumbel (m.gumbel@hs-mannheim.de)
 *         (c) 2014 Markus Gumbel
 */
trait MatlabFormatter extends ClassTableFormatter {

  /**
   * A list of bdas separated by newline.
   * @return
   */
  def bdaMkMatlabString = {
    val numbers = 1 to bdas.size
    val l = numbers zip bdas
    l.map(e => e._1 + ": " + e._2).mkString("\n")
  }
}
