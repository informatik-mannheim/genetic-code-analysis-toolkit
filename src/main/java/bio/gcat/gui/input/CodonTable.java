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
package bio.gcat.gui.input;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.font.TextAttribute;
import java.util.List;
import java.util.Map;
import javax.swing.JPanel;
import bio.gcat.nucleic.Base;
import bio.gcat.nucleic.Tuple;

public class CodonTable extends DefaultInput {
	private static final long serialVersionUID = 1l;

	private static final String NAME = "Codon Table";
	private static final Font NUMBER_FONT = new Font("Monospaced",Font.BOLD,9);
	
	@Override public String getName() { return NAME; }

	public CodonTable() { createTuplePanels(); }
	
	protected void createTuplePanels() {
		Base bases[] = DEFAULT_ACID.bases; int basesLength = bases.length;
		List<Tuple> tuples = Tuple.allTuples(DEFAULT_ACID,3);
		
		setLayout(new GridLayout(basesLength, basesLength, 5, 5));
		for(int panelOffset=0;panelOffset<tuples.size();panelOffset+=basesLength)
			add(createTuplePanel(tuples, panelOffset, basesLength));
	}
	protected JPanel createTuplePanel(List<Tuple> tuples, int panelOffset, int size) {
		JPanel panel = new JPanel(new GridLayout(size,1));
		panel.setBackground(BASE_COLORS.get(DEFAULT_ACID.bases[panelOffset/16]));
		for(int offset=0;offset<size;offset++)
			panel.add(createTupleButton(tuples.get(panelOffset+offset)));
		return panel;
	}
	private CodonButton createTupleButton(final Tuple tuple) {		
		CodonButton button = new CodonButton(tuple);
		buttons.put(tuple,button);
		return button;
	}
	
	protected class CodonButton extends TupleButton {
		private static final long serialVersionUID = 1l;
		
		public CodonButton(Tuple tuple) { super(tuple); }
		
		public void setTupleUsed(int used) {
			super.setTupleUsed(used); Font font = getFont();
			@SuppressWarnings("unchecked") Map<TextAttribute,Object> attributes = (Map<TextAttribute,Object>)font.getAttributes();
			attributes.put(TextAttribute.WEIGHT, used!=0?TextAttribute.WEIGHT_BOLD:TextAttribute.WEIGHT_REGULAR);
			attributes.put(TextAttribute.UNDERLINE, used!=0?TextAttribute.UNDERLINE_LOW_DOTTED:null);
			attributes.put(TextAttribute.FOREGROUND, used<=0?Color.BLACK:used==1?USED_COLOR:DUPLICATE_COLOR);
			setFont(font.deriveFont(attributes));
			repaint();
		}

		@Override public void paintComponent(Graphics graphics) {
			super.paintComponent(graphics);
			if(used>=2) {
				graphics.setColor(DUPLICATE_COLOR);
				graphics.setFont(NUMBER_FONT);
				graphics.drawString(Integer.toString(used),getWidth()/2+graphics.getFontMetrics(getFont()).stringWidth(getText())/2+2,graphics.getFontMetrics().getHeight()-5);
			}
		}
	}
}