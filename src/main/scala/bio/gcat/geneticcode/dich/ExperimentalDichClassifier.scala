package bio.gcat.geneticcode.dich

/**
 * Binary dichotomic algorithm as in Definition 3.2
 * Verified.
 * @param i1 Index of first base.
 * @param i2 Index of second base.
 * @param q1 Tupel (B1, B2) for first question.
 * @param q2 Set Q2 = {B3, B4} for second question.
 * @return 0 for class H1 and 1 for H2.
 */
class DichClassifier3(val i1: Int, val i2: Int, val i3: Int,
                    val q1: (Compound, Compound), val q2: (Compound, Compound),
                    val q3: Set[Compound])
  extends Classifier[Int] {
  def classify(c: Codon) = {
    if (c(i1) == q1._1) 0 // OK
    else if (c(i1) == q1._2) 1 // OK
    else if (c(i2) == q2._1) 0
    else if (c(i2) == q2._2) 1
    else if (q3 contains c(i3)) 0 // OK
    else 1 // OK
  }

  override def equals(that: Any) = that match {
    case d: DichClassifier3 => {
      d.i1 == i1 && d.i2 == i2 && d.i3 == i3 &&
        d.q1 == q1 && d.q2 == q2 && d.q3 == q3
    }
    case _ => false
  }

  override def toString = {
    val s = "[Q1 = (" + q1._1.toString + "," + q1._2.toString + ") @pos " + i1 + "; " +
      "Q2 = (" + q2._1.toString + "," + q2._2.toString + ") @pos " + i2 + "; " +
      "Q3 = {" + q3.mkString(",") + "} @pos " + i3 + "]"
    s
  }
}

// OK
object RumerClassifier3
  extends DichClassifier3(1, 0, 2,
    (Adenine, Cytosine), (Guanine, Cytosine), Set(Guanine, Cytosine))


class DichClassifier4(val i1: Int, val i2: Int, val i3: Int,
                    val q1: Set[Compound], val q2: Set[Compound],
                    val q3: Set[Compound])
  extends Classifier[Int] {
  def classify(c: Codon) = {
    if (q1 contains c(i1)) {
      if (q2 contains c(i2)) {
        if (q3 contains c(i3)) 0 else 0
      } else {
        if (q3 contains c(i3)) 0 else 1
      }
    } else {
      if (q2 contains c(i2)) {
        if (q3 contains c(i3)) 1 else 0
      } else {
        if (q3 contains c(i3)) 1 else 1
      }
    }
  }

  override def equals(that: Any) = that match {
    case d: DichClassifier4 => {
      d.i1 == i1 && d.i2 == i2 && d.i3 == i3 &&
        d.q1 == q1 && d.q2 == q2 && d.q3 == q3
    }
    case _ => false
  }

}
