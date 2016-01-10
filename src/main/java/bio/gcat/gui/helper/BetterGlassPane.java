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
package bio.gcat.gui.helper;

import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;

public class BetterGlassPane extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener {
	private static final long serialVersionUID = 1l;

	private JRootPane pane;
	
	public BetterGlassPane() {
		super(); setOpaque(false); setVisible(true);
		addMouseListener(this); addMouseMotionListener(this); addMouseWheelListener(this);
	}
	public BetterGlassPane(JRootPane pane) { this(); (this.pane=pane).setGlassPane(this); }

	@Override public void mouseClicked(MouseEvent event) { dispatchEvent(event);  }
	@Override public void mousePressed(MouseEvent event) { dispatchEvent(event); }
	@Override public void mouseReleased(MouseEvent event) { dispatchEvent(event); }
	@Override public void mouseEntered(MouseEvent event) { dispatchEvent(event); }
	@Override public void mouseExited(MouseEvent event) { dispatchEvent(event); }
    
	@Override public void mouseDragged(MouseEvent event) { dispatchEvent(event); }
	@Override public void mouseMoved(MouseEvent event) { dispatchEvent(event); }
    
	@Override public void mouseWheelMoved(MouseWheelEvent event) { dispatchEvent(event); }
	
	/**
	 * If someone sets a new cursor to the GlassPane
	 * we expect that he knows what he is doing
	 * and return the super.contains(x,y)
	 * otherwise we return false to respect the cursors
	 * for the underneath components
	 */
	public boolean contains(int x, int y) {
		Container container = pane.getContentPane();
		Point containerPoint = SwingUtilities.convertPoint(this, x, y, container);
		if (containerPoint.y>0) {
			Component component = SwingUtilities.getDeepestComponentAt(
				container, containerPoint.x, containerPoint.y);
			return component==null||component.getCursor()==Cursor.getDefaultCursor();
		} else return true;
	}
	
	private void dispatchEvent(MouseEvent event) {
		Container container = pane.getContentPane();
		Point glassPanePoint = event.getPoint(), containerPoint = SwingUtilities.convertPoint(this, glassPanePoint, container);
		if (containerPoint.y>0) {
			Component component = SwingUtilities.getDeepestComponentAt(
				container, containerPoint.x, containerPoint.y);
			if(component!=null) {
				Point componentPoint = SwingUtilities.convertPoint(this, glassPanePoint, component);
				MouseEvent newMouseEvent = !(event instanceof MouseWheelEvent)?
					new MouseEvent(component,event.getID(),event.getWhen(),event.getModifiers(),componentPoint.x,componentPoint.y,event.getClickCount(),event.isPopupTrigger(),event.getButton()):
					new MouseWheelEvent(component,event.getID(),event.getWhen(),event.getModifiers(),componentPoint.x,componentPoint.y,event.getXOnScreen(),event.getYOnScreen(),event.getClickCount(),event.isPopupTrigger(),((MouseWheelEvent)event).getScrollType(),((MouseWheelEvent)event).getScrollAmount(),((MouseWheelEvent)event).getWheelRotation(),((MouseWheelEvent)event).getPreciseWheelRotation());
				component.dispatchEvent(newMouseEvent);
			}
		}
	}
}