package net.gumbix.lal

/**
 * @author Markus Gumbel (m.gumbel@hs-mannheim.de)
 *         (c) 2012 Markus Gumbel
 */
class F2Vector(val x: Int*) {

  private def checkDim(v: F2Vector) {
    if (v.size != x.size) throw new IllegalArgumentException("Vector " +
      "must have size " + x.size + " but has " + v.size)
  }

  def +(v: F2Vector) = {
    checkDim(v)
    val n = v.x.zip(x).map(e => (e._1 + e._2) % 2).toList
    F2Vector(n: _*)
  }

  def -(v: F2Vector) = v + this

  def size = x.size

  override def equals(that: Any) = that match {
    case v: F2Vector => v.x == x
    case _ => false
  }

  override def hashCode() = x.hashCode

  override def toString() = x.mkString("")
}

object F2Vector {
  def apply(v: Int*) = new F2Vector(v: _*)
}
