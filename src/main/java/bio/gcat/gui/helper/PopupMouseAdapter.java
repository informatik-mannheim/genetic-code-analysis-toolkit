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
package bio.gcat.gui.helper;

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