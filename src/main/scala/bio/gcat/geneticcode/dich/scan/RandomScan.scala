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
package bio.gcat.geneticcode.dich.scan

import java.io.{File, PrintWriter}
import java.text.SimpleDateFormat
import java.util.Date
import bio.gcat.geneticcode.dich._
import bio.gcat.geneticcode.dich.ct.CodingClassTable
import scala.Some

/**
 * Generate n class tables (models M_i) with a specific characteristic and save
 * their E (compatibility) errors. The characteristics are
 * - the size of the BDA list (min, max)
 * - the number of classes (|M|).
 * The class tables uses the standard genetic code.
 * The errors are written to a file in the current directory. The
 * filename has the form Error-<suffix>.dat. <suffix> can be customized.
 * Note: This class is used to produce figure 3 in M. Gumbel et al: "On Models...".
 * BioSystems. 10.1016/j.biosystems.2016.12.001.
 * @param repeats Number of repeatitions (n).
 * @param bdas List of classifiers (e.g. BDAs) that are always included.
 * @param depthFrom The minimum number of BDAs.
 * @param depthTo The maximum number of BDAs.
 * @param classSize The intended class size |M| when a model is selected.
 * @param fileSuffix An optional file suffix.
 * @author Markus Gumbel (m.gumbel@hs-mannheim.de)
 *         (c) 2016 Markus Gumbel
 */
class RandomScan(repeats: Int, bdas: List[Classifier[Int]], depthFrom: Int, depthTo: Int, classSize: Int,
                 fileSuffix: Option[String] = None,
                 allClassifiers: ClassifierSet = BinaryDichotomicAlgorithm.bdas216) {

  run()

  def run() {
    val maxRepeats = 90000000 // Some sort of breaking condition.
    val suffix = fileSuffix match {
      case None => {
        val d = new SimpleDateFormat("yyyyMMdd-hhmmss").format(new Date())
        "-b" + depthTo + "-d" + depthTo + "-" + d
      }
      case _ => fileSuffix.get
    }
    val errorFile = new PrintWriter(new File("Error" + suffix + ".dat"), "UTF-8")

    var i = 0
    var r = 0
    while (r < repeats && i < maxRepeats) {
      i += 1
      val ct = pickRandomClassTable()
      if (ct.classes.size == classSize) {
        println(ct.relErrorC)
        errorFile.println(ct.relErrorC)
        errorFile.flush()
        r += 1
      }
    }
  }

  /**
   * Pick a random class table using depthFrom to depthTo BDAs.
   */
  def pickRandomClassTable() = {
    /**
     * Create a random BDA set optionally containing fixed BDAs
     * @param l Fixed BDAs or empty if not applicable.
     * @param n Counter to decrease starting with the depth size.
     */
    def randomBDAs(l: List[Classifier[Int]], n: Int): List[Classifier[Int]] = n match {
      case 0 => l
      case _ => {
        val r = (math.random * 216).asInstanceOf[Int]  // a random BDA
        val bda = (BinaryDichotomicAlgorithm.bdas216.classifiers(r)).asInstanceOf[BinaryDichotomicAlgorithm]
        if (!(l.contains(bda) || l.contains(bda.complement))) randomBDAs(bda :: l, n - 1) else randomBDAs(l, n)
      }
    }
    // Random number in range depthFrom, ..., depthTo:
    val d = (math.random * (depthTo - depthFrom + 1)).asInstanceOf[Int]
    val bdasL = randomBDAs(List() ++ bdas, depthFrom + d)
    new CodingClassTable(bdasL, IUPAC.STANDARD)
  }
}

/**
 * A default executable.
 */
object RandomScan {
  def main(args: Array[String]): Unit = {
    new RandomScan(500, List(RumerBDA, AntiCodonBDA), 5, 8, 24, Some("-tmp"))
  }
}
