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

import static bio.gcat.Utilities.EIGHTH_PI;
import static bio.gcat.Utilities.HALF_PI;
import static bio.gcat.Utilities.QUARTER_PI;
import static bio.gcat.Utilities.SIXTEENTH_PI;
import static bio.gcat.Utilities.TWO_PI;
import static bio.gcat.gui.helper.Guitilities.EMPTY_BORDER;
import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.min;
import static java.lang.Math.round;
import static java.lang.Math.sin;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.font.TextAttribute;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Stream;

import javax.swing.border.EmptyBorder;

import bio.gcat.nucleic.Base;
import bio.gcat.nucleic.Tuple;

public class CodonCircle extends DefaultInput {
	private static final long serialVersionUID = 1l;

	private static final String NAME = "Codon Circle";
	private static final float[] CIRCLES = new float[]{1f,.825f,.5f};
	
	private int circles = 3;
	private boolean representable = true;
	
	private Font boldFont;
	
	@Override public String getName() { return NAME; }
	
	public CodonCircle() {
		setLayout(null);
		setBorder(new EmptyBorder(10,10,10,10));
		setPreferredSize(new Dimension(300,300));
		addComponentListener(new ComponentAdapter() {
			@Override public void componentResized(ComponentEvent event) {
				Insets insets = getInsets();
				int width = getWidth(),height = getHeight(),diameter = min(width-insets.left-insets.right,height-insets.top-insets.bottom);
				float radius = (float)diameter/2; Point2D.Float center = new Point2D.Float((float)width/2,(float)height/2);
				for(TupleButton button:buttons.values()) {
					CircleButton circleButton = (CircleButton)button;
					FontMetrics metrics = circleButton.getFontMetrics(circleButton.getFont());
					circleButton.setLocation((int)round(center.x-sin(circleButton.angle)*radius*circleButton.circle-(float)metrics.stringWidth(circleButton.getText())/2),
						(int)round(center.y-cos(circleButton.angle)*radius*circleButton.circle-(float)metrics.getHeight()/2));
				}
			}
		});
		
		Base[] bases = DEFAULT_ACID.bases;
		float piFraction = (float)(PI/32), piFractionHalf = piFraction/2;
		for(int baseA=0;baseA<4;baseA++) { //inner circle
			float angleBaseA = (float)(-HALF_PI-HALF_PI*baseA+QUARTER_PI);
			add(createTupleButton(new Tuple(bases[baseA]),angleBaseA,CIRCLES[2]/2,32f));
			for(int baseB=0;baseB<4;baseB++) { //middle circle
				float angleBaseB = angleBaseA+(float)(EIGHTH_PI-EIGHTH_PI*baseB+SIXTEENTH_PI);
				add(createTupleButton(new Tuple(bases[baseA],bases[baseB]),angleBaseB,CIRCLES[1]-.155f,24f));
				for(int baseC=0;baseC<4;baseC++) { //outer circle
					float angleBaseC = angleBaseB+(float)(piFraction-piFraction*baseC+piFractionHalf);
					add(createTupleButton(new Tuple(bases[baseA],bases[baseB],bases[baseC]),angleBaseC,CIRCLES[0]-.08f,9f));
				}
			}
		}
	}
	private CircleButton createTupleButton(final Tuple tuple, float angle, float circle, float font) {		
		CircleButton button = new CircleButton(tuple,angle,circle,font);
		buttons.put(tuple,button);
		return button;
	}
	
	public int getCircles() { return circles; }
	public void setCircles(int circles) {
		// only display of 1, 2, or 3 circles are supported, rest is unsupported, set to 1 
		this.circles = (this.representable=circles>0&&circles<=3)?circles:1;
		for(TupleButton button:buttons.values())
			button.setVisible(this.circles>=button.tuple.length());
		repaint();
	}
	
