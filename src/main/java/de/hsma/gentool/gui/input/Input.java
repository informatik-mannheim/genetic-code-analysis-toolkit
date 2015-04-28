package de.hsma.gentool.gui.input;

import java.awt.Component;
import de.hsma.gentool.gui.editor.NucleicEditor;

public interface Input {	
	public String getName();
	
	public Component getComponent(NucleicEditor editor);
}