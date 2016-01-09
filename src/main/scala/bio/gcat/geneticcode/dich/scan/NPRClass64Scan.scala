package bio.gcat.geneticcode.dich.scan

import bio.gcat.geneticcode.dich.ct.{NonPowerClassTable, ClassTable}
import bio.gcat.geneticcode.dich.{IUPAC, Classifier, Scan}

import scala.collection.JavaConversions._

/**
 * Sequential version only.
 * @author Markus Gumbel (m.gumbel@hs-mannheim.de)
 *         (c) 2012 Markus Gumbel
 */
object NPRClass64Scan {
  def main(args: Array[String]) {
    new NPRClass64Scan(List(), 6)
  }
}

class NPRClass64Scan(bdas: List[Classifier[Int]], size: Int)
  extends Scan[ClassTable](bdas, size) {

  val prefixDepth = 1

  override def startMessage = "# Under construction # 64 classes (NPR compatible)"

  override def isSolution(classTable: ClassTable) = true // Not used.

  override def createClassTable(bda: List[Classifier[Int]], w: Int) = {
    val t = newClassTable(bda)
    val noClasses = math.pow(2, t.bdas.size).asInstanceOf[Int]
    val crit1 = t.classes.size == noClasses
    var config = -1
    if (crit1) {
      config = isCompatibleWithConfig(t)
      log fine "BDA # " + configs
      if (config >= 0) {
        log fine "No. of questions = " + t.bdas.size
        log fine t.classesMkString(config)
        log warning "Yeah!"
        System.exit(0)
      }
    }
    (crit1 && (config >= 0), w)
  }

  def newClassTable(bda: List[Classifier[Int]]) =
    new NonPowerClassTable(bda, IUPAC.EUPLOTID_NUCLEAR)

  def isCompatibleWithConfig(t: NonPowerClassTable): Int = {

    if (t.bdas.size <= prefixDepth) {

      def check(table: Map[String, List[Char]]): Boolean = {
        val l = for (clazz <- t.classes) yield {
          val aaAre = t.class2AAList(clazz).mkString("").toList // Already sorted!
          val aaShould = table(clazz.mkString(""))
          // Just for debugging:
          val x1 = aaAre.mkString(",")
          val x2 = aaShould.mkString(",")
          // end
          aaAre == aaShould
        }
        l.forall(b => b) // all have to be true.
      }
      // Check all permutations:
      val res = for (i <- 0 until 4096) yield {
        if (check(t.npbs.prefixL(i))) i else -1
      }
      val r = res.filter(e => e >= 0)
      if (r.size > 0) r.head else -1
    } else 0
  }
}
