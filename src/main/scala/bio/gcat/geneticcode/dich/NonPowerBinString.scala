package bio.gcat.geneticcode.dich

import collection.mutable

/**
 * @author Markus Gumbel (m.gumbel@hs-mannheim.de)
 *         (c) 2012 Markus Gumbel
 */
class NonPowerBinString(val code: Int, val prefixSize: Int = 1) {

  /**
   * A list (12 elements) of tuples (left and right) that contain
   * a list of binary strings and its corresponding amino acid (stored as a tuple).
   * Uses IUPAC.STANDARD.
   * As in Gonzales et al, Phys. Rev. E 78 (2008) but with stop codons.
   */
  val origAssignmentStandard:
  List[Tuple2[List[Tuple2[String, Char]], List[Tuple2[String, Char]]]] = List(
    (List(("000000", 'W')), List(("111111", 'M'))),
    // 2
    (List(("000010", 'S'), ("000001", 'S')), List(("111110", 'F'), ("111101", 'F'))),
    (List(("000100", '!'), ("000011", '!')), List(("111100", 'K'), ("111011", 'K'))),
    (List(("000110", 'Y'), ("000101", 'Y')), List(("111010", 'N'), ("111001", 'N'))),
    (List(("001000", 'L'), ("000111", 'L')), List(("111000", 'R'), ("110111", 'R'))),
    (List(("001010", 'H'), ("001001", 'H')), List(("110110", 'D'), ("110101", 'D'))),
    (List(("001100", 'Q'), ("001011", 'Q')), List(("110100", 'E'), ("110011", 'E'))),
    // 3
    (List(("001110", 'C'), ("001101", 'C'), ("010000", '!')),
      List(("101111", 'I'), ("110010", 'I'), ("110001", 'I'))),
    // 4
    (List(("100000", 'S'), ("010010", 'S'), ("010001", 'S'), ("001111", 'S')),
      List(("110000", 'T'), ("101110", 'T'), ("101101", 'T'), ("011111", 'T'))),
    (List(("100010", 'P'), ("100001", 'P'), ("010100", 'P'), ("010011", 'P')),
      List(("101100", 'A'), ("101011", 'A'), ("011110", 'A'), ("011101", 'A'))),
    (List(("100100", 'V'), ("010110", 'V'), ("010101", 'V'), ("100011", 'V')),
      List(("011100", 'G'), ("101001", 'G'), ("101010", 'G'), ("011011", 'G'))),
    (List(("100110", 'L'), ("100101", 'L'), ("011000", 'L'), ("010111", 'L')),
      List(("101000", 'R'), ("100111", 'R'), ("011010", 'R'), ("011001", 'R')))
  )

  val origAssignmentEuplotid:
  List[Tuple2[List[Tuple2[String, Char]], List[Tuple2[String, Char]]]] = List(
    (List(("000000", 'W')), List(("111111", 'M'))),
    // 2
    (List(("000010", 'S'), ("000001", 'S')), List(("111110", 'F'), ("111101", 'F'))),
    (List(("000100", '!'), ("000011", '!')), List(("111100", 'K'), ("111011", 'K'))),
    (List(("000110", 'Y'), ("000101", 'Y')), List(("111010", 'N'), ("111001", 'N'))),
    (List(("001000", 'L'), ("000111", 'L')), List(("111000", 'R'), ("110111", 'R'))),
    (List(("001010", 'H'), ("001001", 'H')), List(("110110", 'D'), ("110101", 'D'))),
    (List(("001100", 'Q'), ("001011", 'Q')), List(("110100", 'E'), ("110011", 'E'))),
    // 3
    (List(("001110", 'C'), ("001101", 'C'), ("010000", 'C')),
      List(("101111", 'I'), ("110010", 'I'), ("110001", 'I'))),
    // 4
    (List(("100000", 'S'), ("010010", 'S'), ("010001", 'S'), ("001111", 'S')),
      List(("110000", 'T'), ("101110", 'T'), ("101101", 'T'), ("011111", 'T'))),
    (List(("100010", 'P'), ("100001", 'P'), ("010100", 'P'), ("010011", 'P')),
      List(("101100", 'A'), ("101011", 'A'), ("011110", 'A'), ("011101", 'A'))),
    (List(("100100", 'V'), ("010110", 'V'), ("010101", 'V'), ("100011", 'V')),
      List(("011100", 'G'), ("101001", 'G'), ("101010", 'G'), ("011011", 'G'))),
    (List(("100110", 'L'), ("100101", 'L'), ("011000", 'L'), ("010111", 'L')),
      List(("101000", 'R'), ("100111", 'R'), ("011010", 'R'), ("011001", 'R')))
  )

