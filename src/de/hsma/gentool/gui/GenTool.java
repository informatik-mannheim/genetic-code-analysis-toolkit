package de.hsma.gentool.gui;

import static de.hsma.gentool.Utilities.*;
import static de.hsma.gentool.batch.Action.TaskAttribute.*;
import static de.hsma.gentool.gui.helper.Guitilities.*;
import static de.hsma.gentool.nucleic.Acid.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.font.TextAttribute;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.EventListenerList;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Document;
import javax.swing.text.NumberFormatter;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import de.hsma.gentool.Configurable;
import de.hsma.gentool.Option;
import de.hsma.gentool.Parameter;
import de.hsma.gentool.Utilities.DefiniteFuture;
import de.hsma.gentool.Utilities.RememberFileChooser;
import de.hsma.gentool.batch.Action;
import de.hsma.gentool.batch.Action.Task;
import de.hsma.gentool.batch.Action.TaskAttribute;
import de.hsma.gentool.gui.editor.NucleicDocument;
import de.hsma.gentool.gui.editor.NucleicEditor;
import de.hsma.gentool.gui.editor.NucleicListener;
import de.hsma.gentool.gui.helper.FoldingPanel;
import de.hsma.gentool.gui.input.CodonTable;
import de.hsma.gentool.gui.input.Input;
import de.hsma.gentool.log.InjectionLogger;
import de.hsma.gentool.log.Logger;
import de.hsma.gentool.nucleic.Acid;
import de.hsma.gentool.nucleic.Tuple;
import de.hsma.gentool.operation.Operation;
import de.hsma.gentool.operation.analysis.Analysis;
import de.hsma.gentool.operation.analysis.Analysis.Result;
import de.hsma.gentool.operation.split.Split;
import de.hsma.gentool.operation.test.Test;
import de.hsma.gentool.operation.transformation.Transformation;

