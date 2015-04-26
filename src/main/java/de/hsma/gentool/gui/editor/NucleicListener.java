package de.hsma.gentool.gui.editor;

import java.util.Collection;
import java.util.EventListener;
import java.util.EventObject;
import de.hsma.gentool.nucleic.Tuple;

public interface NucleicListener extends EventListener {
	public void tuplesInsert(NucleicEvent event);
	public void tuplesRemoved(NucleicEvent event);
	public void tuplesChanged(NucleicEvent event);
	
	public static class NucleicEvent extends EventObject {
		private static final long serialVersionUID = 1l;
		
		private Collection<Tuple> tuples;
		
		public NucleicEvent(Object source, Collection<Tuple> tuples) {
			super(source);
			this.tuples = tuples;
		}
		
		public Collection<Tuple> getTuples() { return tuples; }
	}
}