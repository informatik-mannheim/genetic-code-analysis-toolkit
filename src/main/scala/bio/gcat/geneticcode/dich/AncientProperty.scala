package bio.gcat.geneticcode.dich

import bio.gcat.geneticcode.core.{StopCodon, CodonMapping}
import bio.gcat.geneticcode.core.{AminoAcidMapping => AA}
import bio.gcat.geneticcode.dich.CodonImplicitDefs._

/**
 * Here are the ancient code tables defined (as used in Karin Adler's bachelor thesis).
 * @param label Description for the ancient genetic code table.
 * @param c2aa
 * Defines the mapping for non-nonsense-codons to amino acids.
 * Any codons not found in this mapping table is considered to
 * be a nonsense-codon (i.e. stop codon).
 * @author Markus Gumbel (m.gumbel@hs-mannheim.de), Karin Adler
 *         (c) 2014 Markus Gumbel
 */
abstract class AncientProperty(val label: String, c2aa: Map[Codon, CodonMapping])
  extends CodonProperty {

  // TODO not String!!!
  def property(c: Codon) =
    if (c2aa.contains(c)) c2aa(c).toString else StopCodon.toString

  override def toString = "ancient code table classifier (" + label + ")"
}

/**
 * As explained in Jimenez-Montano 1999.
 */
object Jiminez8AncientProperty extends AncientProperty("Jiminez 8 classes",
  Map(
    Codon("UUU") -> AA("L"),
    Codon("UUC") -> AA("L"),
    Codon("UUA") -> AA("L"),
    Codon("UUG") -> AA("L"),

    Codon("CUU") -> AA("L"),
    Codon("CUC") -> AA("L"),
    Codon("CUA") -> AA("L"),
    Codon("CUG") -> AA("L"),

    Codon("AUU") -> AA("V"),
    Codon("AUC") -> AA("V"),
    Codon("AUA") -> AA("V"),
    Codon("AUG") -> AA("V"),

    Codon("GUU") -> AA("V"),
    Codon("GUC") -> AA("V"),
    Codon("GUA") -> AA("V"),
    Codon("GUG") -> AA("V"),

    // -- NCN
    Codon("UCU") -> AA("P"),
    Codon("UCC") -> AA("P"),
    Codon("UCA") -> AA("P"),
    Codon("UCG") -> AA("P"),

    Codon("CCU") -> AA("P"),
    Codon("CCC") -> AA("P"),
    Codon("CCA") -> AA("P"),
    Codon("CCG") -> AA("P"),

    Codon("ACU") -> AA("A"),
    Codon("ACC") -> AA("A"),
    Codon("ACA") -> AA("A"),
    Codon("ACG") -> AA("A"),

    Codon("GCU") -> AA("A"),
    Codon("GCC") -> AA("A"),
    Codon("GCA") -> AA("A"),
    Codon("GCG") -> AA("A"),

    // --  NAN
    Codon("UAU") -> StopCodon,
    Codon("UAC") -> StopCodon,
    Codon("UAA") -> StopCodon,
    Codon("UAG") -> StopCodon,

    Codon("CAU") -> StopCodon,
    Codon("CAC") -> StopCodon,
    Codon("CAA") -> StopCodon,
    Codon("CAG") -> StopCodon,

    Codon("AAU") -> AA("D"),
    Codon("AAC") -> AA("D"),
    Codon("AAA") -> AA("D"),
    Codon("AAG") -> AA("D"),

    Codon("GAU") -> AA("D"),
    Codon("GAC") -> AA("D"),
    Codon("GAA") -> AA("D"),
    Codon("GAG") -> AA("D"),

    // --  NGN
    Codon("UGU") -> AA("R"),
    Codon("UGC") -> AA("R"),
    Codon("UGA") -> AA("R"),
    Codon("UGG") -> AA("R"),

    Codon("CGU") -> AA("R"),
    Codon("CGC") -> AA("R"),
    Codon("CGA") -> AA("R"),
    Codon("CGG") -> AA("R"),

    Codon("AGU") -> AA("G"),
    Codon("AGC") -> AA("G"),
    Codon("AGA") -> AA("G"),
    Codon("AGG") -> AA("G"),

    Codon("GGU") -> AA("G"),
    Codon("GGC") -> AA("G"),
    Codon("GGA") -> AA("G"),
    Codon("GGG") -> AA("G")
  )
)

