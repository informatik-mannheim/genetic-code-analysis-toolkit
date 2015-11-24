package bio.gcat.gui.input;

import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import bio.gcat.nucleic.Base;
import bio.gcat.nucleic.Compound;
import bio.gcat.nucleic.Tuple;

public class TesseraTable extends CodonTable {
	private static final long serialVersionUID = 1l;
	
	private static final String NAME = "Tessera Table";
	
	@Override public String getName() { return NAME; }
	
	protected void createTuplePanels() {
		Base bases[] = DEFAULT_ACID.bases; int basesLength = bases.length;
		List<Tuple> tuples = new ArrayList<>(Compound.TESSERA.tuples);
		Collections.sort(tuples);
		
		setLayout(new GridLayout(basesLength, basesLength, 5, 5));
		for(int panelOffset=0;panelOffset<tuples.size();panelOffset+=basesLength)
			add(createTuplePanel(tuples, panelOffset, basesLength));
	}
}
