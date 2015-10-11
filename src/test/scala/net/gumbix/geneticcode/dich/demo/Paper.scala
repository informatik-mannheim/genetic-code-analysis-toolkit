package net.gumbix.geneticcode.dich.demo

import org.junit.{Ignore, Test}
import net.gumbix.geneticcode.dich.{BinaryDichotomicAlgorithm => BDA, Adenine => A, Uracil => U, Cytosine => C, Guanine => G, _}
import scala.Predef._
import net.gumbix.geneticcode.dich.scan.{RandomScan, AminoMappingScan, ScanNumberOfClassesScan, ClassPower2Scan}
import net.gumbix.geneticcode.dich.ct.{ClassTable, CodingClassTable}

/**
 * Note: This class contains the instructions to produce the figures and tables
 * as well as some other statements in M. Gumbel et al: "On Models...".
 * BioSystems. 10.1016/j.biosystems.2014.12.001.
 * @author Markus Gumbel (m.gumbel@hs-mannheim.de)
 *         (c) 2014 Markus Gumbel
 */
class Paper {

  // Figure and tables:

  /**
   * Figure 1b.
   */
  @Test
  @Ignore
  def geneticCodeWithAllPartitions() {
    val bdas = List(RumerBDA, ParityBDA, AntiCodonBDA)
    val ct = new CodingClassTable(bdas, IUPAC.STANDARD)
    println(ct.classes.size)
    println(ct.mkString)
    println(ct.laTeXMkString())
  }

  /**
   * Table 1.
   */
  @Test
  @Ignore
  def model24ClassesRumer() {
    val bdas = List(
      RumerBDA,
      new BDA(0, 1, (A, U), Set(A, U)),
      new BDA(0, 1, (A, U), Set(A, C)),
      new BDA(0, 1, (A, U), Set(U, C)),
      new BDA(0, 1, (C, G), Set(A, U)),
      new BDA(0, 1, (C, G), Set(A, C)),
      new BDA(1, 2, (U, C), Set(A, G))
    )
    val ct = new ClassTable(bdas, IUPAC.STANDARD)
    println(ct.classes.size)
    println(ct.errorC)
    println(ct.classesMkString)
    println(ct.aaMkString)
    println(ct.degeneracy.mkString)
    println(ct.mkString)
    println(ct.laTeXBdaMkString)
    println(ct.laTeXMkString())
  }


  val N = 5000

  /**
   * Fig. 3a.
   * Create data for a histogram. Model has 24 classes
   * and uses 5 to 9 bits. Any BDA is allowed.
   */
  @Test
  @Ignore
  def histClasses24k56789() {
    new RandomScan(N, List(), 5, 9, 24, Some("-free56789"))
  }

  /**
   * Fig. 3b.
   * Create data for a histogram. Model has 24 classes
   * and uses exactly 7 bits (incl. Rumer)
   */
  @Test
  @Ignore
  def histClasses24withRumer() {
    // Allow any error using max. 7 bits.
    new RandomScan(N, List(RumerBDA), 7, 7, 24, Some("-Rumer"))
  }

  /**
   * Table 2.
   */
  @Test
  @Ignore
  def classes64withRumer() {
    val bdas = List(
      RumerBDA,
      new BDA(1, 0, (A, C), Set(G, C)),
      new BDA(1, 0, (U, G), Set(A, U)),
      new BDA(2, 0, (A, U), Set(A, C)),
      new BDA(2, 0, (A, U), Set(U, G)),
      new BDA(2, 0, (C, G), Set(A, C))
    )
    val t = new ClassTable(bdas, IUPAC.STANDARD)
    println(t.classes.size)
    println(t.mkString)
    println(t.bdaMkString)
    println(t.laTeXBdaMkString)
    println(t.laTeXMkString())
  }

  /**
   * Table 3.
   */
  @Test
  @Ignore
  def classes64Red7WithRumerParityCompl() {
    val bdas = List(
      RumerBDA,
      ParityBDA,
      AntiCodonBDA,
      new BDA(0, 1, (A, U), Set(A, U)),
      new BDA(1, 0, (U, G), Set(A, U)),
      new BDA(2, 1, (A, U), Set(A, C)),
      new BDA(2, 1, (U, C), Set(A, C))
    )
    val ct = new CodingClassTable(bdas, IUPAC.STANDARD)
    println(ct.classes.size)
    println(ct.mkString)
    println(ct.laTeXBdaMkString)
    println(ct.laTeXMkString())
  }

  /**
   * Table 4.
   */
  @Test
  @Ignore
  def classe64with7RHBDAs() {
    val bdas = List(
      BDA(0, 1, (A, U), Set(C, G)),
      BDA(0, 1, (C, G), Set(C, G)),
      BDA(1, 0, (A, U), Set(C, G)),
      BDA(1, 0, (C, G), Set(C, G)),
      BDA(2, 0, (C, U), Set(A, U)),
      BDA(2, 0, (C, U), Set(C, G)),
      BDA(2, 1, (A, G), Set(C, G))
    )
    val ct = new CodingClassTable(bdas, IUPAC.STANDARD)
    println(ct.classes.size)
    println(ct.mkString)
    println(ct.laTeXBdaMkString)
    println(ct.laTeXMkString())
  }

  // Other statements:

  /**
   * Scan for all possible number of classes (section 4.1).
   * Warning: Quite long runtime!
   */
  @Test
  @Ignore
  def scanForNumberOfClasses() {
    new ScanNumberOfClassesScan().run()
  }

  /**
   * Scan for amino acid mapping (section 4.2).
   */
  @Test
  @Ignore
  def scanAminoClasses() {
    new AminoMappingScan(List(), 5, 21)     // 5 bits, 21 classes.
  }

  /**
   * 53856 solutions for |M|=64 when using 6 BDAs (section 4.3).
   * Warning: Quite long runtime!
   */
  @Test
  @Ignore
  def scanFor64Classes() {
    new ClassPower2Scan(List(), 6).run()
  }

  /**
   * Shows that there are no solutions when Rumer, Parity and Complementary are included (section 4.3)
   */
  @Test
  @Ignore
  def classes64withRumerParityScan() {
    new ClassPower2Scan(List(RumerBDA.complement, ParityBDA.complement), 6).run()
    new ClassPower2Scan(List(RumerBDA.complement, AntiCodonBDA), 6).run()
    new ClassPower2Scan(List(ParityBDA.complement, AntiCodonBDA), 6).run()
    /*
64 classes (via class size doubling per BDA) sequentially
[2, 1, (A,C), {U,A}Rumer_; 1, 3, (C,G), {G,A}AntiCodon] fixed.
max. 8.820117E7 combinations will be tested.
solutions = 0
Saved to Scan-b2-d6-20130917-041153.ser
T = 42.751 s
64 classes (via class size doubling per BDA) sequentially
[3, 2, (A,G), {U,G}Parity_; 1, 3, (C,G), {G,A}AntiCodon] fixed.
max. 8.820117E7 combinations will be tested.
solutions = 0
Saved to Scan-b2-d6-20130917-041235.ser
T = 52.917 s
     */
  }

  /**
   * Demonstrate complementary BDAs (section 2.2, def. 2.3).
   */
  @Test
  @Ignore
  def complementaryRumerPartitions() {
    val bdas = List(RumerBDA, RumerBDA.complement)
    val t = new ClassTable(bdas)
    println(t.mkString)
    println(t.laTeXBdaMkString)
    println(t.laTeXMkString())
  }
}


