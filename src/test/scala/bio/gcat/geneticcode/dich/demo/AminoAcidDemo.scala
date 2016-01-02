package bio.gcat.geneticcode.dich.demo

import bio.gcat.geneticcode.dich.ct.{AminoClassTable, CodingClassTable}
import bio.gcat.geneticcode.dich.{Adenine => A, Uracil => U, Guanine => G, Cytosine => C, IUPAC, BinaryDichotomicAlgorithm, RumerBDA}
import org.junit.{Ignore, Test}

/**
 * @author Markus Gumbel (m.gumbel@hs-mannheim.de)
 *         (c) 2013 Markus Gumbel
 */
class AminoAcidDemo {

  /**
   * Taken from Paper class. Maybe used in slides (Ulm?).
   */
  @Test
  @Ignore
  def model21Classes() {
    val bdas = List(
      new BinaryDichotomicAlgorithm(0, 1, (A, U), Set(A, U)),
      new BinaryDichotomicAlgorithm(0, 1, (A, U), Set(A, C)),
      new BinaryDichotomicAlgorithm(0, 1, (A, U), Set(A, G)),
      new BinaryDichotomicAlgorithm(0, 1, (A, G), Set(C, G)),
      new BinaryDichotomicAlgorithm(1, 2, (U, C), Set(A, G)),
      new BinaryDichotomicAlgorithm(1, 0, (C, G), Set(A, G))
    )
    val ct = new CodingClassTable(bdas, IUPAC.STANDARD)
    println(ct.classes.size)
    println(ct.classesMkString)
    println(ct.degeneracy.mkString)
    println(ct.mkString)
    println(ct.laTeXMkString())
    println(ct.laTeXBdaMkString())
    println(ct.errorC)
  }

  /**
   * Taken from Paper class. Maybe used in slides (Ulm?).
   */
  @Test
  @Ignore
  def model24Classes() {
    val bdas = List(
      new BinaryDichotomicAlgorithm(0, 1, (A, U), Set(A, U)),
      new BinaryDichotomicAlgorithm(0, 1, (A, U), Set(A, C)),
      new BinaryDichotomicAlgorithm(0, 1, (A, U), Set(A, G)),
      new BinaryDichotomicAlgorithm(0, 1, (A, U), Set(U, C)),
      new BinaryDichotomicAlgorithm(0, 1, (C, G), Set(A, U)),
      new BinaryDichotomicAlgorithm(0, 1, (C, G), Set(U, C)),
      new BinaryDichotomicAlgorithm(1, 2, (U, C), Set(U, C))
    )
    val ct = new CodingClassTable(bdas, IUPAC.STANDARD)
    println(ct.classes.size)
    println(ct.classesMkString)
    println(ct.degeneracy.mkString)
    println(ct.mkString)
    println(ct.laTeXBdaMkString)
    println(ct.laTeXMkString())
  }

  /**
   * Taken from Paper class. Maybe used in slides (Ulm?).
   */
  @Test
  @Ignore
  def model21ClassesOld() {
    val bdas = List(
      new BinaryDichotomicAlgorithm(0, 1, (A, U), Set(A, U)),
      new BinaryDichotomicAlgorithm(0, 1, (A, U), Set(A, C)),
      new BinaryDichotomicAlgorithm(0, 1, (A, U), Set(A, G)),
      new BinaryDichotomicAlgorithm(0, 1, (A, U), Set(U, C)),
      new BinaryDichotomicAlgorithm(0, 2, (A, C), Set(A, U)),
      new BinaryDichotomicAlgorithm(0, 2, (A, C), Set(A, C))
    )
    val ct = new CodingClassTable(bdas, IUPAC.STANDARD)
    println(ct.classes.size)
    println(ct.classesMkString)
    println(ct.degeneracy.mkString)
    println(ct.mkString)
    println(ct.laTeXBdaMkString)
    println(ct.laTeXMkString())
  }

  @Test
  @Ignore
  def aminoClassTable() {
    val ct = new AminoClassTable(List(RumerBDA), 1) // , new IdAminoAcidProperty(1))
    println(ct.mkString)
  }
}
