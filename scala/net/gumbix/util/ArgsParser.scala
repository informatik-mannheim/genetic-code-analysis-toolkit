package net.gumbix.util

import net.gumbix.geneticcode.dich._

/**
 * Very simple but useful args parser.
 * @author Markus Gumbel (m.gumbel@hs-mannheim.de)
 *         (c) 2013 Markus Gumbel
 */
trait ArgsParser {

  def toBDAList(args: Array[String], pos: Int) = args(pos) match {
    case "()" => List()
    case _ => {
      args(pos).map {
        c => c match {
          case 'R' => RumerBDA
          case 'P' => ParityBDA
          case 'A' => AntiCodonBDA
        }
      }.toList
    }
  }

  def toInt(args: Array[String], pos: Int) = args(pos).toInt

  def toDouble(args: Array[String], pos: Int) = args(pos).toDouble

  def toProp(args: Array[String], pos: Int) = args(pos) match {
    case "64" => IdCodonProperty
    case "AST" => new IdAminoAcidProperty(IUPAC.STANDARD)
    case "AVM" => new IdAminoAcidProperty(IUPAC.VERTEBRATE_MITOCHONDRIAL)
    case _ => new IdAminoAcidProperty(IUPAC.STANDARD)
  }
}
