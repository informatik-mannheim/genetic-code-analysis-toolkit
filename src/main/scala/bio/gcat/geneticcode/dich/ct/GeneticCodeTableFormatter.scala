package bio.gcat.geneticcode.dich.ct

import bio.gcat.geneticcode.dich._

/**
 * Formats a genetic code table.
 * @author Markus Gumbel (m.gumbel@hs-mannheim.de)
 *         (c) 2014 Markus Gumbel
 */
trait GeneticCodeTableFormatter {
  /**
   * Order of nucleotides in mkString.
   * @param c Nucleotide
   * @return A number between 1 to 4 representing the order.
   */
  def order = UcagOrder(_)

  def UcagOrder(c: Compound) = c match {
    case Adenine => 3
    case Cytosine => 2
    case Guanine => 4
    case Uracil => 1
  }

  def CguaOrder(c: Compound) = c match {
    case Adenine => 4
    case Cytosine => 1
    case Guanine => 2
    case Uracil => 3
  }
}
