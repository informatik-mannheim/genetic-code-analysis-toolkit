package net.gumbix.geneticcode.dich.demo

import net.gumbix.geneticcode.dich.ct.CodingClassTable
import net.gumbix.geneticcode.dich.scan.{ClassPower2Scan, ErrorScan}
import net.gumbix.geneticcode.dich.{Adenine => A, BinaryDichotomicAlgorithm => BDA, Cytosine => C, Guanine => G, Uracil => U, _}
import org.junit.{Ignore, Test}

/**
 * Analyse ancient code tables (see Karin Adler's bachelor thesis).
 * @author Markus Gumbel (m.gumbel@hs-mannheim.de), Karin Adler
 *         (c) 2014 Markus Gumbel
 */
class AncientDemo {

  /**
   * 44 solutions.
   */
  @Test
  @Ignore
  def jiminez() {
    val bdas = List(
      BDA(1, 0, (A, U), Set(A, G)),
      BDA(1, 0, (A, U), Set(U, C)),
      BDA(1, 0, (C, G), Set(A, G))
    )
    val ct = new CodingClassTable(bdas, IUPAC.STANDARD)
    println(ct.classes.size)
    println(ct.mkString)
    println(ct.laTeXBdaMkString)
    println(ct.laTeXMkString())
  }
}