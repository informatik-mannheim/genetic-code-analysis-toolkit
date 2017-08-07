/*
 * Copyright [2017] [Mannheim University of Applied Sciences]
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package bio.gcat.geneticcode.dich.test

import java.text.SimpleDateFormat

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
    Loggable.fileLog = false // avoid output
    Loggable.consoleLog = false
    var counter = 0
    val s = new Scan[ClassTable](depth) with ClassTableScan {

      doPersist = false

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
      doPersist = false
    }
    val should = "Scan-b0-d2-" +
      new SimpleDateFormat("yyyyMMdd-hhmmss").format(s.date)

    assertEquals(should, s.filename)
    //assertEquals(23220, s.solutions.size)
  }
}
