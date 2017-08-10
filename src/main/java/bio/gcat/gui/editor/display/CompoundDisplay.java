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
package bio.gcat.gui.editor.display;

import static bio.gcat.Utilities.fixPosition;
import static bio.gcat.gui.helper.Guitilities.getImageIcon;
import static bio.gcat.gui.helper.Guitilities.invokeAppropriate;
import static java.lang.Math.max;
import static java.lang.Math.min;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;

import bio.gcat.gui.editor.NucleicDisplay;
import bio.gcat.gui.editor.NucleicEditor;
import bio.gcat.gui.editor.NucleicListener;
import bio.gcat.nucleic.Compound;
import bio.gcat.nucleic.Tuple;

public class CompoundDisplay extends JPanel implements NucleicDisplay, NucleicListener, CaretListener {
	private static final long serialVersionUID = 1l;

	public static final String LABEL = "Compound";
	public static final Icon ICON = getImageIcon("color_swatch");
	
	private final Color COLOR_NONPOLAR = new Color(255,231,95),
                         COLOR_POLAR = new Color(179,222,192),
                         COLOR_BASIC = new Color(187,191,224),
                        COLOR_ACIDIC = new Color(248,183,211),
                       COLOR_TESSERA = new Color(179,222,192),
                       COLOR_SPECIAL = new Color(176,176,176);
	private final int defaultCharWidth;
	
	private NucleicEditor editor;
	private JTextComponent textPane;
	private int caretStart, caretEnd;
	
	private int border;
	
	public CompoundDisplay(NucleicEditor editor) {
		(textPane=(this.editor=editor).getTextPane()).addCaretListener(this);
		setBackground(Color.WHITE);
		setFont(textPane.getFont());
		FontMetrics metrics = getFontMetrics(getFont());
		defaultCharWidth = metrics.charWidth('0');
		setBorderSpacing(5);
		setPreferredSize();
	}

	@Override public String getLabel() { return LABEL; }
	@Override public Icon getIcon() { return ICON; }
	
	@Override public boolean hasPreferredSize() { return true; }
	@Override public void setPreferredSize() {
		Insets insets = getInsets();
		Dimension dimension = getPreferredSize();
		dimension.setSize(-insets.left-insets.right+(editor.getWidth()-editor.getNumberPanel().getWidth())/2, Integer.MAX_VALUE-Short.MAX_VALUE);
		setPreferredSize(dimension);
		setSize(dimension);
	}

	public int getBorderSpacing() { return border; }
	public void setBorderSpacing(int borderSpacing) {
		this.border = borderSpacing;
		Border inner = new EmptyBorder(0,borderSpacing,0,borderSpacing);
		setBorder(new CompoundBorder(new MatteBorder(0,1,0,0,Color.LIGHT_GRAY),inner));
		setPreferredSize();
	}
	
	@Override public void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);
		
		Rectangle clip = graphics.getClipBounds();
		FontMetrics metrics = textPane.getFontMetrics(textPane.getFont());
		int startOffset = textPane.viewToModel(new Point(0,clip.y)), endOffset = textPane.viewToModel(new Point(0,clip.y+clip.height));
		
		try {
			while(startOffset<=endOffset) {
				graphics.setColor(getForeground());
				drawCompounds(graphics, tuplesInRow(startOffset), getOffsetY(startOffset, metrics));
				startOffset = javax.swing.text.Utilities.getRowEnd(textPane, startOffset)+1;
			}
		} catch(BadLocationException e) { /** nothing to do here */  }
	}
	
	protected void drawCompounds(Graphics graphics, Tuple[] tuples, int y) {
		int chars = 0, x = getInsets().left, defaultTupleLength = editor.getTupleLength();
		for(Tuple tuple:tuples) {
			String text; Color color = COLOR_SPECIAL;
			if(tuple!=null) {
				Compound compound = tuple.getCompound();
				if(compound!=null) {
					text = compound.abbreviation;
					switch(compound.property) {
					case NONPOLAR: color = COLOR_NONPOLAR; break;
					case POLAR: color = COLOR_POLAR; break;
					case BASIC: color = COLOR_BASIC; break;
					case ACIDIC: color = COLOR_ACIDIC; break; 
					case TESSERA: color = COLOR_TESSERA; break; }
				} else if(Compound.isStart(tuple)) text = "STA";
					else if(Compound.isStop(tuple)) text = "STP";
					else text = "UNK";
			} else text = "ERR";
			
			// adapt text length to tuple length
			if(defaultTupleLength>0&&defaultTupleLength<text.length())
				text = text.substring(0,defaultTupleLength);
			
			drawString(graphics, text, x, y, color, caretStart-defaultTupleLength<=chars&&caretEnd>=chars);
			int length = text.length()+1;
			chars += length; x += length*defaultCharWidth;
		}
	}
	protected void drawString(Graphics graphics, String string, int x, int y, Color color, boolean marked) {
		FontMetrics metrics = graphics.getFontMetrics();
	    Rectangle2D rectangle = metrics.getStringBounds(string, graphics);
	    graphics.setColor(!marked?color:color.darker());
	    graphics.fillRect(x, y - metrics.getAscent(), (int)rectangle.getWidth(), (int)rectangle.getHeight());
	    graphics.setColor(getForeground());
			graphics.drawString(string, x, y);
	}

	protected Tuple[] tuplesInRow(int offset) {
		try {
			int rowStartOffset = javax.swing.text.Utilities.getRowStart(textPane,offset),
				rowEndOffset = javax.swing.text.Utilities.getRowEnd(textPane,rowStartOffset);
			//text version: return Tuple.toArray(textPane.getText(rowStartOffset, rowEndOffset-rowStartOffset));
			return editor.getTupleMap().subMap(fixPosition(rowStartOffset), fixPosition(rowEndOffset+1)).values().toArray(new Tuple[0]);
		}	catch(BadLocationException e) { return null; }
	}
	private int getOffsetY(int rowOffset,FontMetrics metrics) throws BadLocationException {
		Rectangle rectangle = textPane.modelToView(rowOffset);
		return (rectangle.y+rectangle.height)-metrics.getDescent();
	}

	@Override public void tuplesRemoved(NucleicEvent event) { tuplesInsert(event); }
	@Override public void tuplesInsert(NucleicEvent event) {
		invokeAppropriate(new Runnable() {
			public void run() { repaint(); }
		});
	}
	@Override public void tuplesUndoableChange(NucleicEvent event) { /* undoable change, nothing to do here */ }
	@Override public void optionsChange(NucleicEvent event) { /* nothing to do here */ }

	@Override public void caretUpdate(CaretEvent event) {
		int dot = event.getDot(), mark = event.getMark();
		caretStart = min(dot, mark); caretEnd = max(dot, mark);
		invokeAppropriate(new Runnable() {
			public void run() { repaint(); }
		});
	}
}