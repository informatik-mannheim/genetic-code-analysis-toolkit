package net.gumbix.geneticcode.dich.demo

import org.junit.{Ignore, Test}
import net.gumbix.geneticcode.dich.{BinaryDichotomicAlgorithm => BDA, Adenine => A, Uracil => U, Cytosine => C, Guanine => G, _}
import net.gumbix.lal.{F2VectorSpace, F2Vector}
import net.gumbix.geneticcode.dich.ct.{CodingClassTable, ClassTable}

/**
 * Try to map 64 codons to 64 classes.
 * @author Markus Gumbel (m.gumbel@hs-mannheim.de)
 *         (c) 2012 Markus Gumbel
 */
class Class64Demo {

  @Test
  @Ignore
  // The first found solution.
  def lutz20130527() {
    val bdas = List(
      new BDA(0, 1, (C, G), Set(A, U)),
      new BDA(0, 1, (A, U), Set(C, G)),
      new BDA(0, 1, (C, G), Set(C, G)),
      new BDA(1, 0, (A, U), Set(C, G)),
      new BDA(1, 0, (C, G), Set(C, G)),
      new BDA(2, 0, (C, U), Set(A, U)),
      new BDA(2, 0, (C, U), Set(C, G)),
      new BDA(2, 1, (A, G), Set(C, G))
    )
    printAll(bdas)
  }


  @Test
  @Ignore
  // The first found solution.
  def example001() {
    val bdas = List(
      new BDA(0, 1, (A, U), Set(A, U)),
      new BDA(0, 1, (A, U), Set(C, G)),
      new BDA(0, 1, (C, G), Set(A, U)),
      new BDA(2, 1, (A, U), Set(A, C)),
      new BDA(2, 1, (A, U), Set(U, G)),
      new BDA(2, 1, (C, G), Set(A, C))
    )
    printLaTex(bdas)
  }

  @Test
  @Ignore
  // Another solution.
  def example002() {
    val bdas = List(
      new BDA(0, 1, (A, U), Set(C, G)),
      new BDA(0, 1, (C, G), Set(A, G)),
      new BDA(0, 1, (C, G), Set(U, C)),
      new BDA(2, 1, (A, G), Set(U, G)),
      new BDA(2, 1, (U, C), Set(A, C)),
      new BDA(2, 1, (U, C), Set(U, G))
    )
    printLaTex(bdas)
  }

  @Test
  @Ignore
  def example003() {
    val bdas = List(
      new BDA(0, 1, (A, U), Set(A, U)),
      new BDA(0, 1, (A, U), Set(A, C)),
      new BDA(0, 1, (A, U), Set(A, G)),
      new BDA(0, 1, (A, U), Set(U, C)),
      new BDA(0, 1, (C, G), Set(A, U)),
      new BDA(0, 2, (A, U), Set(A, U))
    )
    val t = new ClassTable(bdas)
    println(t.classesMkString)
    println(t.class2AAList)
  }

  /**
   * Unknown model.
   */
  @Test
  @Ignore
  def Class64() {
    val bdas = List(
      new BDA(0, 1, (A, U), Set(A, U)),
      new BDA(0, 1, (A, U), Set(C, G)),
      new BDA(0, 1, (C, G), Set(A, U)),
      new BDA(2, 1, (A, U), Set(A, C)),
      new BDA(2, 1, (A, U), Set(U, G)),
      new BDA(2, 1, (C, G), Set(A, C))
    )
    val ct = new CodingClassTable(bdas, IUPAC.STANDARD)
    println(ct.classes.size)
    println(ct.mkString)
    println(ct.laTeXBdaMkString())
  }

  /**
   * According to Alberto's talk at 2013-02-26 (Bologna workshop):
   * Only questions strong/weak for base indices 1 and 2 and
   * Purine/Pyrimidine for index 3.
   * This version produces 64 (yeah!) classes.
   */
  @Test
  @Ignore
  def exampleAlbertoAllBDAs() {
    val bdas = List(
      // Pos. 0, 1
      BDA(0, 1, (A, U), Set(A, U)),
      BDA(0, 1, (A, U), Set(C, G)),
      BDA(0, 1, (C, G), Set(A, U)),
      BDA(0, 1, (C, G), Set(C, G)),
      // Reverse
      BDA(1, 0, (A, U), Set(A, U)),
      BDA(1, 0, (A, U), Set(C, G)),
      BDA(1, 0, (C, G), Set(A, U)),
      BDA(1, 0, (C, G), Set(C, G)),

      // Pos 0, 2
      BDA(0, 2, (A, U), Set(A, G)),
      BDA(0, 2, (A, U), Set(C, U)),
      BDA(0, 2, (C, G), Set(A, G)),
      BDA(0, 2, (C, G), Set(C, U)),
      // Reverse
      BDA(2, 0, (A, G), Set(A, U)),
      BDA(2, 0, (C, U), Set(A, U)),
      BDA(2, 0, (A, G), Set(C, G)),
      BDA(2, 0, (C, U), Set(C, G)),

      // Pos 1, 2
      BDA(1, 2, (A, U), Set(A, G)),
      BDA(1, 2, (A, U), Set(C, U)),
      BDA(1, 2, (C, G), Set(A, G)),
      BDA(1, 2, (C, G), Set(C, U)),
      // Reverse
      BDA(2, 1, (A, G), Set(A, U)),
      BDA(2, 1, (C, U), Set(A, U)),
      BDA(2, 1, (A, G), Set(C, G)),
      BDA(2, 1, (C, U), Set(C, G))
    )
    printAll(bdas)
  }

