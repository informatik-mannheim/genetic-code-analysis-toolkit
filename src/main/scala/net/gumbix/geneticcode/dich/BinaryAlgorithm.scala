package net.gumbix.geneticcode.dich

import net.gumbix.geneticcode.dich.Classifier

/**
 * As thought on Nov. 2nd
 * @author Markus Gumbel (m.gumbel@hs-mannheim.de)
 *         (c) 2013 Markus Gumbel
 */
class BinaryAlgorithm(val i1: Int, val i2: Int, val i3: Int,
                      val q1: Set[Compound], val q2: Set[Compound],
                      val q3: Set[Compound])
  extends Classifier[Int] {
  def classify(c: Codon) = {
    if (q1 contains c(i1)) {
      if (q2 contains c(i2)) 0 else 1
    } else {
      if (q3 contains c(i3)) 0 else 1
    }
  }

  override def equals(that: Any) = that match {
    case d: DichClassifier4 => {
      d.i1 == i1 && d.i2 == i2 && d.i3 == i3 &&
        d.q1 == q1 && d.q2 == q2 && d.q3 == q3
    }
    case _ => false
  }

  override def toString = {
    val s = (i1 + 1) + ", " + (i2 + 1) + ", " + (i3 + 1) +
      ", {" + q1.mkString(",") + "}" +
      ", {" + q2.mkString(",") + "}" +
      ", {" + q3.mkString(",") + "}"
    "BA " + s
  }
}

object BinaryAlgorithm {
  val allBAs = {
    val nucs = Array(Adenine, Uracil, Cytosine, Guanine)
    val q1Nucs = List(
      (0, 1), (0, 2), (0, 3), (1, 2), (1, 3), (2, 3),
      (1, 0), (2, 0), (3, 0), (2, 1), (3, 1), (3, 2)
    )

    val u = for (i1 <- 0 to 2; i2 <- 0 to 2; i3 <- 0 to 2;
                 q1 <- q1Nucs; q2 <- q1Nucs; q3 <- q1Nucs) yield {
      new BinaryAlgorithm(i1, i2, i3,
        Set(nucs(q1._1), nucs(q1._2)),
        Set(nucs(q2._1), nucs(q2._2)),
        Set(nucs(q3._1), nucs(q3._2)))
    }
    ClassifierSet(u.toArray, "972 BAs")
  }
}


object RumerBA
  extends BinaryAlgorithm(1, 1, 0,
    Set(Cytosine, Adenine), Set(Cytosine, Guanine), Set(Cytosine, Guanine))
