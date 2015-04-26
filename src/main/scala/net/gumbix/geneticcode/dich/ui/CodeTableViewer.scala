package net.gumbix.geneticcode.dich.ui

import javax.swing.{JEditorPane, JTextArea, JTextField, JFrame}
import java.awt.BorderLayout
import net.gumbix.geneticcode.dich.ct.ClassTable

/**
 * @author Markus Gumbel (m.gumbel@hs-mannheim.de)
 *         (c) 2013 Markus Gumbel
 */
class CodeTableViewer(val ct: ClassTable) {
  val text = "<html><b>hi!</b> there</html>"
  val jf = new JFrame("Viewer")
  val textfield = new JEditorPane()
  textfield.setContentType("text/html")
  textfield.setEditable(false)
  textfield.setText(ct.htmlMkString())
  jf.getContentPane.add(textfield, BorderLayout.CENTER)
  jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
  jf.pack
  jf.setVisible(true)

  Thread.sleep(900000)
}