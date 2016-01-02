package bio.gcat.geneticcode.dich.conc

import bio.gcat.geneticcode.dich.ct.ClassTable
import scala.actors.Actor

/**
 * @author Markus Gumbel (m.gumbel@hs-mannheim.de)
 *         (c) 2012 Markus Gumbel
 */
class Counter[T <: ClassTable](val master: ConcScan[T]) extends Actor {

  private var counts = 0

  def act() {
    loop {
      receive {
        case ComputingMsg(c) => {
          counts += c
          // println("count = " + c)
          if (counts == 0) master ! EndMsg
        }
      }
    }
  }
}

case class EndMsg()