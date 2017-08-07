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
package bio.gcat.lal

/**
 * @author Markus Gumbel (m.gumbel@hs-mannheim.de)
 *         (c) 2012 Markus Gumbel
 */
class F2Vector(val x: Int*) {

  private def checkDim(v: F2Vector) {
    if (v.size != x.size) throw new IllegalArgumentException("Vector " +
      "must have size " + x.size + " but has " + v.size)
  }

  def +(v: F2Vector) = {
    checkDim(v)
    val n = v.x.zip(x).map(e => (e._1 + e._2) % 2).toList
    F2Vector(n: _*)
  }

  def -(v: F2Vector) = v + this

  def size = x.size

  override def equals(that: Any) = that match {
    case v: F2Vector => v.x == x
    case _ => false
  }

  override def hashCode() = x.hashCode

  override def toString() = x.mkString("")
}

object F2Vector {
  def apply(v: Int*) = new F2Vector(v: _*)
}
