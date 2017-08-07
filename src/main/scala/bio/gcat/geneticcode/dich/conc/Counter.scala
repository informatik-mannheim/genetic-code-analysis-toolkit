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