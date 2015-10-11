package net.gumbix.geneticcode.dich.test

import org.junit.Test
import junit.framework.Assert._
import net.gumbix.geneticcode.dich.{Guanine, Adenine}

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
