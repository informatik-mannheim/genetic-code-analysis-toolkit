package net.gumbix.geneticcode.dich.demo

import net.gumbix.geneticcode.dich.ui.CodeTableViewer
import org.junit.{Ignore, Test}
import net.gumbix.geneticcode.dich.scan._
import net.gumbix.geneticcode.dich.{BinaryDichotomicAlgorithm => BDA, Adenine => A, Uracil => U, Cytosine => C, Guanine => G, _}
import scala.Predef._
import net.gumbix.geneticcode.dich.ct.{CodingClassTable, ClassTable}


/**
 * @author Markus Gumbel (m.gumbel@hs-mannheim.de)
 *         (c) 2012 Markus Gumbel
 */
class ExperimentalDemo {

  @Test
  @Ignore
  def standard() {
    val t = IUPAC.STANDARD
    val cl = List(
      new AaRSClassifier(t),
      new BDA(0, 1, (A, U), Set(A, G)),
      new BDA(0, 1, (A, G), Set(C, G)),
      new BDA(1, 0, (A, G), Set(U, C)),
      new BDA(1, 0, (U, G), Set(A, C))
    )
    val ct = new ClassTable(cl, t)
    println(ct.classes.size)
    println(ct.mkString)
    println(ct.laTeXBdaMkString())
    println(ct.laTeXMkString())
  }

  @Test
  @Ignore
  def standardRumer() {
    val t = IUPAC.STANDARD
    val cl = List(
      new AaRSClassifier(t),
      RumerBDA,
      new BDA(1, 0, (A, U), Set(U, G)),
      new BDA(1, 0, (U, G), Set(C, G)),
      new BDA(1, 0, (C, G), Set(A, G))
    )
    val ct = new ClassTable(cl, t)
    println(ct.classes.size)
    println(ct.mkString)
    println(ct.laTeXMkString())
  }

  @Test
  @Ignore
  def vertrebrate() {
    val t = IUPAC.VERTEBRATE_MITOCHONDRIAL
    val cl = List(
      new AaRSClassifier(t),
      RumerBDA,
      new BDA(0, 1, (A, C), Set(A, C)),
      new BDA(0, 1, (U, C), Set(U, G)),
      new BDA(1, 0, (U, G), Set(C, G))
    )
    val ct = new ClassTable(cl, t)
    println(ct.classes.size)
    println(ct.mkString)
    println(ct.laTeXMkString())
  }

  @Test
  @Ignore
  def scanWithaaRS() {
    val t = IUPAC.STANDARD
    val aaRS = new AaRSClassifier(t)
    val s = new ErrorScan(
      List(aaRS).reverse,
      .05, 5, 20, // E, k_d, |M|
      t, new IdAminoAcidProperty(t) // code table, aaProps
    ).run()
  }

  @Test
  @Ignore
  def scanWithaaRSAndRumer() {
    val t = IUPAC.STANDARD
    val aaRS = new AaRSClassifier(t)
    val s = new ErrorScan(
      List(aaRS, RumerBDA),
      .1, 7, 21, // E, k_d, |M|
      t, new IdAminoAcidProperty(t) // code table, aaProps
    ).run()
  }

  @Test
  @Ignore
  def scanWithaaRSAndCompl() {
    val t = IUPAC.STANDARD
    val aaRS = new AaRSClassifier(t)
    val s = new ErrorScan(
      List(aaRS, AntiCodonBDA),
      .1, 7, 21, // E, k_d, |M|
      t, new IdAminoAcidProperty(t) // code table, aaProps
    ).run()
  }

  @Test
  @Ignore
  def playWithaaRS() {
    val t = IUPAC.VERTEBRATE_MITOCHONDRIAL
    val a1 = new BinaryAlgorithm(0, 1, 2, Set(C, U), Set(A, U), Set(G, U))
    val a2 = new BinaryAlgorithm(2, 1, 2, Set(C, U), Set(A, U), Set(G, U))
    val a3 = new AaRSClassifier(t)
    val ct = new ClassTable(List(RumerBA, a1, a2, a3), t,
      new IdAminoAcidProperty(t))
    println(ct.classes.size)
    println(ct.mkString)
  }


  @Test
  @Ignore
  def playWithBA() {
    val a1 = new BinaryAlgorithm(0, 1, 2, Set(C, U), Set(A, U), Set(G, U))
    val a2 = new BinaryAlgorithm(2, 1, 2, Set(C, U), Set(A, U), Set(G, U))
    val a3 = new BinaryAlgorithm(2, 1, 0, Set(A, U), Set(A, C), Set(G, U))
    val a4 = new BinaryAlgorithm(1, 2, 0, Set(A, C), Set(A, C), Set(G, U))
    val ct = new ClassTable(List(RumerBA, a1, a2, a3, a4))
    println(ct.classes.size)
    println(ct.mkString)
    new CodeTableViewer(ct)
  }

