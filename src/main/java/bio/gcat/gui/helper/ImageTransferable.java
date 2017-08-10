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

import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;

public class ImageTransferable implements Transferable {
	private Image image;

	public ImageTransferable (Image image) { this.image = image; }

	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
		if(isDataFlavorSupported(flavor))
		     return image;
		else throw new UnsupportedFlavorException(flavor);
	}

	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return flavor == DataFlavor.imageFlavor; }
	public DataFlavor[] getTransferDataFlavors() {
		return new DataFlavor[] { DataFlavor.imageFlavor }; }
}
