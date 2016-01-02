package bio.gcat.geneticcode.dich.scan

import bio.gcat.geneticcode.dich.conc.ConcScan
import bio.gcat.geneticcode.dich.ct.AminoClassTable
import bio.gcat.geneticcode.dich.{ScanConstraint, Scan, Classifier}
import bio.gcat.geneticcode.dich._
import scala.collection.JavaConversions._

/**
 * Work in progress!
 * Try to classify the amino acids.
 * See also ErrorScan.
 * @author Markus Gumbel (m.gumbel@hs-mannheim.de)
 *         (c) 2012 Markus Gumbel
 */
trait AminoMappingConstraints extends ScanConstraint[AminoClassTable] {

  val minClassSize: Int

  override def isValidConfig(t: AminoClassTable, w: Int) =
    t.isAACompatible && t.classes.size > w

  override def isSolution(t: AminoClassTable) =
    t.isAACompatible && t.classes.size >= minClassSize

  // TODO max = t.classes.size not set yet
}

class AminoMappingScan(bdas: List[Classifier[Int]], size: Int,
                       val minClassSize: Int = 16,
                       val iupacCodeTableNo: Int = IUPAC.STANDARD)
  extends Scan[AminoClassTable](bdas, size)
  with AminoMappingConstraints {

  def this(bdas: Array[String], size: Int) =
    this(BdaImplicitDefs.toBda(bdas), size)

  override def startMessage = "Amino Acid classification (serially)"

  override def newClassTable(bda: List[Classifier[Int]]) = {
    new AminoClassTable(bda, iupacCodeTableNo) /* {
      override def isAACompatible =
        aa2classes.filter(e => e._2.size > 1).size <= 2
    } */
  }
}

object AminoMappingScan {
  def main(args: Array[String]) {
    new AminoMappingScan(List(), 7, 16, IUPAC.VERTEBRATE_MITOCHONDRIAL).run()
  }
}

class ConcAminoMappingScan(bdas: List[Classifier[Int]],
                           size: Int,
                           val multipleClasses: Int = 1,
                           val minClassSize: Int = 16,
                           val iupacCodeTableNo: Int = 1,
                           noActors: Int = 3)
  extends ConcScan[AminoClassTable](bdas, size, noActors)
  with AminoMappingConstraints {

  // override lazy val allBdas = BinaryDichotomicAlgorithm.bdas

  def this(bdas: Array[String], size: Int) =
    this(BdaImplicitDefs.toBda(bdas), size)

  override def startMessage = "Amino Acid classification (concurrently)"

  override def newClassTable(bda: List[Classifier[Int]]) = {
    new AminoClassTable(bda, iupacCodeTableNo) {

      new AminoClassTable(bda, iupacCodeTableNo) {
        override def isAACompatible =
          aa2classes.filter(e => !e._1.isStop).filter(e => e._2.size > 1).size <= 3
      }

      /**
       * Count for each amino acid the number of classes it belongs to which
       * is greater than multipleClasses. This must yield in an empty list.
       */
      def xisAACompatible =
        aa2classes.filter(e => e._2.size > multipleClasses).size == 0
    }
  }

  def cloneScan() = new ConcAminoMappingScan(bdas, size, multipleClasses, minClassSize,
    iupacCodeTableNo, noActors)
}

object ConcAminoMappingScan {
  def main(args: Array[String]) {
    // 7 BDAs, no multiple class, at least 14 classes, ...
    new ConcAminoMappingScan(List(), 5, 2, 15, IUPAC.VERTEBRATE_MITOCHONDRIAL, 3).run()
  }
}