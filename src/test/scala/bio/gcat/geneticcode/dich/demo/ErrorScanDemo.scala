package bio.gcat.geneticcode.dich.demo

import bio.gcat.geneticcode.dich.scan.ErrorScan
import bio.gcat.geneticcode.dich.{IdStandardAminoAcidProperty, IUPAC, RumerBDA, BinaryDichotomicAlgorithm}
import org.junit.{Ignore, Test}
import bio.gcat.geneticcode.exec.Main.main

/**
 * @author Markus Gumbel (m.gumbel@hs-mannheim.de)
 *         (c) 2014 Markus Gumbel
 */
class ErrorScanDemo {

  @Test
  @Ignore
  def demo {
    main("ErrorScan () .03 7 21 1 AST".split(" "))
  }

  @Test
  def errorScan {
    new ErrorScan(List(RumerBDA), 9.0/64.0, 12, 20, // BDA-set, error, max. BDA-set size, classes
      IUPAC.STANDARD, IdStandardAminoAcidProperty, BinaryDichotomicAlgorithm.bdas216).run()
  }
}
