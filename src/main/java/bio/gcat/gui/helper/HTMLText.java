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
