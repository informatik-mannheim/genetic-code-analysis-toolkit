/*
 * Copyright [2014] [Mannheim University of Applied Sciences]
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
package de.hsma.gentool.gui.helper;

import javax.swing.text.AbstractDocument;
import javax.swing.text.BoxView;
import javax.swing.text.ComponentView;
import javax.swing.text.Element;
import javax.swing.text.IconView;
import javax.swing.text.LabelView;
import javax.swing.text.ParagraphView;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;

public class WrapEditorKit extends StyledEditorKit {
	private static final long serialVersionUID = 1l;
	
	private ViewFactory defaultFactory = new WrapColumnFactory();

	public ViewFactory getViewFactory() {
		return defaultFactory;
	}

	static class WrapColumnFactory implements ViewFactory {
		public View create(Element element) {
			String kind = element.getName();
			if(kind!=null) {
				if(kind.equals(AbstractDocument.ContentElementName)) {
					return new WrapLabelView(element);
				} else if(kind.equals(AbstractDocument.ParagraphElementName)) {
					return new ParagraphView(element);
				} else if(kind.equals(AbstractDocument.SectionElementName)) {
					return new BoxView(element,View.Y_AXIS);
				} else if(kind.equals(StyleConstants.ComponentElementName)) {
					return new ComponentView(element);
				} else if(kind.equals(StyleConstants.IconElementName)) { return new IconView(element); }
			}

			return new LabelView(element);
		}
	}

	static class WrapLabelView extends LabelView {
		public WrapLabelView(Element element) {
			super(element);
		}

		public float getMinimumSpan(int axis) {
			switch(axis) {
			case View.X_AXIS:
				return 0;
			case View.Y_AXIS:
				return super.getMinimumSpan(axis);
			default:
				throw new IllegalArgumentException("Invalid axis: "+axis);
			}
		}
	}
}
