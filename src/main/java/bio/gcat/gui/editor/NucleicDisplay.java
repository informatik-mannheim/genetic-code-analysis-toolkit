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
package bio.gcat.gui.editor;

import java.awt.Color;
import javax.swing.Icon;
import javax.swing.border.Border;
import javax.swing.border.MatteBorder;

public interface NucleicDisplay {
	public static Border DEFAULT_DISPLAY_BORDER = new MatteBorder(0,2,0,0,Color.LIGHT_GRAY);
	
	public String getLabel();
	public Icon getIcon();
	
	public boolean hasPreferredSize();
	public void setPreferredSize();
}