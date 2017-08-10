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
package bio.gcat.lal.test

import bio.gcat.lal.F2Vector
import junit.framework.Assert._
import org.junit.Test

/**
 * @author Markus Gumbel (m.gumbel@hs-mannheim.de)
 *         (c) 2012 Markus Gumbel
 */
class F2Test {

  @Test(expected = classOf[java.lang.IllegalArgumentException])
  def testDifferentSize() {
    val x1 = F2Vector(0)
    val x2 = F2Vector(0, 1)
    val s = x1 + x2
  }

  @Test
  def test1011() {
    val x1 = F2Vector(1, 0)
    val x2 = F2Vector(1, 1)
    val s = x1 + x2
    assertEquals(F2Vector(0, 1), s)
  }

  @Test
  def test0000() {
    val x1 = F2Vector(0, 0)
    val x2 = F2Vector(0, 0)
    val s = x1 + x2
    assertEquals(F2Vector(0, 0), s)
  }

  @Test
  def test1111() {
    val x1 = F2Vector(1, 1)
    val x2 = F2Vector(1, 1)
    val s = x1 + x2
    assertEquals(F2Vector(0, 0), s)
  }
}