  val origAssignment = code match {
    case IUPAC.STANDARD => origAssignmentStandard
    case IUPAC.EUPLOTID_NUCLEAR => origAssignmentEuplotid
  }

  /**
   * One possibility to map NRP strings to codons.
   */
  val binString2Codon = {
    import bio.gcat.geneticcode.dich.CodonImplicitDefs._

    val m = new mutable.HashMap[String, Codon]()

    // As in Gonzales et al, Phys. Rev. E 78 (2008)
    // contradicts to table 18 in Mathematical Model...

    val s = List(
      // W,  M
      ("000000", "UGG"), ("111111", "AUG"),
      // #2
      // S, S, F, F
      ("000010", "AGU"), ("000001", "AGC"), ("111110", "UUU"), ("111101", "UUC"),
      // Stop Ochre, Stop Amber, K, K
      ("000100", "UAA"), ("000011", "UAG"), ("111100", "AAA"), ("111011", "AAG"),
      // Y, Y, N, N
      ("000110", "UAU"), ("000101", "UAC"), ("111010", "AAU"), ("111001", "AAC"),
      // L2, L2, R2, R2
      ("001000", "UAA"), ("000111", "UUG"), ("111000", "AGA"), ("110111", "AGG"),
      // H, H, D, D
      ("001010", "CAU"), ("001001", "CAC"), ("110110", "GAU"), ("110101", "GAC"),
      // Q, Q, E, E
      ("001100", "CAA"), ("001011", "CAG"), ("110100", "GAA"), ("110011", "GAG"),
      // #3
      // C, C, Stop Opal
      ("001110", "UGU"), ("001101", "UGC"), ("010000", "UGA"),
      // I, I, I
      ("101111", "AUU"), ("110010", "AUC"), ("110001", "AUA"),
      // #4
      // 4xS4
      ("100000", "UCU"), ("010010", "UCC"), ("010001", "UCA"), ("001111", "UCG"),
      // 4xT
      ("110000", "ACU"), ("101110", "ACC"), ("101101", "ACA"), ("011111", "ACG"),
      // 4xP
      ("100010", "CCU"), ("100001", "CCC"), ("010100", "CCA"), ("010011", "CCG"),
      // 4xA
      ("101100", "GCU"), ("101011", "GCC"), ("011110", "GCA"), ("011101", "GCG"),
      // 4xV
      ("100100", "GUU"), ("010110", "GUC"), ("010101", "GUA"), ("100011", "GUG"),
      // 4xG
      ("011100", "GGU"), ("101001", "GGC"), ("101010", "GGA"), ("011011", "GGG"),
      // 4xL4
      ("100110", "CUU"), ("100101", "CUC"), ("011000", "CUA"), ("010111", "CUG"),
      // 4xR4
      ("101000", "CGU"), ("100111", "CGC"), ("011010", "CGA"), ("011001", "CGG")
    )
    s.foreach(e => m(e._1) = new Codon(e._2))
    if (m.values.size != 64) {
      throw new RuntimeException("Problem: # of codonds <> 64")
    }
    m.toMap
  }

  /**
   * @param k A decimal number.
   * @return A set of binary strings that have the decimal value k.
   */
  def binStringByNumber(k: Int) = {
    val v = for (s <- binString2AA(0).keys) yield {
      val n = s.map(c => if (c == '0') 0 else 1).toArray
      (s, 8 * n(0) + 7 * n(1) + 4 * n(2) + 2 * n(3) + 1 * n(4) + 1 * n(5))
    }
    v.filter(e => e._2 == k).map(e => e._1).toSet
  }