  /**
   * According to Alberto's talk at 2013-02-26 (Bologna workshop):
   * Only questions strong/weak for base indices 1 and 2 and
   * Purine/Pyrimidine for index 3.
   * This example is not relevant anymore.
   */
  @Test
  @Ignore
  def exampleAlbertoBioBDAExample32() {
    val bdas = List(
      new BDA(0, 1, (A, U), Set(A, U)),
      new BDA(0, 1, (A, U), Set(C, G)),
      new BDA(0, 1, (C, G), Set(A, U)),

      new BDA(2, 0, (A, G), Set(A, U)),
      new BDA(2, 0, (C, U), Set(A, U)),
      new BDA(2, 0, (A, G), Set(C, G))
    )
    printAll(bdas)
  }

  /**
   * 64 classes with 8 BDAs and biological meaning!
   * This example is also not relevant anymore as we have the results
   * for all solutions.
   */
  @Test
  @Ignore
  def exampleAlberto8BioBDAExample64() {
    val bdas = List(
      new BDA(0, 1, (A, U), Set(A, U)),
      new BDA(0, 1, (A, U), Set(C, G)),
      new BDA(0, 1, (C, G), Set(A, U)),

      new BDA(2, 0, (A, G), Set(A, U)),
      new BDA(2, 0, (C, U), Set(A, U)),
      new BDA(2, 0, (A, G), Set(C, G)),

      // new BDA(0, 1, (A, U), Set(A, U)),
      new BDA(1, 2, (A, U), Set(C, G)),
      new BDA(1, 2, (C, G), Set(C, G))
    )
    printAll(bdas)
  }

  /**
   * 64 classes with 8 BDAs and biological meaning!
   * This example is also not relevant anymore as we have the results
   * for all solutions.
   */
  @Test
  @Ignore
  def exampleAlberto7RHBDAExample64() {
    val bdas = List(
      // BDA(0, 1, (A, U), Set(A, U)),
      BDA(0, 1, (A, U), Set(C, G)),
      // BDA(0, 1, (C, G), Set(A, U)),
      BDA(0, 1, (C, G), Set(C, G)),

      // BDA(1, 0, (A, U), Set(A, U)),
      BDA(1, 0, (A, U), Set(C, G)),
      // BDA(1, 0, (C, G), Set(A, U)),
      BDA(1, 0, (C, G), Set(C, G)),

      // BDA(2, 0, (A, G), Set(A, U)),
      BDA(2, 0, (C, U), Set(A, U)),
      // BDA(2, 0, (A, G), Set(C, G)),
      BDA(2, 0, (C, U), Set(C, G)),

      // BDA(2, 1, (A, G), Set(A, U)),
      // BDA(2, 1, (C, U), Set(A, U)),
      BDA(2, 1, (A, G), Set(C, G))
      // BDA(2, 1, (C, U), Set(C, G))
    )
    printAll(bdas)
  }

  /**
   * 64 classes with 11 BDAs and biological meaning and
   * a hamming distance >= 2 between different amino acids.
   */
  @Test
  @Ignore
  def exampleAlbertoHammingDist2() {
    val bdas = List(
      new BDA(2, 0, (A, G), Set(C, G)),
      new BDA(2, 0, (C, U), Set(A, U)),
      new BDA(2, 0, (A, G), Set(A, U)),
      new BDA(0, 1, (C, G), Set(A, U)),
      new BDA(0, 1, (A, U), Set(C, G)),
      new BDA(0, 1, (A, U), Set(A, U)),
      new BDA(0, 1, (A, U), Set(A, U)),
      new BDA(0, 1, (A, G), Set(A, G)),
      new BDA(1, 2, (U, G), Set(A, G)),
      new BDA(1, 0, (A, G), Set(U, C)),
      new BDA(2, 1, (A, U), Set(A, C)),
      new BDA(2, 1, (C, G), Set(A, C))
      // new BDA(0, 1, (A, U), Set(A, U))
    )
    printAll(bdas)
  }

