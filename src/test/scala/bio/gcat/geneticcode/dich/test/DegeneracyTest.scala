package bio.gcat.geneticcode.dich.test

import bio.gcat.geneticcode.core.StandardCodeDegeneracy
import junit.framework.Assert._
import org.junit.Test

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
