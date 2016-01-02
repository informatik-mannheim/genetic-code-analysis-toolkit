package bio.gcat.geneticcode.dich.test

import bio.gcat.geneticcode.dich._
import junit.framework.Assert._
import org.junit.Test

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
