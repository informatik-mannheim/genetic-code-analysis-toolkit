package bio.gcat.util

/**
 * @author Markus Gumbel (m.gumbel@hs-mannheim.de)
 *         (c) 2012 Markus Gumbel
 */
object Combine {

  /**
   * Combine each element in the list with each other. (i, j) == (j, i) and i <> j.
   * @param l
   * @return
   */
  def combine[A](l: List[A]): List[(A, A)] = l match {
    case Nil => List()
    case hd :: tl => {
      // Combine the first element (hd) with all others
      // in the remaining list (tl):
      val l = for (e <- tl) yield (hd, e)
      // Do this also for the remaining list (tl):
      val sl = combine(tl)
      l ::: sl
    }
  }
}
