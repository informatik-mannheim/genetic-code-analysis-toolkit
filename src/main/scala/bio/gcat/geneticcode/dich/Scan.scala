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
package bio.gcat.geneticcode.dich

import java.text.SimpleDateFormat
import java.util.{Date, HashMap}
import bio.gcat.geneticcode.dich.ct.{ClassTable, CodingClassTable}
import bio.gcat.geneticcode.dich.db.DB
import bio.gcat.util.Version

import scala.collection.JavaConversions._
import scala.reflect.ClassTag

case class ClassifierSet(classifiers: Array[Classifier[Int]], label: String)

/**
 * @author Markus Gumbel (m.gumbel@hs-mannheim.de)
 *         (c) 2012 Markus Gumbel
 */
abstract class Scan[T <: ClassTable : ClassTag]
(bdas: List[Classifier[Int]], depth: Int,
 val allClassifiers: ClassifierSet = BinaryDichotomicAlgorithm.bdas216)
  extends ScanConstraint[T] with DB[Scan[T]] with Serializable {

  private val serialVersionUID = 20130322;

  def this(size: Int) = this(List(), size)

  def this(bdas: Array[String], size: Int) =
    this(BdaImplicitDefs.toBda(bdas), size)

  // private val allClassifiers = classifierSet.allClassifiers

  /**
    * True if scan will be persisted, otherwise false
    */
  var doPersist = true

  /**
   * Number of configurations that have been tested.
   */
  var configs: Double = 0.0

  private var cMax = 0

  def max_=(value: Int) {
    if (value > cMax) cMax = value
  }

  /**
   * The optimal (best) solution found yet.
   * @return
   */
  def max = cMax

  /**
   * Create all valid configurations.
   * @param bdas The list of BDAs which have already been computed.
   * @param h An index pointing to the start BDA in the array of
   *          all BDAs. This is to avoid redundant BDAs in the bda parameter.
   * @param w A criterion for the decision if a new configuration
   *          is a valid one, i.e. the subtree should be followed.
   */
  protected[this] def create(bdas: List[Classifier[Int]], h: Int, w: Int) {
    // Check if the bda list has reached the maximum scan depth:
    if (bdas.size < depth) {
      // Go through all BDAs excepts those which have already been considered;
      // i.e. those with and index i<h.
      for (i <- h until allClassifiers.classifiers.size) {
        val nBda = allClassifiers.classifiers(i) // new BDA candidate.
        // Check if it is not already in the bda list.
        // This might happen as we can pass in a list of pre-defined BDAs.
        if (!bdas.contains(nBda)) {
          val nbdas = nBda :: bdas // Element is added at front...
          val (valid, wn) = createClassTable(nbdas.reverse, w) // ..so revert list.
          configs += 1 // This is a configuration that was tested.
          // If this configuration is valid the search is continued:
          if (valid) createMsg(nbdas, i + 1, wn)
        }
      }
    }
    doneMsg()
  }

  /**
   * For concurrency
   * @param bdas
   * @param h
   * @param w
   */
  def createMsg(bdas: List[Classifier[Int]], h: Int, w: Int) {
    create(bdas, h, w)
  }

  def doneMsg() {
  }

  // DB4O bug
  private val csolutions = new java.util.ArrayList[T]()

  def foundSolution(ct: T) {
    csolutions.add(ct)
  }

  /**
   * Used also for Matlab.
   * A very tricky part as we use now generics.
   * see http://stackoverflow.com/questions/2252641/cannot-find-class-manifest-for-element-type-t
   * or
   * http://www.scala-lang.org/sites/default/files/sids/cunei/Thu,%202009-10-01,%2013:54/arrays.pdf
   * @return
   */
  def solutions = {
    csolutions.toArray[T](new Array[T](0))
  }

  /**
   * Factory method for the underlying class table.
   * @param bda
   * @return
   */
  def newClassTable(bda: List[Classifier[Int]]): T

  /**
   * A template for the creation of a class table.
   * This method can be overwritten if the algorithm
   * cannot be configured via the methods isSolution(),
   * scanParameter() and isValidConfiguration().
   * @param bda
   * @return 1. False if current class table is an invalid solution and
   *         2. a scan parameter value that is passed to the next call.
   */
  protected[this] def createClassTable(bda: List[Classifier[Int]], w: Int) = {
    val t = newClassTable(bda)
    val valid = isValidConfig(t, w)
    if (valid && isSolution(t)) {
      foundSolution(t)
      bda.foreach(count(_))
      output(t)
    }
    (valid, scanParameter(t))
  }

  def output(t: ClassTable) {
    log fine "\n--------------------------------------------------------"
    log fine new Date().toString + " / " + Version
    log fine "solution # = " + solutions.size
    log fine t.mkFullString
    log finest "\n# configurations so far = " + configs
    log finest "|used questions so far| = " + qCounter.size
    log finest "used questions so far = " + qCounter.toList.sortBy(x => (x._2, x._1.toString))
  }

  def combinations = {
    val N = allClassifiers.classifiers.size.asInstanceOf[Double]
    (1 to (depth - bdas.size)).map(i => (N - i + 1) / i).product
  }

  def progress = configs / combinations

  val qCounter = new HashMap[Classifier[Int], Int]()

  def count(q: Classifier[Int]) {
    if (qCounter.get(q) == 0) {
      qCounter(q) = 1
    } else {
      qCounter(q) = qCounter(q) + 1
    }
  }

  val date = new Date()

  val suffix = {
    val d = new SimpleDateFormat("yyyyMMdd-hhmmss").format(date)
    "-b" + bdas.size + "-d" + depth + "-" + d
  }

  val filename = "Scan" + suffix

  def startMessage = "Default scan"

  def init() {
    create(bdas, 0, 0)
  }

  // Note: must be at the end of the class. Otherwise
  // some variables might not be instantiated.
  def run() {
    val startTime = System.currentTimeMillis()
    log fine "Welcome to " + Version.toString + "."
    log info startMessage
    log info "Classifier/BDA set: " + allClassifiers.label
    log info (if (bdas.isEmpty) "No fixed classifiers/BDAs." else bdas.mkString("[", "; ", "]") + " fixed.")
    log info "max. " + combinations + " combinations will be tested."
    if (bdas.size >= depth) {
      log info "Nothing to scan. Only using fixed classifiers/BDAs as parameters."
      configs += 1
      createClassTable(bdas, 0)
    } else {
      // Regular scan:
      init()
    }
    log fine "\nDone!"
    log fine configs + " combinations out of about " +
      combinations + " combinations (duplicates are not yet considered.)"
    log info "solutions = " + solutions.size

    if (doPersist) {
      store(filename)
      log info "Saved to " + filename + ".ser"
    }
    val duration = (System.currentTimeMillis() - startTime) / 1000.0
    log info "T = " + duration + " s"
  }
}

// TODO: make genetic code table a parameter
trait ClassTableScan {
  def newClassTable(bda: List[Classifier[Int]]) =
    new ClassTable(bda, IUPAC.EUPLOTID_NUCLEAR)
}

trait CodingClassTableScan {
  def newClassTable(bda: List[Classifier[Int]]) =
    new CodingClassTable(bda, IUPAC.EUPLOTID_NUCLEAR)
}

object Scan extends DB[Scan[ClassTable]]
