package bio.gcat.geneticcode.dich.demo

import bio.gcat.geneticcode.dich.ct.ClassTable
import bio.gcat.geneticcode.dich.{BinaryDichotomicAlgorithm => BDA}
import org.junit.{Ignore, Test}

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