	@Override protected void paintComponent(Graphics defaultGraphics) {
		super.paintComponent(defaultGraphics);
		Graphics2D graphics = (Graphics2D)defaultGraphics;
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		
		Insets insets = getInsets();
		int width = getWidth(),height = getHeight(),diameter = min(width-insets.left-insets.right,height-insets.top-insets.bottom);
		float radius = (float)diameter/2; Point2D.Float center = new Point2D.Float((float)width/2,(float)height/2);
		
		Base[] bases = DEFAULT_ACID.bases;
		for(int circle=CIRCLES.length-circles;circle<CIRCLES.length;circle++) {
			int sections = (int)Math.pow(4,CIRCLES.length-circle);
			float angle = 360f/sections, size = diameter*CIRCLES[circle];
			for(int section=0;section<sections;section++) {
				graphics.setColor(BASE_COLORS.getOrDefault(bases[3-(section+(circle==CIRCLES.length-1?2:0))%4],Color.BLACK));
				graphics.fill(new Arc2D.Float(center.x-radius*CIRCLES[circle],center.y-radius*CIRCLES[circle],size,size,angle*section-90,angle,Arc2D.PIE));
			}
		}
		
		graphics.setColor(Color.BLACK);
		for(int circle=CIRCLES.length-circles;circle<CIRCLES.length;circle++)			
			graphics.draw(new Ellipse2D.Float(center.x-radius*CIRCLES[circle],center.y-radius*CIRCLES[circle],diameter*CIRCLES[circle],diameter*CIRCLES[circle]));
		
		float piFraction = (float)(TWO_PI/64);
		for(int line=0;line<64;line++) {
			float inner, outer = CIRCLES[CIRCLES.length-circles]*radius;
			double x = sin(piFraction*line), y = cos(piFraction*line);
			if(line%16==0) {
				inner = 0f;
			} else if(line%4==0) {
				if(circles<2)
					continue;
				inner = CIRCLES[2]*radius;
			} else {
				if(circles<3)
					continue;
				inner = CIRCLES[1]*radius;
			}
			graphics.draw(new Line2D.Float(
				center.x-(float)(x*inner),center.y-(float)(y*inner),
				center.x-(float)(x*outer),center.y-(float)(y*outer)));
		}
		
		if(!representable) {
			graphics.setColor(Color.RED);
			graphics.setFont(boldFont!=null?boldFont:(boldFont=getFont().deriveFont(Font.BOLD)));
			FontMetrics metrics = getFontMetrics(boldFont);
			String lines[] = new String[] {
				"There is no representation of this input for tuples",
				"of this length. Consider switching to another input.",
			};
			// determine length of longest line via stream
			final float lineLeft = width-Stream.of(lines).map(line->metrics.stringWidth(line)).max(Comparator.naturalOrder()).get()-10,
				lineHeight = metrics.getHeight()+2; // gap between lines
			for(int line=0;line<lines.length;line++)
				graphics.drawString(lines[line], lineLeft,
					height-((lines.length-line-1)*lineHeight)-10);
		}
	}
	
	@Override public void optionsChange(NucleicEvent event) {
		super.optionsChange(event); setCircles(event.getOptions().tupleLength);
	}
	
	protected class CircleButton extends TupleButton {
		private static final long serialVersionUID = 1l;
		
		public final float angle,circle;
		
		public CircleButton(Tuple tuple, float angle, float circle, float font) {
			super(tuple); this.angle = angle; this.circle = circle;
			setFocusable(false); setContentAreaFilled(false); setBorderPainted(false); setBorder(EMPTY_BORDER);
			setFont(getFont().deriveFont(Font.BOLD,font)); FontMetrics metrics = getFontMetrics(getFont().deriveFont(Font.BOLD));
			setSize(metrics.stringWidth(getText()),metrics.getHeight());
		}
		
		public void setTuple(Tuple tuple) {
			setText((this.tuple=tuple)!=null?Character.toString(tuple.getBases()[tuple.getBases().length-1].letter):null);
		}
		
		@Override public void setTupleUsed(int used) {
			super.setTupleUsed(used);
			Font font = getFont();
			@SuppressWarnings("unchecked") Map<TextAttribute,Object> attributes = (Map<TextAttribute,Object>)font.getAttributes();
			attributes.put(TextAttribute.FOREGROUND, used<=0?Color.BLACK:used==1?USED_COLOR:DUPLICATE_COLOR);
			setFont(font.deriveFont(attributes));
			repaint();
		}
	}
}
