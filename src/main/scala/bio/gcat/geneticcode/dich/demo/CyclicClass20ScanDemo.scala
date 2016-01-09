package bio.gcat.geneticcode.dich.demo

import bio.gcat.geneticcode.dich.scan.Class20CyclicScan
import bio.gcat.geneticcode.dich.{AntiCodonBDA, ParityBDA, RumerBDA}
import org.junit.{Ignore, Test}

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
