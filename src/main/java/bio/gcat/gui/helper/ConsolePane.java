/*
 * Copyright [2016] [Mannheim University of Applied Sciences]
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
package bio.gcat.gui.helper;

import static bio.gcat.Utilities.NEW_LINE;

import java.awt.Component;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.StringReader;

import javax.swing.AbstractAction;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

import bio.gcat.log.Logger;

public class ConsolePane extends JTextPane implements Logger {
	private static final long serialVersionUID = 1l;
	
	public static final String SUCCESS = "success", FAILURE = "failure";
	
	private HTMLDocument document;
	private HTMLEditorKit editorKit;
	
	public ConsolePane() {
		setBorder(new EmptyBorder(5,5,5,5));
		setContentType("text/html");
		setEditorKit(editorKit=new HTMLEditorKit());
		StyleSheet style = editorKit.getStyleSheet();
		style.addRule("body { color:#000; font-family:monospace; font-size:10px; }");
		style.addRule(".success { color:#468C46; }");
		style.addRule(".failure { color:#C83232; }");
		setDocument(document=(HTMLDocument)editorKit.createDefaultDocument());
		
		setEditable(false); setFont(new Font(Font.MONOSPACED,Font.PLAIN,14));
		addMouseListener(new PopupMouseAdapter(new JPopupMenu() { private static final long serialVersionUID = 1l; {
			add(new AbstractAction("Copy to Clipboard") {
				private static final long serialVersionUID = 1l;
				@Override public void actionPerformed(ActionEvent event) {
					ConsolePane.this.copyText();
				}
			});
			add(new AbstractAction("Clear") {
				private static final long serialVersionUID = 1l;
				@Override public void actionPerformed(ActionEvent event) {
					ConsolePane.this.clearText();
				}
			});
		}}));
	}
	
	@Override public String getText() {
		HTMLText text = new HTMLText();			
		try { editorKit.read(new StringReader(super.getText()), text, 0);	}
		catch (BadLocationException|IOException e) { e.printStackTrace(); /* nothing to do here */ }
		return text.getText();
	}
	
	public void copyText() {
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		toolkit.getSystemClipboard().setContents(
			new StringSelection(getText()),null);
		toolkit.beep();
	}
	
	public void insertText(String text) {
		try { editorKit.insertHTML(document,document.getLength(),text,0,0,null); }
		catch(BadLocationException|IOException e) { e.printStackTrace();  /* nothing to do here */ }
	}
	public void insertText(String text, String styleClass) {
		insertText(applyStyleClass(text,styleClass));
	}
	
	public void appendText(String text) {
		boolean atBottom = isAtBottom();
		if(document.getLength()!=0)
			insertText(NEW_LINE);
		insertText(text);
		if(atBottom) scrollToBottom();
	}
	public void appendText(String text, String styleClass) {
		appendText(applyStyleClass(text,styleClass));
	}
	
	public void clearText() { setText(null); }
	
	@Override public void log(String format,Object... arguments) {
		appendText(String.format(format,arguments));
	}
	@Override public void log(String message,Throwable throwable) {
		appendText(message,FAILURE); appendText(throwable.getMessage(),FAILURE);
	}
	
	private String applyStyleClass(String text, String styleClass) {
		return String.format("<span class=\"%s\">%s</span>",styleClass,text);
	}
	private boolean isAtBottom() {
		JScrollBar scrollBar = getScrollBar(); if(scrollBar==null) return true;
		return scrollBar.getValue()+scrollBar.getVisibleAmount()+scrollBar.getBlockIncrement()>scrollBar.getMaximum();
	}
	private void scrollToBottom() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JScrollBar scrollBar = getScrollBar(); if(scrollBar==null) return;
				setCaretPosition(getDocument().getLength());
				scrollBar.setValue(scrollBar.getMaximum());
			}
		});
	}
	private JScrollBar getScrollBar() {
		Component parent = this;
		do if((parent=parent.getParent())==null)
			return null;
		while(!(parent instanceof JScrollPane));
		return ((JScrollPane)parent).getVerticalScrollBar();
	}
}