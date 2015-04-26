package net.gumbix.geneticcode.dich.ct

import net.gumbix.geneticcode.dich.{CodonProperty, Codon, Classifier}
import java.util.{HashSet, HashMap}
import net.gumbix.geneticcode.core.CodonMapping
import scala.collection.JavaConversions._

/**
 * TODO: Move to abstract class as these are properties of a class table.
 * @author Markus Gumbel (m.gumbel@hs-mannheim.de)
 *         (c) 2014 Markus Gumbel
 */
trait ClassTableFormatter extends GeneticCodeTableFormatter {
  val codons: List[Codon]
  val bdas: List[Classifier[Int]]
  val codonProperty: CodonProperty
  val class2codons: HashMap[List[Int], List[Codon]]
  val codon2class: HashMap[Codon, List[Int]]
  val aa2classes: HashMap[CodonMapping, HashSet[List[Int]]]

  def colorRGB(codon: Codon) = {
    val list = codon2class(codon) // List of Int
    val m = list.size / 3
    val rList = list.take(m)
    val gList = list.drop(m).take(m)
    val bList = list.drop(2 * m)

    def rgbValue(l: List[Int]) = l match {
      case Nil => 255 // treat as white
      case _ => {
        val n = l.size
        val k = (0 until n).map(i => math.pow(2, i))
        val z = (k zip l).map(e => e._1 * e._2).sum
        val p = z / (math.pow(2, n) - 1) * 100 // 0 .. 100  or 000 to 111
        (255 - p).asInstanceOf[Int] // 155 .. 255 or 111 to 000
      }
    }

    val r = rgbValue(rList)
    val g = rgbValue(gList)
    val b = rgbValue(bList)
    (r, g, b)
  }
}
