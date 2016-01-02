package bio.gcat.geneticcode.dich.test

import bio.gcat.geneticcode.dich.ct.ClassTable
import bio.gcat.geneticcode.dich.scan.ClassPower2Scan
import bio.gcat.geneticcode.dich.{ClassTableScan, Classifier, Scan}
import bio.gcat.util.Loggable
import junit.framework.Assert._
import org.junit.{Ignore, Test}

/**
 * @author Markus Gumbel (m.gumbel@hs-mannheim.de)
 *         (c) 2012 Markus Gumbel
 */
class ScanTest {

  @Test
  def testCountBdas() {
    val scan = new Scan[ClassTable](1) with ClassTableScan {

    }
    assertEquals(216, scan.allClassifiers.classifiers.size)
  }

  @Test
  def testCountDepth1() {
    countDepth(1, 216)
  }

  @Test
  @Ignore
  def testCountDepth2() {
    countDepth(2, 23220)
  }

  @Test
  @Ignore
  def testCountDepth3() {
    countDepth(3, 1656360)
  }

  def countDepth(depth: Int, isValue: Int) {
    var counter = 0
    val s = new Scan[ClassTable](depth) with ClassTableScan {

      override def createClassTable(bda: List[Classifier[Int]], w: Int) = {
        val t = new ClassTable(bda)
        if (bda.size == depth) counter += 1
        (true, w)
      }
    }
    s.run()
    assertEquals(isValue, counter)
  }

  /**
   * Takes quite long
   */
  @Test
  @Ignore
  def testPower2() {
    Loggable.fileLog = false // to save time
    val s = new ClassPower2Scan(List(), 2)
    s.run()
    assertEquals(23220, s.solutions.size)
  }

  @Test
  def testMetadata() {
    Loggable.fileLog = false // to save time
    val s = new Scan[ClassTable](List(), 2) with ClassTableScan {

      }
    val info = s.filename
    println(info)
    //assertEquals(23220, s.solutions.size)
  }
}
