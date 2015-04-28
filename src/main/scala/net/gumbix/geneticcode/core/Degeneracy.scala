package net.gumbix.geneticcode.core

import net.gumbix.geneticcode.dich.{RumerBDA}
import scala.collection.JavaConversions._
import scala.Predef._
import net.gumbix.geneticcode.dich.ct.ClassTable
import org.apache.commons.math3.stat.Frequency

/**
 * The degeneracy of a genetic code table.
 * @author Markus Gumbel (m.gumbel@hs-mannheim.de)
 *         (c) 2013 Markus Gumbel
 */
class Degeneracy(val t: ClassTable) {

  def degeneracy = {
    val hist = new Frequency()
    t.class2codons.foreach(e => hist.addValue(e._2.size))
    val d = (1 to 64).map(n => (n, hist.getCount(n).asInstanceOf[Int])).toMap
    require {
      val sum = d.map {
        e =>
          val d = e._1  // Degeneracy
        val c = e._2  // Count
          d*c
      }.sum
      sum == 64
    }
    d
  }

  def minDeg = degeneracy.filter(e => e._2 > 0).keys.min

  def maxDeg = degeneracy.filter(e => e._2 > 0).keys.max

  /**
   * Check if this degeneracy is compatible to the given degeneracy.
   * I.e. test whether the class table of this degeneracy could
   * be further divided such such the given degeneracy will evolve.
   * @param deg
   * @return
   */
  def isCompatible(deg: Degeneracy) = {
    //maxDeg >= deg.maxDeg && minDeg >= deg.minDeg
    val c1 = maxDeg >= deg.maxDeg
    val c2 = minDeg >= deg.minDeg
    val u = (1 to 64).map{
      n =>
        if (deg.degeneracy(n) != 0) degeneracy(n) <= deg.degeneracy(n) else true
    }
    val c3 = u.forall(e => e)
    c1 && c2 && c3
  }

  override def equals(o: Any) = o match {
    case ot: Degeneracy => {
      degeneracy == ot.degeneracy
    }
    case _ => false
  }

  override def toString() = mkString

  def mkString = degeneracy.filter(e => e._2 > 0).toSeq.sortBy(s => s._1).
    map(e => "(" + e._1 + ": " + e._2 + ")").mkString(", ")
}

/**
 * TODO derive from real code table.
 */
object StandardCodeDegeneracy extends Degeneracy(new ClassTable(List(RumerBDA))) {
  override def degeneracy = {
    def deg(n: Int) = n match {
      case 6 => 3
      case 4 => 5
      case 3 => 2
      case 2 => 9
      case 1 => 2
      case _ => 0
    }

    (1 to 64).map(n => (n, deg(n))).toMap
  }
}

