package net.gumbix.geneticcode.dich.demo

import org.junit.{Ignore, Test}
import net.gumbix.geneticcode.dich._
import net.gumbix.geneticcode.dich.scan.ErrorScan
import net.gumbix.geneticcode.dich.{BinaryDichotomicAlgorithm => BDA}
import net.gumbix.geneticcode.dich.{Adenine => A, Uracil => U, Cytosine => C, Guanine => G}
import net.gumbix.geneticcode.dich.ct.ClassTable

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
