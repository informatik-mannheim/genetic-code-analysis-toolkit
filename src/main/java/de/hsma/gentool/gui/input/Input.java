package de.hsma.gentool.gui.input;

import static de.hsma.gentool.nucleic.Base.*;
import java.awt.Color;
import java.awt.Component;
import java.util.Map;
import com.google.common.collect.ImmutableMap;
import de.hsma.gentool.gui.editor.NucleicEditor;
import de.hsma.gentool.nucleic.Base;

public interface Input {
	public static final Map<Base,Color> BASE_COLORS = ImmutableMap.of(
		URACILE,new Color(143,199,150), THYMINE,new Color(143,199,150), CYTOSINE,new Color(255,245,154),
		ADENINE,new Color(241,159,193), GUANINE,new Color(131,208,240));
	
	public String getName();
	
	public Component getComponent(NucleicEditor editor);
}