object Jiminez4AncientProperty extends AncientProperty("Jiminez 4 classes",
  Map(
    Codon("UUU") -> AA("L"),
    Codon("UUC") -> AA("L"),
    Codon("UUA") -> AA("L"),
    Codon("UUG") -> AA("L"),

    Codon("CUU") -> AA("L"),
    Codon("CUC") -> AA("L"),
    Codon("CUA") -> AA("L"),
    Codon("CUG") -> AA("L"),

    Codon("AUU") -> AA("L"),
    Codon("AUC") -> AA("L"),
    Codon("AUA") -> AA("L"),
    Codon("AUG") -> AA("L"),

    Codon("GUU") -> AA("L"),
    Codon("GUC") -> AA("L"),
    Codon("GUA") -> AA("L"),
    Codon("GUG") -> AA("L"),

    // -- NCN
    Codon("UCU") -> AA("P"),
    Codon("UCC") -> AA("P"),
    Codon("UCA") -> AA("P"),
    Codon("UCG") -> AA("P"),

    Codon("CCU") -> AA("P"),
    Codon("CCC") -> AA("P"),
    Codon("CCA") -> AA("P"),
    Codon("CCG") -> AA("P"),

    Codon("ACU") -> AA("P"),
    Codon("ACC") -> AA("P"),
    Codon("ACA") -> AA("P"),
    Codon("ACG") -> AA("P"),

    Codon("GCU") -> AA("P"),
    Codon("GCC") -> AA("P"),
    Codon("GCA") -> AA("P"),
    Codon("GCG") -> AA("P"),

    // --  NAN
    Codon("UAU") -> StopCodon,
    Codon("UAC") -> StopCodon,
    Codon("UAA") -> StopCodon,
    Codon("UAG") -> StopCodon,

    Codon("CAU") -> StopCodon,
    Codon("CAC") -> StopCodon,
    Codon("CAA") -> StopCodon,
    Codon("CAG") -> StopCodon,

    Codon("AAU") -> StopCodon,
    Codon("AAC") -> StopCodon,
    Codon("AAA") -> StopCodon,
    Codon("AAG") -> StopCodon,

    Codon("GAU") -> StopCodon,
    Codon("GAC") -> StopCodon,
    Codon("GAA") -> StopCodon,
    Codon("GAG") -> StopCodon,

    // --  NGN
    Codon("UGU") -> AA("R"),
    Codon("UGC") -> AA("R"),
    Codon("UGA") -> AA("R"),
    Codon("UGG") -> AA("R"),

    Codon("CGU") -> AA("R"),
    Codon("CGC") -> AA("R"),
    Codon("CGA") -> AA("R"),
    Codon("CGG") -> AA("R"),

    Codon("AGU") -> AA("R"),
    Codon("AGC") -> AA("R"),
    Codon("AGA") -> AA("R"),
    Codon("AGG") -> AA("R"),

    Codon("GGU") -> AA("R"),
    Codon("GGC") -> AA("R"),
    Codon("GGA") -> AA("R"),
    Codon("GGG") -> AA("R")
  )
)

/**
 * As explained in DiGiulio 2008: Extension of the Coevolution theory of the
 * beginning of the Genetic Code
 */
object DiGiulioSNSCode extends AncientProperty("DiGiulio 8/4 classes",
  Map(
    Codon("UUU") -> StopCodon,
    Codon("UUC") -> StopCodon,
    Codon("UUA") -> StopCodon,
    Codon("UUG") -> StopCodon,

    Codon("CUU") -> StopCodon,
    Codon("CUC") -> AA("V"),
    Codon("CUA") -> StopCodon,
    Codon("CUG") -> AA("V"),

    Codon("AUU") -> StopCodon,
    Codon("AUC") -> StopCodon,
    Codon("AUA") -> StopCodon,
    Codon("AUG") -> StopCodon,

    Codon("GUU") -> StopCodon,
    Codon("GUC") -> AA("V"),
    Codon("GUA") -> StopCodon,
    Codon("GUG") -> AA("V"),

    // -- NCN
    Codon("UCU") -> StopCodon,
    Codon("UCC") -> StopCodon,
    Codon("UCA") -> StopCodon,
    Codon("UCG") -> StopCodon,

    Codon("CCU") -> StopCodon,
    Codon("CCC") -> AA("E"),
    Codon("CCA") -> StopCodon,
    Codon("CCG") -> AA("E"),

    Codon("ACU") -> StopCodon,
    Codon("ACC") -> StopCodon,
    Codon("ACA") -> StopCodon,
    Codon("ACG") -> StopCodon,

    Codon("GCU") -> StopCodon,
    Codon("GCC") -> AA("A"),
    Codon("GCA") -> StopCodon,
    Codon("GCG") -> AA("A"),

    // --  NAN
    Codon("UAU") -> StopCodon,
    Codon("UAC") -> StopCodon,
    Codon("UAA") -> StopCodon,
    Codon("UAG") -> StopCodon,

    Codon("CAU") -> StopCodon,
    Codon("CAC") -> AA("E"),
    Codon("CAA") -> StopCodon,
    Codon("CAG") -> AA("E"),

    Codon("AAU") -> StopCodon,
    Codon("AAC") -> StopCodon,
    Codon("AAA") -> StopCodon,
    Codon("AAG") -> StopCodon,

    Codon("GAU") -> StopCodon,
    Codon("GAC") -> AA("D"),
    Codon("GAA") -> StopCodon,
    Codon("GAG") -> AA("D"),

    // --  NGN
    Codon("UGU") -> StopCodon,
    Codon("UGC") -> StopCodon,
    Codon("UGA") -> StopCodon,
    Codon("UGG") -> StopCodon,

    Codon("CGU") -> StopCodon,
    Codon("CGC") -> AA("E"),
    Codon("CGA") -> StopCodon,
    Codon("CGG") -> AA("E"),

    Codon("AGU") -> StopCodon,
    Codon("AGC") -> StopCodon,
    Codon("AGA") -> StopCodon,
    Codon("AGG") -> StopCodon,

    Codon("GGU") -> StopCodon,
    Codon("GGC") -> AA("S"),
    Codon("GGA") -> StopCodon,
    Codon("GGG") -> AA("G")
  )
)

/**
 * As explained in DiGiulio 2008: Extension of the Coevolution theory of the
 * beginning of the Genetic Code
 */
