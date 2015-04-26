package net.gumbix.geneticcode.dich.scan

import net.gumbix.geneticcode.dich._
import scala.collection.mutable.ArrayBuffer
import net.gumbix.geneticcode.dich.{BinaryDichotomicAlgorithm => BDA, Adenine => A, Uracil => U, Cytosine => C, Guanine => G, _}

/**
 * @author Markus Gumbel (m.gumbel@hs-mannheim.de)
 *         (c) 2013 Markus Gumbel
 */
object RHScan {
  def main(args: Array[String]) {
    new RHScan(List(), 10).run()
  }

  val allRHBdas = {
    val bioBDAs = List(
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
    ClassifierSet(bioBDAs.toArray, "RH-BDAs")
  }
}

class RHScan(bdas: List[BinaryDichotomicAlgorithm], size: Int)
  extends HammingGroupScan(bdas, size) {

  override val allClassifiers = RHScan.allRHBdas
  override def startMessage = "RH-BDA"
}
