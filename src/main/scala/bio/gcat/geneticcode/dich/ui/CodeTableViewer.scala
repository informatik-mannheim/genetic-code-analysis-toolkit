/*
 * Copyright [2017] [Mannheim University of Applied Sciences]
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package bio.gcat.geneticcode.dich.ui

import javax.swing.{JEditorPane, JTextArea, JTextField, JFrame}
import java.awt.BorderLayout

import bio.gcat.geneticcode.dich.ct.ClassTable

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