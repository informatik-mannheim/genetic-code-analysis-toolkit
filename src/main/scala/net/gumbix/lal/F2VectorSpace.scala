package net.gumbix.lal

import scala.collection.immutable.HashSet

/**
 * @author Markus Gumbel (m.gumbel@hs-mannheim.de)
 *         (c) 2012 Markus Gumbel
 */
class F2VectorSpace(val dim: Int = 1) {

  /**
   * Zero vector (0, 0, ..., 0)
   */
  val zero = {
    val zeros = (1 to dim).map(c => 0).toList
    F2Vector(zeros: _*)
  }

  /**
   * Tests if vectors in vl create a sub vector space in F2.
   * @param vl
   * @return True if vl is a F2 subspace, false if not.
   */
  def isMovedSubSpace(vl: List[F2Vector]) = {
    val u = movedSubSpaceExceptions(vl).filter(c => c._2 == None)
    u.size > 0
  }

  /**
   * Iterate through all vectors in vl and create a set with x-v. Test for every v element in vl
   * if it is a vector subspace.
   * @param vl
   * @return A list of size the size of vl where each entry
   *         is either None (indicating this is a subspace) or a list of exceptions.
   */
  def movedSubSpaceExceptions(vl: List[F2Vector]) = {
    vl.map{v0 =>
      val nvl = vl.map(v => v - v0)
      (v0, subSpaceExceptions(nvl))
    }
  }

  /**
   * Tests if vectors in vl create a sub vector space in F2.
   * @param vl
   * @return True if vl is a F2 subspace, false if not.
   */
  def isSubSpace(vl: List[F2Vector]) = subSpaceExceptions(vl) match {
    case None => true
    case _ => false
  }

  /**
   * Tests if vectors in vl create a sub vector space in F2 or show the exceptions.
   * @param vl
   * @return None if vl is a F2 subspace or a list of vector-tuples
   *         which sum is not part of vl.
   */
  def subSpaceExceptions(vl: List[F2Vector]) = {
    val elements = HashSet() ++ vl

    def check(v1: F2Vector, v2: F2Vector) = {
      if (elements.contains(v1 + v2)) None else Some((v1, v2))
    }

    /**
     * Tests if vectors in vl create a sub vector space in F2.
     * @param vl
     * @return None if vl is a F2 subspace or a list of vector-tuples
     *         which sum is not part of vl. If 0 is not within vl Some(Nil) is returned.
     */
    def svs(vl: List[F2Vector]): Option[List[(F2Vector, F2Vector)]] = vl match {
      case Nil => None
      case _ => {
        val v1 = vl.head
        val r1 = for (v2 <- vl) yield check(v1, v2)
        val r2 = r1.filter(p => p.isInstanceOf[Some[(F2Vector, F2Vector)]])
        if (r2.isEmpty) svs(vl.tail) else Some(r2.map(c => c.get))
      }
    }

    if (!vl.contains(zero)) Some(Nil) else svs(vl)
  }
}
