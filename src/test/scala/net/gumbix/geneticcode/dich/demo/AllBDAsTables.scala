package net.gumbix.geneticcode.dich.demo

import org.junit.{Ignore, Test}
import net.gumbix.geneticcode.dich._
import net.gumbix.geneticcode.dich.scan.{ErrorScan, AminoMappingScan, ClassPower2Scan}
import net.gumbix.geneticcode.dich.{BinaryDichotomicAlgorithm => BDA}
import net.gumbix.geneticcode.dich.{Adenine => A, Uracil => U, Cytosine => C, Guanine => G}
import net.gumbix.geneticcode.dich.ct.ClassTable

/**
 * @author Markus Gumbel (m.gumbel@hs-mannheim.de)
 *         (c) 2013 Markus Gumbel
 */
class AllBDAsTables {

  @Test
  @Ignore
  def printAllBDAsAsTable() {
    BDA.bdas216.classifiers.foreach {
      bda =>
        val t = new ClassTable(List(bda))
        //println(t.laTeXBdaMkString())
        //println(t.laTeXMkString()) + "\n")
        println("--------------------------------------")
        println(t.bdaMkString + "\n")
        println(t.mkString() + "\n")
    }
  }

  @Test
  @Ignore
  def printAllBDAsAsLaTeX() {
    var i = 1
    BDA.bdas216.classifiers.foreach {
      bda =>
        println("\\section{" + i + "}")
        val t = new ClassTable(List(bda))
        println("\\begin{tabular}{|l||c|c|c|c|}\\hline")
        println("BDA & $(i_1, i_2) $ & $Q_1$ & $Q_2$ \\\\ \\hline  \\hline")
        println(t.laTeXBdaMkString())
        println("\\end{tabular}\n")

        println("\n\\vspace{1cm}\n")

        println("\\begin{tabular}{|c||c c|c c|c c|c c||c|}\\hline")
        println(t.laTeXMkString())
        println("\\end{tabular}")
        i += 1
    }
  }
}
