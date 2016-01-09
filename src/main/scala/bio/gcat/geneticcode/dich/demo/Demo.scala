package bio.gcat.geneticcode.dich.demo

import bio.gcat.geneticcode.dich._
import bio.gcat.geneticcode.dich.ct.ClassTable

/**
 * @author Markus Gumbel (m.gumbel@hs-mannheim.de)
 *         (c) 2012 Markus Gumbel
 */
object Demo {

  val r2 = new BinaryDichotomicAlgorithm(0, 1, (Adenine, Cytosine), Set(Guanine, Cytosine))
  val p2 = new BinaryDichotomicAlgorithm(1, 2, (Adenine, Guanine), Set(Adenine, Cytosine))
  val a2 = new BinaryDichotomicAlgorithm(2, 0, (Cytosine, Guanine), Set(Guanine, Adenine))

  def main(args: Array[String]) {
    val bdas = List(RumerBDA, ParityBDA, AntiCodonBDA) // r2, p2, a2)

    for (n <- 1 to bdas.size) {
      val l = bdas.take(n)
      val t = new ClassTable(l)

      println("-----------")
      println("No. of questions = " + t.bdas.size)
      println(t.bdaMkString)
      println(t.mkString())
      println("deg. = " + t.degeneracy.mkString)
      println("|classes| = " + t.classes.size)
      println(t.classesMkString)
      //println("")
      //println(t.classesMkStringSize)
      println(t.aaMkString())
    }
  }
}