object DiGiulioNNSCode extends AncientProperty("DiGiulio 8/4 classes",
  Map(
    Codon("UUU") -> StopCodon,
    Codon("UUC") -> AA("S"),
    Codon("UUA") -> StopCodon,
    Codon("UUG") -> StopCodon,

    Codon("CUU") -> StopCodon,
    Codon("CUC") -> AA("V"),
    Codon("CUA") -> StopCodon,
    Codon("CUG") -> AA("V"),

    Codon("AUU") -> StopCodon,
    Codon("AUC") -> AA("D"),
    Codon("AUA") -> StopCodon,
    Codon("AUG") -> AA("D"),

    Codon("GUU") -> StopCodon,
    Codon("GUC") -> AA("V"),
    Codon("GUA") -> StopCodon,
    Codon("GUG") -> AA("V"),

    // -- NCN
    Codon("UCU") -> StopCodon,
    Codon("UCC") -> AA("S"),
    Codon("UCA") -> StopCodon,
    Codon("UCG") -> AA("S"),

    Codon("CCU") -> StopCodon,
    Codon("CCC") -> AA("E"),
    Codon("CCA") -> StopCodon,
    Codon("CCG") -> AA("E"),

    Codon("ACU") -> StopCodon,
    Codon("ACC") -> AA("D"),
    Codon("ACA") -> StopCodon,
    Codon("ACG") -> AA("D"),

    Codon("GCU") -> StopCodon,
    Codon("GCC") -> AA("A"),
    Codon("GCA") -> StopCodon,
    Codon("GCG") -> AA("A"),

    // --  NAN
    Codon("UAU") -> StopCodon,
    Codon("UAC") -> AA("S"),
    Codon("UAA") -> StopCodon,
    Codon("UAG") -> StopCodon,

    Codon("CAU") -> StopCodon,
    Codon("CAC") -> AA("E"),
    Codon("CAA") -> StopCodon,
    Codon("CAG") -> AA("E"),

    Codon("AAU") -> StopCodon,
    Codon("AAC") -> AA("E"),
    Codon("AAA") -> StopCodon,
    Codon("AAG") -> AA("E"),

    Codon("GAU") -> StopCodon,
    Codon("GAC") -> AA("D"),
    Codon("GAA") -> StopCodon,
    Codon("GAG") -> AA("D"),

    // --  NGN
    Codon("UGU") -> StopCodon,
    Codon("UGC") -> AA("S"),
    Codon("UGA") -> StopCodon,
    Codon("UGG") -> AA("S"),

    Codon("CGU") -> StopCodon,
    Codon("CGC") -> AA("E"),
    Codon("CGA") -> StopCodon,
    Codon("CGG") -> AA("E"),

    Codon("AGU") -> StopCodon,
    Codon("AGC") -> AA("S"),
    Codon("AGA") -> StopCodon,
    Codon("AGG") -> StopCodon,

    Codon("GGU") -> StopCodon,
    Codon("GGC") -> AA("G"),
    Codon("GGA") -> StopCodon,
    Codon("GGG") -> AA("G")
  )
)


/**
 * As explained in Jolivet, Rothen in 2001: Peculiar symmetry of DNA Sequences...
 */
object StereochemicalCommaLess1 extends AncientProperty("StereochemicalCommaLess",
  Map(
    Codon("UUU") -> StopCodon,
    Codon("UUC") -> AA("F"),
    Codon("UUA") -> StopCodon,
    Codon("UUG") -> StopCodon,

    Codon("CUU") -> StopCodon,
    Codon("CUC") -> AA("L"),
    Codon("CUA") -> StopCodon,
    Codon("CUG") -> AA("L"),

    Codon("AUU") -> StopCodon,
    Codon("AUC") -> AA("I"),
    Codon("AUA") -> StopCodon,
    Codon("AUG") -> StopCodon,

    Codon("GUU") -> AA("V"),
    Codon("GUC") -> AA("V"),
    Codon("GUA") -> AA("V"),
    Codon("GUG") -> StopCodon,

    // -- NCN
    Codon("UCU") -> StopCodon,
    Codon("UCC") -> StopCodon,
    Codon("UCA") -> StopCodon,
    Codon("UCG") -> StopCodon,

    Codon("CCU") -> StopCodon,
    Codon("CCC") -> StopCodon,
    Codon("CCA") -> StopCodon,
    Codon("CCG") -> StopCodon,

    Codon("ACU") -> StopCodon,
    Codon("ACC") -> StopCodon,
    Codon("ACA") -> StopCodon,
    Codon("ACG") -> StopCodon,

    Codon("GCU") -> StopCodon,
    Codon("GCC") -> AA("A"),
    Codon("GCA") -> StopCodon,
    Codon("GCG") -> StopCodon,

    // --  NAN
    Codon("UAU") -> StopCodon,
    Codon("UAC") -> AA("Y"),
    Codon("UAA") -> StopCodon,
    Codon("UAG") -> StopCodon,

    Codon("CAU") -> StopCodon,
    Codon("CAC") -> StopCodon,
    Codon("CAA") -> StopCodon,
    Codon("CAG") -> AA("Q"),

    Codon("AAU") -> StopCodon,
    Codon("AAC") -> AA("N"),
    Codon("AAA") -> StopCodon,
    Codon("AAG") -> StopCodon,

    Codon("GAU") -> AA("D"),
    Codon("GAC") -> AA("D"),
    Codon("GAA") -> AA("E"),
    Codon("GAG") -> AA("E"),

    // --  NGN
    Codon("UGU") -> StopCodon,
    Codon("UGC") -> StopCodon,
    Codon("UGA") -> StopCodon,
    Codon("UGG") -> StopCodon,

    Codon("CGU") -> StopCodon,
    Codon("CGC") -> StopCodon,
    Codon("CGA") -> StopCodon,
    Codon("CGG") -> StopCodon,

    Codon("AGU") -> StopCodon,
    Codon("AGC") -> StopCodon,
    Codon("AGA") -> StopCodon,
    Codon("AGG") -> StopCodon,

    Codon("GGU") -> StopCodon,
    Codon("GGC") -> AA("G"),
    Codon("GGA") -> StopCodon,
    Codon("GGG") -> StopCodon
  )
)


/**
 * As explained in Woese C. R. (1973): Evolution of the Genetic Code.
 */
