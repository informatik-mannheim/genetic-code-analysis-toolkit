package net.gumbix.geneticcode.feedback

import org.biojava3.core.sequence.RNASequence

/**
 * A codon (triplet or RNA bases).
 * (c) 2012 by Markus Gumbel (m.gumbel@hs-mannheim.de)
 * @author Markus Gumbel
 */
case class Codon(val sequence: String) extends RNASequence(sequence)