public class GenTool extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1l;
	
	private static final String VERSION = "1.2";
	private static final String
		ACTION_NEW = "new",
		ACTION_OPEN = "open",
		ACTION_SAVE = "save",
		ACTION_SAVE_AS = "save_as",
		ACTION_EXIT = "close",
		ACTION_UNDO = "undo",
		ACTION_REDO = "redo",
		ACTION_CUT = "cut",
		ACTION_COPY = "copy",
		ACTION_PASTE = "paste",
		ACTION_DELETE = "delete",
		ACTION_FIND = "find",
		ACTION_FIND_NEXT = "find_next",
		ACTION_REPLACE = "replace",
		ACTION_SPLIT = "split",
		ACTION_GO_TO = "go_to",
		ACTION_SELECT_ALL = "select_all",
		ACTION_CLEAR_ALL = "clear_all",
		ACTION_NEW_WINDOW = "new_window",
		ACTION_COPY_WINDOW = "copy_window",
		ACTION_TOGGLE_TOOLBAR = "toggle_toolbar",
		ACTION_SHOW_BATCH = "open_batch",
		ACTION_ADD_TO_BATCH = "add_to_batch",
		ACTION_PREFERENCES = "preferences",
		ACTION_ABOUT = "about";
	
	private static final List<GenTool> TOOLS = new ArrayList<>();
	private static final GenBatch BATCH;
	static {
		UIManager.put("FileChooser.readOnly", Boolean.TRUE);
		try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
		catch(Exception e) { e.printStackTrace(); };
		BATCH = new GenBatch();
	}
	
	protected final ListeningExecutorService service = MoreExecutors.listeningDecorator(Executors.newSingleThreadExecutor());
	protected final List<ListenableFuture<Collection<Tuple>>> futures = Collections.synchronizedList(new LinkedList<>());
	protected final Map<TaskAttribute,Object> attributes;
	protected int futureModifications;
	
	private NucleicEditor editor;
	
	private JSplitPane[] split;
	private JMenuBar menubar;
	private JMenu[] menu;
	private JPanel toolbars, bottom;
	private JToolBar[] toolbar;
	private JLabel status;
	private JButton cancel;
	
	private CatalogPanel catalog;
	private FindDialog find;
	private ConsolePane console;
	
	private File currentFile;
	
	public GenTool() {
		super("Genetic Code Tool (GenTool)"); TOOLS.add(this);
		setIconImage(new ImageIcon(getResource("icon.png")).getImage());
		setMinimumSize(new Dimension(660,400));
		setPreferredSize(new Dimension(1120,680));
		setSize(getPreferredSize());
		setLocationByPlatform(true);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override public void windowClosing(WindowEvent event) {
				closeTool();
			}
			@Override public void windowClosed(WindowEvent event) {
				service.shutdownNow();
				TOOLS.remove(GenTool.this);
				if(TOOLS.isEmpty()) {
					if(BATCH!=null)
						BATCH.dispose();
				}
			}
		});
		
		attributes = new HashMap<>();
		attributes.put(TEST_CRITERIA,TEST_CRITERIA_BREAK_IF_FALSE);
		attributes.put(ANALYSIS_HANDLER,new Analysis.Handler() {
			@Override public void handle(Result result) {
				console.log("Analysis \"%s\" result: %s",result.getAnalysis().getName(),result.toString());
			}
		});
		attributes.put(SPLIT_PICK,new Split.Pick() {
			@Override public Collection<Tuple> pick(List<Collection<Tuple>> split) {
				     if(split==null||split.isEmpty()) return null;
				else if(split.size()==1) return split.get(0);
				else {
					DefiniteFuture<Collection<Tuple>> future = new DefiniteFuture<Collection<Tuple>>();
					
					JDialog dialog = new JDialog(GenTool.this,"Pick Sequence",true);
					dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					
					GroupLayout layout = new GroupLayout(dialog.getContentPane());
					dialog.getContentPane().setLayout(layout);
					layout.setAutoCreateGaps(true);
					layout.setAutoCreateContainerGaps(true);
					
					JLabel label = new JLabel("Sequences:");
					
					@SuppressWarnings("unchecked") JComboBox<Collection<Tuple>> combo = new JComboBox<Collection<Tuple>>(split.toArray((Collection<Tuple>[])new Collection<?>[0]));
					combo.setRenderer(new ListCellRenderer<Collection<Tuple>>() {
						private JLabel label = new JLabel() {
							private static final long serialVersionUID = 1l;
							private final Color lineColor = new Color(0,0,0,64);
							@Override public void paint(Graphics graphics) {
								super.paint(graphics);
								int defaultTupleLength = editor.getDocument().getDefaultTupleLength();
								if(defaultTupleLength>0) {
									graphics.setColor(lineColor);
									int width=getFontMetrics(getFont()).charWidth('0'), tupleWidth = width*defaultTupleLength;
									for(int left=getInsets().left+width/2+tupleWidth; left<combo.getWidth(); left += tupleWidth + width)
										graphics.drawLine(left, 0, left, combo.getHeight());
								}
							}
						};
						
						@Override public Component getListCellRendererComponent(JList<? extends Collection<Tuple>> list,Collection<Tuple> tuples,int index,boolean isSelected,boolean cellHasFocus) {						
					    label.setText(Tuple.joinTuples(tuples));
					    
					    if(isSelected) {
					    	label.setBackground(list.getSelectionBackground());
					    	label.setForeground(list.getSelectionForeground());
					    } else {
					    	label.setBackground(list.getBackground());
					    	label.setForeground(list.getForeground());
					    }
					    label.setEnabled(list.isEnabled());
					    label.setFont(list.getFont());
					    label.setOpaque(true);
	
					    return label;
						}
					});
					combo.setFont(new Font(Font.MONOSPACED,Font.PLAIN,12));
					
					JButton pick = new JButton("Pick");
					pick.addActionListener(new ActionListener() {
						@Override public void actionPerformed(ActionEvent e) { future.set(combo.getModel().getElementAt(combo.getSelectedIndex())); dialog.dispose(); }
					});
					
					JButton cancel = new JButton("Cancel");
					cancel.addActionListener(new ActionListener() {
						@Override public void actionPerformed(ActionEvent e) { dialog.dispose(); }
					});
					
					layout.setHorizontalGroup(
						layout.createSequentialGroup()
							.addComponent(label)
							.addGroup(layout.createParallelGroup(Alignment.TRAILING)
									.addComponent(combo, 285, 285, GroupLayout.DEFAULT_SIZE)
									.addGroup(layout.createSequentialGroup()
										.addComponent(pick)
										.addComponent(cancel)	
									)									
							)
					);
	
					layout.setVerticalGroup(
						layout.createSequentialGroup()
							.addGroup(layout.createParallelGroup(Alignment.CENTER)
								.addComponent(label)
								.addComponent(combo)
							)
							.addGroup(layout.createParallelGroup()
								.addComponent(pick)
								.addComponent(cancel)
							)
					);
					
					dialog.pack();
					dialog.setResizable(false);
					dialog.setLocationRelativeTo(GenTool.this);
					dialog.setVisible(true);
					
					return future.get();
				}
			}
		});
		
		menubar = new JMenuBar(); menu = new JMenu[4];
		menubar.add(menu[0] = new JMenu("File"));
		menubar.add(menu[1] = new JMenu("Edit"));
		menubar.add(menu[2] = new JMenu("Window"));
		menubar.add(menu[3] = new JMenu("Help"));
		setJMenuBar(menubar);

		menu[0].add(createMenuItem("New", "document.png", KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK), ACTION_NEW, this));
		menu[0].add(createMenuItem("Open...", "folder-horizontal-open.png", KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK), ACTION_OPEN, this));
		menu[0].add(createMenuItem("Save", "disk.png", KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK), ACTION_SAVE, this));
		menu[0].add(createMenuItem("Save As...", "disk--arrow.png", ACTION_SAVE_AS, this));
		menu[0].add(createSeparator());
		menu[0].add(createMenuItem("Exit", "control-power.png", ACTION_EXIT, this));
		menu[1].add(createMenuItem("Undo", "arrow_undo.png", KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK), ACTION_UNDO, this));
		menu[1].add(createMenuItem("Redo", "arrow_redo.png", KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK), ACTION_REDO, this));
		menu[1].add(createSeparator());
		menu[1].add(createMenuItem("Cut", "cut.png", KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK), ACTION_CUT, this));
		menu[1].add(createMenuItem("Copy", "page_copy.png", KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK), ACTION_COPY, this));
		menu[1].add(createMenuItem("Paste", "page_paste.png", KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK), ACTION_PASTE, this));
		menu[1].add(createMenuItem("Delete", "cross.png", KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), ACTION_DELETE, this));
		menu[1].add(createSeparator());
		menu[1].add(createMenuItem("Find...", KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK), ACTION_FIND, this));
		menu[1].add(createMenuItem("Find Next", KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0), ACTION_FIND_NEXT, this));
		menu[1].add(createMenuItem("Replace...", KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK), ACTION_REPLACE, this));
		menu[1].add(createMenuItem("Split...", ACTION_SPLIT, this));
		menu[1].add(createMenuItem("Go To...", KeyStroke.getKeyStroke(KeyEvent.VK_G, InputEvent.CTRL_DOWN_MASK), ACTION_GO_TO, this));
		menu[1].add(createSeparator());
		menu[1].add(createMenuItem("Select All", KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK), ACTION_SELECT_ALL, this));
		menu[1].add(createMenuItem("Clear All", ACTION_CLEAR_ALL, this));
		menu[2].add(createMenuItem("New Window", ACTION_NEW_WINDOW, this));
		menu[2].add(createMenuItem("Copy Window", ACTION_COPY_WINDOW, this));
		menu[2].add(createMenuItem("Hide Toolbar", ACTION_TOGGLE_TOOLBAR, this));
		menu[2].add(createSeparator());
    //menu[2].add(createMenuItem("BDA", "action_bda", this)); //TODO: BCA
		menu[2].add(createSeparator());
		menu[2].add(createMenuItem("Open GenBatch", "calculator.png", KeyStroke.getKeyStroke(KeyEvent.VK_B, InputEvent.CTRL_DOWN_MASK), ACTION_SHOW_BATCH, this));
		menu[2].add(createMenuItem("Add Sequence", "calculator_add.png", ACTION_ADD_TO_BATCH, this));
    menu[2].add(createSeparator());
		menu[2].add(createMenuItem("Preferences", ACTION_PREFERENCES, this));
		menu[3].add(createMenuItem("About GenTool", "icon.png", ACTION_ABOUT, this));
		for(String action:new String[]{ACTION_SAVE,ACTION_UNDO,ACTION_REDO,ACTION_CUT,ACTION_COPY,ACTION_DELETE})
			getMenuItem(menubar,action).setEnabled(false);
		getMenuItem(menubar,ACTION_PREFERENCES).setEnabled(false);
		
		toolbar = new JToolBar[1];
		toolbar[0] = new JToolBar("File");
		toolbar[0].add(createToolbarButton("New File", "document.png", ACTION_NEW, this));
		toolbar[0].add(createToolbarButton("Open File", "folder-horizontal-open.png", ACTION_OPEN, this));
		toolbar[0].add(createToolbarButton("Save File", "disk.png", ACTION_SAVE, this));
		toolbar[0].add(createToolbarButton("Save As File", "disk--arrow.png", ACTION_SAVE_AS, this));
		toolbar[0].addSeparator();
		toolbar[0].add(createToolbarButton("Open GenBatch", "calculator.png", ACTION_SHOW_BATCH, this));
		toolbar[0].add(createToolbarButton("Add Sequence", "calculator_add.png", ACTION_ADD_TO_BATCH, this));
		toolbar[0].addSeparator();
		toolbar[0].add(createToolbarButton("Exit", "control-power.png", ACTION_EXIT, this));
		
		toolbars = new JPanel(new FlowLayout(FlowLayout.LEADING));
		for(JToolBar bar:toolbar)
			toolbars.add(bar);
		add(toolbars,BorderLayout.NORTH);
		
		editor = new NucleicEditor();
		editor.getTextPane().addCaretListener(new CaretListener() {
			@Override public void caretUpdate(CaretEvent event) {
				boolean selection = event.getDot()!=event.getMark();
				for(String action:new String[]{ACTION_CUT,ACTION_COPY,ACTION_DELETE})
					getMenuItem(menubar,action).setEnabled(selection);
			}
		});
		editor.getTextPane().getDocument().addDocumentListener(new DocumentListener() {
			@Override public void removeUpdate(DocumentEvent event) { changedUpdate(event); }			
			@Override public void insertUpdate(DocumentEvent event) { changedUpdate(event); }			
			@Override public void changedUpdate(DocumentEvent event) {
				boolean empty = editor.getText().isEmpty();
				for(String action:new String[]{ACTION_FIND,ACTION_FIND_NEXT,ACTION_REPLACE,ACTION_SPLIT})
					getMenuItem(menubar,action).setEnabled(!empty);
			}
		});
		editor.addNucleicListener(new NucleicListener() {
			@Override public void tuplesInsert(NucleicEvent event) { /* nothing to do here */ }
			@Override public void tuplesRemoved(NucleicEvent event) { /* nothing to do here */ }
			@Override public void tuplesChanged(NucleicEvent event) {
				getMenuItem(menubar,ACTION_UNDO).setEnabled(editor.canUndo());
				getMenuItem(menubar,ACTION_REDO).setEnabled(editor.canRedo());
			}
		});
		
		find = new FindDialog();
		
		split = new JSplitPane[2];
		split[0] = createSplitPane(JSplitPane.VERTICAL_SPLIT,false,false,0.725,editor,new JScrollPane(console=new ConsolePane()));
		split[1] = createSplitPane(JSplitPane.HORIZONTAL_SPLIT,false,true,360,0.195,new JScrollPane(catalog=createCatalog()),split[0]);
		add(split[1],BorderLayout.CENTER);
		
		add(bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT)),BorderLayout.SOUTH);
		
		status = new JLabel();
		status.setBorder(new EmptyBorder(0,5,0,5));
		status.setHorizontalAlignment(JLabel.RIGHT);
		bottom.add(status);
		
		cancel = new JButton(new ImageIcon(getResource("cancel.png")));
		cancel.setFocusable(false); cancel.setBorderPainted(false);
		cancel.setContentAreaFilled(false); cancel.setBorder(EMPTY_BORDER);
		cancel.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent event) {
				try { futures.get(0).cancel(true); }
				catch(IndexOutOfBoundsException e) { /* nothing to do here */ }
			}
		});
		bottom.add(cancel);
		
		updateStatus();
		SwingUtilities.invokeLater(new Runnable() {
			@Override public void run() {
				editor.requestFocus();
			}
		});
	}
	
	protected CatalogPanel createCatalog() {
		catalog = new CatalogPanel();
		
		// Groups for operations
		for(Entry<String,Collection<Class<? extends Operation>>> group:Operation.getGroups().asMap().entrySet())
			catalog.addPage(createGroupPanel(group.getValue()),group.getKey(),false);
		
		return catalog;
	}
	
	private Component createGroupPanel(Collection<Class<? extends Operation>> operations) {
		Box group = Box.createVerticalBox();
		for(Class<? extends Operation> operation:operations)
			addOperation(group, operation);
		return group;
	}
	private void addOperation(Container container, final Class<? extends Operation> operation) {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(new EmptyBorder(0,5,0,5));
		
		final Parameter[] parameters = Operation.getParameters(operation);
		final Map<Parameter,Option.Component> components = new HashMap<>();
		final String name = Operation.getName(operation);
		
		JButton button = new JButton(name);
		button.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent event) {
				List<Object> values = new ArrayList<>(parameters!=null?parameters.length:0);
				if(parameters!=null) for(Parameter parameter:parameters)
					values.add(components.get(parameter).getValue());
				submitOperation(operation,values.toArray());
			}
		});
		panel.add(button, BorderLayout.WEST);
		
		Box parameterBox = Box.createHorizontalBox();
		if(parameters!=null) for(Parameter parameter:parameters) {
			Option.Component component = parameter.new Component();
			components.put(parameter,component);
			parameterBox.add(component);
		}
		panel.add(parameterBox, BorderLayout.CENTER);

		JButton batch = new JButton(new ImageIcon(getResource("calculator_add_small.png")));
		batch.setToolTipText("Add operation to batch");
		batch.setFocusable(false); batch.setBorderPainted(false);
		batch.setContentAreaFilled(false); batch.setBorder(EMPTY_BORDER);
		batch.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				List<Object> values = new ArrayList<>(parameters!=null?parameters.length:0);
				if(parameters!=null) for(Parameter parameter:parameters)
					values.add(components.get(parameter).getValue());
				addBatchOperation(operation,values.toArray());
			}
		});
		panel.add(batch, BorderLayout.EAST);
		
		container.add(panel);
	}
	public void addBatchOperation(Class<? extends Operation> operation,Object... values) {
		BATCH.addOperation(operation,values);
		showBatch();
	}
	
	@Override public void actionPerformed(ActionEvent event) {
		String action;
		switch(action=event.getActionCommand()) {
		case ACTION_NEW: newFile(); break;
		case ACTION_OPEN: openFile(); break;
		case ACTION_SAVE: saveFile(); break;
		case ACTION_SAVE_AS: saveFileAs(); break;
		case ACTION_EXIT: closeTools(); break;
		case ACTION_UNDO: undoEdit(); break;
		case ACTION_REDO: redoEdit(); break;
		case ACTION_CUT: cutText(); break;
		case ACTION_COPY: copyText(); break;
		case ACTION_PASTE: pasteText(); break;
		case ACTION_DELETE: deleteText(); break;
		case ACTION_FIND: findText(); break;
		case ACTION_FIND_NEXT: findNext(); break;
		case ACTION_REPLACE: replaceText(); break;
		case ACTION_SPLIT: splitText(); break;
		case ACTION_GO_TO: goToTuple(); break;
		case ACTION_SELECT_ALL: selectText(); break;
		case ACTION_CLEAR_ALL: clearText(); break;
		case ACTION_NEW_WINDOW: newTool(); break;
		case ACTION_COPY_WINDOW: copyTool(); break;
		case ACTION_TOGGLE_TOOLBAR: toggleToolbar(); break;
		case ACTION_SHOW_BATCH: showBatch(); break;
		case ACTION_ADD_TO_BATCH: addToBatch(); break;
		case ACTION_PREFERENCES: showPreferences(); break;
		case ACTION_ABOUT: showAbout(); break;
	  //case "action_bda": //TODO: BCA
	  	/*scala.collection.immutable.HashSet tuple =
      	new scala.collection.immutable.HashSet<>();*/
	  	
	      /*scala.collection.immutable.HashSet tuple =
	              new scala.collection.immutable.HashSet<>().$plus(Adenine$.MODULE$).$plus(Uracil$.MODULE$);
	      List bdas = new ArrayList();
	      bdas.add(new BinaryDichotomicAlgorithm(0, 1,
	              new Tuple2<Compound, Compound>(Adenine$.MODULE$, Uracil$.MODULE$),
	              tuple
	              ));
	
	      scala.collection.immutable.List bdasList = Nil$.MODULE$;
	
	      for(Object bda:bdas) {
	          bdasList = bdasList.$colon$colon(bda);
	      }*/
	
	      //ClassTable ct = new ClassTable(bdasList, IUPAC.STANDARD(), IdStandardAminoAcidProperty$.MODULE$);
	      //ct.codon2class
	
	      /*JFrame frame = new JFrame("Class Table Viewer");
	      frame.getContentPane().add(new JGeneticCodeTable(ct), BorderLayout.CENTER);
	      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	      frame.setPreferredSize(new Dimension(700, 500));
	      frame.pack();
	      frame.setVisible(true);
	
	      break;*/
		default: System.err.println(String.format("Action %s not implemented.", action)); }
	}

	public ListenableFuture<Collection<Tuple>> submitOperation(Class<? extends Operation> operation) { return submitAction(new Action(operation)); }
	public ListenableFuture<Collection<Tuple>> submitOperation(Class<? extends Operation> operation,Object... values) { return submitAction(new Action(operation,attributes,values)); }
	public ListenableFuture<Collection<Tuple>> submitOperation(Class<? extends Operation> operation,Map<TaskAttribute,Object> attributes,Object... values) { return submitAction(new Action(operation,attributes,values)); }
	protected ListenableFuture<Collection<Tuple>> submitAction(Action action) {
		Class<? extends Operation> operation = action.getOperation(); String name = Operation.getName(operation); 
		ListenableFuture<Collection<Tuple>> future = submitTask(action.new Task(editor.getTuples()));
		Futures.addCallback(future,new FutureCallback<Collection<Tuple>>() {
			@Override public void onSuccess(Collection<Tuple> tuples) {
				if(Transformation.class.isAssignableFrom(operation)||Split.class.isAssignableFrom(operation))
					editor.setTuples(tuples);
				else if(Test.class.isAssignableFrom(operation))
					console.log("Sequence does apply to test \"%s\"", name);
			}
			@Override public void onFailure(Throwable thrown) {
				if(Test.class.isAssignableFrom(operation)&&thrown instanceof Test.Failed)
					   console.log("Sequence does NOT apply to \"%s\"", name);
				else console.log("Exception in operation \"%s\": %s", name, Optional.ofNullable(thrown.getMessage()).orElse("Unknown cause"));
			}
		});
		if(Transformation.class.isAssignableFrom(operation)||Split.class.isAssignableFrom(operation)) {
			futureModifications++; Futures.addCallback(future,new FutureCallback<Collection<Tuple>>() {
				@Override public void onFailure(Throwable thrown) { onSuccess(null); }
				@Override public void onSuccess(Collection<Tuple> tuples) {
					futureModifications--;
					updateStatus();
				}
			});
			updateStatus();
		}
		return future;
	}
	protected ListenableFuture<Collection<Tuple>> submitTask(Task task) {
		ListenableFuture<Collection<Tuple>> future = service.submit(InjectionLogger.injectLogger(console,task));
		futures.add(future); Futures.addCallback(future,new FutureCallback<Collection<Tuple>>() {
			@Override public void onFailure(Throwable thrown) { onSuccess(null); }
			@Override public void onSuccess(Collection<Tuple> tuples) {
				futures.remove(future);
				updateStatus();
			}
		});
		updateStatus();
		return future;
	}
	protected void updateStatus() {
		invokeAppropriate(new Runnable() {
			@Override public void run() {
				int size = futures.size();
				if(size>0) {
					status.setText(size==1?"Computing...":"Computing ("+size+")...");
					cancel.setVisible(true);
				} else {
					status.setText("Ready");
					cancel.setVisible(false);
				}
				editor.getTextPane().setEditable(futureModifications==0);
			}
		});
	}
	
	public boolean newFile() {
		if(!confirmIfDirty())
			return false;
		editor.setText(EMPTY);
		editor.setClean();
		currentFile = null;
		getMenuItem(menu[0],ACTION_SAVE).setEnabled(false);
		return true;
	}
	public boolean openFile() {
		if(!confirmIfDirty())
			return false;

		JFileChooser chooser = new FileChooser();
		chooser.setDialogTitle("Open");
		if(chooser.showOpenDialog(this)!=JFileChooser.APPROVE_OPTION)
			return false;
		
		return openFile(chooser.getSelectedFile());
	}
	public boolean openFile(File file) {
		try {
			editor.setText(new String(readFile(file)));
			editor.setClean();
			currentFile = file;
			getMenuItem(menu[0],ACTION_SAVE).setEnabled(true);
			return true;
		}	catch(IOException e) { return false; }
	}
	public boolean saveFile() {
		if(currentFile!=null)
			   return saveFile(currentFile);
		else return saveFileAs();
	}
	public boolean saveFileAs() {
		JFileChooser chooser = new FileChooser();
		chooser.setDialogTitle("Save As");
		if(chooser.showSaveDialog(this)==JFileChooser.APPROVE_OPTION)
			   return saveFile(chooser.getSelectedFile());
		else return false;
	}

	public void undoEdit() {
		editor.undoEdit();
		editor.requestFocus();
	}
	public void redoEdit() {
		editor.redoEdit();
		editor.requestFocus();
	}
	public void cutText() {
		editor.getTextPane().cut();
		editor.requestFocus();
	}
	public void copyText() {
		editor.getTextPane().copy();
		editor.requestFocus();
	}
	public void pasteText() {
		editor.getTextPane().paste();
		editor.requestFocus();
	}
	public void deleteText() {
		JTextPane textPane = editor.getTextPane();
		int selectionStart = textPane.getSelectionStart(), selectionEnd = textPane.getSelectionEnd();
		if(selectionStart!=selectionEnd)
			try { textPane.getDocument().remove(selectionStart, selectionEnd-selectionStart); }
			catch(BadLocationException e) { /** nothing to do here */ }
	}
	
	public void selectText() {
		editor.getTextPane().selectAll();
		editor.requestFocus();
	}
	public void clearText() {
		try {
			Document document = editor.getDocument();
			document.remove(0, document.getLength());
		} catch(BadLocationException e) { /* nothing to do here */ }
		editor.requestFocus();
	}
	public void findText() {
		if(!find.isVisible())
			find.showFind();
	}
	public void replaceText() {
		if(!find.isVisible())
			find.showReplace();
	}
	public void splitText() {
		if(!find.isVisible())
			find.showSplit();
	}
	public void findNext() {
		String term = find.getTerm();
		if(!term.isEmpty()) {
			JTextPane textPane = editor.getTextPane();
			String text = textPane.getText();
			int start = -1, end = 0, carret = find.searchDown()?textPane.getSelectionEnd():editor.getTextPane().getSelectionStart();
			if(!find.regexSearch()) {
				if(find.searchDown()) {
					if((start=text.indexOf(term, carret))==-1&&find.wrapSearch())
						start = text.indexOf(term);
				} else {
					if((carret==0||(start=text.lastIndexOf(term, carret-term.length()))==-1)&&find.wrapSearch())
						start = text.lastIndexOf(term);
				}
				if(start!=-1) end = start+term.length();
			} else {
				Pattern pattern;
				try { pattern = Pattern.compile(term, Pattern.CASE_INSENSITIVE); }
				catch(PatternSyntaxException e) {
					JOptionPane.showMessageDialog(find, e.getLocalizedMessage(), find.getTitle(), JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				Matcher matcher = pattern.matcher(text);
				if(find.searchDown()) {
					if(matcher.find(carret)||(find.wrapSearch()&&matcher.find())) {
						start = matcher.start();
						end = matcher.end();
					}
				} else {
					int wrapStart = -1;
					while(matcher.find())
					  if(matcher.end()<=carret) {
					  	start = matcher.start();
					  	end = matcher.end();
					  } else if(start ==-1&&find.wrapSearch()) {
					  	wrapStart = matcher.start();
					  	end = matcher.end();
						} else break;
					if(wrapStart!=-1) start = wrapStart;
				}
			}
		
			if(start!=-1) {
				textPane.setSelectionStart(start);
				textPane.setSelectionEnd(end);
				editor.requestFocus();
			} else JOptionPane.showMessageDialog(find, String.format("Cannot find \"%s\"", term), find.getTitle(), JOptionPane.INFORMATION_MESSAGE);
		} else findText();
	}
	protected void replaceNext() {
		JTextPane textPane = editor.getTextPane();
		int start = textPane.getSelectionStart(), end = textPane.getSelectionEnd();
		if(start!=end) {
			String term = find.getTerm(), selected = textPane.getSelectedText(), replace = null;
			if(!find.regexSearch()) {
				if(selected.equalsIgnoreCase(term))
					replace = find.getReplace();
			} else {
				Pattern pattern;
				try { pattern = Pattern.compile(term, Pattern.CASE_INSENSITIVE); }
				catch(PatternSyntaxException e) {
					JOptionPane.showMessageDialog(find, e.getLocalizedMessage(), find.getTitle(), JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				Matcher matcher = pattern.matcher(selected);
				if(matcher.find())
					replace = matcher.replaceAll(find.getReplace());
			}
			
			if(replace!=null) try {
				Document document = editor.getDocument();
				document.remove(start,end-start);
				document.insertString(start,replace,null);				
			} catch(BadLocationException e) { /* nothting to do here */ }
			
			findNext();
		} else findNext();
	}
	protected void replaceAll() {
		Pattern pattern; String term = find.getTerm();
		try { pattern = Pattern.compile(find.regexSearch()?term:Pattern.quote(term), Pattern.CASE_INSENSITIVE); }
		catch(PatternSyntaxException e) {
			JOptionPane.showMessageDialog(find, e.getLocalizedMessage(), find.getTitle(), JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		editor.setText(pattern.matcher(editor.getText()).replaceAll(find.getReplace()));
	}
	protected void splitAt() {
		String term = find.getTerm();
		if(find.regexSearch())
			try { Pattern.compile(find.getTerm()); }
			catch(PatternSyntaxException e) {
				JOptionPane.showMessageDialog(find, e.getLocalizedMessage(), find.getTitle(), JOptionPane.ERROR_MESSAGE);
				return;
			}
		
		submitOperation(Split.Expression.class, term, find.regexSearch(), find.removeDelimiter());
	}
	
	public void goToTuple() {
		new GoTo().setVisible(true);
	}
	
	public void newTool() {
		new GenTool().setVisible(true);
	}
	public void copyTool() {
		GenTool tool = new GenTool();
		tool.editor.setText(editor.getText());
		tool.setVisible(true);
	}
	public boolean closeTool() {
		if(!confirmIfDirty())
			return false;
		this.dispose();
		return true;
	}
	public boolean closeTools() {
		List<GenTool> tools = new ArrayList<>(GenTool.TOOLS);
		Collections.reverse(tools);
		for(GenTool tool:tools)
			if(!tool.closeTool())
				return false;
		return true;
	}
	
	public void toggleToolbar() {
		boolean visible = toolbars.isVisible();
		toolbars.setVisible(!visible);
		getMenuItem(menubar,ACTION_TOGGLE_TOOLBAR).setText(visible?"Show Toolbar":"Hide Toolbar");
	}
	
	public void showBatch() {
		if(!BATCH.isVisible()) {
			BATCH.setLocationRelativeTo(GenTool.this);
			BATCH.setVisible(true);
		} else BATCH.requestFocus();
	}
	public void addToBatch() {
		BATCH.addSequence(editor.getTuples());
		showBatch();
	}
	
	public void showPreferences() {
		final JDialog preferences = new JDialog(this, "Preferences", true);
		
		preferences.pack();
		preferences.setResizable(false);
		preferences.setLocationRelativeTo(this);
		preferences.setVisible(true);
	}
	public void showAbout() { showAbout(this); }
	public static void showAbout(Frame parent) {
		final JDialog about = new JDialog(parent, "About Genetic Code Tool (GenTool)", true);
		setBoxLayout(about.getContentPane(), BoxLayout.Y_AXIS);
		((JPanel)about.getContentPane()).setBorder(new EmptyBorder(10,10,10,10));
		
		Border gap = new EmptyBorder(5,0,5,0); JLabel label;
		about.add(label=new JLabel(new ImageIcon(getResource("gentool.png")), SwingConstants.CENTER));
		label.setBorder(gap); label.setAlignmentX(CENTER_ALIGNMENT);
		about.add(label=new JLabel("\u00A9 2014 Hochschule Mannheim - Version "+VERSION));
		label.setBorder(gap); label.setAlignmentX(CENTER_ALIGNMENT);
		label.setFont(label.getFont().deriveFont(Font.BOLD));
		about.add(label=new JLabel("(Prof. Dr. Elena Fimmel, Prof. Dr. Lutz Str\u00FCngmann, Kristian Kralji\u0107)"));
		label.setBorder(gap);label.setAlignmentX(CENTER_ALIGNMENT);
		about.add(label=new JLabel("http://am.informatik.hs-mannheim.de/GenTool/"));
		label.setAlignmentX(CENTER_ALIGNMENT);
		@SuppressWarnings("unchecked") Map<TextAttribute,Object> attributes = (Map<TextAttribute,Object>)label.getFont().getAttributes();
		attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
		label.setFont(label.getFont().deriveFont(attributes));
		label.setForeground(Color.BLUE);
		label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		label.addMouseListener(new MouseAdapter() {			
			@Override public void mouseClicked(MouseEvent event) {
				if(Desktop.isDesktopSupported())
					try { Desktop.getDesktop().browse(new URI(((JLabel)event.getSource()).getText())); }
					catch(Exception e) { /* nothing to do here */ }
			}
		});
		
		JPanel buttons = new JPanel(new FlowLayout(FlowLayout.TRAILING));
		about.add(buttons);
		
		JButton close = new JButton("Close");
		close.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				about.dispose();
			}
		});
		buttons.add(close);
		
		about.pack();
		about.setResizable(false);
		about.setLocationRelativeTo(parent);
		about.setVisible(true);
	}

	protected boolean saveFile(File file) {
		try {
			writeFile(editor.getText(), file);
			editor.setClean();
			currentFile = file;
			getMenuItem(menu[0], ACTION_SAVE).setEnabled(true);
			return true;
		}	catch(IOException e) { return false; }
	}
	protected boolean confirmIfDirty() {
		if(editor.isDirty())
			switch(JOptionPane.showOptionDialog(this, currentFile==null?"Do you want to save changes?":"Do you want to save changes to "+currentFile.getName()+"?", this.getTitle(), 0, JOptionPane.QUESTION_MESSAGE, null, new String[]{"Save","Don't Save","Cancel"}, null)) {
			case JOptionPane.YES_OPTION:
				if(!saveFile())
					return false;
			case JOptionPane.NO_OPTION:
				return true;
			case JOptionPane.CANCEL_OPTION: default:
				return false;
		} else return true;
	}
	
	class CatalogPanel extends JPanel {
		private static final long serialVersionUID = 1l;
		
		private final GridBagConstraints defaultConstraints;

		protected List<FoldingPanel> pages = new ArrayList<>();
		protected FoldingPanel input;
		
		public CatalogPanel() {
			super(new GridBagLayout());
			
			defaultConstraints = new GridBagConstraints();
			defaultConstraints.weightx = 1.; defaultConstraints.gridx = 0;
			defaultConstraints.fill = GridBagConstraints.HORIZONTAL;
			
			JPanel trailer = new JPanel();
			GridBagConstraints constraints =
				(GridBagConstraints)defaultConstraints.clone();
			constraints.weighty = 1.;
			add(trailer, constraints);
			
			input = addPage(new JPanel(new BorderLayout()),"Input",true);
			input.add(createInputOptions(), BorderLayout.SOUTH);
			setDefaultInput();
		}
		
		public List<FoldingPanel> getPages() { return Collections.unmodifiableList(pages); }
		public FoldingPanel addPage(String title, boolean expanded) { return addPage(new JPanel(), title, expanded); }
		public FoldingPanel addPage(Component child, String title, boolean expanded) {
			FoldingPanel page = new FoldingPanel(child, title);
			page.setBorder(CATEGORY_BORDER);
			page.setFont(UIManager.getFont("CheckBox.font").deriveFont(Font.BOLD));
			page.setForeground(UIManager.getColor(TITLE_FOREGROUND));
			page.setExpanded(expanded);
			add(page, defaultConstraints, getComponentCount()-1);
			pages.add(page);
			return page;
		}
		
		protected void setDefaultInput() { setInput(new CodonTable()); }
		public void setInput(Class<? extends Input> input) throws InstantiationException, IllegalAccessException { setInput(input.newInstance()); }
		public void setInput(final Input input) {
			((JPanel)this.input.getChild()).add(input.getComponent(editor), BorderLayout.CENTER);
			this.input.setActions((input instanceof Configurable)?new javax.swing.Action[]{new AbstractAction(null, new ImageIcon(getResource("cog.png"))) {
				private static final long serialVersionUID = 1l;
				@Override public void actionPerformed(ActionEvent event) {
					((Configurable)input).showPreferencesDialog(GenTool.this,"Input Preferences");
					setInput(input); /* re-set input, so preferences get loaded again */
				}
			}}:new javax.swing.Action[0]);
		}
		
		protected Component createInputOptions() {
			JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
			addInputOption(panel,new Option("tupleLength", "Default Tuple Length", editor.getDocument().getDefaultTupleLength(), 0, 10, 1)).addChangeListener(new ChangeListener() {
				@Override public void stateChanged(ChangeEvent event) {
					((NucleicDocument)editor.getTextPane().getDocument()).setDefaultTupleLength((Integer)((JSpinner)event.getSource()).getValue());
					editor.repaint();
				}
			});
			addInputOption(panel,new Option("acid", "Default Acid", RNA,
				new Acid[]{null,RNA,DNA},
				EMPTY,DNA.name(),RNA.name())
			).addItemListener(new ItemListener() {
				@Override public void itemStateChanged(ItemEvent event) {
					editor.getDocument().setDefaultAcid(event.getStateChange()==ItemEvent.SELECTED?valueOf((String)event.getItem()):null);
				}
			});
			return panel;
		}
		private Option.Component addInputOption(Container container, final Option option) {
			Option.Component component; 
			container.add(new JLabel(option.label+":"));
			container.add(component = option.new Component());
			return component;
		}
	}
	
	class FindDialog extends JDialog {
		private static final long serialVersionUID = 1l;
		
		private static final int MODE_FIND = 0, MODE_REPLACE = 1, MODE_SPLIT = 2;
		
    protected EventListenerList listenerList = new EventListenerList();
    
    private JLabel what, with;
		private JTextField term, replace;
		private JButton find, findBatch, replaceNext, replaceAll, replaceBatch, split, splitBatch;
		private JCheckBox wrap, regex, delimiter;
		private JRadioButton up, down;
		private JPanel direction, options;
		
		private Document termDefaultDocument, replaceDefaultDocument;
		private NucleicDocument termNucleicDocument, replaceNucleicDocument;
		
		public FindDialog() {
			super(GenTool.this, "Find", false);
			
			GroupLayout layout = new GroupLayout(getContentPane());
			getContentPane().setLayout(layout);
			layout.setAutoCreateGaps(true);
			layout.setAutoCreateContainerGaps(true);
			
			what = new JLabel("Find what:");
			
			termDefaultDocument = (term = new JTextField()).getDocument();
			term.setDocument(termNucleicDocument=new NucleicDocument());
			term.requestFocus();
			term.getDocument().addDocumentListener(new DocumentListener() {
				@Override public void removeUpdate(DocumentEvent event) { changedUpdate(event); }				
				@Override public void insertUpdate(DocumentEvent event) { changedUpdate(event); }
				@Override public void changedUpdate(DocumentEvent event) {
					boolean text = !term.getText().isEmpty();
					for(Component component:new Component[]{find,findBatch,replaceNext,replaceAll,replaceBatch,split,splitBatch})
						component.setEnabled(text);
				}
			});
			
			with = new JLabel("Replace with:");
			with.setVisible(false);
			
			replaceDefaultDocument = (replace = new JTextField()).getDocument();
			replace.setDocument(replaceNucleicDocument=new NucleicDocument());
			replace.setVisible(false);
			
			find = new JButton("Find Next");
			find.setEnabled(false);
			find.addActionListener(new ActionListener() {
				@Override public void actionPerformed(ActionEvent event) {
					findNext();
				}
			});
			
			findBatch = new JButton(new ImageIcon(getResource("calculator_add_small.png")));
			findBatch.setToolTipText("Add test to batch");
			findBatch.setEnabled(false);
			findBatch.addActionListener(new ActionListener() {
				@Override public void actionPerformed(ActionEvent e) {
					addBatchOperation(Test.Expression.class,term.getText(),regex.isSelected());
				}
			});
			
			replaceNext = new JButton("Replace");
			replaceNext.setEnabled(false);
			replaceNext.addActionListener(new ActionListener() {
				@Override public void actionPerformed(ActionEvent event) {
					replaceNext();
				}
			});
			
			replaceAll = new JButton("Replace All");
			replaceAll.setEnabled(false);
			replaceAll.addActionListener(new ActionListener() {
				@Override public void actionPerformed(ActionEvent event) {
					replaceAll();
				}
			});
			
			replaceBatch = new JButton(new ImageIcon(getResource("calculator_add_small.png")));
			replaceBatch.setToolTipText("Add replace operation to batch");
			replaceBatch.setEnabled(false);
			replaceBatch.addActionListener(new ActionListener() {
				@Override public void actionPerformed(ActionEvent e) {
					addBatchOperation(Transformation.Expression.class,term.getText(),replace.getText(),regex.isSelected());
				}
			});
			
			split = new JButton("Split");
			split.setEnabled(false);
			split.addActionListener(new ActionListener() {
				@Override public void actionPerformed(ActionEvent event) {
					splitAt();
				}
			});
			
			splitBatch = new JButton(new ImageIcon(getResource("calculator_add_small.png")));
			splitBatch.setToolTipText("Add split to batch");
			splitBatch.setEnabled(false);
			splitBatch.addActionListener(new ActionListener() {
				@Override public void actionPerformed(ActionEvent e) {
					addBatchOperation(Split.Expression.class,term.getText(),regex.isSelected(),delimiter.isSelected());
				}
			});
			
			JButton cancel = new JButton("Cancel");
			cancel.addActionListener(new ActionListener() {
				@Override public void actionPerformed(ActionEvent event) {
					setVisible(false);
				}
			});
			
			options = setBoxLayout(new JPanel(new FlowLayout()), BoxLayout.Y_AXIS);
			options.setBorder(BorderFactory.createTitledBorder(BorderFactory
	        .createEtchedBorder(EtchedBorder.LOWERED), "Options"));
			
			options.add(wrap = new JCheckBox("Wrap search", true));
			options.add(regex = new JCheckBox("Regular expressions"));
			options.add(delimiter = new JCheckBox("Remove delimiter"));
			regex.addItemListener(new ItemListener() {
				@Override public void itemStateChanged(ItemEvent event) {
					term.setDocument(moveDocument(term.getDocument(),regex.isSelected()?termDefaultDocument:termNucleicDocument));
					replace.setDocument(moveDocument(replace.getDocument(),regex.isSelected()?replaceDefaultDocument:replaceNucleicDocument));
				}
				
				private Document moveDocument(Document document, Document targetDocument) {
					if(document!=targetDocument) try {
							targetDocument.remove(0, targetDocument.getLength());
							targetDocument.insertString(0, document.getText(0, document.getLength()), null);
						} catch(BadLocationException e) { /* nothing to do here */ }
					return targetDocument;
				}
			});
			
			direction = new JPanel(new FlowLayout(FlowLayout.LEADING));
			direction.setBorder(BorderFactory.createTitledBorder(BorderFactory
	        .createEtchedBorder(EtchedBorder.LOWERED), "Direction"));
			direction.add(up = new JRadioButton("Up"));
			direction.add(down = new JRadioButton("Down", true));
			createButtonGroup(up, down);

			layout.setHorizontalGroup(
				layout.createSequentialGroup()
					.addGroup(layout.createParallelGroup(Alignment.LEADING)
						.addComponent(what, 65, 65, GroupLayout.DEFAULT_SIZE)
						.addComponent(with)
					)
					.addGroup(layout.createParallelGroup(Alignment.LEADING)
						.addComponent(term, 260, 260, GroupLayout.DEFAULT_SIZE)
						.addComponent(replace)
						.addGroup(layout.createSequentialGroup()
								.addComponent(options, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, 260)
								.addComponent(direction)
						)
					)
					.addGroup(layout.createParallelGroup(Alignment.LEADING)
						.addGroup(layout.createSequentialGroup()
							.addComponent(find, 90, 90, 140)
							.addComponent(findBatch, 40, 40, 40)
						)
						.addComponent(replaceNext, 140, 140, 140)
						.addGroup(layout.createSequentialGroup()
							.addComponent(replaceAll, 90, 90, 140)
							.addComponent(replaceBatch, 40, 40, 40)
						)
						.addGroup(layout.createSequentialGroup()
							.addComponent(split, 90, 90, 140)
							.addComponent(splitBatch, 40, 40, 40)
						)
						.addComponent(cancel, 140, 140, 140)
					)
			);
			layout.linkSize(SwingConstants.HORIZONTAL, what, with);
			
			layout.setVerticalGroup(
				layout.createSequentialGroup()
					.addGroup(layout.createParallelGroup(Alignment.BASELINE)
						.addComponent(what)
						.addComponent(term)
						.addComponent(find)
						.addComponent(findBatch)
						.addComponent(split)
						.addComponent(splitBatch)
					)
					.addGroup(layout.createParallelGroup(Alignment.BASELINE)
						.addComponent(with)
						.addComponent(replace)
						.addComponent(replaceNext)
					)
					.addGroup(layout.createParallelGroup(Alignment.LEADING)
						.addComponent(options)
						.addComponent(direction)
						.addGroup(layout.createSequentialGroup()
							.addGroup(layout.createParallelGroup(Alignment.LEADING)
								.addComponent(replaceAll)
								.addComponent(replaceBatch)
							)
							.addComponent(cancel)
						)
					)	
			);
			layout.linkSize(SwingConstants.VERTICAL, options, direction);
			
			pack();
			setLocationRelativeTo(GenTool.this);
			setResizable(false);
		}
		
		public String getTerm() { return term.getText(); }
		public String getReplace() { return replace.getText(); }
		public boolean wrapSearch() { return wrap.isSelected(); }
		public boolean regexSearch() { return regex.isSelected(); }
		public boolean removeDelimiter() { return delimiter.isSelected(); }
		public boolean searchDown() { return down.isSelected(); }		
		
		public void showFind() { showMode(MODE_FIND); }
		public void showReplace() { showMode(MODE_REPLACE); }
		public void showSplit() { showMode(MODE_SPLIT); }
		
		private void showMode(int mode) {
			switch(mode) {
			case MODE_FIND: setTitle("Find"); break;
			case MODE_REPLACE: setTitle("Find / Replace"); break;
			case MODE_SPLIT: setTitle("Split"); break; }
			what.setText(mode!=MODE_SPLIT?"Find what:":"Split at:");
			
			for(Component component:new Component[]{findBatch})
				component.setVisible(mode==MODE_FIND);
			for(Component component:new Component[]{with,replace,replaceNext,replaceAll,replaceBatch})
				component.setVisible(mode==MODE_REPLACE);
			for(Component component:new Component[]{split,splitBatch,delimiter})
				component.setVisible(mode==MODE_SPLIT);
			for(Component component:new Component[]{find,wrap,direction})
				component.setVisible(mode!=MODE_SPLIT);
			
			pack(); setVisible(true);
		}
		
		@Override public void setVisible(boolean visible) {
			super.setVisible(visible);
			term.requestFocus();
		}
	}
	
	class GoTo extends JDialog {
		private static final long serialVersionUID = 1l;

    protected EventListenerList listenerList = new EventListenerList();

		private JFormattedTextField number;
		private JButton goTo;
		
		public GoTo() {
			super(GenTool.this, "Go To Tuple", true);
			
			GroupLayout layout = new GroupLayout(getContentPane());
			getContentPane().setLayout(layout);
			layout.setAutoCreateGaps(true);
			layout.setAutoCreateContainerGaps(true);
			
			JLabel label = new JLabel("Tuple number:");
			
			NumberFormat numberFormat = NumberFormat.getIntegerInstance();
			numberFormat.setGroupingUsed(false);
			
			NumberFormatter numberFormatter = new NumberFormatter(numberFormat);
			numberFormatter.setAllowsInvalid(true);
			numberFormatter.setMinimum(1);
			
			number = new JFormattedTextField(numberFormatter);
			number.setFocusLostBehavior(JFormattedTextField.PERSIST);
			number.addKeyListener(new KeyAdapter() {				
				@Override public void keyPressed(KeyEvent event) {
					if(event.getKeyCode()==KeyEvent.VK_ENTER)
						goTo.getActionListeners()[0].actionPerformed(null);
				}
			});
			number.addFocusListener(new FocusAdapter() {
				@Override public void focusGained(FocusEvent event) {
					SwingUtilities.invokeLater(new Runnable() {
						@Override public void run() { number.selectAll(); }
					});
				}
			});
			number.getDocument().addDocumentListener(new DocumentListener() {
				@Override public void removeUpdate(DocumentEvent event) { changedUpdate(event); }				
				@Override public void insertUpdate(DocumentEvent event) { changedUpdate(event); }
				@Override public void changedUpdate(DocumentEvent event) {
					if(goTo!=null) goTo.setEnabled(!number.getText().isEmpty());
				}
			});
			number.setValue(1);
			number.requestFocus();
			
			goTo = new JButton("Go To");
			goTo.addActionListener(new ActionListener() {
				@Override public void actionPerformed(ActionEvent event) {
					try {
						number.commitEdit();

						int number = 0, goToNumber = getNumber();
						Matcher matcher = Pattern.compile(/*"(.*?)( |$)"*/"(?: |^)([^ ]*)").matcher(editor.getText());
						while(matcher.find())
							if(++number==goToNumber) {
								JTextPane textPane = editor.getTextPane();
								textPane.setSelectionStart(matcher.start(1));
								textPane.setSelectionEnd(matcher.end(1));
								setVisible(false);
								editor.requestFocus();
								return;
							}
						
						JOptionPane.showMessageDialog(goTo, "The tuple number is beyond the total number of tuples.", getTitle(), JOptionPane.INFORMATION_MESSAGE);
						setNumber(number);
						requestFocus();
					} catch(ParseException e) {
						JOptionPane.showMessageDialog(GoTo.this, "Please do only enter positive numeric values.", GoTo.this.getTitle(), JOptionPane.ERROR_MESSAGE);
						number.requestFocus();
					}
				}
			});
			JButton cancel = new JButton("Cancel");
			cancel.addActionListener(new ActionListener() {
				@Override public void actionPerformed(ActionEvent event) {
					setVisible(false);
				}
			});
			
			layout.setHorizontalGroup(
				layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
					.addComponent(label)
					.addComponent(number, GroupLayout.DEFAULT_SIZE, 200, GroupLayout.DEFAULT_SIZE)
					.addGroup(layout.createSequentialGroup()
						.addComponent(goTo)
						.addComponent(cancel)
					)
			);
			layout.linkSize(SwingConstants.HORIZONTAL, number, label);
			layout.linkSize(SwingConstants.HORIZONTAL, goTo, cancel);
			
			layout.setVerticalGroup(
				layout.createSequentialGroup()
					.addComponent(label)
					.addComponent(number)
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(goTo)
						.addComponent(cancel)
					)
			);
			
			pack();
			setMinimumSize(getSize());
			setMaximumSize(getSize());
			setLocationRelativeTo(GenTool.this);
			setResizable(false);
		}
		
		@Override public void requestFocus() {
			number.requestFocus();
		}
		
		public void setNumber(int number) { this.number.setValue(number); }
		public int getNumber() { return ((Number)number.getValue()).intValue(); }
	}
	
	class ConsolePane extends JTextArea implements Logger {
		private static final long serialVersionUID = 1l;
		public ConsolePane() {
			this.setEditable(false);
			((DefaultCaret)this.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		}
		public void resetText() { setText(null); }
		public void insertText(String text) { console.insert(text,console.getText().length()); }
		public void appendText(String text) {
			if(!getText().isEmpty())
				insertText(NEW_LINE);
			insertText(text);
		}
		
		@Override public void log(String format,Object... arguments) {
			appendText(String.format(format,arguments));
		}
		@Override public void log(String message,Throwable throwable) {
			appendText(message); appendText(throwable.getMessage());
		}
	}
	
	public static class FileChooser extends RememberFileChooser {
		private static final long serialVersionUID = 1l;
		
		public static final String FILE_EXTENSION = "gen";
		private static File currentDirectory;
		
		public FileChooser() {
			setFileFilter(new FileFilter() {
				@Override	public String getDescription() { return "Genetic Sequence (*."+FILE_EXTENSION+")"; }
				@Override	public boolean accept(File file) { return file.isDirectory()||file.getName().toLowerCase().endsWith('.'+FILE_EXTENSION); }
			});
		}
		
		@Override public void approveSelection() {
  		File file = getSelectedFile();
  		if(!file.getName().toLowerCase().endsWith('.'+FILE_EXTENSION))
				setSelectedFile(file=new File(file.getAbsolutePath()+'.'+FILE_EXTENSION));
  		if(getDialogType()==SAVE_DIALOG&&file!=null&&file.exists())
  			if(JOptionPane.showOptionDialog(getParent(),String.format("%s already exists.\nDo you want to replace it?",file.getName()),"Confirm Overwrite",JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE,null,null,null)==JOptionPane.NO_OPTION)
  				return;
  		currentDirectory = file.getParentFile();
  		if(currentDirectory!=null&&!currentDirectory.isDirectory())
  			currentDirectory = null;
  		super.approveSelection();
    }
	}

	public static void main(final String[] args) throws ParseException {		
		SwingUtilities.invokeLater(new Runnable() {
			@Override public void run() {
				final GenTool tool = new GenTool();
				if(args.length>=1)
					switch(args[0]) {
					case "-open":
						if(args.length>=2)
							tool.openFile(new File(args[1]));
						break;
					default: System.out.println(String.format("Unknown argument \"%s\"", args[0])); }
				tool.setVisible(true);
			}
		});
	}
}