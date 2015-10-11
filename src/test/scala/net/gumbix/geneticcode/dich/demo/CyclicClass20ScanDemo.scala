package net.gumbix.geneticcode.dich.demo

import org.junit.{Ignore, Test}
import net.gumbix.geneticcode.dich.{BinaryDichotomicAlgorithm => BDA, Adenine => A, Uracil => U, Cytosine => C, Guanine => G, _}
import scan.{Class20CyclicScan, ClassPower2Scan}

/**
  * @author Markus Gumbel (m.gumbel@hs-mannheim.de)
  * (c) 2012 Markus Gumbel
  */
class CyclicClass20ScanDemo {

   @Test
   @Ignore
   def all() {
     new Class20CyclicScan(List(), 5)
   }

  @Test
  @Ignore
  def rumerParity() {
    new Class20CyclicScan(List(ParityBDA, RumerBDA), 5)
  }

   @Test
   @Ignore
   def rumerParityAntiCodon() {
     new Class20CyclicScan(List(ParityBDA, RumerBDA, AntiCodonBDA), 5)
   }
 }
