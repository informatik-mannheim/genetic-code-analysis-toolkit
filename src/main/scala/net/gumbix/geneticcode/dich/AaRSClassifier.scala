package net.gumbix.geneticcode.dich

import java.util.HashMap
import net.gumbix.geneticcode.core.AminoAcidMapping

/**
 * @author Markus Gumbel (m.gumbel@hs-mannheim.de)
 *         (c) 2013 Markus Gumbel
 */
class AaRSClassifier(val iupacNumber: Int)
  extends Classifier[Int] with AAImplicitDefs {

  import net.gumbix.geneticcode.core.{AminoAcidMapping => AA}
  import scala.collection.JavaConversions._

  val aa2aaRS = {
    val m = new HashMap[AminoAcidMapping, Int]

    m.put(AA("R"), 0)
    m.put(AA("C"), 0)
    m.put(AA("I"), 0)
    m.put(AA("L"), 0)
    m.put(AA("M"), 0)
    m.put(AA("V"), 0)
    m.put(AA("Q"), 0)
    m.put(AA("E"), 0)
    m.put(AA("W"), 0)
    m.put(AA("Y"), 0)
    m.put(AA("G"), 1)
    m.put(AA("H"), 1)
    m.put(AA("P"), 1)
    m.put(AA("S"), 1)
    m.put(AA("T"), 1)
    m.put(AA("N"), 1)
    m.put(AA("D"), 1)
    m.put(AA("K"), 0) // or 2, both!
    m.put(AA("A"), 1)
    m.put(AA("F"), 1)
    m
  }

  def classify(c: Codon) = codon2AA(c) match {
    case h: AminoAcidMapping => aa2aaRS(h)
    case _ => 2
  }

  override def toString = "aaRS classifier (" + iupacNumber + ")"
}
