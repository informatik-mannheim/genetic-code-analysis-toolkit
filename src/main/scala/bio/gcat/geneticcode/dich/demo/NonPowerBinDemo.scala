package bio.gcat.geneticcode.dich.demo

import bio.gcat.geneticcode.dich.{IUPAC, NonPowerBinString}
import org.junit.{Ignore, Test}

/**
 * @author Markus Gumbel (m.gumbel@hs-mannheim.de)
 *         (c) 2012 Markus Gumbel
 */
class NonPowerBinDemo {

  @Test
  @Ignore
  def main() {
    val npr = new NonPowerBinString(IUPAC.EUPLOTID_NUCLEAR, 1)
    npr.binString2AA(0).keys.toList.sortBy(_.toString).foreach {
      bin =>
        println(bin + " -> " + npr.binString2AA(0)(bin) +
          " | " + npr.binString2AA(4095)(bin))
    }

    println(npr.mkPrefixLString(0))
    println()
    println(npr.mkPrefixLString(1000))
    // prefix.toList.sortBy(e => (e._1.size, e._1.toString)).foreach(println)
  }

  @Test
  def euplotid() {
    val npr = new NonPowerBinString(IUPAC.EUPLOTID_NUCLEAR, 1)
    println(npr.mkPrefixLString(0))
  }

  @Test
  def standard() {
    val npr = new NonPowerBinString(IUPAC.STANDARD, 1)
    println(npr.mkPrefixLString(0))
  }

  @Test
  @Ignore
  def listAll() {
    val npr = new NonPowerBinString(IUPAC.EUPLOTID_NUCLEAR, 1)
    for (i <- 0 until 4096) {
      println("Table " + i)
      println(npr.mkPrefixLString(i))
      println()
    }
  }

  @Test
  @Ignore
  def listSome() {
    val npr = new NonPowerBinString(IUPAC.EUPLOTID_NUCLEAR, 1)
    for (i <- List(0, 1, 3, 7, 15, 64+128+256, 1+4+16+64+256+1024)) {
      println("Table " + i)
      println(npr.mkPrefixLString(i))
      println()
    }
  }
}
