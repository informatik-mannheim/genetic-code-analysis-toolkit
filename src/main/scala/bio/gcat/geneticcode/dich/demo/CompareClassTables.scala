package bio.gcat.geneticcode.dich.demo

import bio.gcat.geneticcode.dich.ct.ClassTable
import bio.gcat.geneticcode.dich.{RumerBDA, BinaryDichotomicAlgorithm}
import bio.gcat.util.Combine
import org.junit.{Ignore, Test}

/**
 * @author Markus Gumbel (m.gumbel@hs-mannheim.de)
 *         (c) 2013 Markus Gumbel
 */
class CompareClassTables {

  @Test
  @Ignore
  def compare() {
    val allBDAs = BinaryDichotomicAlgorithm.bdas216.classifiers.toList
    val classTables = allBDAs.map(bda => new ClassTable(List(bda)))
    val comb = Combine.combine(classTables)
    val res = for ((ct1, ct2) <- comb) yield {
      (ct1, ct2, ct1.codon2class.equals(ct2.codon2class))
    }
    val hits = res.filter(c => c._3 == true)

    println("Comparison: " + 216*215/2 + " = " + res.size)
    println("redundant: " + hits.size)
    /*
    hits.foreach{
      h =>
        println(h._1.bdaMkString)
        println(h._2.bdaMkString)
        println("")
    }
    */
  }

  @Test
  @Ignore
  def compareTest() {
    val ct = new ClassTable(List(RumerBDA))
    assert(ct.codon2class == ct.codon2class)
    assert(ct.codon2class.equals(ct.codon2class))
  }
}