object StereochemicalWoeseLastStep extends AncientProperty("WoeseLastStep",
  Map(
    Codon("UUU") -> StopCodon,
    Codon("UUC") -> StopCodon,
    Codon("UUA") -> StopCodon,
    Codon("UUG") -> StopCodon,

    Codon("CUU") -> StopCodon,
    Codon("CUC") -> StopCodon,
    Codon("CUA") -> StopCodon,
    Codon("CUG") -> StopCodon,

    Codon("AUU") -> AA("I"),
    Codon("AUC") -> AA("I"),
    Codon("AUA") -> AA("I"),
    Codon("AUG") -> AA("M"),

    Codon("GUU") -> AA("V"),
    Codon("GUC") -> AA("V"),
    Codon("GUA") -> AA("V"),
    Codon("GUG") -> AA("V"),

    // -- NCN
    Codon("UCU") -> AA("S"),
    Codon("UCC") -> AA("S"),
    Codon("UCA") -> AA("S"),
    Codon("UCG") -> AA("S"),

    Codon("CCU") -> AA("P"),
    Codon("CCC") -> AA("P"),
    Codon("CCA") -> AA("P"),
    Codon("CCG") -> AA("P"),

    Codon("ACU") -> AA("T"),
    Codon("ACC") -> AA("T"),
    Codon("ACA") -> AA("T"),
    Codon("ACG") -> AA("T"),

    Codon("GCU") -> AA("A"),
    Codon("GCC") -> AA("A"),
    Codon("GCA") -> AA("A"),
    Codon("GCG") -> AA("A"),

    // --  NAN
    Codon("UAU") -> StopCodon,
    Codon("UAC") -> StopCodon,
    Codon("UAA") -> StopCodon,
    Codon("UAG") -> StopCodon,

    Codon("CAU") -> AA("H"),
    Codon("CAC") -> AA("H"),
    Codon("CAA") -> AA("Q"),
    Codon("CAG") -> AA("Q"),

    Codon("AAU") -> AA("N"),
    Codon("AAC") -> AA("N"),
    Codon("AAA") -> AA("K"),
    Codon("AAG") -> AA("K"),

    Codon("GAU") -> AA("D"),
    Codon("GAC") -> AA("D"),
    Codon("GAA") -> AA("E"),
    Codon("GAG") -> AA("E"),

    // --  NGN
    Codon("UGU") -> AA("C"),
    Codon("UGC") -> AA("C"),
    Codon("UGA") -> StopCodon,
    Codon("UGG") -> AA("W"),

    Codon("CGU") -> AA("R"),
    Codon("CGC") -> AA("R"),
    Codon("CGA") -> AA("R"),
    Codon("CGG") -> AA("R"),

    Codon("AGU") -> AA("S"),
    Codon("AGC") -> AA("S"),
    Codon("AGA") -> AA("R"),
    Codon("AGG") -> AA("R"),

    Codon("GGU") -> StopCodon,
    Codon("GGC") -> StopCodon,
    Codon("GGA") -> StopCodon,
    Codon("GGG") -> StopCodon
  )
)


/**
 * As explained in Woese C. R. (1973): Evolution of the Genetic Code.
 */
object StereochemicalWoeseLastStepEasierVersion extends AncientProperty("WoeseLastStep",
  Map(
    Codon("UUU") -> StopCodon,
    Codon("UUC") -> StopCodon,
    Codon("UUA") -> StopCodon,
    Codon("UUG") -> StopCodon,

    Codon("CUU") -> StopCodon,
    Codon("CUC") -> StopCodon,
    Codon("CUA") -> StopCodon,
    Codon("CUG") -> StopCodon,

    Codon("AUU") -> AA("I"),
    Codon("AUC") -> AA("I"),
    Codon("AUA") -> AA("I"),
    Codon("AUG") -> AA("I"),

    Codon("GUU") -> AA("V"),
    Codon("GUC") -> AA("V"),
    Codon("GUA") -> AA("V"),
    Codon("GUG") -> AA("V"),

    // -- NCN
    Codon("UCU") -> AA("S"),
    Codon("UCC") -> AA("S"),
    Codon("UCA") -> AA("S"),
    Codon("UCG") -> AA("S"),

    Codon("CCU") -> AA("P"),
    Codon("CCC") -> AA("P"),
    Codon("CCA") -> AA("P"),
    Codon("CCG") -> AA("P"),

    Codon("ACU") -> AA("T"),
    Codon("ACC") -> AA("T"),
    Codon("ACA") -> AA("T"),
    Codon("ACG") -> AA("T"),

    Codon("GCU") -> AA("A"),
    Codon("GCC") -> AA("A"),
    Codon("GCA") -> AA("A"),
    Codon("GCG") -> AA("A"),

    // --  NAN
    Codon("UAU") -> StopCodon,
    Codon("UAC") -> StopCodon,
    Codon("UAA") -> StopCodon,
    Codon("UAG") -> StopCodon,

    Codon("CAU") -> AA("H"),
    Codon("CAC") -> AA("H"),
    Codon("CAA") -> AA("Q"),
    Codon("CAG") -> AA("Q"),

    Codon("AAU") -> AA("N"),
    Codon("AAC") -> AA("N"),
    Codon("AAA") -> AA("K"),
    Codon("AAG") -> AA("K"),

    Codon("GAU") -> AA("D"),
    Codon("GAC") -> AA("D"),
    Codon("GAA") -> AA("E"),
    Codon("GAG") -> AA("E"),

    // --  NGN
    Codon("UGU") -> AA("C"),
    Codon("UGC") -> AA("C"),
    Codon("UGA") -> AA("W"),
    Codon("UGG") -> AA("W"),

    Codon("CGU") -> AA("R"),
    Codon("CGC") -> AA("R"),
    Codon("CGA") -> AA("R"),
    Codon("CGG") -> AA("R"),

    Codon("AGU") -> AA("S"),
    Codon("AGC") -> AA("S"),
    Codon("AGA") -> AA("R"),
    Codon("AGG") -> AA("R"),

    Codon("GGU") -> StopCodon,
    Codon("GGC") -> StopCodon,
    Codon("GGA") -> StopCodon,
    Codon("GGG") -> StopCodon
  )
)


