package net.gumbix.geneticcode.dich.test

import org.junit.Test
import junit.framework.Assert._
import net.gumbix.geneticcode.dich._
import collection.immutable.HashSet

/**
 * @author Markus Gumbel (m.gumbel@hs-mannheim.de)
 *         (c) 2012 Markus Gumbel
 */
class DichQuestionTest {

  val nucs = Nucleotides.nucleotides.toSet

  @Test
  def complement() {
    assertEquals(Set[Compound](Guanine, Cytosine, Uracil),
      nucs.diff(Set[Compound](Adenine)))
    assertEquals(Set[Compound](Guanine, Cytosine, Uracil, Adenine),
      nucs.diff(Set[Compound]()))
  }
}
