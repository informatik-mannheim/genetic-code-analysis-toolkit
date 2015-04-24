package de.hsma.gentool.gui.input;

import static de.hsma.gentool.Utilities.*;
import static de.hsma.gentool.gui.helper.Guitilities.*;
import static de.hsma.gentool.nucleic.Acid.*;
import static java.lang.Math.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.font.TextAttribute;
import java.util.Map;
import javax.swing.border.EmptyBorder;
import de.hsma.gentool.nucleic.Base;
import de.hsma.gentool.nucleic.Tuple;

public class CodonWheel extends DefaultInput {
	private static final long serialVersionUID = 1l;

	private static final String NAME = "Codon Wheel";
	private static final float[] CIRCLES = new float[]{1f,.825f,.5f};
	
	@Override public String getName() { return NAME; }
	
	public CodonWheel() {
		setLayout(null);
		setBorder(new EmptyBorder(10,10,10,10));
		setPreferredSize(new Dimension(300,300));
		addComponentListener(new ComponentAdapter() {
			@Override public void componentResized(ComponentEvent event) {
				Insets insets = getInsets();
				int width = getWidth(),height = getHeight(),diameter = min(width-insets.left-insets.right,height-insets.top-insets.bottom),radius = round((float)diameter/2);
				Point center = new Point(round((float)width/2),round((float)height/2));
				
				for(TupleButton button:buttons.values()) {
					WheelButton wheelButton = (WheelButton)button;
					FontMetrics metrics = wheelButton.getFontMetrics(wheelButton.getFont());
					wheelButton.setLocation(center.x-(int)round(sin(wheelButton.angle)*radius*wheelButton.circle)-metrics.stringWidth(wheelButton.getText())/2,
						center.y-(int)round(cos(wheelButton.angle)*radius*wheelButton.circle)-metrics.getHeight()/2);
				}
			}
		});
		
		Base[] bases = RNA.bases;
		float piFraction = (float)(PI/32), piFractionHalf = piFraction/2;
		for(int baseA=0;baseA<4;baseA++) { //inner circle
			float angleBaseA = (float)(-HALF_PI-HALF_PI*baseA+QUARTER_PI);
			add(createTupleButton(new Tuple(bases[baseA]),angleBaseA,CIRCLES[2]/2,26f));
			for(int baseB=0;baseB<4;baseB++) { //middle circle
				float angleBaseB = angleBaseA+(float)(EIGHTH_PI-EIGHTH_PI*baseB+SIXTEENTH_PI);
				add(createTupleButton(new Tuple(bases[baseA],bases[baseB]),angleBaseB,CIRCLES[1]-.155f,18f));
				for(int baseC=0;baseC<4;baseC++) { //outer circle
					float angleBaseC = angleBaseB+(float)(piFraction-piFraction*baseC+piFractionHalf);
					add(createTupleButton(new Tuple(bases[baseA],bases[baseB],bases[baseC]),angleBaseC,CIRCLES[0]-.08f,8f));
				}
			}
		}
	}
	private WheelButton createTupleButton(final Tuple tuple, float angle, float circle, float font) {		
		WheelButton button = new WheelButton(tuple,angle,circle,font);
		buttons.put(tuple,button);
		return button;
	}
	
	@Override protected void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);
		((Graphics2D)graphics).setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		
		Insets insets = getInsets();
		int width = getWidth(),height = getHeight(),diameter = min(width-insets.left-insets.right,height-insets.top-insets.bottom),radius = round((float)diameter/2);
		Point center = new Point(round((float)width/2),round((float)height/2));
		
		graphics.setColor(Color.BLACK);
		graphics.drawOval(center.x-radius,center.y-radius,diameter,diameter);
		for(int circle=0;circle<CIRCLES.length;circle++)
			graphics.drawOval(round(center.x-radius*CIRCLES[circle]),round(center.y-radius*CIRCLES[circle]),round(diameter*CIRCLES[circle]),round(diameter*CIRCLES[circle]));
		
		float piFraction = (float)(TWO_PI/64);
		for(int line=0;line<64;line++) {
			float circle = line%16==0?0f:line%4==0?CIRCLES[2]:CIRCLES[1];
			int x = (int)round(sin(piFraction*line)*radius),y = (int)round(cos(piFraction*line)*radius); 
			graphics.drawLine(center.x-round(x*circle),center.y-round(y*circle),center.x-x,center.y-y);
		}
	}
	
	protected class WheelButton extends TupleButton {
		private static final long serialVersionUID = 1l;
		
		public final float angle,circle;
		
		public WheelButton(Tuple tuple, float angle, float circle, float font) {
			super(tuple); this.angle = angle; this.circle = circle;
			setFocusable(false); setContentAreaFilled(false); setBorderPainted(false); setBorder(EMPTY_BORDER);
			setFont(getFont().deriveFont(font)); FontMetrics metrics = getFontMetrics(getFont().deriveFont(Font.BOLD));
			setSize(metrics.stringWidth(getText()),metrics.getHeight());
		}
		
		public void setTuple(Tuple tuple) {
			setText((this.tuple=tuple)!=null?Character.toString(tuple.getBases()[tuple.getBases().length-1].letter):null);
		}
		
		@Override public void setTupleUsed(int used) {
			super.setTupleUsed(used);
			Font font = getFont();
			@SuppressWarnings("unchecked") Map<TextAttribute,Object> attributes = (Map<TextAttribute,Object>)font.getAttributes();
			attributes.put(TextAttribute.WEIGHT, used!=0?TextAttribute.WEIGHT_BOLD:TextAttribute.WEIGHT_REGULAR);
			attributes.put(TextAttribute.FOREGROUND, used<=0?Color.BLACK:used==1?USED_COLOR:DUPLICATE_COLOR);
			setFont(font.deriveFont(attributes));
			repaint();
		}
	}
}