  /**
   * Why only 1?
   */
  @Test
  @Ignore
  def exampleAlbertoHammingDist2Bug() {
    val bdas = List(
      new BDA(2, 0, (A, G), Set(C, G)),
      new BDA(2, 0, (C, U), Set(A, U)),
      new BDA(2, 0, (A, G), Set(A, U)),
      new BDA(0, 1, (C, G), Set(A, U)),
      new BDA(0, 1, (A, U), Set(C, G)),
      new BDA(0, 1, (A, U), Set(A, U)),
      // new BDA(0, 1, (A, U), Set(A, U)),
      new BDA(0, 1, (A, G), Set(A, G)),
      new BDA(1, 2, (U, G), Set(A, G)),
      new BDA(1, 0, (A, G), Set(U, C)),
      new BDA(2, 1, (A, U), Set(A, C)),
      new BDA(2, 1, (C, G), Set(A, C))
      // new BDA(0, 1, (A, U), Set(A, U))
    )
    printAll(bdas)
  }

  @Test
  @Ignore
  def red64scanAlberto() {
    val bdas = List(
      new BDA(0, 1, (A, U), Set(A, U)),
      new BDA(0, 1, (A, U), Set(C, G)),
      new BDA(0, 1, (C, G), Set(A, U)),

      new BDA(2, 0, (A, G), Set(A, U)),
      new BDA(2, 0, (C, U), Set(A, U)),
      new BDA(2, 0, (A, G), Set(C, G))
    )

    val scan = new Scan[ClassTable](bdas, 9) with ClassTableScan {
      override val filename = "RedScan64"
    }
    scan.run()
  }

  /**
   * According to Alberto's talk at 2013-02-26 (Bologna workshop):
   * Only questions strong/weak for base indices 1 and 2 and
   * Purine/Pyrimidine for index 3.
   * This is a first guess which produces 32 classes only.
   * This example is not relevant anymore.
   */
  @Test
  @Ignore
  def exampleAlberto() {
    val bdas = List(
      new BDA(0, 1, (A, U), Set(A, U)),
      new BDA(0, 1, (A, U), Set(C, G)),
      new BDA(0, 1, (C, G), Set(A, U)),

      new BDA(2, 0, (C, U), Set(C, G)),
      new BDA(2, 0, (A, G), Set(C, G)),
      new BDA(2, 0, (C, U), Set(A, U))
    )
    val t = new ClassTable(bdas)
    println(t.classes.size)
    println(t.classesMkStringSize)
  }

  @Test
  @Ignore
  def exampleRHBIO1() {
    val bdas = List(
      new BDA(0, 1, (A, U), Set(A, U))
    )
    val t = new ClassTable(bdas)
    val bdas2 = List(
      new BDA(1, 0, (A, U), Set(A, U))
    )
    val t2 = new ClassTable(bdas2)
    println(t.mkString)
    println(t2.mkString)
  }

  @Test
  @Ignore
  def example64classesSpecificBDAs() {
    val bdas = List(
      new BDA(0, 2, (G, C), Set(U, C)),
      new BDA(2, 1, (A, G), Set(G, U)),
      new BDA(1, 0, (A, C), Set(G, C)),
      new BDA(0, 1, (A, U), Set(A, U)),
      new BDA(1, 0, (U, G), Set(A, U)),
      new BDA(2, 1, (A, U), Set(A, C)),
      new BDA(2, 1, (U, C), Set(A, C))
    )
    printAll(bdas)
  }

  def printAll(bdas: List[BDA]) {
    val t = new CodingClassTable(bdas)
    println("|classes| = " + t.classes.size)
    println("Sub space = " + t.isVectorSubSpace)
    println("Moved sub space = " + t.isMovedVectorSubSpace)
    println("h (in) = " + t.hammingDistInAA)
    println("h (btw.) = " + t.hammingDistBtwAA)
    println(t.mkString)
    println("h_g = " + t.hammingDistanceGrouped)
    println("h_g_min_max = " + t.hammingDistanceGroupedMinMax)
    println(t.laTeXBdaMkString())
    println(t.laTeXMkString)
    println(t.classesMkStringSize)
    println(t.f2.movedSubSpaceExceptions(t.f2Vectors).mkString("\n"))
  }

  def printLaTex(bdas: List[BDA]) {
    val t = new ClassTable(bdas)
    println(t.classes.size)
    println(t.mkString)
    println(t.laTeXMkString)
    println(t.classesMkStringSize)
  }
}