/**
 * As explained in Baumann, U. and Oro, J. (1993): Three stages in the evolution of the genetic code.
 * with Asp
 */
object BaumannOroPhase1Asp extends AncientProperty("BaumannOroPhase1Asp",
  Map(
    Codon("UUU") -> AA("L"),
    Codon("UUC") -> AA("L"),
    Codon("UUA") -> AA("L"),
    Codon("UUG") -> AA("L"),

    Codon("CUU") -> AA("L"),
    Codon("CUC") -> AA("L"),
    Codon("CUA") -> AA("L"),
    Codon("CUG") -> AA("L"),

    Codon("AUU") -> AA("I"),
    Codon("AUC") -> AA("I"),
    Codon("AUA") -> AA("I"),
    Codon("AUG") -> AA("I"),

    Codon("GUU") -> AA("V"),
    Codon("GUC") -> AA("V"),
    Codon("GUA") -> AA("V"),
    Codon("GUG") -> AA("V"),

    // -- NCN
    Codon("UCU") -> AA("S"),
    Codon("UCC") -> AA("S"),
    Codon("UCA") -> AA("S"),
    Codon("UCG") -> AA("S"),

    Codon("CCU") -> AA("P"),
    Codon("CCC") -> AA("P"),
    Codon("CCA") -> AA("P"),
    Codon("CCG") -> AA("P"),

    Codon("ACU") -> AA("T"),
    Codon("ACC") -> AA("T"),
    Codon("ACA") -> AA("T"),
    Codon("ACG") -> AA("T"),

    Codon("GCU") -> AA("A"),
    Codon("GCC") -> AA("A"),
    Codon("GCA") -> AA("A"),
    Codon("GCG") -> AA("A"),

    // --  NAN
    Codon("UAU") -> StopCodon,
    Codon("UAC") -> StopCodon,
    Codon("UAA") -> StopCodon,
    Codon("UAG") -> StopCodon,

    Codon("CAU") -> StopCodon,
    Codon("CAC") -> StopCodon,
    Codon("CAA") -> StopCodon,
    Codon("CAG") -> StopCodon,

    Codon("AAU") -> StopCodon,
    Codon("AAC") -> StopCodon,
    Codon("AAA") -> StopCodon,
    Codon("AAG") -> StopCodon,

    Codon("GAU") -> AA("D"),
    Codon("GAC") -> AA("D"),
    Codon("GAA") -> AA("D"),
    Codon("GAG") -> AA("D"),

    // --  NGN
    Codon("UGU") -> StopCodon,
    Codon("UGC") -> StopCodon,
    Codon("UGA") -> StopCodon,
    Codon("UGG") -> StopCodon,

    Codon("CGU") -> AA("R"),
    Codon("CGC") -> AA("R"),
    Codon("CGA") -> AA("R"),
    Codon("CGG") -> AA("R"),

    Codon("AGU") -> AA("S"),
    Codon("AGC") -> AA("S"),
    Codon("AGA") -> AA("R"),
    Codon("AGG") -> AA("R"),

    Codon("GGU") -> AA("G"),
    Codon("GGC") -> AA("G"),
    Codon("GGA") -> AA("G"),
    Codon("GGG") -> AA("G")
  )
)

/**
 * As explained in Baumann, U. and Oro, J. (1993): Three stages in the evolution of the genetic code.
 * with Asp
 */
object BaumannOroPhase1Glu extends AncientProperty("BaumannOroPhase1Glu",
  Map(
    Codon("UUU") -> AA("L"),
    Codon("UUC") -> AA("L"),
    Codon("UUA") -> AA("L"),
    Codon("UUG") -> AA("L"),

    Codon("CUU") -> AA("L"),
    Codon("CUC") -> AA("L"),
    Codon("CUA") -> AA("L"),
    Codon("CUG") -> AA("L"),

    Codon("AUU") -> AA("I"),
    Codon("AUC") -> AA("I"),
    Codon("AUA") -> AA("I"),
    Codon("AUG") -> AA("I"),

    Codon("GUU") -> AA("V"),
    Codon("GUC") -> AA("V"),
    Codon("GUA") -> AA("V"),
    Codon("GUG") -> AA("V"),

    // -- NCN
    Codon("UCU") -> AA("S"),
    Codon("UCC") -> AA("S"),
    Codon("UCA") -> AA("S"),
    Codon("UCG") -> AA("S"),

    Codon("CCU") -> AA("P"),
    Codon("CCC") -> AA("P"),
    Codon("CCA") -> AA("P"),
    Codon("CCG") -> AA("P"),

    Codon("ACU") -> AA("T"),
    Codon("ACC") -> AA("T"),
    Codon("ACA") -> AA("T"),
    Codon("ACG") -> AA("T"),

    Codon("GCU") -> AA("A"),
    Codon("GCC") -> AA("A"),
    Codon("GCA") -> AA("A"),
    Codon("GCG") -> AA("A"),

    // --  NAN
    Codon("UAU") -> StopCodon,
    Codon("UAC") -> StopCodon,
    Codon("UAA") -> StopCodon,
    Codon("UAG") -> StopCodon,

    Codon("CAU") -> StopCodon,
    Codon("CAC") -> StopCodon,
    Codon("CAA") -> StopCodon,
    Codon("CAG") -> StopCodon,

    Codon("AAU") -> StopCodon,
    Codon("AAC") -> StopCodon,
    Codon("AAA") -> StopCodon,
    Codon("AAG") -> StopCodon,

    Codon("GAU") -> AA("E"),
    Codon("GAC") -> AA("E"),
    Codon("GAA") -> AA("E"),
    Codon("GAG") -> AA("E"),

    // --  NGN
    Codon("UGU") -> StopCodon,
    Codon("UGC") -> StopCodon,
    Codon("UGA") -> StopCodon,
    Codon("UGG") -> StopCodon,

    Codon("CGU") -> AA("R"),
    Codon("CGC") -> AA("R"),
    Codon("CGA") -> AA("R"),
    Codon("CGG") -> AA("R"),

    Codon("AGU") -> AA("S"),
    Codon("AGC") -> AA("S"),
    Codon("AGA") -> AA("R"),
    Codon("AGG") -> AA("R"),

    Codon("GGU") -> AA("G"),
    Codon("GGC") -> AA("G"),
    Codon("GGA") -> AA("G"),
    Codon("GGG") -> AA("G")
  )
)

