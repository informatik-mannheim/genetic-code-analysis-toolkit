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

import bio.gcat.geneticcode.dich.ct.ClassTable
import bio.gcat.geneticcode.dich._
import org.junit.{Ignore, Test}
import junit.framework.Assert._
import bio.gcat.geneticcode.dich.CodonImplicitDefs._

/**
 * @author Markus Gumbel (m.gumbel@hs-mannheim.de)
 *         (c) 2012 Markus Gumbel
 */
class ClassTableTest {

  @Test(expected = classOf[java.lang.IllegalArgumentException])
  def testEmpty() {
    val ct = new ClassTable(Nil)
  }

  @Test
  def testTwo() {
    val ct = new ClassTable(RumerBDA :: ParityBDA :: Nil)
    assertEquals(4, ct.classes.size)
    assertEquals(2, ct.bdas.size)
  }

  @Test
  def testGeneticCode() {
    val ct = new ClassTable(List(RumerBDA), IUPAC.VERTEBRATE_MITOCHONDRIAL)
    // println(ct.mkString)
  }

  @Test
  def testCodonProperty() {
    val ct = new ClassTable(List(AntiCodonBDA, RumerBDA, ParityBDA),
      IUPAC.STANDARD, IdCodonProperty)
    assertEquals(0, ct.errorC)
    assertEquals("AUG", ct.codonProperty.property(Codon("AUG")).toString)
  }

  @Test
  def testAminoAcidProperty() {
    val ct = new ClassTable(List(AntiCodonBDA, RumerBDA, ParityBDA),
      IUPAC.STANDARD, new IdAminoAcidProperty(IUPAC.STANDARD))
    assertEquals("M", ct.codonProperty.property(Codon("AUG")).toString)
  }

  @Ignore
  @Test
  def testAminoAcidX() {
    val ct = new ClassTable(List(AntiCodonBDA, RumerBDA, ParityBDA),
      IUPAC.STANDARD, new PolarityPAminoAcidProperty(IUPAC.STANDARD))
    println(ct.prop2ClassesList.mkString("\n"))
    println(ct.prop2Error)
    println(ct.errorC)
    println(ct.mkString)
  }
}
