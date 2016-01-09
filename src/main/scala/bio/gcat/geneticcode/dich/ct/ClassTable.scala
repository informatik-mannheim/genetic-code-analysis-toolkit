package bio.gcat.geneticcode.dich.ct

import java.util

import bio.gcat.geneticcode.core.{Degeneracy, CodonMapping}
import bio.gcat.geneticcode.dich._
import Nucleotides._

// Not supported by db4o:
// import collection.immutable.{HashMap,HashSet}

import java.util.{HashMap, HashSet}
import scala.collection.JavaConversions._

/**
 * A class table that partitions a set of 64 codons according
 * to a list of binary dichotomic algorithmns.
 * @param bdas
 */
class ClassTable(val bdas: List[Classifier[Int]],
                 val iupacNumber: Int = IUPAC.EUPLOTID_NUCLEAR,
                 // TODO parameter must be constant
                 val codonProperty: CodonProperty = IdStandardAminoAcidProperty)
  extends AAImplicitDefs with Serializable
  with PlainTextFormatter with LaTeXFormatter with HtmlFormatter {

  require(!bdas.isEmpty)

  private val serialVersionUID = 20130322;

  def this(bdas: Array[String], iupacNumber: Int) =
    this(BdaImplicitDefs.toBda(bdas), iupacNumber)

  def this(bdas: Array[String]) = this(bdas, IUPAC.EUPLOTID_NUCLEAR)

  /**
   * All 64 codons.
   */
  lazy val codons = {
    for (c1 <- nucleotides; c2 <- nucleotides; c3 <- nucleotides) yield {
      Codon(Array(c1, c2, c3))
    }
  }

  /**
   * Map a codon to a dichotomic class.
   */
  val codon2class = {
    val m = new HashMap[Codon, List[Int]]()
    codons.foreach(c => m(c) = bdas.map(_.classify(c)))
    m
  }

  /**
   * Generic mapper. In beta-version.
   * @param c
   * @tparam A Type from
   * @tparam B Type to
   * @return
   */
  private def a2bList[A, B](c: util.Map[B, A]) = {
    val m = new HashMap[A, List[B]]()
    c.foreach {
      e =>
        val (u, v) = (e._1, e._2) // A, B
        val prev = if (m.contains(v)) m(v) else Nil
        m(v) = u :: prev
    }
    m
  }

  /**
   * Map a dichotomic class to a list of codons.
   * Note: codons cannot be duplicates, thus a list is used.
   */
  lazy val class2codonList = a2bList[List[Int], Codon](codon2class)

  lazy val class2codonList2 = { // original impl.
    val m = new HashMap[List[Int], List[Codon]]()
    codon2class.foreach {
      e =>
        val (codon, clazz) = (e._1, e._2)
        val prev = if (m.contains(clazz)) m(clazz) else Nil
        m(clazz) = codon :: prev
    }
    m
  }

  /**
   * Map a dichotomic class to a list of properties.
   * Note: codons cannot be duplicates, thus a list is used.
   */
  lazy val class2propsList = {
    val m = new HashMap[List[Int], List[String]]()
    codon2class.foreach {
      e =>
        val (codon, clazz) = (e._1, e._2)
        val prev = if (m.contains(clazz)) m(clazz) else Nil
        m(clazz) = codonProperty.property(codon) :: prev
    }
    m
  }

  lazy val class2AAList = {
    val m = new HashMap[List[Int], List[CodonMapping]]()
    class2codonList.foreach {
      e =>
        val (clazz, codons) = (e._1, e._2)
        m(clazz) = codons.map(c => codon2AA(c)).toList.sortBy(c => c.toString)
    }
    m
  }

  /**
   * Map a dichotomic class to a set of amino acids.
   * Note: Amino acids may be redundant, so a set is used.
   */
  lazy val class2AASet = {
    val m = new HashMap[List[Int], HashSet[CodonMapping]]()
    class2codonList.foreach {
      e =>
        val (clazz, codons) = (e._1, e._2)
        m(clazz) = new HashSet(codons.map(c => codon2AA(c)).toSet)
    }
    m
  }

  /**
   * Map an amino acid to a set of classes.
   */
  lazy val aa2classes = {
    // TODO redundant: use function
    val m = new HashMap[CodonMapping, HashSet[List[Int]]]()
    codon2class.foreach {
      e =>
        val (aa, clazz) = (codon2AA(e._1), e._2)
        val set = if (m.contains(aa)) m(aa) else new HashSet[List[Int]]()
        set += clazz
        m(aa) = set
    }
    m
  }

  /**
   * Create a list of classes for each property.
   */
  lazy val prop2ClassesList = {
    import scala.collection.mutable._
    import scala.collection._
    val m = mutable.HashMap[String, MutableList[immutable.List[Int]]]()
    codon2class.foreach {
      e =>
        val (codon, clazz) = (e._1, e._2)
        val p = codonProperty.property(codon) // Convert into a property
      val list = if (m.contains(p)) m(p) else MutableList[List[Int]]()
        m(p) = list :+ clazz
    }
    m
  }

  /**
   * Calculate the error for all properties.
   * @return a map of type property -> error.
   */
  lazy val prop2Error = prop2ClassesList.map {
    e =>
      val (prop, clazzList) = (e._1, e._2)
      val g = clazzList.groupBy(e => e)
      val h = g.values.map(_.size).max // Find the maximum element.
    val err = clazzList.size - h
      (prop, err)
  }

  /**
   * Relative error indicating if this code table is compatible to the property.
   */
  val relErrorC = errorC / 64.0

  /**
   * Absolute error indicating if this code table is compatible to the property.
   */
  lazy val errorC = prop2Error.map(e => e._2).sum

  val errorA = {
    def h(c: List[Int]) = {
      val p = class2propsList(c) // all amino acids for a class.
      // Group properties to sets, identify the max. set size:
      def m(l: List[String]) = l.groupBy(e => e).map(e => e._2.size).max

      p.size - m(p)
    }
    // Sum over all classes:
    classes.map(c => h(c)).sum
  }

  val relErrorA = errorA / 64.0

  val error = errorA + errorC

  val relError = (errorA + errorC) / 64.0

  def isPropertyCompatible = errorC == 0

  /**
   * If an amino acid has more than one classes the mapping is not
   * compatible anymore. Stop codons are ignored.
   * @return
   */
  def isAACompatible =
    aa2classes.filter(e => !e._1.isStop).filter(e => e._2.size > 1).size == 0

  /**
   * The degeneracy of the genetic code (according to Gonzalez et al.)
   * @return
   */
  def degeneracy = new Degeneracy(this)

  def classes = class2codonList.keySet

  def mkFullString() = {
    "\nBDAs:" +
      "\n" + bdaMkString +
      "\n\n|classes| = " + classes.size +
      "\n\nerror = " + error + " / 64 ("+ relError + "), (errorA =  " + errorA +
      ", errorC = " + errorC  + ")" +
      "\n\nclass table:" +
      "\n" + mkString() +
      "\ndeg. = " + degeneracy.mkString +
      "\nclasses (sorted by bin. string):" +
      "\n" + classesMkString +
      "\n\n" + "classes (sorted by size):" +
      "\n" + classesMkStringSize +
      "\n\namino acids:" +
      "\n" + aaMkString
  }
}