package net.gumbix.geneticcode.dich.conc

import scala.actors.migration._
import scala.actors.Actor
import scala.concurrent.SyncVar
import scala.reflect.ClassTag

import net.gumbix.geneticcode.dich.BinaryDichotomicAlgorithm
import net.gumbix.geneticcode.dich.Classifier
import net.gumbix.geneticcode.dich.ClassifierSet
import net.gumbix.geneticcode.dich.Scan
import net.gumbix.geneticcode.dich.ct.ClassTable

/**
 * Run the scan algorithm in parallel.
 * param noActors The number of actors used (default is 3).
 * @author Markus Gumbel (m.gumbel@hs-mannheim.de)
 *         (c) 2013 Markus Gumbel
 */
abstract class ConcScan[T <: ClassTable : ClassTag]
(bdas: List[Classifier[Int]], depth: Int, val noActors: Int = 3,
 allClassifiers: ClassifierSet = BinaryDichotomicAlgorithm.bdas216)
  extends Scan[T](bdas, depth, allClassifiers) with Actor {

  var master = this

  val jobCounter = new Counter(this)

  var slaves = Array(this)

  var id = 0

  def cloneScan(): ConcScan[T]

  // TODO make Master have its own class?
  /**
   * This method initializes the slaves and assigns them to the master actor.
   */
  override def init() {
    jobCounter.start()

    val actors = (1 to noActors).map {
      i =>
      // TODO Bug!
        val actor = cloneScan()
        actor.id = i
        actor.master = this
        log info "Created " + actor.toString
        actor
    }
    val aa = actors.toList.toArray
    slaves = aa
    actors.foreach {
      a =>
        a.slaves = aa
        a.master = this
    }
    actors.foreach(_.start)
    log info "Slaves started."
    create(bdas, 0, 0)
    start()
    sv.take
    log info "Concurrency done."
  }

  val sv = new SyncVar[Boolean]

  def act() {
    loop {
      receive {
        case ScanMsg(bdas, h, w) => create(bdas, h, w)
        case OutputMsg(t, actor) => {
          log fine "*** Result by " + actor + ":"
          super.output(t)
        }
        case SolutionMsg(t: T, actor) => super.foundSolution(t)
        case EndMsg => {
          // TODO kill slaves: slaves.foreach(s => s.exit())
          sv.put(true)
          log info "Concurrent calculation end message."
        }
      }
    }
  }

  override def createMsg(bdas: List[Classifier[Int]], h: Int, w: Int) {
    master.jobCounter ! ComputingMsg(1)
    val msg = ScanMsg(bdas, h, w)
    val n = (slaves.size * math.random).asInstanceOf[Int]
    slaves(n) ! msg
  }

  override def doneMsg() {
    master.jobCounter ! ComputingMsg(-1)
  }

  override def foundSolution(t: T) {
    master ! SolutionMsg(t, this)
  }

  override def output(t: ClassTable) {
    master ! OutputMsg(t, this)
  }

  override def store(filename: String) {
    println("Serializable not possible yet.")
  }

  override def toString = "ConcScan" + id
}

case class ScanMsg(bdas: List[Classifier[Int]], h: Int, w: Int)

case class OutputMsg[T <: ClassTable](t: T, who: Actor)

case class SolutionMsg[T <: ClassTable](t: T, who: Actor)

case class ComputingMsg(c: Int)