package net.gumbix.geneticcode.dich.test

import net.gumbix.geneticcode.dich._
import org.junit.Test
import junit.framework.Assert._
import net.gumbix.geneticcode.dich.CodonImplicitDefs._
import net.gumbix.geneticcode.core.StandardCodeDegeneracy

/**
 * @author Markus Gumbel (m.gumbel@hs-mannheim.de)
 *         (c) 2013 Markus Gumbel
 */
class DegeneracyTest {

  @Test
  def testStandard() {
    assertEquals(1, StandardCodeDegeneracy.minDeg)
    assertEquals(6, StandardCodeDegeneracy.maxDeg)
  }

}
