package net.gumbix.geneticcode.dich.scan

import net.gumbix.geneticcode.dich.{BinaryDichotomicAlgorithm => BDA, Adenine => A, Uracil => U, Cytosine => C, Guanine => G, _}
import net.gumbix.util.PowerSet
import net.gumbix.geneticcode.dich.ct.CodingClassTable

/**
 * Calculate the minimal subset of the 16 known Bio-BDAs.
 * @author Markus Gumbel (m.gumbel@hs-mannheim.de)
 *         (c) 2012 Markus Gumbel
 */
object MinBioBDASet extends PowerSet {

  val minBioBDAs = List(
    // BDA(0, 1, (A, U), Set(A, U)),
    BDA(0, 1, (A, U), Set(C, G)),
    // BDA(0, 1, (C, G), Set(A, U)),
    BDA(0, 1, (C, G), Set(C, G)),

    // BDA(1, 0, (A, U), Set(A, U)),
    BDA(1, 0, (A, U), Set(C, G)),
    // BDA(1, 0, (C, G), Set(A, U)),
    BDA(1, 0, (C, G), Set(C, G)),

    // BDA(2, 0, (A, G), Set(A, U)),
    BDA(2, 0, (C, U), Set(A, U)),
    // BDA(2, 0, (A, G), Set(C, G)),
    BDA(2, 0, (C, U), Set(C, G)),

    // BDA(2, 1, (A, G), Set(A, U)),
    // BDA(2, 1, (C, U), Set(A, U)),
    BDA(2, 1, (A, G), Set(C, G))
    // BDA(2, 1, (C, U), Set(C, G))
  )

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

  def size64(t: CodingClassTable) = t.classes.size == 64

  def subspace(t: CodingClassTable) = t.isMovedVectorSubSpace == true

  def main(args: Array[String]) {
    scan(1)
  }

  //val allRemoves = powerSetIndices(0, bioBDAs.size - 1, bioBDAs.size - 5)
  val allRemoves = powerSetIndices(0, bioBDAs.size - 1, 4)

  def scan(size: Int) {
    println("BDA list size = " + (bioBDAs.size - size))
    val l = shortenedBDAList(size)
    val l64 = l.map(bda => new CodingClassTable(bda)).filter(size64)
    println("Solutions with 64 classes = " + l64.size)

    val svs = l64.filter(subspace)
    println("Solutions with 64 classes and moved vector subspace = " + svs.size)

    if (l64.size > 0) scan(size + 1)
  }

  /**
   * Create a list of BDA-lists from the BioBDA-list where BDAs are removed
   * that are part of the power set of size length.
   * @param length
   * @return
   */
  def shortenedBDAList(length: Int): List[List[BDA]] = {
    val removes = allRemoves.filter(e => e.size == length)
    for (remove <- removes) yield {
      val z = (0 until bioBDAs.size).zip(bioBDAs) // (index, BDA)-list
      val u = z.filter(e => !remove.contains(e._1))
      u.map(_._2).toList // Return the BDA list only, not the indices
    }
  }

  def print(t: CodingClassTable) = {
    println(t.bdaMkString)
    println(t.mkString)
    println(t.classesMkString)
    println(t.hammingDistBtwAA)
    println(t.isVectorSubSpace)
    println(t.isMovedVectorSubSpace)
  }
}