  @Test
  @Ignore
  def playWithRumerBA() {
    val ct = new ClassTable(List(RumerBA))
    println(ct.classes.size)
    println(ct.mkString)
    new CodeTableViewer(ct)
  }

  @Test
  @Ignore
  def playWithBA1() {
    val ct = new ClassTable(List(new BinaryAlgorithm(1, 1, 1, Set(U, C), Set(A, C), Set(C, U))))
    println(ct.classes.size)
    println(ct.mkString)
  }

  @Test
  @Ignore
  def x() {
    val bdas = List(
      new BDA(2, 0, (A, G), Set(C, G)),
      new BDA(2, 0, (C, U), Set(A, U)),
      new BDA(2, 0, (A, G), Set(A, U)),
      new BDA(0, 1, (C, G), Set(A, U)),
      new BDA(0, 1, (A, U), Set(C, G)),
      new BDA(0, 1, (A, U), Set(A, U)),
      new BDA(0, 1, (A, U), Set(A, U)),
      new BDA(0, 1, (A, U), Set(A, C)),
      new BDA(0, 1, (A, C), Set(A, C)),
      new BDA(1, 2, (A, C), Set(A, C)),
      new BDA(1, 0, (A, U), Set(U, C)),
      new BDA(1, 0, (U, G), Set(A, U))
    )
    val ct = new CodingClassTable(bdas)
    println(ct.hammingDistMinMax)
    println(ct.mkString)
  }

  @Test
  @Ignore
  def experimental() {
    val bdas = List(
      // new BDA(2, 2, (A, U), Set(G, U)),
      new BDA(0, 0, (A, C), Set(U, C)),
      new BDA(0, 0, (A, G), Set(C, G)),
      new BDA(1, 1, (A, C), Set(U, C)),
      new BDA(1, 1, (A, G), Set(C, G)), // OK
      new BDA(2, 1, (A, U), Set(G, U))
    ) // r2, p2, a2)

    for (n <- 1 to bdas.size) {
      val l = bdas.take(n)
      val t = new ClassTable(l)

      println("-----------")
      println("No. of questions = " + t.bdas.size)
      println(t.bdaMkString)
      println(t.mkString())
      println("deg. = " + t.degeneracy.mkString)
      println("|classes| = " + t.classes.size)
      println(t.classesMkString)
      //println("")
      //println(t.classesMkStringSize)
      println(t.aaMkString())
    }
  }

  @Test
  @Ignore
  def exampleAlberto() {
    val bdas = List(
      new BDA(2, 2, (A, U), Set(G, U)),
      RumerBDA
    )
    new ClassPower2Scan(bdas, 6)
  }

  @Test
  @Ignore
  def exampleAlberto2() {
    val bdas = List(
      new BDA(2, 2, (A, U), Set(G, U)),
      new BDA(0, 0, (A, C), Set(U, C)),
      new BDA(0, 0, (A, G), Set(C, G)),
      new BDA(1, 1, (A, C), Set(U, C)),
      new BDA(1, 1, (A, G), Set(C, G))
    )
    new IncreaseScan(32, bdas, 6).run()
  }

  @Test
  @Ignore
  def exampleAlberto3() {
    val bdas = List(
      // new BDA(2, 2, (A, U), Set(G, U)),
      new BDA(0, 0, (A, C), Set(U, C)),
      new BDA(0, 0, (A, G), Set(C, G)),
      new BDA(1, 1, (A, C), Set(U, C)),
      new BDA(1, 1, (A, G), Set(C, G))
    )
    new AminoMappingScan(bdas, 6)
  }

  @Test
  @Ignore
  def exampleFooBar() {
    val bdas = List(
      new BDA(2, 0, (A, G), Set(C, G)),
      new BDA(2, 0, (C, U), Set(A, U)),
      new BDA(2, 0, (A, G), Set(A, U)),
      new BDA(0, 1, (C, G), Set(A, U)),
      new BDA(0, 1, (A, U), Set(C, G)),
      new BDA(0, 1, (A, U), Set(A, U)),
      new BDA(0, 1, (A, U), Set(A, U)),
      new BDA(0, 1, (A, U), Set(A, C)),
      new BDA(0, 1, (A, C), Set(A, C)),
      new BDA(1, 2, (A, C), Set(A, C)),
      new BDA(1, 0, (A, U), Set(U, C)),
      new BDA(1, 0, (U, G), Set(A, U))
    )

    val scan = new Scan[CodingClassTable](bdas, 12) with CodingClassTableScan
    scan.run()
    scan.solutions.filter(s => s.classes.size >= 64).foreach {
      s =>
        println(s.mkString)
        println("codons = " + s.hammingDist.mkString(","))
        println("aa = " + s.hammingDistBtwAA)
    }
  }

