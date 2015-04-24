package net.gumbix.geneticcode.dich

import net.gumbix.geneticcode.dich.CodonImplicitDefs._
import scala.collection.mutable

/**
 * @author Markus Gumbel (m.gumbel@hs-mannheim.de)
 *         (c) 2014 Markus Gumbel
 */
abstract class DichProperty(val label: String)
  extends CodonProperty with DichPartition {

  def property(c: Codon) = {
    if (h1.contains(c)) "0" else if (h2.contains(c)) "1" else "_"
  }

  override def toString = "Dich. code table classifier (" + label + ")"
}
