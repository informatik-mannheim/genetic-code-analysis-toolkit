package net.gumbix.geneticcode.codes

import net.gumbix.geneticcode.dich.{Compound, Codon}
import net.gumbix.geneticcode.dich.CodonImplicitDefs._

/**
 * @author Markus Gumbel (m.gumbel@hs-mannheim.de)
 *         (c) 2012 Markus Gumbel
 */
class Code(val codons: Set[Codon]) {

  /**
   * TODO hack according to http://stackoverflow.com/questions/3307427/scala-double-definition-2-methods-have-the-same-type-erasure
   * @param codonsString
   */
  def this(codonsString: => Set[String]) = {
    this(codonsString.map(s => string2Codon(s)))
  }

  val n = codons.size

  val codonsL = codons.toList

  /**
   * Shift the codons of the sequence seq by p positions.
   * @param seq
   * @param p The positions to shift (1 or 2)
   * @return
   */
  def shift(seq: List[Codon], p: Int) = {
    require(p > 0 && p < 3)
    val u = seq.mkString("") + seq.head.toString // append first codon to simulate cycle
    val n = seq.size
    val c = u.substring(p, 3 * n + p)
    val d = for (i <- 0 to n - 1) yield {
      val s = c.substring(3 * i, 3 * i + 3)
      new Codon(s)
    }
    d.toList
  }

  /**
   * TODO Verify this (Elena & Co.)
   * Test if all codons in the sequence seq are a member of X (codon set).
   * @param seq
   * @return 1 if condition is true, or 0 if not. This is not a boolean
   *         variable because we compute with the result.
   */
  def containsX(seq: List[Codon]) = {
    if (seq.filter(c => codons.contains(c)).size == seq.size) 1 else 0
  }

  def isUnique(w: List[Codon]) = {
    val w0 = w
    val w1 = shift(w, 1)
    val w2 = shift(w, 2)
    val z = containsX(w0) + containsX(w1) + containsX(w2)
    z <= 1
  }

  /**
   * Create all words of length m
   * @param m
   * @return
   */
  def concats(m: Int): List[List[Codon]] = {
    def concatI(k: Int): List[List[Codon]] = {
      if (k <= 1) {
        // Just return all codons:
        for (c <- codonsL) yield List(c)
      }
      else {
        // Combine all codons with the already created lists of codons:
        val d = for (c <- codonsL) yield {
          val comb = concatI(k - 1)
          for (e <- comb) yield c :: e
        }
        d.flatten
      }
    }
    concatI(m)
  }

  /**
   * Is the code circular for a given m only.
   * @param m
   * @return
   */
  def isNCircularM(m: Int) = {
    val b = for (w <- concats(m)) yield isUnique(w)
    b.forall(p => p)
  }

  def isNCircular = {
    val g = math.min(4, n)
    val c = for (m <- 1 to g) yield isNCircularM(m)
    c.forall(p => p)
  }
}

object Code {
  // For Matlab
  def apply(codons: Array[String]) = new Code(Set() ++ codons)
}