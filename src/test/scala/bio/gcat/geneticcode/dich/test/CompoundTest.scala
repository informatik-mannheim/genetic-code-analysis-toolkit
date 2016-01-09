package bio.gcat.geneticcode.dich.test

import bio.gcat.geneticcode.dich.{Adenine, Guanine}
import junit.framework.Assert._
import org.junit.Test

/**
 * @author Markus Gumbel (m.gumbel@hs-mannheim.de)
 * (c) 2012 Markus Gumbel
 */
class CompoundTest {

  @Test
  def testEqual() {
    assertEquals(true, Adenine == Adenine)
    assertEquals(false, Adenine == Guanine)
  }
}
