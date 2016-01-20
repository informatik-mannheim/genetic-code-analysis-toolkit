package bio.gcat.gui.helper;

import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

public class HTMLText extends HTMLDocument {
	private static final long serialVersionUID = 1l;
	
	private StringBuilder text = new StringBuilder();
	private HTMLEditorKit.ParserCallback callback;
	
	public String getText() { return text.toString(); }
	public HTMLEditorKit.ParserCallback getReader(int pos) {
		return callback!=null?callback:new HTMLEditorKit.ParserCallback() {
			public void handleText(char[] data, int pos) {
				text.append(data).append('\n'); }
		};
	}
}
