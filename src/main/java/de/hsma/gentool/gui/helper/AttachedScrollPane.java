package de.hsma.gentool.gui.helper;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.beans.Transient;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.ScrollPaneLayout;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class AttachedScrollPane extends JScrollPane {
	private static final long serialVersionUID = 1l;

	/**
	 * Identifies the area along the right side of the viewport between the
	 * upper right corner and the lower right corner.
	 */
	String ROW_FOOTER = "ROW_FOOTER";

	/**
	 * The row footer child. Default is <code>null</code>.
	 * @see #setRowFooter
	 */
	protected JViewport rowFooter;
	
	private ChangeListener viewportChange;
	
	public AttachedScrollPane() {
		super();
		adoptLayout();
	}
	public AttachedScrollPane(Component view,int vsbPolicy,int hsbPolicy) {
		super(view,vsbPolicy,hsbPolicy);
		adoptLayout();
	}
	public AttachedScrollPane(Component view) {
		super(view);
		adoptLayout();
	}
	public AttachedScrollPane(int vsbPolicy,int hsbPolicy) {
		super(vsbPolicy,hsbPolicy);
		adoptLayout();
	}

	private void adoptLayout() {
		viewportChange = new ChangeListener() {
			@Override public void stateChanged(ChangeEvent e) {
				if(rowFooter!=null) {
					Point master = ((JViewport)e.getSource()).getViewPosition(), slave = rowFooter.getViewPosition();
					if(master.y!=slave.y)
						rowFooter.setViewPosition(new Point(slave.x, master.y));
				}
			}
		};
		
		setLayout(new ScrollPaneLayout() {
			private static final long serialVersionUID = 1l;

			/**
			 * The row footer child. Default is <code>null</code>.
			 * @see JScrollPane#setRowFooter
			 */
			protected JViewport rowFoot;

			@Override public void syncWithScrollPane(JScrollPane sp) {
				super.syncWithScrollPane(sp);
				rowFoot = ((AttachedScrollPane)sp).getRowFooter();
			}

			@Override public void addLayoutComponent(String s,Component c) {
				if(s.equals(ROW_FOOTER))
					rowFoot = (JViewport)addSingletonComponent(rowFoot, c);
				else super.addLayoutComponent(s,c);
			}

			@Override public void removeLayoutComponent(Component c) {
				if(c==rowFoot)
					rowFoot = null;
				else super.removeLayoutComponent(c);
			}

			/**
			 * Returns the <code>JViewport</code> object that is the row footer.
			 * @return the <code>JViewport</code> object that is the row footer
			 * @see JScrollPane#getRowFooter
			 */
			@SuppressWarnings("unused") public JViewport getRowFooter() {
				return rowFoot;
			}

			@Override public Dimension preferredLayoutSize(Container parent) {
				Dimension prefSize = super.preferredLayoutSize(parent);
				int prefWidth = prefSize.width;
				if((rowFoot!=null)&&rowFoot.isVisible()) {
					prefWidth += rowFoot.getPreferredSize().width;
				}
				return new Dimension(prefWidth, prefSize.height);
			}
			
			@Override public Dimension minimumLayoutSize(Container parent) {
				Dimension minSize = super.minimumLayoutSize(parent);
				int minWidth = minSize.width, minHeight = minSize.height;
				if(rowFoot!=null&&rowFoot.isVisible()) {
          Dimension size = rowFoot.getMinimumSize();
          minWidth += size.width;
          minHeight = Math.max(minHeight, size.height);
				}
				return new Dimension(minWidth, minHeight);
			}
			
			@Override public void layoutContainer(Container parent) {
				super.layoutContainer(parent);
				
				// copied from super.layoutContainer
				JScrollPane scrollPane = (JScrollPane)parent;
        Rectangle availR = scrollPane.getBounds();
        availR.x = availR.y = 0;

        Insets insets = parent.getInsets();
        availR.x = insets.left;
        availR.y = insets.top;
        availR.width -= insets.left + insets.right;
        availR.height -= insets.top + insets.bottom;
        
				Rectangle rowHeadR = rowHead.getBounds(), rowFootR = new Rectangle(rowHeadR);
				if(rowFoot!=null&&rowFoot.isVisible()) {
					rowFootR.width = rowFoot.getPreferredSize().width;
					rowFootR.x = rowHeadR.x+availR.width;
					rowFoot.setBounds(rowFootR);
				}
			}
		});
	}
	
	// Overwrite to support layoutContainer (substract rowFoot width from availR)
	@Override public Rectangle getBounds() {
		Rectangle boundsR = super.getBounds();
		if(rowFooter!=null&&rowFooter.isVisible()) {
			boundsR.width -= rowFooter.getPreferredSize().width;
		}
		return boundsR;
	}
	
	@Override public Rectangle getViewportBorderBounds() {
		Rectangle borderR = super.getViewportBorderBounds();

		/* If there's a visible row footer remove the space it needs
		 * from the right of border.
		 */
		JViewport rowFoot = getRowFooter();
		if(rowFoot!=null&&rowFoot.isVisible()) {
			int rowFootWidth = rowFoot.getWidth();
			//border.x += rowFootWidth; //BETTER: Only do if NOT leftToRight 
			borderR.width -= rowFootWidth;
		}

		return borderR;
	}

	/**
	 * Returns the row footer.
	 * @return the <code>rowFooter</code> property
	 * @see #setRowFooter
	 */
	@Transient public JViewport getRowFooter() {
		return rowFooter;
	}

	@Override public void setRowHeader(JViewport rowHeader) {
		JViewport oldRowHeader = getRowHeader();
		super.setRowHeader(rowHeader);
		if(oldRowHeader!=null)
			oldRowHeader.removeChangeListener(viewportChange);
		if(rowHeader!=null)
			rowHeader.addChangeListener(viewportChange);
	}
	
	/**
	 * Removes the old rowFooter, if it exists; if the new rowFooter
	 * isn't <code>null</code>, syncs the y coordinate of its
	 * viewPosition with
	 * the viewport (if there is one) and then adds it to the scroll pane.
	 * <p>
	 * Most applications will find it more convenient to use
	 * <code>setRowFooterView</code>
	 * to add a row footer component and its viewport to the scroll pane.
	 *
	 * @param rowFooter the new row footer to be used; if <code>null</code>
	 *          the old row footer is still removed and the new rowFooter
	 *          is set to <code>null</code>
	 * @see #getRowFooter
	 * @see #setRowFooterView
	 *
	 * @beaninfo
	 *        bound: true
	 *       expert: true
	 *  description: The row footer child for this scrollpane
	 */
	public void setRowFooter(JViewport rowFooter) {
		JViewport old = getRowFooter();
		this.rowFooter = rowFooter;
		if(rowFooter!=null) {
			add(rowFooter, ROW_FOOTER);
		}	else if(old!=null) {
			remove(old);
		}
		firePropertyChange("rowFooter", old, rowFooter);
		revalidate();
		repaint();
	}

	/**
	 * Creates a row-footer viewport if necessary, sets
	 * its view and then adds the row-footer viewport
	 * to the scrollpane.  For example:
	 * <pre>
	 * JScrollPane scrollpane = new JScrollPane();
	 * scrollpane.setViewportView(myBigComponentToScroll);
	 * scrollpane.setRowFooterView(myBigComponentsRowFooter);
	 * </pre>
	 *
	 * @see #setRowFooter
	 * @see JViewport#setView
	 * @param view the component to display as the row footer
	 */
	public void setRowFooterView(Component view) {
		if(getRowFooter()==null) {
			setRowFooter(createViewport());
		}
		getRowFooter().setView(view);
  }
}