/**
 * As explained in Baumann, U. and Oro, J. (1993): Three stages in the evolution of the genetic code.
 * with His and Asn and Asp
 */
object BaumannOroPhase2FirstPossibility extends AncientProperty("BaumannOroPhase2",
  Map(
    Codon("UUU") -> AA("F"),
    Codon("UUC") -> AA("F"),
    Codon("UUA") -> AA("L"),
    Codon("UUG") -> AA("L"),

    Codon("CUU") -> AA("L"),
    Codon("CUC") -> AA("L"),
    Codon("CUA") -> AA("L"),
    Codon("CUG") -> AA("L"),

    Codon("AUU") -> AA("I"),
    Codon("AUC") -> AA("I"),
    Codon("AUA") -> AA("I"),
    Codon("AUG") -> AA("I"),

    Codon("GUU") -> AA("V"),
    Codon("GUC") -> AA("V"),
    Codon("GUA") -> AA("V"),
    Codon("GUG") -> AA("V"),

    // -- NCN
    Codon("UCU") -> AA("S"),
    Codon("UCC") -> AA("S"),
    Codon("UCA") -> AA("S"),
    Codon("UCG") -> AA("S"),

    Codon("CCU") -> AA("P"),
    Codon("CCC") -> AA("P"),
    Codon("CCA") -> AA("P"),
    Codon("CCG") -> AA("P"),

    Codon("ACU") -> AA("T"),
    Codon("ACC") -> AA("T"),
    Codon("ACA") -> AA("T"),
    Codon("ACG") -> AA("T"),

    Codon("GCU") -> AA("A"),
    Codon("GCC") -> AA("A"),
    Codon("GCA") -> AA("A"),
    Codon("GCG") -> AA("A"),

    // --  NAN
    Codon("UAU") -> AA("Y"),
    Codon("UAC") -> AA("Y"),
    Codon("UAA") -> StopCodon,
    Codon("UAG") -> StopCodon,

    Codon("CAU") -> AA("H"),
    Codon("CAC") -> AA("H"),
    Codon("CAA") -> AA("H"),
    Codon("CAG") -> AA("H"),

    Codon("AAU") -> AA("N"),
    Codon("AAC") -> AA("N"),
    Codon("AAA") -> AA("N"),
    Codon("AAG") -> AA("N"),

    Codon("GAU") -> AA("E"),
    Codon("GAC") -> AA("E"),
    Codon("GAA") -> AA("E"),
    Codon("GAG") -> AA("E"),

    // --  NGN
    Codon("UGU") -> AA("C"),
    Codon("UGC") -> AA("C"),
    Codon("UGA") -> StopCodon,
    Codon("UGG") -> AA("C"),

    Codon("CGU") -> AA("R"),
    Codon("CGC") -> AA("R"),
    Codon("CGA") -> AA("R"),
    Codon("CGG") -> AA("R"),

    Codon("AGU") -> AA("S"),
    Codon("AGC") -> AA("S"),
    Codon("AGA") -> AA("R"),
    Codon("AGG") -> AA("R"),

    Codon("GGU") -> AA("G"),
    Codon("GGC") -> AA("G"),
    Codon("GGA") -> AA("G"),
    Codon("GGG") -> AA("G")
  )
)

/**
 * As explained in Jukes: Possibilities for the evolution of the genetic code from a precending form.
 * Testing first table with 10 AA and 10 tRNA
 */
