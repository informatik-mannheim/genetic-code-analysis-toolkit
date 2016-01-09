package bio.gcat.geneticcode.dich

import bio.gcat.geneticcode.dich.Nucleotides._

/**
 * @author Markus Gumbel (m.gumbel@hs-mannheim.de)
 *         (c) 2012 Markus Gumbel
 */
abstract class Classifier[A] extends Serializable {

  private val serialVersionUID = 20130322;

  def classify(c: Codon): A
}

/**
 * Binary dichotomic algorithm as in Definition 3.2
 * Verified.
 * @param i1 Index of first base.
 * @param i2 Index of second base.
 * @param q1 Tupel (B1, B2) for first question.
 * @param q2 Set Q2 = {B3, B4} for second question.
 * @return 0 for class H1 and 1 for H2.
 */
class BinaryDichotomicAlgorithm(val i1: Int, val i2: Int,
                                val q1: (Compound, Compound), val q2: Set[Compound])
  extends Classifier[Int] {
  def classify(c: Codon) = {
    if (c(i1) == q1._1) 0 // OK
    else if (c(i1) == q1._2) 1 // OK
    else if (q2 contains c(i2)) 0 // OK
    else 1 // OK
  }

  /**
   * The complement questions that partitions 0,1 into 1,0.
   */
  lazy val complement = {
    // TODO lazy is required to avoid endless recursion. Hmm.
    val q2Compl = nucleotides.toSet.diff(q2)
    new BinaryDichotomicAlgorithm(i1, i2, (q1._2, q1._1), q2Compl)
  }

  override def equals(that: Any) = that match {
    case d: BinaryDichotomicAlgorithm => {
      // TODO consider complement BDA
      d.i1 == i1 && d.i2 == i2 && d.q1 == q1 && d.q2 == q2
    }
    case _ => false
  }

  /**
   * @return A unique number for this BDA. Not finished yet.
   */
  def number = {
    def p(nuc: Compound) = nuc match {
      case Adenine => 0
      case Uracil => 1
      case Guanine => 2
      case Cytosine => 3
    }
    1
  }

  override def toString = {
    val s = (i1 + 1) + ", " + (i2 + 1) + ", (" + q1._1.toString + "," + q1._2.toString + ")" +
      ", {" + q2.mkString(",") + "}"
    val o = this match {
      case RumerBDA => "Rumer"
      case ParityBDA => "Parity"
      case AntiCodonBDA => "AntiCodon"
      case RumerBDA.complement => "Rumer_"
      case ParityBDA.complement => "Parity_"
      case AntiCodonBDA.complement => "AntiCodon_"
      case _ => ""
    }
    s + o
  }

  def toScalaString = {
    val s = i1 + ", " + i2 + ", (" + q1._1.toString + "," + q1._2.toString + ")" +
      ", Set(" + q2.mkString(",") + ")"
    s
  }
}

object BinaryDichotomicAlgorithm {
  def apply(i1: Int, i2: Int, q1: (Compound, Compound), q2: Set[Compound]) =
    new BinaryDichotomicAlgorithm(i1, i2, q1, q2)

  val nucs = Array(Adenine, Uracil, Cytosine, Guanine)

  val q1Nucs = List(
    (0, 1), (0, 2), (0, 3), (1, 2), (1, 3), (2, 3),
    (1, 0), (2, 0), (3, 0), (2, 1), (3, 1), (3, 2)
  )

  /**
   * Just half of all combinations as the missing nucleotide-tuples
   * would lead to a complement partition.
   */
  val q1NucsHalf = List(
    (0, 1), (0, 2), (0, 3), (1, 2), (1, 3), (2, 3)
  )

  // Only have half of all elements as they define a set
  val q2NucSets = List(
    (0, 1), (0, 2), (0, 3), (1, 2), (1, 3), (2, 3)
  )

  // 6 possibilities
  val positions = List((0, 1), (0, 2), (1, 2), (1, 0), (2, 0), (2, 1))

  /**
   * 432 different bdas.
   */
  val bdas = {
    val b = for (pos <- positions; q1 <- q1Nucs; q2 <- q2NucSets) yield {
      new BinaryDichotomicAlgorithm(pos._1, pos._2,
        (nucs(q1._1), nucs(q1._2)), Set(nucs(q2._1), nucs(q2._2)))
    }
    ClassifierSet(b.toArray, "432 BDAs.")
  }

  /**
   * 216 different bdas.
   */
  val bdas216 = {
    val b = for (pos <- positions; q1 <- q1NucsHalf; q2 <- q2NucSets) yield {
      new BinaryDichotomicAlgorithm(pos._1, pos._2,
        (nucs(q1._1), nucs(q1._2)), Set(nucs(q2._1), nucs(q2._2)))
    }
    ClassifierSet(b.toArray, "216 BDAs.")
  }

}

// OK
object RumerBDA
  extends BinaryDichotomicAlgorithm(1, 0, (Cytosine, Adenine), Set(Cytosine, Guanine))

// OK
object ParityBDA
  extends BinaryDichotomicAlgorithm(2, 1, (Guanine, Adenine), Set(Cytosine, Adenine))

// OK
object AntiCodonBDA
  extends BinaryDichotomicAlgorithm(0, 2, (Cytosine, Guanine), Set(Guanine, Adenine))