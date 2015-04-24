package de.hsma.gentool.gui.input;

import static de.hsma.gentool.gui.helper.Guitilities.*;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JPanel;
import de.hsma.gentool.gui.editor.NucleicEditor;
import de.hsma.gentool.gui.editor.NucleicListener;
import de.hsma.gentool.nucleic.Tuple;

public abstract class DefaultInput extends JPanel implements Input, NucleicListener {
	private static final long serialVersionUID = 1l;

	protected static final Color USED_COLOR = new Color(70,100,255), DUPLICATE_COLOR = new Color(255,70,10);
	
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
				Collection<Tuple> tuples = event.getTuples();
				for(TupleButton button:buttons.values())
					button.setTupleUsed(Collections.frequency(tuples, button.getTuple()));
			}
		});
	}
	@Override public void tuplesChanged(NucleicEvent event) { /* undoable change, nothing to do here */ }
	
	protected class TupleButton extends JButton {
		private static final long serialVersionUID = 1l;
		
		protected Tuple tuple;
		protected int used;
		
		public TupleButton(Tuple tuple) {
			setTuple(tuple); setFocusable(false); setContentAreaFilled(false); setBorderPainted(false); setBorder(EMPTY_BORDER);
			addActionListener(new ActionListener() {
				@Override public void actionPerformed(ActionEvent event) {
					if(editor!=null&&editor.getTextPane().isEditable())
						editor.appendTuples(Arrays.asList(tuple));
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
