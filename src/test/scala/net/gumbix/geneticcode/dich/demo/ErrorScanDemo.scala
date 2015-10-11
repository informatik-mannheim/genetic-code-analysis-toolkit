package net.gumbix.geneticcode.dich.demo

import org.junit.{Ignore, Test}
import net.gumbix.geneticcode.dich._
import net.gumbix.geneticcode.dich.scan.ErrorScan
import net.gumbix.geneticcode.dich.{BinaryDichotomicAlgorithm => BDA}
import net.gumbix.geneticcode.dich.{Adenine => A, Uracil => U, Cytosine => C, Guanine => G}
import net.gumbix.geneticcode.exec.Main.main

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
