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

import bio.gcat.lal.F2VectorSpace
import junit.framework.Assert._
import bio.gcat.lal.{F2Vector => F}
import org.junit.Test

/**
 * @author Markus Gumbel (m.gumbel@hs-mannheim.de)
 *         (c) 2012 Markus Gumbel
 */
class F2VectorSpaceTest {

  @Test
  def testSubSpace() {
    val c = List(F(0, 0, 0), F(0, 1, 0), F(1, 0, 0), F(1, 1, 0))
    val vs = new F2VectorSpace(3)
    assertEquals(None, vs.subSpaceExceptions(c))
    assertTrue(vs.isSubSpace(c))
    assertTrue(vs.isMovedSubSpace(c))
  }

  @Test
  def testNoSubSpace() {
    val c = List(F(0, 0, 0), F(0, 1, 0), F(1, 0, 0))
    val vs = new F2VectorSpace(3)
    assertEquals(Some(List((F(0, 1, 0), F(1, 0, 0)))), vs.subSpaceExceptions(c))
    assertTrue(!vs.isSubSpace(c))
    assertTrue(!vs.isMovedSubSpace(c))
  }

  @Test
  def testMovedSubSpace() {
    val c = List(F(0, 0, 1), F(0, 1, 1), F(1, 0, 1), F(1, 1, 1))
    val vs = new F2VectorSpace(3)
    assertEquals(Some(Nil), vs.subSpaceExceptions(c))
    assertTrue(!vs.isSubSpace(c))
    assertTrue(vs.isMovedSubSpace(c))
    val l = vs.movedSubSpaceExceptions(c)
    // println(l.mkString("\n"))
  }
}
