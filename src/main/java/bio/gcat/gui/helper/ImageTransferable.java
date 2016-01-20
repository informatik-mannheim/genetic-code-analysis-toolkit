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