  @Test
  @Ignore
  def exampleLoadFoo() {
    val scan = Scan.load("Foo")
    println("size = " + scan.solutions.size)
    scan.solutions.foreach {
      s =>
        println(s.bdaMkString)
        println(s.codons.mkString(","))
        println(s.mkString)
        println(s.classesMkString)
        println(s.codon2class)
    }
  }

  @Test
  @Ignore
  def exampleAnalyse() {
    val scan = Scan.load("c:\\Users\\Markus\\Local-Docs\\Professur-HS-Mannheim\\Projekte und Forschung\\Projekte\\Codons11\\Analysis\\ser\\Scan-b6-d9-20130322-022143")
    println("size = " + scan.solutions.size)
    val x = scan.solutions.filter(ct => ct.bdas.contains(RumerBDA))
    println(x.size)
    println(x.head.bdaMkString)
    println(x.head.mkString)
  }

  @Test
  @Ignore
  def h() {
    new ClassPower2Scan(List(RumerBDA), 2).run()
  }

  @Test
  @Ignore
  def exampleXY() {
    val bdas = List(
      new BDA(0, 1, (A, U), Set(A, U)),
      new BDA(0, 1, (A, U), Set(C, G)),
      new BDA(0, 1, (C, G), Set(A, U)),
      new BDA(0, 1, (C, G), Set(C, G)),

      new BDA(1, 0, (A, U), Set(A, U)),
      new BDA(1, 0, (A, U), Set(C, G)),
      new BDA(1, 0, (C, G), Set(A, U)),
      new BDA(1, 0, (C, G), Set(C, G)),

      new BDA(2, 0, (A, G), Set(A, U)),
      new BDA(2, 0, (C, U), Set(A, U)),
      new BDA(2, 0, (A, G), Set(C, G)),
      new BDA(2, 0, (C, U), Set(C, G)),

      new BDA(2, 1, (A, G), Set(A, U)),
      new BDA(2, 1, (C, U), Set(A, U)),
      new BDA(2, 1, (A, G), Set(C, G)),
      new BDA(2, 1, (C, U), Set(C, G))
    )
    val t = new CodingClassTable(bdas)
    println(t.hammingDist.sortBy(e => e._2).mkString(","))
    println(t.hammingDistanceGrouped)
    println(t.hammingDistanceGroupedMinMax)
    println("in = " + t.hammingDistInAA)
    println("btw. = " + t.hammingDistBtwAA)
  }

  @Test
  @Ignore
  def class5() {
    val bdas = List(
      new BDA(0, 1, (A, U), Set(A, U)),
      new BDA(0, 1, (A, U), Set(A, C)),
      new BDA(0, 1, (A, U), Set(A, G))
    )
    val t = new ClassTable(bdas)
    println(t.bdaMkString)
    println(t.classes.size)
    println(t.mkString)
  }

  /**
   * BA search.
   */
  @Test
  @Ignore
  def errorScanAminoMappingBA() {
    val scan = new ErrorScan(List(), 0.0 / 64.0, 4, 16,
      IUPAC.STANDARD, IdStandardAminoAcidProperty,
      BinaryAlgorithm.allBAs
      // BDA.bdas216
    )
    scan.run()
  }

  /**
   * BA search.
   */
  @Test
  @Ignore
  def errorScanAminoMappingBA2() {
    val ba1 = new BinaryAlgorithm(0, 0, 0, Set(A, U), Set(A, U), Set(A, C))
    val ba2 = new BinaryAlgorithm(0, 0, 1, Set(C, U), Set(A, G), Set(A, U))
    val ba3 = new BinaryAlgorithm(0, 0, 1, Set(C, U), Set(A, G), Set(A, C))
    val scan = new ErrorScan(List(ba1, ba2, ba3), 3.0 / 64.0, 5, 21,
      IUPAC.STANDARD, IdStandardAminoAcidProperty,
      BinaryAlgorithm.allBAs
      // BDA.bdas216
    )
    scan.run()
  }
}

