package bio.gcat.geneticcode.dich.demo

import bio.gcat.geneticcode.dich.ct.CodingClassTable
import bio.gcat.geneticcode.dich.{BinaryDichotomicAlgorithm => BDA}
import bio.gcat.geneticcode.dich.{Adenine => A}
import bio.gcat.geneticcode.dich.{Uracil => U}
import bio.gcat.geneticcode.dich.{Guanine => G}
import bio.gcat.geneticcode.dich.{Cytosine => C}
import bio.gcat.geneticcode.dich.{_}
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