package bio.gcat.geneticcode.dich.demo

import bio.gcat.geneticcode.dich.ct.ClassTable
import bio.gcat.geneticcode.dich.scan.ErrorScan
import bio.gcat.geneticcode.dich._
import org.junit.{Ignore, Test}

/**
 * @author Markus Gumbel (m.gumbel@hs-mannheim.de)
 *         (c) 2013 Markus Gumbel
 */
class DichPartDemo {

  @Test
  @Ignore
  def rumerDich {
    val rumerDich = new DichProperty("Rumer") {

      def dich0(c: Compound) = RumerMapping.dich(c)

      def dich1(c: Compound) = RumerMapping.dich(c)

      def dich2(c: Compound) = RumerMapping.dich(c)
    }
    val ct1 = new ClassTable(List(RumerBDA), IUPAC.STANDARD, rumerDich)
    println(ct1.mkString)
  }

  @Test
  @Ignore
  def dich {
    printDich(DichPartitionRumer)
    printDich {
      new DichPartition {
        def dich0(c: Compound) = RumerMapping.dich(c)

        def dich1(c: Compound) = IdMapping.dich(c)

        def dich2(c: Compound) = IdMapping.dich(c)
      }
    }
  }

  def printDich(p: DichPartition) {
    println("Is dich. partition? " + p.isDichPartition)
    println(p.mkString)
  }
}
