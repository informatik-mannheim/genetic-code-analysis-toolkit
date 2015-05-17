package de.hsma.gentool.gui;

import static de.hsma.gentool.Utilities.*;
import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class GenBDA extends JFrame {
	private static final long serialVersionUID = 1l;
	
	public GenBDA() {		
		super("Genetic Code BDA Editor (GenBDA)");
		setIconImage(new ImageIcon(getResource("application_osx_terminal.png")).getImage());
		setMinimumSize(new Dimension(660,400));
		setPreferredSize(new Dimension(1020,400));
		setSize(getPreferredSize());
		setLocationByPlatform(true);
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

		/*menubar = new JMenuBar(); menu = new JMenu[4];
		menubar.add(menu[0] = new JMenu("File"));
		menubar.add(menu[1] = new JMenu("Edit"));
		menubar.add(menu[2] = new JMenu("Window"));
		menubar.add(menu[3] = new JMenu("Help"));
		setJMenuBar(menubar);
		
		menu[0].add(createMenuItem("Import...", "table_go.png", KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.CTRL_DOWN_MASK), ACTION_IMPORT, this));
		menu[0].add(createMenuItem("Export...", "table_save.png", ACTION_EXPORT, this));
		menu[0].add(createMenuItem("Execute", "table_lightning.png", KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.CTRL_DOWN_MASK), ACTION_EXECUTE, this));
		menu[0].add(createSeparator());
		menu[0].add(createMenuItem("Close", "control-power.png", ACTION_CLOSE, this));
		menu[1].add(createMenuText("Actions:"));
		menu[1].add(createMenuItem("Add", KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK), ACTION_ACTION_ADD, this));
		menu[1].add(createMenuItem("Edit...", KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_DOWN_MASK), ACTION_ACTION_EDIT, this));
		menu[1].add(createMenuItem("Remove", KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), ACTION_ACTION_REMOVE, this));
		menu[1].add(createMenuItem("Clear", ACTION_ACTIONS_CLEAR, this));
		menu[1].add(seperateMenuItem(createMenuItem("Move Up", KeyStroke.getKeyStroke(KeyEvent.VK_UP, InputEvent.CTRL_DOWN_MASK), ACTION_ACTION_MOVE_UP, this)));
		menu[1].add(createMenuItem("Move Down", KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, InputEvent.CTRL_DOWN_MASK), ACTION_ACTION_MOVE_DOWN, this));
		menu[1].add(createSeparator());
		menu[1].add(createMenuText("Sequences:"));
		menu[1].add(createMenuItem("Remove", "table_row_delete.png", KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), ACTION_SEQUENCES_REMOVE, this));
		menu[1].add(createMenuItem("Clear", ACTION_SEQUENCES_CLEAR, this));
		menu[2].add(createMenuItem("Preferences", ACTION_PREFERENCES, this));
		menu[3].add(createMenuItem("About GenBatch", "calculator.png", ACTION_ABOUT, this));
		for(String action:new String[]{ACTION_ACTION_EDIT,ACTION_ACTION_REMOVE,ACTION_ACTIONS_CLEAR,ACTION_ACTION_MOVE_UP,ACTION_ACTION_MOVE_DOWN,ACTION_SEQUENCES_REMOVE,ACTION_SEQUENCES_CLEAR})
			getMenuItem(menubar,action).setEnabled(false);
		getMenuItem(menubar,ACTION_PREFERENCES).setEnabled(false);
		registerKeyStroke(getRootPane(),KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0),"remove",new ActionListener() {
			@Override public void actionPerformed(ActionEvent event) {
				 if(actionPanel.list.hasFocus()) removeAction();
				else if(sequenceList.hasFocus()) removeSequences();
			}
		});
		
		JButton execute = createToolbarButton("Execute Batch", "table_lightning.png", ACTION_EXECUTE, this);
		execute.setText(execute.getToolTipText());
		execute.setFont(execute.getFont().deriveFont(Font.ITALIC));
		
		toolbar = new JToolBar[1];
		toolbar[0] = new JToolBar("File");
		toolbar[0].add(createToolbarButton("Import", "table_go.png", ACTION_IMPORT, this));
		toolbar[0].add(createToolbarButton("Export", "table_save.png", ACTION_EXPORT, this));
		toolbar[0].addSeparator();
		toolbar[0].add(execute);*/
		
		scala.collection.immutable.HashSet<net.gumbix.geneticcode.dich.Compound> tuples =
  		new scala.collection.immutable.HashSet<>();
  	tuples = tuples.$plus(net.gumbix.geneticcode.dich.Adenine$.MODULE$).$plus(net.gumbix.geneticcode.dich.Uracil$.MODULE$);
  	
  	net.gumbix.geneticcode.dich.BinaryDichotomicAlgorithm bda =
  		new net.gumbix.geneticcode.dich.BinaryDichotomicAlgorithm(0,1,
  			new scala.Tuple2<net.gumbix.geneticcode.dich.Compound,net.gumbix.geneticcode.dich.Compound>(
  				net.gumbix.geneticcode.dich.Adenine$.MODULE$, net.gumbix.geneticcode.dich.Uracil$.MODULE$),
  				tuples);
  	
  	scala.collection.immutable.List<?> bdas =
  		scala.collection.immutable.Nil$.MODULE$;
  	bdas = bdas.$colon$colon(net.gumbix.geneticcode.dich.RumerBDA$.MODULE$);
  	bdas = bdas.$colon$colon(net.gumbix.geneticcode.dich.ParityBDA$.MODULE$);
  	bdas = bdas.$colon$colon(net.gumbix.geneticcode.dich.AntiCodonBDA$.MODULE$);
    bdas = bdas.$colon$colon(bda);
    	    
    @SuppressWarnings("unchecked") net.gumbix.geneticcode.dich.ct.ClassTable ct =
    	new net.gumbix.geneticcode.dich.ct.ClassTable(
    		(scala.collection.immutable.List<net.gumbix.geneticcode.dich.Classifier<Object>>)bdas,
				net.gumbix.geneticcode.dich.IUPAC.STANDARD(),
				new net.gumbix.geneticcode.dich.IdAminoAcidProperty(1));
    ct.codon2class(); //TODO to determine classes during split
    
    getContentPane().add(new net.gumbix.geneticcode.dich.ui.JGeneticCodeTable(ct), BorderLayout.CENTER);
	}
}
