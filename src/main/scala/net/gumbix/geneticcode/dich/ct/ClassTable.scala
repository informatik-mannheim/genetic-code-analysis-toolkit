package net.gumbix.geneticcode.dich.ct

import net.gumbix.geneticcode.dich.Nucleotides._
import net.gumbix.geneticcode.dich._

// Not supported by db4o:
// import collection.immutable.{HashMap,HashSet}

import net.gumbix.geneticcode.core.{Degeneracy, CodonMapping}
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
   * Map a dichotomic class to a list of codons.
   * Note: codons cannot be duplicates, thus a list is used.
   */
  lazy val class2codons = {
    val m = new HashMap[List[Int], List[Codon]]()
    codon2class.foreach {
      e =>
        val (codon, clazz) = (e._1, e._2)
        val prev = if (m.contains(clazz)) m(clazz) else Nil
        m(clazz) = codon :: prev
    }
    m
  }

  lazy val class2AAList = {
    val m = new HashMap[List[Int], List[CodonMapping]]()
    class2codons.foreach {
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
    class2codons.foreach {
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
   * If an amino acid has more than one classes the mapping is not
   * compatible anymore. Stop codons are ignored.
   * @return
   */
  def isAACompatible =
    aa2classes.filter(e => !e._1.isStop).filter(e => e._2.size > 1).size == 0

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
   * Error indicating if this code table is compatible to the property.
   */
  lazy val errorC = {
    val sum = prop2Error.map(e => e._2).sum
    sum / 64.0
  }

  def isPropertyCompatible = errorC == 0

  /**
   * The degeneracy of the genetic code (according to Gonzalez et al.)
   * @return
   */
  def degeneracy = new Degeneracy(this)

  def classes = class2codons.keySet

  def mkFullString() = {
    "\nBDAs:" +
      "\n" + bdaMkString +
      "\n\n|classes| = " + classes.size +
      "\n\nerror = " + errorC + " = " + (errorC * 64).toString + " / 64" +
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