  def table(k: Int) = {
    def row(i: Int) = {
      val binStrings = binStringByNumber(i).toList
      i + ": " + binStrings.sortBy(s => s).reverse.mkString(" ") + " -> " +
        binStrings.map(s => binString2AA(k)(s)).mkString(",")
    }
    val rows1 = for (i <- 0 to 11) yield row(i)
    val rows2 = for (i <- 23 to 12 by -1) yield row(i)

    rows1.mkString("\n") + "\n" + rows2.mkString("\n")
  }

  /**
   * Get a mapping for npr binary strings dependent on a swap configuration.
   * A swap configuration is a list of 12 entries containing 0 or 1
   * (represented as in integer number from 0 to 4095)
   * to indicate whether the left or the right side of an
   * amino acid assignment is used.
   */
  lazy val binString2AA: Array[Map[String, Char]] = {

    val a = Array.ofDim[Map[String, Char]](4096)
    for (i <- 0 until 4096) {
      val m = new mutable.HashMap[String, Char]()
      val pattern = number2List(i).reverse // because ...01 has to be 10...
      val u = pattern.zip(origAssignment)
      u.foreach {
        c =>
          val (swap, element) = (c._1, c._2)
          m ++= getLine(element, swap)
      }
      a(i) = m.toMap
      if (a(i).keySet.size != 64) {
        throw new RuntimeException("Problem: non-power binary strings size <> 64")
      }
    }
    a
  }

  /**
   * Convert a number <code>n</code> to a binary list.
   * @param n
   * @return
   */
  def number2List(n: Int) = {
    if (n < 0 || n >= 4096) throw new IllegalArgumentException()
    Integer.toBinaryString(n + 4096).substring(1, 13).
      map(c => if (c == '0') 0 else 1).toList
  }

  /**
   * Compute a row in the npr cascading-table
   * @param line a line in npr cascading-table
   * @param swap 0 = leave it as it is, 1 = swap amino acids assignments.
   * @return A list of tuples which map a npr binary string
   *         to an amino acid.
   */
  def getLine(line: (List[(String, Char)],
    List[(String, Char)]), swap: Int) = swap match {
    case 0 => line._1 ::: line._2 // Do nothing, just build list.
    case 1 => {
      // swap sides:
      val l1 = line._1
      val l2 = line._2.reverse // ...as notation is reversed.
      val l = l1.zip(l2) // Combine element.
      l.map {
        e =>
          val (le1, le2) = (e._1, e._2)
          // Cross it:
          (le1._1, le2._2) ::(le2._1, le1._2) :: Nil
      }.flatten
    }
  }

  /**
   * An array of size 4096 which holds a map
   * that maps a npr binary string to a sorted list of amino acids.
   */
  lazy val prefixL = {
    val a = Array.ofDim[Map[String, List[Char]]](4096)
    for (i <- 0 until 4096) {
      val m = new mutable.HashMap[String, mutable.ArrayBuffer[Char]]()
      for (j <- 1 to prefixSize) {
        binString2AA(i).foreach {
          e =>
            val (bin, aa) = e
            //val pattern = bin.substring(6 - j, 6) // 6 exclusive
            //val pattern = bin.substring(0, j) // 6 exclusive
            val pattern = bin.substring(4, 5) // 6 exclusive
            val s = if (m.contains(pattern)) m(pattern) else new mutable.ArrayBuffer[Char]()
            s += aa
            m(pattern) = s
        }
      }
      val m2 = new mutable.HashMap[String, List[Char]]()
      m.keys.foreach {
        bin =>
          m2(bin) = m(bin).sortBy(x => x.toString).toList
      }
      a(i) = m2.toMap
    }
    a
  }

  def mkPrefixLString(which: Int) = {
    prefixL(which).toList.sortBy(e => (e._1.size, e._1.toString)).map {
      e =>
        val (bin, aas) = e
        bin + " -> " + aas.mkString(",")
    }.mkString("\n")
  }
}