object JukesFirstStepTenAA extends AncientProperty("Jukes1",
  Map(
    Codon("UUU") -> AA("L"),
    Codon("UUC") -> AA("L"),
    Codon("UUA") -> AA("L"),
    Codon("UUG") -> AA("L"),

    Codon("CUU") -> AA("L"),
    Codon("CUC") -> AA("L"),
    Codon("CUA") -> AA("L"),
    Codon("CUG") -> AA("L"),

    Codon("AUU") -> AA("V"),
    Codon("AUC") -> AA("V"),
    Codon("AUA") -> AA("V"),
    Codon("AUG") -> AA("V"),

    Codon("GUU") -> AA("V"),
    Codon("GUC") -> AA("V"),
    Codon("GUA") -> AA("V"),
    Codon("GUG") -> AA("V"),

    // -- NCN
    Codon("UCU") -> AA("P"),
    Codon("UCC") -> AA("P"),
    Codon("UCA") -> AA("P"),
    Codon("UCG") -> AA("P"),

    Codon("CCU") -> AA("P"),
    Codon("CCC") -> AA("P"),
    Codon("CCA") -> AA("P"),
    Codon("CCG") -> AA("P"),

    Codon("ACU") -> AA("A"),
    Codon("ACC") -> AA("A"),
    Codon("ACA") -> AA("A"),
    Codon("ACG") -> AA("A"),

    Codon("GCU") -> AA("A"),
    Codon("GCC") -> AA("A"),
    Codon("GCA") -> AA("A"),
    Codon("GCG") -> AA("A"),

    // --  NAN
    Codon("UAU") -> AA("H"),
    Codon("UAC") -> AA("H"),
    Codon("UAA") -> AA("Q"),
    Codon("UAG") -> AA("Q"),

    Codon("CAU") -> AA("H"),
    Codon("CAC") -> AA("H"),
    Codon("CAA") -> AA("Q"),
    Codon("CAG") -> AA("Q"),

    Codon("AAU") -> AA("D"),
    Codon("AAC") -> AA("D"),
    Codon("AAA") -> AA("E"),
    Codon("AAG") -> AA("E"),

    Codon("GAU") -> AA("D"),
    Codon("GAC") -> AA("D"),
    Codon("GAA") -> AA("E"),
    Codon("GAG") -> AA("E"),

    // --  NGN
    Codon("UGU") -> AA("R"),
    Codon("UGC") -> AA("R"),
    Codon("UGA") -> AA("R"),
    Codon("UGG") -> AA("R"),

    Codon("CGU") -> AA("R"),
    Codon("CGC") -> AA("R"),
    Codon("CGA") -> AA("R"),
    Codon("CGG") -> AA("R"),

    Codon("AGU") -> AA("G"),
    Codon("AGC") -> AA("G"),
    Codon("AGA") -> AA("G"),
    Codon("AGG") -> AA("G"),

    Codon("GGU") -> AA("G"),
    Codon("GGC") -> AA("G"),
    Codon("GGA") -> AA("G"),
    Codon("GGG") -> AA("G")
  )
)

/**
 * As explained in Jukes: Possibilities for the evolution of the genetic code from a precending form.
 * Testing second table with 18 AA
 */
object JukesSecondStepNewAA extends AncientProperty("Jukes2",
  Map(
    Codon("UUU") -> AA("F"),
    Codon("UUC") -> AA("F"),
    Codon("UUA") -> AA("L"),
    Codon("UUG") -> AA("L"),

    Codon("CUU") -> AA("L"),
    Codon("CUC") -> AA("L"),
    Codon("CUA") -> AA("L"),
    Codon("CUG") -> AA("L"),

    Codon("AUU") -> AA("I"),
    Codon("AUC") -> AA("I"),
    Codon("AUA") -> AA("I"),
    Codon("AUG") -> AA("I"),

    Codon("GUU") -> AA("V"),
    Codon("GUC") -> AA("V"),
    Codon("GUA") -> AA("V"),
    Codon("GUG") -> AA("V"),

    // -- NCN
    Codon("UCU") -> AA("S"),
    Codon("UCC") -> AA("S"),
    Codon("UCA") -> AA("S"),
    Codon("UCG") -> AA("S"),

    Codon("CCU") -> AA("P"),
    Codon("CCC") -> AA("P"),
    Codon("CCA") -> AA("P"),
    Codon("CCG") -> AA("P"),

    Codon("ACU") -> AA("T"),
    Codon("ACC") -> AA("T"),
    Codon("ACA") -> AA("T"),
    Codon("ACG") -> AA("T"),

    Codon("GCU") -> AA("A"),
    Codon("GCC") -> AA("A"),
    Codon("GCA") -> AA("A"),
    Codon("GCG") -> AA("A"),

    // --  NAN
    Codon("UAU") -> AA("Y"),
    Codon("UAC") -> AA("Y"),
    Codon("UAA") -> StopCodon,
    Codon("UAG") -> StopCodon,

    Codon("CAU") -> AA("H"),
    Codon("CAC") -> AA("H"),
    Codon("CAA") -> AA("Q"),
    Codon("CAG") -> AA("Q"),

    Codon("AAU") -> AA("N"),
    Codon("AAC") -> AA("N"),
    Codon("AAA") -> AA("K"),
    Codon("AAG") -> AA("K"),

    Codon("GAU") -> AA("D"),
    Codon("GAC") -> AA("D"),
    Codon("GAA") -> AA("E"),
    Codon("GAG") -> AA("E"),

    // --  NGN
    Codon("UGU") -> AA("C"),
    Codon("UGC") -> AA("C"),
    Codon("UGA") -> AA("C"),
    Codon("UGG") -> AA("C"),

    Codon("CGU") -> AA("R"),
    Codon("CGC") -> AA("R"),
    Codon("CGA") -> AA("R"),
    Codon("CGG") -> AA("R"),

    Codon("AGU") -> AA("S"),
    Codon("AGC") -> AA("S"),
    Codon("AGA") -> AA("S"),
    Codon("AGG") -> AA("S"),

    Codon("GGU") -> AA("G"),
    Codon("GGC") -> AA("G"),
    Codon("GGA") -> AA("G"),
    Codon("GGG") -> AA("G")
  )
)

/**
 * As explained in Wong: the evolution of an universal genetic code.
 *
 */
