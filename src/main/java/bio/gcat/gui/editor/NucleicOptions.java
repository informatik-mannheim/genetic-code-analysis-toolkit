package bio.gcat.gui.editor;

import bio.gcat.nucleic.Acid;

public class NucleicOptions {
	public static enum EditorMode { SEQUENCE,SET }
	
	public int tupleLength = 3;
	public Acid defaultAcid = Acid.RNA;
	
	protected EditorMode editorMode = EditorMode.SEQUENCE;
}