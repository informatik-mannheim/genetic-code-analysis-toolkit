package bio.gcat.geneticcode.dich.demo

import bio.gcat.geneticcode.dich._
import bio.gcat.geneticcode.dich.scan.{ClassPower2Scan, ErrorScan}
import org.junit.{Ignore, Test}

/**
 * Analyse ancient code tables (see Karin Adler's bachelor thesis).
 * @author Markus Gumbel (m.gumbel@hs-mannheim.de), Karin Adler
 *         (c) 2014 Markus Gumbel
 */
class AncientScanDemo {

  /**
   * 44 solutions.
   */
  @Test
  @Ignore
  def scanAncientJiminez8() {
    val s = new ErrorScan(
      List(),
      .1, 3, 8, // E, k_d, |M|
      IUPAC.STANDARD, Jiminez8AncientProperty // code table, ancient code table
    )
    s.run
    s.store("Jiminiez8")
    // println(s.mkSolutionsScalaObject("Jiminez"))
  }

  /**
   * Try to merge with us.
   */
  @Test
  @Ignore
  def scanJiminez8And64() {
    val scan = Scan.load("Jiminiez8")
    for (ct <- scan.solutions) {
      new ClassPower2Scan(
        ct.bdas,
        6 // k_d
      ).run()
    }
  }

  /**
   * Try to merge with us. All 44 Jiminez-BDAs have 57 results with E_d = 12 / 64.
   */
  @Test
  @Ignore
  def scanJiminez8And21() {
    val scan = Scan.load("Jiminiez8")
    for (ct <- scan.solutions) {
      new ErrorScan(
        List(RumerBDA, AntiCodonBDA) ::: ct.bdas,
        .2, 6, 21, // E, k_d, |M|
        IUPAC.STANDARD // code table, ancient code table
      ).run()
    }
  }

  @Test
  @Ignore
  def scanJiminez8And21plusAars() {
    val t = IUPAC.STANDARD
    val aaRS = new AaRSClassifier(t)
    val scan = Scan.load("Jiminiez8")
    for (ct <- scan.solutions) {
      new ErrorScan(
        List(aaRS) ::: ct.bdas,
        .07, 8, 21, // E, k_d, |M|
        IUPAC.STANDARD // code table, ancient code table
      ).run()
    }
  }

  /**
   * 0 solutions.
   */
  @Test
  @Ignore
  def scanAncientJimenez4() {
    new ErrorScan(
      List(),
      .1, 6, 4, // E, k_d, |M|
      IUPAC.STANDARD, Jiminez4AncientProperty // code table, ancient code table
    ).run()
  }

  /**
   * 485 solutions.
   */
  @Test
  @Ignore
  def scanAncientJimenez8Tmp() {
    new ErrorScan(
      List(),
      .2, 8, 8, // E, k_d, |M|
      IUPAC.STANDARD, Jiminez8AncientProperty // code table, ancient code table
    ).run()
  }

  /**
   * 6 AA, Stop Codons.
   */
  @Test
  @Ignore
  def scanAncientDiGiulioSNS() {
    new ErrorScan(
      List(),
      .4, 20, 8, // E, k_d, |M|
      IUPAC.STANDARD, DiGiulioSNSCode // code table, ancient code table
    ).run()
  }

  /**
   * 7 AA + 1 Stop + "leere" Codons
   */
  @Test
  @Ignore
  def scanAncientDiGiulioNNS() {
    new ErrorScan(
      List(ParityBDA),
      .4, 8, 9, // E, k_d, |M|
      IUPAC.STANDARD, DiGiulioNNSCode // code table, ancient code table
    ).run()
  }

  /**
   * 11 AA + 1 Stop
   * 0 solutions at 0,1; 8; 8.
   * 0 solutions at 0,1; 8; 16.
   * 0 solutions at 0,1; 8; 11.
   * 0 solutions at 0,1; 8; 12.
   */
  @Test
  @Ignore
  def scanAncientCommaLess() {
    new ErrorScan(
      List(AntiCodonBDA),
      .5, 4, 13, // E, k_d, |M|
      IUPAC.STANDARD, StereochemicalCommaLess1 // code table, ancient code table
    ).run()
  }

  /**
   * 16 AA + 1 Stop
   * 88 solutions at 0,1; 8; 8
   * 0 solutions at 0,1; 8, 17
   */
  @Test
  @Ignore
  def scanAncientWoeseLastStep() {
    new ErrorScan(
      List(),
      .2, 8, 17, // E, k_d, |M|
      IUPAC.STANDARD, StereochemicalWoeseLastStep // code table, ancient code table
    ).run()
  }

  /**
   * 15 AA + 1 Stop
   * 0 solutions at 0,1; 8; 16
   * 2302 solutions at 0,1; 8; 8
   * 2822 solutions at 0,1; 8; 10
   * 0 solutions at 0,05; 8, 10
   */
  @Test
  @Ignore
  def scanAncientWoeseLastStepEasierVersion() {
    new ErrorScan(
      List(),
      .08, 8, 16, // E, k_d, |M|
      IUPAC.STANDARD, StereochemicalWoeseLastStepEasierVersion // code table, ancient code table
    ).run()
  }

  /**
   * 10 AA + Stopcodons
   * with Asp
   */
  @Test
  @Ignore
  def scanAncientBaumannOroFirstPhaseAsp() {
    new ErrorScan(
      List(),
      .1, 6, 11, // E, k_d, |M|
      IUPAC.STANDARD, BaumannOroPhase1Asp // code table, ancient code table
    ).run()
  }

  /**
   * 10 AA + Stopcodons
   * with Glu
   */
  @Test
  @Ignore
  def scanAncientBaumannOroFirstPhaseGlu() {
    new ErrorScan(
      List(AntiCodonBDA),
      .2, 6, 11, // E, k_d, |M|
      IUPAC.STANDARD, BaumannOroPhase1Glu // code table, ancient code table
    ).run()
  }

  /**
   * 15 AA + Stopcodons
   * with His and Asn and Asp
   */
  @Test
  @Ignore
  def scanAncientBaumannOroSecondPhase() {
    new ErrorScan(
      List(),
      .1, 6, 16, // E, k_d, |M|
      IUPAC.STANDARD, BaumannOroPhase2FirstPossibility // code table, ancient code table
    ).run()
  }

  /**
   * 10 AA, no Stop Codons
   */
  @Test
  @Ignore
  def scanAncientJukesTenAA() {
    new ErrorScan(
      List(AntiCodonBDA),
      .3, 6, 10, // E, k_d, |M|
      IUPAC.STANDARD, JukesFirstStepTenAA // code table, ancient code table
    ).run()
  }

  /**
   * 18 AA, 1 Stop Codon
   */
  @Test
  @Ignore
  def scanAncientJukesFullAA() {
    new ErrorScan(
      List(),
      .2, 6, 19, // E, k_d, |M|
      IUPAC.STANDARD, JukesSecondStepNewAA // code table, ancient code table
    ).run()
  }

  /**
   * 7 AA, 1 Stop Codon
   */
  @Test
  @Ignore
  def scanAncientWongFirst() {
    new ErrorScan(
      List(AntiCodonBDA),
      .3, 6, 8, // E, k_d, |M|
      IUPAC.STANDARD, WongFirstTable // code table, ancient code table
    ).run()
  }

  /**
   * 16 AA, 1 Stop Codon
   */
  @Test
  @Ignore
  def scanAncientWongSecond() {
    new ErrorScan(
      List(),
      .04, 8, 8, // E, k_d, |M|
      IUPAC.STANDARD, WongSecondTable // code table, ancient code table
    ).run()
  }

}