object WongFirstTable extends AncientProperty("Wong1",
  Map(
    Codon("UUU") -> AA("F"),
    Codon("UUC") -> AA("F"),
    Codon("UUA") -> AA("V"),
    Codon("UUG") -> AA("V"),

    Codon("CUU") -> AA("V"),
    Codon("CUC") -> AA("V"),
    Codon("CUA") -> AA("V"),
    Codon("CUG") -> AA("V"),

    Codon("AUU") -> AA("D"),
    Codon("AUC") -> AA("D"),
    Codon("AUA") -> AA("D"),
    Codon("AUG") -> AA("D"),

    Codon("GUU") -> AA("V"),
    Codon("GUC") -> AA("V"),
    Codon("GUA") -> AA("V"),
    Codon("GUG") -> AA("V"),

    // -- NCN
    Codon("UCU") -> AA("S"),
    Codon("UCC") -> AA("S"),
    Codon("UCA") -> AA("S"),
    Codon("UCG") -> AA("S"),

    Codon("CCU") -> AA("E"),
    Codon("CCC") -> AA("E"),
    Codon("CCA") -> AA("E"),
    Codon("CCG") -> AA("E"),

    Codon("ACU") -> AA("D"),
    Codon("ACC") -> AA("D"),
    Codon("ACA") -> AA("D"),
    Codon("ACG") -> AA("D"),

    Codon("GCU") -> AA("A"),
    Codon("GCC") -> AA("A"),
    Codon("GCA") -> AA("A"),
    Codon("GCG") -> AA("A"),

    // --  NAN
    Codon("UAU") -> AA("F"),
    Codon("UAC") -> AA("F"),
    Codon("UAA") -> StopCodon,
    Codon("UAG") -> StopCodon,

    Codon("CAU") -> AA("E"),
    Codon("CAC") -> AA("E"),
    Codon("CAA") -> AA("E"),
    Codon("CAG") -> AA("E"),

    Codon("AAU") -> AA("D"),
    Codon("AAC") -> AA("D"),
    Codon("AAA") -> AA("D"),
    Codon("AAG") -> AA("D"),

    Codon("GAU") -> AA("D"),
    Codon("GAC") -> AA("D"),
    Codon("GAA") -> AA("E"),
    Codon("GAG") -> AA("E"),

    // --  NGN
    Codon("UGU") -> AA("S"),
    Codon("UGC") -> AA("S"),
    Codon("UGA") -> StopCodon,
    Codon("UGG") -> AA("S"),

    Codon("CGU") -> AA("E"),
    Codon("CGC") -> AA("E"),
    Codon("CGA") -> AA("E"),
    Codon("CGG") -> AA("E"),

    Codon("AGU") -> AA("S"),
    Codon("AGC") -> AA("S"),
    Codon("AGA") -> AA("E"),
    Codon("AGG") -> AA("E"),

    Codon("GGU") -> AA("G"),
    Codon("GGC") -> AA("G"),
    Codon("GGA") -> AA("G"),
    Codon("GGG") -> AA("G")
  )
)

/**
 * As explained in Wong: the evolution of an universal genetic code.
 *
 */
object WongSecondTable extends AncientProperty("Wong2",
  Map(
    Codon("UUU") -> AA("F"),
    Codon("UUC") -> AA("F"),
    Codon("UUA") -> AA("V"),
    Codon("UUG") -> AA("V"),

    Codon("CUU") -> AA("V"),
    Codon("CUC") -> AA("V"),
    Codon("CUA") -> AA("V"),
    Codon("CUG") -> AA("V"),

    Codon("AUU") -> AA("T"),
    Codon("AUC") -> AA("T"),
    Codon("AUA") -> AA("T"),
    Codon("AUG") -> AA("T"),

    Codon("GUU") -> AA("V"),
    Codon("GUC") -> AA("V"),
    Codon("GUA") -> AA("V"),
    Codon("GUG") -> AA("V"),

    // -- NCN
    Codon("UCU") -> AA("S"),
    Codon("UCC") -> AA("S"),
    Codon("UCA") -> AA("S"),
    Codon("UCG") -> AA("S"),

    Codon("CCU") -> AA("P"),
    Codon("CCC") -> AA("P"),
    Codon("CCA") -> AA("P"),
    Codon("CCG") -> AA("P"),

    Codon("ACU") -> AA("T"),
    Codon("ACC") -> AA("T"),
    Codon("ACA") -> AA("T"),
    Codon("ACG") -> AA("T"),

    Codon("GCU") -> AA("A"),
    Codon("GCC") -> AA("A"),
    Codon("GCA") -> AA("A"),
    Codon("GCG") -> AA("A"),

    // --  NAN
    Codon("UAU") -> AA("Y"),
    Codon("UAC") -> AA("Y"),
    Codon("UAA") -> StopCodon,
    Codon("UAG") -> StopCodon,

    Codon("CAU") -> AA("Q"),
    Codon("CAC") -> AA("Q"),
    Codon("CAA") -> AA("Q"),
    Codon("CAG") -> AA("Q"),

    Codon("AAU") -> AA("N"),
    Codon("AAC") -> AA("N"),
    Codon("AAA") -> AA("K"),
    Codon("AAG") -> AA("K"),

    Codon("GAU") -> AA("D"),
    Codon("GAC") -> AA("D"),
    Codon("GAA") -> AA("E"),
    Codon("GAG") -> AA("E"),

    // --  NGN
    Codon("UGU") -> AA("C"),
    Codon("UGC") -> AA("C"),
    Codon("UGA") -> StopCodon,
    Codon("UGG") -> AA("W"),

    Codon("CGU") -> AA("R"),
    Codon("CGC") -> AA("R"),
    Codon("CGA") -> AA("R"),
    Codon("CGG") -> AA("R"),

    Codon("AGU") -> AA("S"),
    Codon("AGC") -> AA("S"),
    Codon("AGA") -> AA("R"),
    Codon("AGG") -> AA("R"),

    Codon("GGU") -> AA("G"),
    Codon("GGC") -> AA("G"),
    Codon("GGA") -> AA("G"),
    Codon("GGG") -> AA("G")
  )
)