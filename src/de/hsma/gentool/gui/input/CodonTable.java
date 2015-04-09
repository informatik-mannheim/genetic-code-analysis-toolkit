package de.hsma.gentool.gui.input;

import static de.hsma.gentool.gui.helper.Guitilities.*;
import static de.hsma.gentool.nucleic.Acid.*;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.TextAttribute;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JPanel;
import de.hsma.gentool.gui.editor.NucleicEditor;
import de.hsma.gentool.gui.editor.NucleicListener;
import de.hsma.gentool.nucleic.Tuple;

public class CodonTable extends JPanel implements Input, NucleicListener {
	private static final long serialVersionUID = 1l;

	private static final String NAME = "Codon Table";
	
	private NucleicEditor editor;
	private Map<Tuple,TupleButton> buttons;
	
	@Override public String getName() { return NAME; }

	public CodonTable() {
		buttons = new HashMap<Tuple,TupleButton>();
		
		int bases = RNA.bases.length;
		List<Tuple> tuples = Tuple.allTuples(RNA,3);
		
		setLayout(new GridLayout(bases, bases, 5, 5));
		for(int panelOffset=0;panelOffset<tuples.size();panelOffset+=bases)
			add(createTuplePanel(tuples, panelOffset, bases));
	}
	
	protected JPanel createTuplePanel(List<Tuple> tuples, int panelOffset, int size) {
		JPanel panel = new JPanel(new GridLayout(size,1));
		for(int offset=0;offset<size;offset++)
			panel.add(createTupleButton(tuples.get(panelOffset+offset)));
		return panel;
	}
	protected TupleButton createTupleButton(final Tuple tuple) {		
		TupleButton button = new TupleButton(tuple);
		button.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent event) {
				if(editor!=null&&editor.getTextPane().isEditable())
					editor.appendTuples(Arrays.asList(tuple));
			}
		});
		buttons.put(tuple,button);
		return button;
	}
	
	@Override public Component getComponent(NucleicEditor editor) {
		(this.editor = editor).addNucleicListener(this);
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
	
	protected static class TupleButton extends JButton {
		private static final long serialVersionUID = 1l;
		
		private static final Font NUMBER_FONT = new Font("Monospaced",Font.BOLD,9);
		
		protected Tuple tuple;
		
		private int used;
		
		public TupleButton() { this(null); }
		public TupleButton(Tuple tuple) {
			setTuple(tuple); setTupleUsed(0);
			setFocusable(false); setContentAreaFilled(false); setBorderPainted(false); setBorder(EMPTY_BORDER);
		}
		
		public Tuple getTuple() { return tuple; }
		public void setTuple(Tuple tuple) {
			setText((this.tuple=tuple)!=null?tuple.toString(true):null);
		}
		
		public int isTupleUsed() { return used; }
		public void setTupleUsed(int used) {
			Font font = getFont(); this.used = used;
			@SuppressWarnings("unchecked") Map<TextAttribute,Object> attributes = (Map<TextAttribute,Object>)font.getAttributes();
			attributes.put(TextAttribute.WEIGHT, used!=0?TextAttribute.WEIGHT_BOLD:TextAttribute.WEIGHT_REGULAR);
			attributes.put(TextAttribute.UNDERLINE, used!=0?TextAttribute.UNDERLINE_LOW_DOTTED:null);
			//attributes.put(TextAttribute.FOREGROUND, used<=1?Color.BLACK:Color.RED);
			setFont(font.deriveFont(attributes));
			repaint();
		}

		@Override public void paintComponent(Graphics graphics) {
			super.paintComponent(graphics);
			if(used>=2) {
				graphics.setColor(Color.RED);
				graphics.setFont(NUMBER_FONT);
				graphics.drawString(Integer.toString(used),getWidth()/2+graphics.getFontMetrics(getFont()).stringWidth(getText())/2+2,graphics.getFontMetrics().getHeight()-5);
			}
		}
		
		@Override public void updateUI() { super.updateUI(); setTupleUsed(isTupleUsed()); }
	}
}