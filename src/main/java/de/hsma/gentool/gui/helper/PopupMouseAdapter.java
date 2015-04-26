package de.hsma.gentool.gui.helper;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPopupMenu;

public class PopupMouseAdapter extends MouseAdapter {
	private JPopupMenu menu;
	public PopupMouseAdapter(JPopupMenu menu) { this.menu = menu; }
	@Override public void mousePressed(MouseEvent event) { mousePopup(event); }
	@Override public void mouseReleased(MouseEvent event) { mousePopup(event); }
	private void mousePopup(MouseEvent event) {
		if(event.isPopupTrigger())
			menu.show(event.getComponent(),event.getX(),event.getY());
	}
}