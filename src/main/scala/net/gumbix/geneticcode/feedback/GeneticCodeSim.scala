package net.gumbix.geneticcode.feedback

import util.Random
import net.gumbix.util.Loggable
import net.gumbix.geneticcode.model.aa11aaRS.{HydroClassAA11, HashValue11}
import net.gumbix.geneticcode.model.{HashValue, HydroClassAA}


/**
 * (c) 2012 by Markus Gumbel (m.gumbel@hs-mannheim.de)
 * @author Markus Gumbel
 */

object GeneticCodeSim extends Loggable {

  var e: EvolvedGeneticCode = null

  def foo() = 1

  def main(args: Array[String]) {
    sim(3, "Hydro11", "Random", 0.5, 1)
    val x = codeDifferences
  }

  def sim(n: Int, model: String, init: String, thres: Double, seed: Long) {

    val randomGen = new Random(seed)

    val tableFactory = new TableCreation(randomGen)
    val table = init match {
      case "Random" => tableFactory.randomTable
      case "Module" => tableFactory.moduloTable
    }
    //e = new HydroClassAA(thres, table.randomTable, randomGen)

    e = model match {
      case "Hash" => new HashValue(thres, table, randomGen)
      case "Hydro" => new HydroClassAA(thres, table, randomGen)
      case "Hydro11" => new HydroClassAA11(thres, table, randomGen)
      case "Hash11" => new HashValue11(thres, table, randomGen)
    }

    log.info(e.tRNAs.size + " tRNA genes:")
    log.info(e.tRNAs.mkString("\n"))
    log.info("")
    log.info(e.aaRSgenes.size + " aaRS genes:")
    log.info(e.aaRSgenes.mkString("\n"))
    log.info("")
    log.info(e.aaRSproteins.size + " aaRS proteins:")
    log.info(e.aaRSproteins.mkString("\n"))
    log.info("")
    log.info("Init code table:")
    log.info(e.codeTables.last.mkString())

    // log.info("\naaRS: " + e.aaRSproteins.mkString(" "))
    // log.info("degeneracy: " + e.codeTables.last.degeneracy.mkString(", "))

    for (i <- 1 until n) {
      log.info(i + ":")
      e.convertTable()
      // log.info(e.codeTable.reverseList.mkString(" "))
      // log.info(e.codeTable.degeneracyByAminoAcids.mkString)
      // log.info("\naaRS: " + e.aaRSproteins.mkString(" "))
      log.info("degeneracy: " + e.codeTables.last.degeneracy.mkString(", "))
      log.info(e.codeTables.last.mkCounterString)
      log.info("")
      log.info(e.codeTables.last.mkString)

    }

    // log.info(degeneracy)
    // log.info(e.codeTables.last.mkString())
  }

  def degeneracy = {
    val degMatrix = Array.ofDim[Double](e.codeTables.size, 64)
    var i = 0
    e.codeTables.foreach {
      codeTable =>
        val row = codeTable.degeneracy
        var j = 0
        row.foreach {
          e =>
            degMatrix(i)(j) = e.asInstanceOf[Double]
            j += 1
        }
        i += 1
    }
    degMatrix
  }

  def numberAminoAcids = {
    val matrix = Array.ofDim[Double](e.codeTables.size)
    var i = 0
    e.codeTables.foreach {
      codeTable =>
        matrix(i) = codeTable.numberAminoAcids
        i += 1
    }
    matrix
  }

  def codeDifferences = {
    val matrix = Array.ofDim[Double](e.codeTables.size)
    matrix(0) = 0.0
    for (i <- 0 until e.codeTables.size - 1) {
      matrix(i + 1) = e.codeTables(i).equalCodes(e.codeTables(i + 1))
    }
    matrix
  }
}
