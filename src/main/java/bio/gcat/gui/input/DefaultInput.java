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
package bio.gcat.gui.input;

import static bio.gcat.gui.helper.Guitilities.*;
import static bio.gcat.nucleic.Acid.*;
import static bio.gcat.nucleic.Base.*;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.text.BadLocationException;
import bio.gcat.gui.editor.NucleicDocument;
import bio.gcat.gui.editor.NucleicEditor;
import bio.gcat.gui.editor.NucleicListener;
import bio.gcat.gui.editor.NucleicOptions.EditorMode;
import bio.gcat.nucleic.Acid;
import bio.gcat.nucleic.Base;
import bio.gcat.nucleic.Tuple;
import com.google.common.collect.ImmutableMap;

public abstract class DefaultInput extends JPanel implements Input, NucleicListener {
	private static final long serialVersionUID = 1l;

	protected static final Acid DEFAULT_ACID = RNA;
	
	protected static final Color USED_COLOR = new Color(70,100,255), DUPLICATE_COLOR = new Color(255,70,10);
	protected static final Map<Base,Color> BASE_COLORS = ImmutableMap.of(
		URACIL,new Color(143,199,150), THYMINE,new Color(143,199,150), CYTOSINE,new Color(255,245,154),
		ADENINE,new Color(241,159,193), GUANINE,new Color(131,208,240));

	protected NucleicEditor editor;
	protected Map<Tuple,TupleButton> buttons;

	public DefaultInput() {
		buttons = new HashMap<Tuple,TupleButton>();
	}
	
	@Override public Component getComponent(NucleicEditor editor) {
		(this.editor = editor).addNucleicListener(this);
		tuplesInsert(new NucleicEvent(editor,editor.getTuples()));
		return this;
	}
	
	@Override public void tuplesRemoved(NucleicEvent event) { tuplesInsert(event); }
	@Override public void tuplesInsert(NucleicEvent event) {
		invokeAppropriate(new Runnable() {
			public void run() {
				Collection<Tuple> tuples = Tuple.normalizeTuples(event.getTuples(),DEFAULT_ACID);
				for(TupleButton button:buttons.values())
					button.setTupleUsed(Collections.frequency(tuples,button.getTuple()));
			}
		});
	}
	@Override public void tuplesUndoableChange(NucleicEvent event) { /* undoable change, nothing to do here */ }
	@Override public void optionsChange(NucleicEvent event) { /* nothing to do here */ }
	
	protected class TupleButton extends JButton {
		private static final long serialVersionUID = 1l;
		
		protected Tuple tuple;
		protected int used;
		
		public TupleButton(Tuple tuple) {
			setTuple(tuple); setFocusable(false); setContentAreaFilled(false); setBorderPainted(false); setBorder(EMPTY_BORDER);
			addActionListener(new ActionListener() {
				@Override public void actionPerformed(ActionEvent event) {
					if(editor!=null&&editor.getTextPane().isEditable())
						if(editor.getOptions().tupleLength==tuple.length()) {
							if(editor.getEditorMode()!=EditorMode.SET||!editor.getTuples().contains(tuple))
								   editor.appendTuples(Arrays.asList(tuple));
							else editor.setTuples(new ArrayList<Tuple>(editor.getTuples()) { private static final long serialVersionUID = 1l; {
								remove(tuple);
							}});
						} else try {	
							NucleicDocument document = editor.getDocument();
							document.insertString(document.getLength(),tuple.toString(),null);
						} catch(BadLocationException e) { /* nothing to do here */ }
				}
			});
		}
		
		public Tuple getTuple() { return tuple; }
		public void setTuple(Tuple tuple) {
			setText((this.tuple=tuple)!=null?tuple.toString(true):null);
		}
		
		public int isTupleUsed() { return used; }
		public void setTupleUsed(int used) { this.used = used; }
		
		@Override public void updateUI() { super.updateUI(); setTupleUsed(isTupleUsed()); }
	}
}
