package de.hsma.gentool.gui.helper;

import static de.hsma.gentool.gui.helper.Guitilities.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

public class FoldingPanel extends JPanel {
	private static final long serialVersionUID = 1l;

	public enum Orientation{HORIZONTAL,VERTICAL}

	private Orientation orientation;
	private boolean expanded = true;
	
	private JPanel collapse;
	private Component child;
	private JCheckBox expand;
	private Box info;
	
	public FoldingPanel(Component child) { this(child, Orientation.VERTICAL); }
	public FoldingPanel(Component child, Orientation orientation) {
		this.child = child;	this.orientation = orientation;
		this.setLayout(new BorderLayout());
		collapse = new JPanel(new BorderLayout());
		collapse.add(child,BorderLayout.CENTER);
		this.add(collapse,BorderLayout.CENTER);
	}

	public FoldingPanel(Component child, String title) { this(child, Orientation.VERTICAL, title); }
	public FoldingPanel(Component child, Orientation orientation, String title) {
		this(child, orientation); Icon icon; boolean horizontal = orientation == Orientation.HORIZONTAL;
		
		Box header = horizontal ? Box.createVerticalBox() : Box.createHorizontalBox();
		header.add(expand = new JCheckBox(title));
		header.add(horizontal?Box.createVerticalGlue():Box.createHorizontalGlue());
		header.add(info = horizontal?Box.createVerticalBox():Box.createHorizontalBox());
		header.add(horizontal?Box.createVerticalStrut(6):Box.createHorizontalStrut(6));
		
		this.add(header, orientation == Orientation.HORIZONTAL ? BorderLayout.WEST : BorderLayout.NORTH);
		expand.setBorder(new EmptyBorder(4,4,4,0));
		expand.setHorizontalTextPosition(JCheckBox.RIGHT);
		expand.setIcon(icon=new ArrowIcon(ArrowIcon.EAST));
		expand.setRolloverIcon(icon);
		expand.setSelectedIcon(icon=new ArrowIcon(ArrowIcon.SOUTH));
		expand.setRolloverSelectedIcon(icon);	
		expand.setSelected(expanded);
		expand.setFocusPainted(false);
		expand.addItemListener(new ItemListener() {
			@Override public void itemStateChanged(ItemEvent event) { setExpanded(expand.isSelected()); }
		});
	}
	
	public boolean isExpanded() { return expanded; }
	public void setExpanded(boolean expanded) {
		if(this.expanded!=expanded) {
			if(expand != null)
				expand.setSelected(expanded);
			this.expanded = expanded;
			Dimension size = child.getPreferredSize();
			if(orientation!=Orientation.HORIZONTAL)
				   this.setCollapseSize(size.width,!expanded ? 0 : size.height);
			else this.setCollapseSize(!expanded ? 0 : size.width, size.height); 
			firePropertyChange("expanded",!expanded,expanded);
		}
	}
	
	protected void setCollapseSize(int width,int height) {
		Dimension size = new Dimension(width,height);
		collapse.setMinimumSize(size);
		collapse.setPreferredSize(size);
		child.revalidate();	repaint();
	}

	@Override public void setFont(Font font) {
		super.setFont(font);
		if(expand!=null)
			expand.setFont(font);
	}
	@Override public void setForeground(Color foreground) {
		super.setForeground(foreground);
		if(expand!=null)
			expand.setForeground(foreground);
	}
	
	public Component getChild() { return child; }
	public void setChild(Component child) {
		collapse.add(this.child = child, BorderLayout.CENTER);
	}
	
	public void clearInfo() { this.info.removeAll(); }
	public void addInfo(Component component) { info.add(component); }
	public void addInfo(Action... actions) {
		for(Action action:actions) {
			JButton button = new JButton(action);
			button.setFocusable(false);	button.setBorderPainted(false);
			button.setContentAreaFilled(false);	button.setBorder(EMPTY_BORDER);
			addInfo(button);
		}
	}
	
	protected static class ArrowIcon implements Icon, SwingConstants {
		private static final float DB = -.06f;
		private int direction;
		private int size;
		private Color color;
		private BufferedImage arrowImage;

		public ArrowIcon(int direction) {
			this(direction, 10, null);
		}

		public ArrowIcon(int direction, Color color) {
			this(direction, 10, color);
		}

		public ArrowIcon(int direction, int size, Color color) {
			this.size = size;
			this.direction = direction;
			this.color = color;
		}

		public int getIconHeight() {
			return size;
		}

		public int getIconWidth() {
			return size;
		}

		public void paintIcon(Component c, Graphics g, int x, int y) {
			g.drawImage(getArrowImage(), x, y, c);
		}

		protected Image getArrowImage() {
			if (arrowImage == null) {
				arrowImage = Guitilities.createTranslucentImage(size, size);
				AffineTransform atx = direction != SOUTH ? new AffineTransform() : null;
				switch (direction) {
				case NORTH:
					atx.setToRotation(Math.PI, size / 2, size / 2);
					break;
				case EAST:
					atx.setToRotation(-(Math.PI / 2), size / 2, size / 2);
					break;
				case WEST:
					atx.setToRotation(Math.PI / 2, size / 2, size / 2);
				case SOUTH:
				default: { /* no xform */}
				}
				Graphics2D ig = (Graphics2D) arrowImage.getGraphics();
				if (atx != null) {
					ig.setTransform(atx);
				}
				int width = size;
				int height = size / 2 + 1;
				int xx = (size - width) / 2;
				int yy = (size - height + 1) / 2;

				Color base = color != null ? color : UIManager.getColor("controlDkShadow").darker();

				paintArrow(ig, base, xx, yy);
				paintArrowBevel(ig, base, xx, yy);
				paintArrowBevel(ig, Guitilities.deriveColorHSB(base, 0f, 0f, .20f), xx, yy + 1);
			}
			return arrowImage;
		}

		protected void paintArrow(Graphics2D g, Color base, int x, int y) {
			g.setColor(base);
			int len = size - 2;
			int xx = x;
			int yy = y - 1;
			while (len >= 2) {
				xx++;
				yy++;
				g.fillRect(xx, yy, len, 1);
				len -= 2;
			}
		}

		protected void paintArrowBevel(Graphics g, Color base, int x, int y) {
			int len = size;
			int xx = x;
			int yy = y;
			Color c2 = Guitilities.deriveColorHSB(base, 0f, 0f, (-DB) * (size / 2));
			while (len >= 2) {
				c2 = Guitilities.deriveColorHSB(c2, 0f, 0f, DB);
				g.setColor(c2);
				g.fillRect(xx, yy, 1, 1);
				g.fillRect(xx + len - 1, yy, 1, 1);
				len -= 2;
				xx++;
				yy++;
			}

		}
	}
}