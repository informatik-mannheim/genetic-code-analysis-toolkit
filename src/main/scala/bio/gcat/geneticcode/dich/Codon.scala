package bio.gcat.geneticcode.dich

import bio.gcat.geneticcode.dich.Nucleotides._
import bio.gcat.geneticcode.dich.CodonImplicitDefs._

/**
 * @author Markus Gumbel (m.gumbel@hs-mannheim.de)
 *         (c) 2012 Markus Gumbel
 */
case class Codon(c: Array[Compound]) {

  def this(s: String) = this(string2Compounds(s))

  def apply(i: Int) = c(i)

  override def toString = c.mkString("")

  /**
   * Required for managing objects in HashMap etc.
   * @return
   */
  override def hashCode() = c.mkString("").hashCode

  override def equals(that: Any) = that match {
    case c: Codon => {
      // TODO: == on ArrayBuffer does not work!
      c.toString == toString
    }
    case _ => false
  }
}

object Codon {
  val codons = {
    for (c1 <- nucleotides; c2 <- nucleotides; c3 <- nucleotides) yield {
      Codon(Array(c1, c2, c3))
    }
  }
}

class Compound extends Serializable {
  override def equals(that: Any) = that match {
    case c: Compound => c.toString == toString
    case _ => false
  }
}

object Adenine extends Compound {
  override def toString = "A"
}

object Uracil extends Compound {
  override def toString = "U"
}

object Cytosine extends Compound {
  override def toString = "C"
}

object Guanine extends Compound {
  override def toString = "G"
}

object Nucleotides {
  def nucleotides = Uracil :: Cytosine :: Adenine :: Guanine :: Nil
}