package de.hsma.gentool.gui.editor;

public interface NucleicDisplay extends NucleicListener {
	public String getLabel();
	
	public boolean hasPreferredSize();
	public void setPreferredSize();
}