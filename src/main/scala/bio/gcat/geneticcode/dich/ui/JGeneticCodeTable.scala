package bio.gcat.geneticcode.dich.ui

import javax.swing._
import bio.gcat.geneticcode.dich._
import bio.gcat.geneticcode.dich.ct.{ClassTable, CodingClassTable}
import bio.gcat.geneticcode.dich.{Adenine => A}
import bio.gcat.geneticcode.dich.{Uracil => U}
import bio.gcat.geneticcode.dich.{Guanine => G}
import bio.gcat.geneticcode.dich.{Cytosine => C}

import java.awt.{Color, Dimension, GridLayout, BorderLayout}
import scala.collection.JavaConversions._
import java.awt.event.{MouseEvent, MouseAdapter}
import java.util
import javax.swing.border.EmptyBorder

/**
 * A panel that renders the genetic code table and their classes.
 * @author Markus Gumbel (m.gumbel@hs-mannheim.de)
 *         (c) 2013 Markus Gumbel
 */
class JGeneticCodeTable(ct: ClassTable) extends JPanel
with AAImplicitDefs {

  private var selClass = List[Int]()

  val selectedCodons = new util.HashSet[Codon]()
  val errorCodons = {
    // ct.
    new util.HashSet[Codon]()
  }
  val bins = new util.HashSet[JLabel]()

  def borderLabel(c: Compound) = {
    def color(c: Compound) = c match {
      case Uracil => new Color(210, 210, 210)
      case Cytosine => new Color(220, 220, 220)
      case Adenine => new Color(200, 200, 200)
      case Guanine => new Color(220, 220, 220)
    }
    val l = new JLabel(c.toString)
    l.setOpaque(true);
    l.setBackground(color(c))
    l.setForeground(Color.BLACK)
    l.setHorizontalAlignment(SwingConstants.CENTER)
    l.setBorder(new EmptyBorder(0, 15, 0, 15))
    l
  }

  def cellLabel(codon: Codon) = {
    val (r, g, b) = ct.colorRGB(codon)
    val panel = new JPanel(new GridLayout(1, 0))
    val l1 = new JLabel(codon2AA(codon).toFullString)
    l1.setOpaque(true);
    l1.setBackground(new Color(r, g, b))
    l1.setHorizontalAlignment(SwingConstants.CENTER)
    l1.setToolTipText(codon.toString)
    panel.add(l1)
    val l2 = new JLabel(ct.codon2class(codon).mkString(""))
    l2.setOpaque(true);
    val color = if (selectedCodons.contains(codon)) Color.RED else Color.WHITE
    l2.setBackground(color)
    l2.setHorizontalAlignment(SwingConstants.CENTER)
    l2.setToolTipText(codon.toString)
    l2.putClientProperty("codon", codon)
    // on click:
    l2.addMouseListener(new MouseAdapter() {
      override def mouseClicked(e: MouseEvent) {
        val c = l2.getClientProperty("codon").asInstanceOf[Codon]
        val clazz = ct.codon2class(c)
        val sel = ct.class2codonList(clazz)
        selectedCodons.clear()
        if (clazz != selClass) {
          selClass = clazz
          selectedCodons.addAll(sel)
        }
        refreshBins()
      }
    })
    panel.add(l2)
    bins.add(l2)
    panel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1))
    panel
  }

  def refreshBins() {
    bins.foreach {
      label =>
        val codon = label.getClientProperty("codon").asInstanceOf[Codon]
        if (codon != null) {
          if (selectedCodons.contains(codon)) {
            label.setBackground(Color.RED)
          } else {
            label.setBackground(Color.WHITE)
          }
        }
    }
  }

  val iupacNumber = ct.iupacNumber

  setLayout(new BorderLayout())
  // Define container:
  val leftColumn = new JPanel(new GridLayout(0, 1))
  val centerTable = new JPanel(new GridLayout(0, 4))
  val rightColumn = new JPanel(new GridLayout(0, 1))
  add(leftColumn, BorderLayout.WEST)
  add(centerTable, BorderLayout.CENTER)
  add(rightColumn, BorderLayout.EAST)

  val sList = ct.codons.sortBy(c => (ct.UcagOrder(c(0)), ct.UcagOrder(c(2)), ct.UcagOrder(c(1))))
  val header = sList.take(4)
  leftColumn.add(new JLabel(""))
  header.foreach(codon => centerTable.add(borderLabel(codon(1))))
  rightColumn.add(new JLabel(""))

  var linebreak = 0
  var groupBreak = 0
  sList.foreach {
    codon =>
      val binS = ct.codon2class(codon).mkString("")
      val cell = cellLabel(codon)
      linebreak += 1
      linebreak match {
        case 1 => {
          leftColumn.add(borderLabel(codon(0)))
          centerTable.add(cell)
        }
        case 4 => {
          linebreak = 0
          groupBreak += 1
          if (groupBreak == 4) {
            groupBreak = 0
            centerTable.add(cell)
            rightColumn.add(borderLabel(codon(2)))
          } else {
            centerTable.add(cell)
            rightColumn.add(borderLabel(codon(2)))
          }
        }
        case _ => centerTable.add(cell)
      }
  }
}

object JGeneticCodeTable {
  def main(args: Array[String]) {
    val bdas = List(
      new BinaryDichotomicAlgorithm(0, 1, (A, U), Set(A, U)),
      new BinaryDichotomicAlgorithm(0, 1, (A, U), Set(A, C)),
      new BinaryDichotomicAlgorithm(0, 1, (A, U), Set(A, G)),
      new BinaryDichotomicAlgorithm(0, 1, (A, G), Set(C, G)),
      new BinaryDichotomicAlgorithm(1, 2, (U, C), Set(A, G)),
      new BinaryDichotomicAlgorithm(1, 0, (C, G), Set(A, G))
    )
    val ct = new CodingClassTable(bdas, IUPAC.STANDARD)
    val frame = new JFrame("Class Table Viewer")
    frame.getContentPane.add(new JGeneticCodeTable(ct), BorderLayout.CENTER)
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
    frame.setPreferredSize(new Dimension(700, 500))
    frame.pack
    frame.setVisible(true)
  }
}
