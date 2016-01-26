/*
 * Copyright [2014] [Mannheim University of Applied Sciences]
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
package bio.gcat.gui;

import static bio.gcat.Utilities.EMPTY;
import static bio.gcat.Utilities.ellipsisText;
import static bio.gcat.Utilities.getConfiguration;
import static bio.gcat.Utilities.readFile;
import static bio.gcat.Utilities.safeSetSystemProperty;
import static bio.gcat.Utilities.setConfiguration;
import static bio.gcat.Utilities.writeFile;
import static bio.gcat.batch.Action.TaskAttribute.ANALYSIS_HANDLER;
import static bio.gcat.batch.Action.TaskAttribute.SPLIT_PICK;
import static bio.gcat.batch.Action.TaskAttribute.TEST_CRITERIA;
import static bio.gcat.batch.Action.TaskAttribute.TEST_CRITERIA_BREAK_IF_FALSE;
import static bio.gcat.gui.helper.Guitilities.CATEGORY_BORDER;
import static bio.gcat.gui.helper.Guitilities.TITLE_FOREGROUND;
import static bio.gcat.gui.helper.Guitilities.createButtonGroup;
import static bio.gcat.gui.helper.Guitilities.createMenuItem;
import static bio.gcat.gui.helper.Guitilities.createSeparator;
import static bio.gcat.gui.helper.Guitilities.createSplitPane;
import static bio.gcat.gui.helper.Guitilities.createSubmenu;
import static bio.gcat.gui.helper.Guitilities.createToolbarButton;
import static bio.gcat.gui.helper.Guitilities.createToolbarMenuButton;
import static bio.gcat.gui.helper.Guitilities.createToolbarTextButton;
import static bio.gcat.gui.helper.Guitilities.getImage;
import static bio.gcat.gui.helper.Guitilities.getImageIcon;
import static bio.gcat.gui.helper.Guitilities.getMenuItem;
import static bio.gcat.gui.helper.Guitilities.getToolbarButton;
import static bio.gcat.gui.helper.Guitilities.invokeAppropriate;
import static bio.gcat.gui.helper.Guitilities.setBoxLayout;
import static bio.gcat.nucleic.helper.GenBank.DATABASE_NUCLEOTIDE;

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
import java.awt.Rectangle;
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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.attribute.UserDefinedFileAttributeView;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
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
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.EventListenerList;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.NumberFormatter;

import org.biojava3.core.sequence.AccessionID;
import org.biojava3.core.sequence.DNASequence;
import org.biojava3.core.sequence.io.FastaReaderHelper;
import org.biojava3.core.sequence.io.FastaWriterHelper;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import bio.gcat.Configurable;
import bio.gcat.Documented;
import bio.gcat.Option;
import bio.gcat.Parameter;
import bio.gcat.Utilities;
import bio.gcat.Utilities.DefiniteFuture;
import bio.gcat.Utilities.FileNameExtensionFileChooser;
import bio.gcat.Utilities.OperatingSystem;
import bio.gcat.batch.Action;
import bio.gcat.batch.Action.Task;
import bio.gcat.batch.Action.TaskAttribute;
import bio.gcat.gui.editor.NucleicAdapter;
import bio.gcat.gui.editor.NucleicDocument;
import bio.gcat.gui.editor.NucleicEditor;
import bio.gcat.gui.editor.NucleicListener;
import bio.gcat.gui.editor.NucleicOptions;
import bio.gcat.gui.editor.NucleicOptions.EditorMode;
import bio.gcat.gui.helper.ConsolePane;
import bio.gcat.gui.helper.FoldingPanel;
import bio.gcat.gui.helper.Guitilities;
import bio.gcat.gui.helper.Guitilities.IconButton;
import bio.gcat.gui.input.CodonCircle;
import bio.gcat.gui.input.Input;
import bio.gcat.gui.input.TesseraTable;
import bio.gcat.log.InjectionLogger;
import bio.gcat.nucleic.Acid;
import bio.gcat.nucleic.Tuple;
import bio.gcat.nucleic.helper.GenBank;
import bio.gcat.operation.Operation;
import bio.gcat.operation.analysis.Analysis;
import bio.gcat.operation.analysis.Analysis.Result;
import bio.gcat.operation.split.Split;
import bio.gcat.operation.test.Test;
import bio.gcat.operation.transformation.Transformation;
import lc.kra.Characters;

public class AnalysisTool extends JFrame implements ActionListener, NucleicListener {
	private static final long serialVersionUID = 1l;
	
	public static final String GENETIC_EXTENSION = "gcat";
	public static final FileNameExtensionFilter
		GENETIC_EXTENSION_FILTER = new FileNameExtensionFilter("Genetic Code Sequence (*."+GENETIC_EXTENSION+")", GENETIC_EXTENSION),
		FASTA_EXTENSION_FILTER = new FileNameExtensionFilter("FASTA Files (*.fasta;*.fna,*.fa)","fasta","fna","fa"),
		TEXT_EXTENSION_FILTER = new FileNameExtensionFilter("Text Documents (*.txt)", "txt");
	
	public static final String NAME = "Genetic Code Analysis Toolkit (GCAT)", VERSION;
	static {
		String version = "UNKNOWN";
		try(InputStream versionInput=Optional.ofNullable(AnalysisTool.class.getClassLoader().
				getResourceAsStream("bio/gcat/version.properties")).orElseThrow(IOException::new)) {
			Properties versionProperties = new Properties();
			versionProperties.load(versionInput);
			version = versionProperties.getProperty("version",version);
		} catch(IOException e1) {
			try(InputStream mavenInput=Optional.ofNullable(AnalysisTool.class.getClassLoader().
					getResourceAsStream("META-INF/maven/bio.gcat/gcat/pom.properties")).orElseThrow(IOException::new)) {
				Properties mavenProperties = new Properties();
				mavenProperties.load(mavenInput);
				version = mavenProperties.getProperty("version",version);
			} catch(IOException e2) { /* nothing to do here */ }
		} finally { VERSION = version; }
	}
	
	private static final int
		MENU_FILE = 0,
		MENU_EDIT = 1,
		MENU_WINDOW = 2,
		MENU_HELP = 3;
	private static final int
		SUBMENU_IMPORT = 0,
		SUBMENU_RECENT = 1;
	private static final String
		ACTION_NEW = "new",
		ACTION_OPEN = "open",
		ACTION_SAVE = "save",
		ACTION_SAVE_AS = "save_as",
		ACTION_IMPORT = "import",
		ACTION_GENBANK = "genbank",
		ACTION_EXPORT = "export",
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
		ACTION_SHOW_BDA = "show_bda",
		ACTION_SHOW_BATCH = "show_batch",
		ACTION_ADD_TO_BATCH = "add_to_batch",
		ACTION_PREFERENCES = "preferences",
		ACTION_HELP = "help",
		ACTION_ABOUT = "about";
	
	@SuppressWarnings("unused") private static final int
		TOOLBAR_FILE = 0,
		TOOLBAR_EDIT = 1,
		TOOLBAR_WINDOW = 2,
		TOOLBAR_HELP = 3;
	
	private static final String
		CONFIGURATION_RECENT = "recent";
	
	private static final String
		ATTRIBUTE_LABEL = "gene_sequence.label";
	
	private static final List<AnalysisTool> TOOLS = new ArrayList<>();
	private static BDATool bda; private static BatchTool batch;
	
	protected final ListeningExecutorService service = MoreExecutors.listeningDecorator(Executors.newSingleThreadExecutor());
	protected final List<ListenableFuture<Collection<Tuple>>> futures = Collections.synchronizedList(new LinkedList<>());
	protected final Map<TaskAttribute,Object> attributes;
	protected int futureModifications;
	
	private NucleicEditor editor;
	private Option.Component optionLabel;
	
	private JSplitPane[] split;
	private JMenuBar menubar;
	private JMenu[] menu, submenu;
	private JPanel toolbars, bottom;
	private JToolBar[] toolbar;
	private JLabel status;
	private JButton cancel;
	
	private CatalogPanel catalogPanel;
	private FindDialog findDialog;
	private ConsolePane consolePane;
	
	private File currentFile;
	
	public AnalysisTool() {
		TOOLS.add(this);
		updateTitle();
		setIconImages(Arrays.asList(getImage("icon"),getImage("icon_medium"),getImage("icon_large")));
		setMinimumSize(new Dimension(800,600));
		setPreferredSize(new Dimension(1480,840));
		
		Rectangle screenSize = getGraphicsConfiguration().getBounds();
		if(screenSize.width>=1480&&screenSize.height>=840)
			setSize(1480,840);
		else if(screenSize.width>=1120&&screenSize.height>=680)
			setSize(1120,680);
		else if(screenSize.width>=800&&screenSize.height>=600)
			setSize(800,600);
		else setExtendedState(JFrame.MAXIMIZED_BOTH); 
		
		setSize(getPreferredSize());
		setLocationByPlatform(true);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override public void windowClosing(WindowEvent event) { closeTool(); }
			@Override public void windowClosed(WindowEvent event) {
				try { service.shutdownNow(); } 
				catch(SecurityException e) { /* nothing to do here */ }
				TOOLS.remove(AnalysisTool.this);
				if(TOOLS.isEmpty()) {
					if(batch!=null)
						batch.dispose();
					if(bda!=null)
						bda.dispose();
				}
			}
		});
		
		attributes = new HashMap<>();
		attributes.put(TEST_CRITERIA,TEST_CRITERIA_BREAK_IF_FALSE);
		attributes.put(ANALYSIS_HANDLER,new Analysis.Handler() {
			@Override public void handle(Result result) {
				if(result!=null) {
					consolePane.appendText(String.format("Analysis \"%s\" result: ",result.getAnalysis().getName()),ConsolePane.SUCCESS);
					consolePane.insertText(result.toString());
				} else consolePane.appendText("No result from analysis.",ConsolePane.FAILURE);
			}
		});
		attributes.put(SPLIT_PICK,new Split.Pick() {
			@Override public Collection<Tuple> pick(List<Collection<Tuple>> split) {
				     if(split==null||split.isEmpty()) return null;
				else if(split.size()==1) return split.get(0);
				else {
					DefiniteFuture<Collection<Tuple>> future = new DefiniteFuture<Collection<Tuple>>();
					
					JDialog dialog = new JDialog(AnalysisTool.this,"Pick Sequence",true);
					dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					
					GroupLayout layout = new GroupLayout(dialog.getContentPane());
					dialog.getContentPane().setLayout(layout);
					layout.setAutoCreateGaps(true);
					layout.setAutoCreateContainerGaps(true);
					
					JLabel label = new JLabel("Pick one or more (holding the "+(!OperatingSystem.MAC.equals(OperatingSystem.currentOperatingSystem())?"Ctrl":"Command")+" key) sequences from the list:");
					JButton pick = new JButton(), cancel = new JButton("Cancel");
					
					@SuppressWarnings("unchecked") JList<Collection<Tuple>> list = new JList<Collection<Tuple>>(split.toArray((Collection<Tuple>[])new Collection<?>[0])) {
						private static final long serialVersionUID = 1l;
						private final Color lineColor = new Color(0,0,0,64);
						@Override public void paint(Graphics graphics) {
							super.paint(graphics);
							int tupleLength = editor.getTupleLength();
							if(tupleLength>0) {
								graphics.setColor(lineColor);
								int width=getFontMetrics(getFont()).charWidth('0'), tupleWidth = width*tupleLength;
								for(int left=getInsets().left+width/2+tupleWidth; left<getWidth(); left += tupleWidth + width)
									graphics.drawLine(left, 0, left, getHeight());
							}
						}
					};
					list.setCellRenderer(new ListCellRenderer<Collection<Tuple>>() {
						private JLabel label = new JLabel();
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
					list.addMouseListener(new MouseAdapter() {
						public void mouseClicked(MouseEvent event) {
							if(event.getClickCount()==2&&list.locationToIndex(event.getPoint())!=-1)
								pick.getAction().actionPerformed(null);
						}
					});
					list.setFont(new Font(Font.MONOSPACED,Font.PLAIN,12));
					JScrollPane scroll = new JScrollPane(list);
					
					pick.setAction(new AbstractAction("Pick") {
						private static final long serialVersionUID = 1l;
						@Override public void actionPerformed(ActionEvent e) {
							if(!list.isSelectionEmpty())
								future.set(Lists.newArrayList(Iterables.concat(list.getSelectedValuesList())));
							dialog.dispose();
						}
					});
					cancel.addActionListener(new ActionListener() {
						@Override public void actionPerformed(ActionEvent e) { dialog.dispose(); }
					});
					
					layout.setHorizontalGroup(
						layout.createParallelGroup(Alignment.TRAILING)
							.addComponent(label)
							.addComponent(scroll, 325, 325, GroupLayout.DEFAULT_SIZE)
							.addGroup(layout.createSequentialGroup()
								.addComponent(pick)
								.addComponent(cancel)	
							)
					);
					layout.linkSize(SwingConstants.HORIZONTAL, scroll, label);
					layout.linkSize(SwingConstants.HORIZONTAL, pick, cancel);
					
					layout.setVerticalGroup(
						layout.createSequentialGroup()
							.addComponent(label)
							.addComponent(scroll, 0, 125, GroupLayout.DEFAULT_SIZE)
							.addGroup(layout.createParallelGroup()
								.addComponent(pick)
								.addComponent(cancel)
							)
					);
					
					dialog.pack();
					dialog.setResizable(false);
					dialog.setLocationRelativeTo(AnalysisTool.this);
					dialog.setVisible(true);
					
					return future.get();
				}
			}
		});
		
		menubar = new JMenuBar(); menu = new JMenu[4];
		menubar.add(menu[MENU_FILE]=new JMenu("File"));
		menubar.add(menu[MENU_EDIT]=new JMenu("Edit"));
		menubar.add(menu[MENU_WINDOW]=new JMenu("Window"));
		menubar.add(menu[MENU_HELP]=new JMenu("Help"));
		setJMenuBar(menubar);
		
		submenu = new JMenu[2];
		menu[MENU_FILE].add(createMenuItem("New", "document", KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK), ACTION_NEW, this));
		menu[MENU_FILE].add(createMenuItem("Open...", "folder-horizontal-open", KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK), ACTION_OPEN, this));
		menu[MENU_FILE].add(createMenuItem("Save", "disk", KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK), ACTION_SAVE, this));
		menu[MENU_FILE].add(createMenuItem("Save As...", "disk--arrow", ACTION_SAVE_AS, this));
		menu[MENU_FILE].add(createSeparator());
		menu[MENU_FILE].add(submenu[SUBMENU_IMPORT]=createSubmenu("Import", "door_in"));
		submenu[SUBMENU_IMPORT].add(createMenuItem("FASTA File", ACTION_IMPORT, this));
		submenu[SUBMENU_IMPORT].add(createMenuItem("GenBank...", ACTION_GENBANK, this));
		menu[MENU_FILE].add(createMenuItem("Export...", "door_out", ACTION_EXPORT, this));
		menu[MENU_FILE].add(createSeparator());
		menu[MENU_FILE].add(submenu[SUBMENU_RECENT]=createSubmenu("Recent Files"));
		menu[MENU_FILE].add(createSeparator());
		menu[MENU_FILE].add(createMenuItem("Exit", "control-power", ACTION_EXIT, this));
		menu[MENU_EDIT].add(createMenuItem("Undo", "arrow_undo", KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK), ACTION_UNDO, this));
		menu[MENU_EDIT].add(createMenuItem("Redo", "arrow_redo", KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK), ACTION_REDO, this));
		menu[MENU_EDIT].add(createSeparator());
		menu[MENU_EDIT].add(createMenuItem("Cut", "cut", KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK), ACTION_CUT, this));
		menu[MENU_EDIT].add(createMenuItem("Copy", "page_copy", KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK), ACTION_COPY, this));
		menu[MENU_EDIT].add(createMenuItem("Paste", "page_paste", KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK), ACTION_PASTE, this));
		menu[MENU_EDIT].add(createMenuItem("Delete", "cross", KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), ACTION_DELETE, this));
		menu[MENU_EDIT].add(createSeparator());
		menu[MENU_EDIT].add(createMenuItem("Find...", KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK), ACTION_FIND, this));
		menu[MENU_EDIT].add(createMenuItem("Find Next", KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0), ACTION_FIND_NEXT, this));
		menu[MENU_EDIT].add(createMenuItem("Replace...", KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK), ACTION_REPLACE, this));
		menu[MENU_EDIT].add(createMenuItem("Split...", ACTION_SPLIT, this));
		menu[MENU_EDIT].add(createMenuItem("Go To...", KeyStroke.getKeyStroke(KeyEvent.VK_G, InputEvent.CTRL_DOWN_MASK), ACTION_GO_TO, this));
		menu[MENU_EDIT].add(createSeparator());
		menu[MENU_EDIT].add(createMenuItem("Select All", KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK), ACTION_SELECT_ALL, this));
		menu[MENU_EDIT].add(createMenuItem("Clear All", ACTION_CLEAR_ALL, this));
		menu[MENU_WINDOW].add(createMenuItem("New Window", ACTION_NEW_WINDOW, this));
		menu[MENU_WINDOW].add(createMenuItem("Copy Window", ACTION_COPY_WINDOW, this));
		menu[MENU_WINDOW].add(createMenuItem("Hide Toolbar", ACTION_TOGGLE_TOOLBAR, this));
		menu[MENU_WINDOW].add(createSeparator());
		menu[MENU_WINDOW].add(createMenuItem("Open Batch Tool", "calculator", KeyStroke.getKeyStroke(KeyEvent.VK_B, InputEvent.CTRL_DOWN_MASK), ACTION_SHOW_BATCH, this));
		menu[MENU_WINDOW].add(createMenuItem("Add Sequence", "calculator_add", ACTION_ADD_TO_BATCH, this));
		menu[MENU_WINDOW].add(createMenuItem("BDA Tool", "bda", ACTION_SHOW_BDA, this));
		menu[MENU_WINDOW].add(createSeparator());
		menu[MENU_WINDOW].add(createMenuItem("Preferences", ACTION_PREFERENCES, this));
		menu[MENU_HELP].add(createMenuItem("Help Contents", "help", ACTION_HELP, this));
		menu[MENU_HELP].add(createSeparator());
		menu[MENU_HELP].add(createMenuItem("About GCAT", "icon", ACTION_ABOUT, this));
		for(String action:new String[]{ACTION_SAVE,ACTION_UNDO,ACTION_REDO,ACTION_CUT,ACTION_COPY,ACTION_DELETE})
			getMenuItem(menubar,action).setEnabled(false);
		getMenuItem(menubar,ACTION_PREFERENCES).setEnabled(false);
		updateRecent();
		
		toolbar = new JToolBar[4];
		toolbar[TOOLBAR_FILE] = new JToolBar("File");
		toolbar[TOOLBAR_FILE].add(createToolbarButton("New File", "document", ACTION_NEW, this));
		toolbar[TOOLBAR_FILE].add(createToolbarButton("Open File", "folder-horizontal-open", ACTION_OPEN, this));
		toolbar[TOOLBAR_FILE].add(createToolbarButton("Save File", "disk", ACTION_SAVE, this));
		toolbar[TOOLBAR_FILE].add(createToolbarButton("Save As File", "disk--arrow", ACTION_SAVE_AS, this));
		toolbar[TOOLBAR_FILE].addSeparator();
		toolbar[TOOLBAR_FILE].add(createToolbarMenuButton("Import", "door_in", new JMenuItem[]{
			createMenuItem("FASTA File", ACTION_IMPORT, this),
			createMenuItem("GenBank...", ACTION_GENBANK, this)
		}));
		toolbar[TOOLBAR_FILE].add(createToolbarButton("Export", "door_out", ACTION_EXPORT, this));
		
		toolbar[TOOLBAR_WINDOW] = new JToolBar("Window");
		toolbar[TOOLBAR_WINDOW].add(createToolbarTextButton("Batch Tool", "calculator", ACTION_SHOW_BATCH, this));
		toolbar[TOOLBAR_WINDOW].add(createToolbarButton("Add Sequence to Batch Tool", "calculator_add", ACTION_ADD_TO_BATCH, this));
		toolbar[TOOLBAR_WINDOW].add(createToolbarButton("BDA Tool", "bda", ACTION_SHOW_BDA, this));
		
		toolbar[TOOLBAR_HELP] = new JToolBar("Help");
		toolbar[TOOLBAR_HELP].add(createToolbarButton("Help Contents", "help", ACTION_HELP, this));
		
		getToolbarButton(toolbar,ACTION_SAVE).setEnabled(false);
		
		toolbars = new JPanel(new FlowLayout(FlowLayout.LEADING));
		for(JToolBar bar:toolbar)
			if(bar!=null) toolbars.add(bar);
		add(toolbars,BorderLayout.NORTH);
		
		(editor=new NucleicEditor()).addNucleicListener(this);
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
				updateTitle();
			}
		});
		editor.addNucleicListener(new NucleicAdapter() {
			@Override public void tuplesUndoableChange(NucleicEvent event) {
				getMenuItem(menubar,ACTION_UNDO).setEnabled(editor.canUndo());
				getMenuItem(menubar,ACTION_REDO).setEnabled(editor.canRedo());
			}
		});
		optionLabel = editor.addOption(new Option("label", "Sequence Label", Parameter.Type.TEXT));
		((JTextField)optionLabel.getComponent()).getDocument().addDocumentListener(new DocumentListener() {
			@Override public void insertUpdate(DocumentEvent event) { changedUpdate(event); }
			@Override public void removeUpdate(DocumentEvent event) { changedUpdate(event); }
			@Override public void changedUpdate(DocumentEvent event) { editor.setDirty(); updateTitle(); }
		});
		optionLabel.setPreferredSize(new Dimension(140,optionLabel.getPreferredSize().height));
		editor.getOptionPanel().add(new JButton(new AbstractAction("Clear Editor") {
			private static final long serialVersionUID = 1;
			@Override public void actionPerformed(ActionEvent e) { editor.setText(null); }
		}));
		
		findDialog = new FindDialog();
		
		split = new JSplitPane[2];
		split[0] = createSplitPane(JSplitPane.VERTICAL_SPLIT,false,false,0.725,editor,new JRootPane() { private static final long serialVersionUID = 1L; {			
			JPanel contentPane = (JPanel)getContentPane();
			contentPane.setLayout(new BorderLayout());
			
			JScrollPane consoleScroll = new JScrollPane(consolePane=new ConsolePane());
			contentPane.add(consoleScroll,BorderLayout.CENTER);
			consoleScroll.setBorder(null);
			
			JPanel glassPane = (JPanel)getGlassPane();
			glassPane.setVisible(true);
			glassPane.setLayout(new FlowLayout(FlowLayout.RIGHT));

			JButton copyButton = new IconButton(getImageIcon("page_copy"),"Copy to clipboard");
			copyButton.addActionListener(new ActionListener() {
				@Override public void actionPerformed(ActionEvent e) { consolePane.copyText(); }
			});
			glassPane.add(copyButton);
			
			JButton clearButton = new IconButton(getImageIcon("bin_closed"),"Clean console");
			clearButton.addActionListener(new ActionListener() {
				@Override public void actionPerformed(ActionEvent e) { consolePane.clearText(); }
			});
			glassPane.add(clearButton);
		}});
		split[1] = createSplitPane(JSplitPane.HORIZONTAL_SPLIT,false,true,410,0.195,new JScrollPane(catalogPanel=createCatalog()),split[0]);
		add(split[1],BorderLayout.CENTER);
		
		add(bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT)),BorderLayout.SOUTH);
		
		status = new JLabel();
		status.setBorder(new EmptyBorder(0,5,0,5));
		status.setHorizontalAlignment(JLabel.RIGHT);
		bottom.add(status);
		
		cancel = new IconButton(getImageIcon("cancel"),"Cancel");
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
	
	public NucleicEditor getEditor() {
		return editor;
	}
	
	public void setTuples(Collection<Tuple> tuples) { editor.setTuples(tuples); }
	public Collection<Tuple> getTuples() {
		return editor.getTuples();
	}
	
	protected CatalogPanel createCatalog() {
		catalogPanel = new CatalogPanel();
		
		// Groups for operations
		for(Entry<String,Collection<Class<? extends Operation>>> group:Operation.getGroups().asMap().entrySet())
			catalogPanel.addPage(createGroupPanel(group.getValue()),group.getKey(),false);
		
		return catalogPanel;
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
		final String name = Operation.getName(operation), icon = Operation.getIcon(operation);
		
		JButton button = new JButton(name);
		if(Test.class.isAssignableFrom(operation)&&!name.endsWith("?"))
			button.setText(name+"?");
		if(icon!=null&&!icon.isEmpty())
			button.setIcon(getImageIcon(icon));
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
		
		JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		panel.add(buttons, BorderLayout.EAST);

		JButton batch = new IconButton(getImageIcon("calculator_add_small"),"Add operation to batch");
		batch.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				List<Object> values = new ArrayList<>(parameters!=null?parameters.length:0);
				if(parameters!=null) for(Parameter parameter:parameters)
					values.add(components.get(parameter).getValue());
				addBatchOperation(operation,values.toArray());
			}
		});
		buttons.add(batch);
		
		if(operation.isAnnotationPresent(Documented.class)) {
			JButton help = new IconButton(getImageIcon("help"),"Show help for this operation");
			help.addActionListener(new ActionListener() {
				@Override public void actionPerformed(ActionEvent event) {
					editor.toggleHelpPage(operation.getAnnotation(Documented.class));
				}
			});
			buttons.add(help);
		}
		
		container.add(panel);
	}
	public void addBatchOperation(Class<? extends Operation> operation,Object... values) {
		showBatch(); batch.addOperation(operation,values);		
	}
	
	@Override public void actionPerformed(ActionEvent event) {
		String action;
		switch(action=event.getActionCommand()) {
		case ACTION_NEW: newFile(); break;
		case ACTION_OPEN: openFile(); break;
		case ACTION_SAVE: saveFile(); break;
		case ACTION_SAVE_AS: saveFileAs(); break;
		case ACTION_IMPORT: importFile(); break;
		case ACTION_GENBANK: importGenBank(); break;
		case ACTION_EXPORT: exportFile(); break;
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
		case ACTION_SHOW_BDA: showBDA(); break;
		case ACTION_SHOW_BATCH: showBatch(); break;
		case ACTION_ADD_TO_BATCH: addToBatch(); break;
		case ACTION_HELP: showHelp(); break;
		case ACTION_ABOUT: showAbout(); break;
		default: System.err.println(String.format("Action %s not implemented.", action)); }
	}

	public ListenableFuture<Collection<Tuple>> submitOperation(Class<? extends Operation> operation) { return submitAction(new Action(operation)); }
	public ListenableFuture<Collection<Tuple>> submitOperation(Class<? extends Operation> operation,Object... values) { return submitAction(new Action(operation,attributes,values)); }
	public ListenableFuture<Collection<Tuple>> submitOperation(Class<? extends Operation> operation,Map<TaskAttribute,Object> attributes,Object... values) { return submitAction(new Action(operation,attributes,values)); }
	protected ListenableFuture<Collection<Tuple>> submitAction(Action action) {
		Class<? extends Operation> operation = action.getOperation(); String name = Operation.getName(operation); 
		ListenableFuture<Collection<Tuple>> future = submitTask(action.new Task(editor.getTuples()));
		Futures.addCallback(future, new FutureCallback<Collection<Tuple>>() {
			@Override public void onSuccess(Collection<Tuple> tuples) {
				if(Transformation.class.isAssignableFrom(operation)||Split.class.isAssignableFrom(operation))
					editor.setTuples(tuples);
				else if(Test.class.isAssignableFrom(operation))
					consolePane.appendText(String.format("Sequence <b>IS</b> \"%s\"", name),ConsolePane.SUCCESS);
			}
			@Override public void onFailure(Throwable thrown) {
				if(Test.class.isAssignableFrom(operation)&&thrown instanceof Test.Failed)
					 consolePane.appendText(String.format("Sequence is <b>NOT</b> \"%s\"", name),ConsolePane.FAILURE);
				else consolePane.appendText(String.format("Exception in operation \"%s\": %s", name, Optional.ofNullable(thrown.getMessage()).orElse("Unknown cause")),ConsolePane.FAILURE);
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
		ListenableFuture<Collection<Tuple>> future = service.submit(InjectionLogger.injectLogger(consolePane,task));
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
	
	public void newFile() {
		if(!confirmIfDirty())
			return;
		
		newText(EMPTY);
		
		editor.setClean(); //clean on open, dirty on import
		currentFile(null);
	}
	public void newText(String text) {
		editor.setEditorMode(EditorMode.SEQUENCE);
		editor.setTupleLength(0);
		editor.setDefaultAcid(null);
		editor.setText(text);
		
		boolean emptyText = text==null||text.isEmpty();
		Collection<Tuple> tuples = editor.getTuples();
		editor.setTupleLength(emptyText?3:Tuple.tuplesLength(tuples));
		editor.setDefaultAcid(emptyText?Acid.RNA:Tuple.tuplesAcid(tuples));
		optionLabel.setValue(null);
		
		editor.getTextPane().setCaretPosition(0); //scroll to top
	}
	
	public void openFile() {
		if(!confirmIfDirty())
			return;
	
		JFileChooser chooser = new FileNameExtensionFileChooser(GENETIC_EXTENSION_FILTER,TEXT_EXTENSION_FILTER);
		if(chooser.showOpenDialog(this)==JFileChooser.APPROVE_OPTION)
			openFile(chooser.getSelectedFile());
	}
	public void openFile(File file) {
		try {
			newText(new String(readFile(file)));
			UserDefinedFileAttributeView view = Files.getFileAttributeView(file.toPath(),UserDefinedFileAttributeView.class);
			if(view.list().contains(ATTRIBUTE_LABEL)) {
				ByteBuffer buffer = ByteBuffer.allocate(view.size(ATTRIBUTE_LABEL));
				view.read(ATTRIBUTE_LABEL, buffer); buffer.flip();
				optionLabel.setValue(Charset.defaultCharset().decode(buffer).toString());
			} else optionLabel.setValue(null);
			
			editor.setClean(); //clean on open, dirty on import
			currentFile(file);
			recentFile(file);
		} catch(IOException e) {
			JOptionPane.showMessageDialog(this,"Could not open file:\n\n"+e.getMessage(),
				"Open",JOptionPane.WARNING_MESSAGE);
		}
	}
	
	public void importFile() {
		if(!confirmIfDirty())
			return;
		
		JFileChooser chooser = new FileNameExtensionFileChooser(FASTA_EXTENSION_FILTER);
		if(chooser.showOpenDialog(this)==JFileChooser.APPROVE_OPTION)
			importFile(chooser.getSelectedFile(), chooser.getFileFilter());
	}
	public void importFile(File file) { importFile(file, null); }
	public void importFile(File file, FileFilter filter) {
		if(filter==FASTA_EXTENSION_FILTER||FASTA_EXTENSION_FILTER.accept(file))
			importFastaFile(file);
		else JOptionPane.showMessageDialog(this,String.format("File format of %s not recognized.",file.getName()),"Import",JOptionPane.WARNING_MESSAGE);
	}
	
	protected void importFastaFile(File file) {
		try(InputStream input=new FileInputStream(file)) {
			importFastaFile(input);
			recentFile(file);
		} catch(IOException e) { /* nothing to do here */ } 
	}
	protected void importFastaFile(InputStream input) {
		try {
			DefiniteFuture<Entry<String,DNASequence>> future = new DefiniteFuture<>();
			Map<String,DNASequence> fastaFile = FastaReaderHelper.readFastaDNASequence(input);
			if(fastaFile==null||fastaFile.isEmpty())
				throw new Exception("FASTA file does not contain any sequences.");
			else if(fastaFile.size()==1)
				future.set(fastaFile.entrySet().iterator().next());
			else {				
				JDialog dialog = new JDialog(AnalysisTool.this,"Pick Sequence",true);
				dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				
				GroupLayout layout = new GroupLayout(dialog.getContentPane());
				dialog.getContentPane().setLayout(layout);
				layout.setAutoCreateGaps(true);
				layout.setAutoCreateContainerGaps(true);
				
				JLabel label = new JLabel("Pick one sequences from the list:");
				JButton pick = new JButton(), cancel = new JButton("Cancel");
				
				@SuppressWarnings("unchecked") JList<Map.Entry<String,DNASequence>> list = new JList<>(fastaFile.entrySet().toArray(new Map.Entry[0]));
				list.setCellRenderer(new ListCellRenderer<Entry<String,DNASequence>>() {
					private JLabel label = new JLabel();
					@Override public Component getListCellRendererComponent(JList<? extends Entry<String,DNASequence>> list,Entry<String,DNASequence> entry,int index,boolean isSelected,boolean cellHasFocus) {						
				    label.setText("<html><b>"+entry.getKey()+"</b>: "+ellipsisText(entry.getValue().toString(),100));
				    
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
				list.addMouseListener(new MouseAdapter() {
					public void mouseClicked(MouseEvent event) {
						if(event.getClickCount()==2&&list.locationToIndex(event.getPoint())!=-1)
							pick.getAction().actionPerformed(null);
					}
				});
				list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				list.setFont(new Font(Font.MONOSPACED,Font.PLAIN,12));
				list.setSelectedIndex(0);
				JScrollPane scroll = new JScrollPane(list);
				
				pick.setAction(new AbstractAction("Pick") {
					private static final long serialVersionUID = 1l;
					@Override public void actionPerformed(ActionEvent e) {
						if(!list.isSelectionEmpty())
							future.set(list.getSelectedValue());
						dialog.dispose();
					}
				});				
				cancel.addActionListener(new ActionListener() {
					@Override public void actionPerformed(ActionEvent e) { dialog.dispose(); }
				});
				
				layout.setHorizontalGroup(
					layout.createParallelGroup(Alignment.TRAILING)
						.addComponent(label)
						.addComponent(scroll, 325, 325, GroupLayout.DEFAULT_SIZE)
						.addGroup(layout.createSequentialGroup()
							.addComponent(pick)
							.addComponent(cancel)	
						)
				);
				layout.linkSize(SwingConstants.HORIZONTAL, scroll, label);
				layout.linkSize(SwingConstants.HORIZONTAL, pick, cancel);
				
				layout.setVerticalGroup(
					layout.createSequentialGroup()
						.addComponent(label)
						.addComponent(scroll, 0, 125, GroupLayout.DEFAULT_SIZE)
						.addGroup(layout.createParallelGroup()
							.addComponent(pick)
							.addComponent(cancel)
						)
				);
				
				dialog.pack();
				dialog.setResizable(false);
				dialog.setLocationRelativeTo(AnalysisTool.this);
				dialog.setVisible(true);
			}
			
			Entry<String,DNASequence> sequence = future.get();
			if(sequence!=null) {
				newText(sequence.getValue().getSequenceAsString());
				optionLabel.setValue(sequence.getKey());

				editor.setDirty(); //clean on open, dirty on import
				currentFile(null); //null because FASTA will overwrite other entires in FASTA file
			}
		} catch(Error | Exception e) { JOptionPane.showMessageDialog(this,"Could not open FASTA file:\n\n"+e.getMessage(),"Import FASTA",JOptionPane.WARNING_MESSAGE); }
	}
	
	public void importGenBank() {
		if(!confirmIfDirty())
			return;
		
		JDialog dialog = new JDialog(AnalysisTool.this,"Search GenBank",true);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		
		GroupLayout layout = new GroupLayout(dialog.getContentPane());
		dialog.getContentPane().setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		
		JLabel label = new JLabel("Search Term:");
		JButton search = new JButton(), pick = new JButton(), cancel = new JButton("Cancel");
		
		JTextField term = new JTextField();
		term.addKeyListener(new KeyAdapter() {
			@Override public void keyPressed(KeyEvent event) {
				if(event.getKeyCode()==KeyEvent.VK_ENTER)
					search.getAction().actionPerformed(null);
			}
		});
		
		DefaultListModel<Map<String,Object>> model = new DefaultListModel<>();
		JList<Map<String,Object>> list = new JList<>(model);
		list.setCellRenderer(new ListCellRenderer<Map<String,Object>>() {
			private JLabel label = new JLabel();
			@Override public Component getListCellRendererComponent(JList<? extends Map<String,Object>> list,Map<String,Object> entry,int index,boolean isSelected,boolean cellHasFocus) {						
		    label.setText("<html><b>"+entry.get("caption")+"</b>: "+entry.get("title"));
		    
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
		list.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent event) {
				if(event.getClickCount()==2&&list.locationToIndex(event.getPoint())!=-1)
					pick.getAction().actionPerformed(null);
			}
		});
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setFont(new Font(Font.MONOSPACED,Font.PLAIN,12));
		list.setSelectedIndex(0);
		JScrollPane scroll = new JScrollPane(list);
		
		search.setAction(new AbstractAction("Search", getImageIcon("magnifier")) {
			private static final long serialVersionUID = 1l;
			@Override public void actionPerformed(ActionEvent event) {
				search.setEnabled(false); search.setText("Searching...");
				new Thread(new Runnable() {
					@Override public void run() {
						try {
							List<Map<String,Object>> summary = GenBank.summary(DATABASE_NUCLEOTIDE, GenBank.search(DATABASE_NUCLEOTIDE, term.getText()));
							SwingUtilities.invokeLater(new Runnable() { @Override public void run() {
								model.removeAllElements(); summary.forEach(element->model.addElement(element));
							}});
						} catch(Exception e) {
							SwingUtilities.invokeLater(new Runnable() { @Override public void run() {
								model.removeAllElements();
								JOptionPane.showMessageDialog(dialog,"Could not search GenBank:\n\n"+e.getMessage(),"Search GenBank",JOptionPane.WARNING_MESSAGE);
							}});
						} finally { search.setEnabled(true); search.setText("Search"); }
					}
				}).start();
			}
		});
		
		pick.setAction(new AbstractAction("Pick") {
			private static final long serialVersionUID = 1l;
			@Override public void actionPerformed(ActionEvent event) {
				if(!list.isSelectionEmpty())
					importGenBank(list.getSelectedValue().get("uid").toString());
				dialog.dispose();
			}
		});
		cancel.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) { dialog.dispose(); }
		});
		
		layout.setHorizontalGroup(
			layout.createParallelGroup(Alignment.TRAILING)
				.addGroup(layout.createSequentialGroup()
					.addComponent(label)
					.addComponent(term)
					.addComponent(search)
				)
				.addComponent(scroll, 325, 325, GroupLayout.DEFAULT_SIZE)
				.addGroup(layout.createSequentialGroup()
					.addComponent(pick)
					.addComponent(cancel)	
				)
		);
		layout.linkSize(SwingConstants.HORIZONTAL, pick, cancel);
		
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
					.addComponent(label)
					.addComponent(term)
					.addComponent(search)
				)
				.addComponent(scroll, 0, 125, GroupLayout.DEFAULT_SIZE)
				.addGroup(layout.createParallelGroup()
					.addComponent(pick)
					.addComponent(cancel)
				)
		);
		layout.linkSize(SwingConstants.VERTICAL, label, term, search);
		
		dialog.pack();
		dialog.setMinimumSize(dialog.getSize());
		dialog.setSize(500, 300);
		dialog.setLocationRelativeTo(AnalysisTool.this);
		dialog.setVisible(true);
	}
	public void importGenBank(String accessionID) {
		try {
			DNASequence sequence = GenBank.sequence(accessionID);
			
			newText(sequence.getSequenceAsString());
			optionLabel.setValue(sequence.getAccession().toString());
			
			editor.setDirty(); //clean on open, dirty on import
			currentFile(null);
		} catch(Error | Exception e) {
			JOptionPane.showMessageDialog(this,"Could not import "+accessionID+" from GenBank:\n\n"+e.getMessage(),
				"Import FASTA",JOptionPane.WARNING_MESSAGE);
		}
	}
	
	public boolean saveFile() {
		if(currentFile!=null)
		     return saveFile(currentFile);
		else return saveFileAs();
	}
	public boolean saveFileAs() {
		JFileChooser chooser = new FileNameExtensionFileChooser(false,GENETIC_EXTENSION_FILTER,FASTA_EXTENSION_FILTER,TEXT_EXTENSION_FILTER); chooser.setDialogTitle("Save As");
		if(chooser.showSaveDialog(this)==JFileChooser.APPROVE_OPTION)
		     return saveFile(chooser.getSelectedFile(), chooser.getFileFilter());
		else return false;
	}
	public boolean saveFile(File file) { return saveFile(file, null); }
	public boolean saveFile(File file, FileFilter fileFilter) {
		if(fileFilter==FASTA_EXTENSION_FILTER||FASTA_EXTENSION_FILTER.accept(file))
		     return saveFastaFile(file);
		else return saveNormalFile(file);
	}
	protected boolean saveNormalFile(File file) {
		if(!exportNormalFile(file))
			return false;
		
		editor.setClean(); //clean on save, unchanged on export
		currentFile(file);
		recentFile(file);
		return true;
	}
	protected boolean saveFastaFile(File file) {
		if(!exportFastaFile(file))
			return false;
		
		editor.setClean(); //clean on save, unchanged on export
		currentFile(file);
		recentFile(file);
		return true;
	}
	
	public boolean exportFile() {
		JFileChooser chooser = new FileNameExtensionFileChooser(false,FASTA_EXTENSION_FILTER); chooser.setDialogTitle("Export");
		if(chooser.showSaveDialog(this)==JFileChooser.APPROVE_OPTION)
		     return exportFile(chooser.getSelectedFile(), chooser.getFileFilter());
		else return false;
	}
	public boolean exportFile(File file) { return exportFile(file, null); }
	public boolean exportFile(File file, FileFilter fileFilter) {
		if(fileFilter==FASTA_EXTENSION_FILTER||FASTA_EXTENSION_FILTER.accept(file))
         return exportFastaFile(file);
		else return exportNormalFile(file);
	}
	protected boolean exportNormalFile(File file) {
		try {
			writeFile(editor.getText(), file);
			UserDefinedFileAttributeView view = Files.getFileAttributeView(file.toPath(), UserDefinedFileAttributeView.class);
			view.write(ATTRIBUTE_LABEL, Charset.defaultCharset().encode((String)optionLabel.getValue()));
			return true;
		}	catch(IOException e) { JOptionPane.showMessageDialog(this,"Could not write file:\n\n"+e.getMessage(),"Save File",JOptionPane.WARNING_MESSAGE); return false; }
	}
	protected boolean exportFastaFile(File file) {
		try {
			DNASequence sequence = new DNASequence(Characters.WHITESPACE.replace(editor.getText(),EMPTY));
			sequence.setAccession(new AccessionID((String)optionLabel.getValue()));
			FastaWriterHelper.writeSequence(file,sequence);
			return true;
		}	catch(Error | Exception e) { JOptionPane.showMessageDialog(this,"Could not write FASTA file:\n\n"+e.getMessage(),"Save File",JOptionPane.WARNING_MESSAGE); return false; }
	}
	
	private boolean confirmIfDirty() {
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
	
	private void currentFile(File file) {
		currentFile = file; updateTitle();
		boolean hasCurrentFile = currentFile!=null;
		getMenuItem(menu[0],ACTION_SAVE).setEnabled(hasCurrentFile);
		getToolbarButton(toolbar[0],ACTION_SAVE).setEnabled(hasCurrentFile);
	}
	private void recentFile(File file) {
		String path = file.getAbsolutePath();
		List<String> paths = new ArrayList<>(Arrays.asList(getConfiguration(CONFIGURATION_RECENT,EMPTY).split("\\|")));
		paths.remove(path); while(paths.size()>9) paths.remove(9);
		paths.add(0,path); setConfiguration(CONFIGURATION_RECENT,paths.stream().collect(Collectors.joining("|")));
		updateRecent();
	}
	private void updateRecent() {
		submenu[SUBMENU_RECENT].removeAll();
		String[] paths = getConfiguration(CONFIGURATION_RECENT,EMPTY).split("\\|");
		for(String path:paths) {
			final File file = new File(path);
			if(file.isFile()) submenu[SUBMENU_RECENT].add(
				new JMenuItem(new AbstractAction(file.getName()) {
					private static final long serialVersionUID = 1l;
					@Override public void actionPerformed(ActionEvent e) { openFile(file); }
				}));
		}
	}
	private void updateTitle() {
		StringBuilder title = new StringBuilder();
		if(currentFile!=null) {
			title.append(currentFile.getName());
			if(editor.isDirty())
				title.append('*');
			title.append(" - ");
		} setTitle(title.append(NAME).toString());
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
		if(!findDialog.isVisible())
			findDialog.showFind();
	}
	public void replaceText() {
		if(!findDialog.isVisible())
			findDialog.showReplace();
	}
	public void splitText() {
		if(!findDialog.isVisible())
			findDialog.showSplit();
	}
	public void findNext() {
		String term = findDialog.getTerm();
		if(!term.isEmpty()) {
			JTextPane textPane = editor.getTextPane();
			String text = textPane.getText();
			int start = -1, end = 0, carret = findDialog.searchDown()?textPane.getSelectionEnd():editor.getTextPane().getSelectionStart();
			if(!findDialog.regexSearch()) {
				if(findDialog.searchDown()) {
					if((start=text.indexOf(term, carret))==-1&&findDialog.wrapSearch())
						start = text.indexOf(term);
				} else {
					if((carret==0||(start=text.lastIndexOf(term, carret-term.length()))==-1)&&findDialog.wrapSearch())
						start = text.lastIndexOf(term);
				}
				if(start!=-1) end = start+term.length();
			} else {
				Pattern pattern;
				try { pattern = Pattern.compile(term, Pattern.CASE_INSENSITIVE); }
				catch(PatternSyntaxException e) {
					JOptionPane.showMessageDialog(findDialog, e.getLocalizedMessage(), findDialog.getTitle(), JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				Matcher matcher = pattern.matcher(text);
				if(findDialog.searchDown()) {
					if(matcher.find(carret)||(findDialog.wrapSearch()&&matcher.find())) {
						start = matcher.start();
						end = matcher.end();
					}
				} else {
					int wrapStart = -1;
					while(matcher.find())
					  if(matcher.end()<=carret) {
					  	start = matcher.start();
					  	end = matcher.end();
					  } else if(start ==-1&&findDialog.wrapSearch()) {
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
			} else JOptionPane.showMessageDialog(findDialog, String.format("Cannot find \"%s\"", term), findDialog.getTitle(), JOptionPane.INFORMATION_MESSAGE);
		} else findText();
	}
	protected void replaceNext() {
		JTextPane textPane = editor.getTextPane();
		int start = textPane.getSelectionStart(), end = textPane.getSelectionEnd();
		if(start!=end) {
			String term = findDialog.getTerm(), selected = textPane.getSelectedText(), replace = null;
			if(!findDialog.regexSearch()) {
				if(selected.equalsIgnoreCase(term))
					replace = findDialog.getReplace();
			} else {
				Pattern pattern;
				try { pattern = Pattern.compile(term, Pattern.CASE_INSENSITIVE); }
				catch(PatternSyntaxException e) {
					JOptionPane.showMessageDialog(findDialog, e.getLocalizedMessage(), findDialog.getTitle(), JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				Matcher matcher = pattern.matcher(selected);
				if(matcher.find())
					replace = matcher.replaceAll(findDialog.getReplace());
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
		Pattern pattern; String term = findDialog.getTerm();
		try { pattern = Pattern.compile(findDialog.regexSearch()?term:Pattern.quote(term), Pattern.CASE_INSENSITIVE); }
		catch(PatternSyntaxException e) {
			JOptionPane.showMessageDialog(findDialog, e.getLocalizedMessage(), findDialog.getTitle(), JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		editor.setText(pattern.matcher(editor.getText()).replaceAll(findDialog.getReplace()));
	}
	protected void splitAt() {
		String term = findDialog.getTerm();
		if(findDialog.regexSearch())
			try { Pattern.compile(findDialog.getTerm()); }
			catch(PatternSyntaxException e) {
				JOptionPane.showMessageDialog(findDialog, e.getLocalizedMessage(), findDialog.getTitle(), JOptionPane.ERROR_MESSAGE);
				return;
			}
		
		submitOperation(Split.Expression.class, term, findDialog.regexSearch(), findDialog.removeDelimiter());
	}
	
	public void goToTuple() {
		new GoTo().setVisible(true);
	}
	
	public void newTool() {
		new AnalysisTool().setVisible(true);
	}
	public void copyTool() {
		AnalysisTool tool = new AnalysisTool();
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
		List<AnalysisTool> tools = new ArrayList<>(AnalysisTool.TOOLS);
		Collections.reverse(tools);
		for(AnalysisTool tool:tools)
			if(!tool.closeTool())
				return false;
		return true;
	}
	
	public void toggleToolbar() {
		boolean visible = toolbars.isVisible();
		toolbars.setVisible(!visible);
		getMenuItem(menubar,ACTION_TOGGLE_TOOLBAR).setText(visible?"Show Toolbar":"Hide Toolbar");
	}
	
	public static BatchTool getBatch() { return batch!=null?batch:(batch=new BatchTool()); }
	public void showBatch() {
		BatchTool batch = getBatch();
		if(!batch.isVisible()) {
			batch.setLocationRelativeTo(AnalysisTool.this);
			batch.setVisible(true);
		} else batch.requestFocus();
	}
	public void addToBatch() {
		showBatch(); batch.addSequence(editor.getTuples(),(String)optionLabel.getValue());
	}
	
	public static BDATool getBDA() { return bda!=null?bda:(bda=new BDATool()); }
	public void showBDA() {
		BDATool bda = getBDA();
		if(!bda.isVisible()) {
			bda.setLocationRelativeTo(AnalysisTool.this);
			bda.setVisible(true);
		} else bda.requestFocus();
	}
	
	public void showHelp() { editor.toggleHelpIndex(); }
	
	public void showAbout() { showAbout(this); }
	public static void showAbout(Frame parent) {
		final JDialog about = new JDialog(parent, "About Genetic Code Analysis Toolkit (GCAT)", true);
		setBoxLayout(about.getContentPane(), BoxLayout.Y_AXIS);
		((JPanel)about.getContentPane()).setBorder(new EmptyBorder(10,10,10,10));
		
		Border gap = new EmptyBorder(5,0,5,0); JLabel label;
		about.add(label=new JLabel(getImageIcon("logo"), SwingConstants.CENTER));
		label.setBorder(gap); label.setAlignmentX(CENTER_ALIGNMENT);
		about.add(label=new JLabel("\u00A9 2014-2015 Mannheim University of Applied Sciences - Version "+VERSION));
		label.setBorder(gap); label.setAlignmentX(CENTER_ALIGNMENT);
		label.setFont(label.getFont().deriveFont(Font.BOLD));
		about.add(label=new JLabel("(Elena Fimmel, Lutz Str\u00FCngmann, Markus Gumbel, Kristian Kralji\u0107)"));
		label.setBorder(gap);label.setAlignmentX(CENTER_ALIGNMENT);
		about.add(label=new JLabel("http://www.gcat.bio/"));
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
	
	@Override public void tuplesInsert(NucleicEvent event) { /* nothing to do here */ }
	@Override public void tuplesRemoved(NucleicEvent event) { /* nothing to do here */ }
	@Override public void tuplesUndoableChange(NucleicEvent event) { /* nothing to do here */ }
	@Override public void optionsChange(NucleicEvent event) {
		NucleicOptions options = event.getOptions(),
			oldOptions = event.getOldOptions();
		if(oldOptions==null)
			return;
		if(oldOptions.tupleLength==3&&options.tupleLength==4) {
			// switch to tessera table input
			catalogPanel.showInput(TesseraTable.class);
		} else if(oldOptions.tupleLength==4&&options.tupleLength==3) {
			// switch to codon wheel input
			catalogPanel.showInput(CodonCircle.class);
		}
	}
	
	class CatalogPanel extends JPanel {
		private static final long serialVersionUID = 1l;
		
		private final GridBagConstraints defaultConstraints;
		
		protected List<FoldingPanel> pagePanels = new ArrayList<>();
		protected FoldingPanel inputPanel;
		protected JComboBox<Input> inputCombo;
		protected javax.swing.Action inputPreferences;
		
		protected List<Input> inputs;
		
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
			
			Input input,defaultInput=null; inputs = new ArrayList<>();
			Reflections inputReflections = new Reflections(new ConfigurationBuilder()
				.addClassLoaders(ClasspathHelper.staticClassLoader(),ClasspathHelper.contextClassLoader()/*,ClassLoader.getSystemClassLoader()*/)
				.setUrls(ClasspathHelper.forPackage(Input.class.getPackage().getName()))
				.setScanners(new SubTypesScanner()));
			for(Class<? extends Input> inputClass:inputReflections.getSubTypesOf(Input.class))
				try {
					inputs.add(input=inputClass.newInstance());
					if(CodonCircle.class.equals(inputClass))
						defaultInput = input;
				} catch(InstantiationException|IllegalAccessException e) { /* nothing to do here */ }
			inputPanel = addPage(new JPanel(new BorderLayout()),"Input",true);
			
			inputCombo = new JComboBox<Input>(inputs.toArray(new Input[0]));
			inputCombo.setRenderer(new DefaultListCellRenderer() {
				private static final long serialVersionUID = 1l;
				@Override public java.awt.Component getListCellRendererComponent(JList<?> list,Object value,int index,boolean isSelected,boolean cellHasFocus) {
					super.getListCellRendererComponent(list,value,index,isSelected,cellHasFocus);
					if(value!=null) setText(((Input)value).getName());
					return this;
				}
			});			
			inputCombo.setSelectedItem(defaultInput);
			inputCombo.addActionListener(new ActionListener() {
				@Override public void actionPerformed(ActionEvent event) {
					showInput((Input)inputCombo.getSelectedItem()); }
				}
			);
			
			inputPanel.addInfo(inputCombo);
			inputPanel.addInfo((inputPreferences=new AbstractAction(null,getImageIcon("cog")) {
				private static final long serialVersionUID = 1l;
				@Override public void actionPerformed(ActionEvent event) {
					Input input = (Input)inputCombo.getSelectedItem();
					if(input instanceof Configurable) {
						((Configurable)input).showPreferencesDialog(AnalysisTool.this,"Input Preferences");
						showInput(input);
					}
				}
			}));
			
			showInput(defaultInput);
		}
		
		public List<FoldingPanel> getPages() { return Collections.unmodifiableList(pagePanels); }
		public FoldingPanel addPage(String title, boolean expanded) { return addPage(new JPanel(), title, expanded); }
		public FoldingPanel addPage(Component child, String title, boolean expanded) {
			FoldingPanel page = new FoldingPanel(child, title);
			page.setBorder(CATEGORY_BORDER);
			page.setFont(UIManager.getFont("CheckBox.font").deriveFont(Font.BOLD));
			page.setForeground(UIManager.getColor(TITLE_FOREGROUND));
			page.setExpanded(expanded);
			add(page, defaultConstraints, getComponentCount()-1);
			pagePanels.add(page);
			return page;
		}
		
		public Input showInput(Class<? extends Input> inputClass) throws NoSuchElementException {
			Input input = inputs.stream().filter(inputCandidate->inputClass
				.isAssignableFrom(inputCandidate.getClass())).findFirst().get();
			showInput(input); return input;
		}
		protected void showInput(final Input input) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override public void run() {
					JPanel panel = (JPanel)inputPanel.getChild(); panel.removeAll();
					if(input!=null) panel.add(input.getComponent(editor), BorderLayout.CENTER);
					inputPanel.revalidate(); inputPanel.repaint();
					inputPreferences.setEnabled(input instanceof Configurable);
					if(inputCombo.getSelectedItem()!=input)
					   inputCombo.setSelectedItem(input);
				}
			});
		}
	}
	
	class FindDialog extends JDialog {
		private static final long serialVersionUID = 1l;
		
		private static final int MODE_FIND = 0, MODE_REPLACE = 1, MODE_SPLIT = 2;
		
    protected EventListenerList listenerList = new EventListenerList();
    
    private int mode;
    
    private JLabel what, with;
		private JTextField term, replace;
		private JButton find, findBatch, replaceNext, replaceAll, replaceBatch, split, splitBatch;
		private JCheckBox wrap, regex, delimiter;
		private JRadioButton up, down;
		private JPanel direction, options;
		
		private Document termDefaultDocument, replaceDefaultDocument;
		private NucleicDocument termNucleicDocument, replaceNucleicDocument;
		
		public FindDialog() {
			super(AnalysisTool.this, "Find", false);
			
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
			
			findBatch = new JButton(getImageIcon("calculator_add_small"));
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
			
			replaceBatch = new JButton(getImageIcon("calculator_add_small"));
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
			
			splitBatch = new JButton(getImageIcon("calculator_add_small"));
			splitBatch.setToolTipText("Add split to batch");
			splitBatch.setEnabled(false);
			splitBatch.addActionListener(new ActionListener() {
				@Override public void actionPerformed(ActionEvent e) {
					addBatchOperation(Split.Expression.class,term.getText(),regex.isSelected(),delimiter.isSelected());
				}
			});
			
			JButton help = new JButton(getImageIcon("help_small"));
			help.setToolTipText("Show help");
			help.addActionListener(new ActionListener() {
				@Override public void actionPerformed(ActionEvent e) {
					switch(mode) {
					case MODE_FIND: editor.toggleHelpPage(Test.Expression.class.getAnnotation(Documented.class)); break;
					case MODE_REPLACE: editor.toggleHelpPage(Transformation.Expression.class.getAnnotation(Documented.class)); break;
					case MODE_SPLIT: editor.toggleHelpPage(Split.Expression.class.getAnnotation(Documented.class)); break; }
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
							.addComponent(split, 90, 90, 140)
							.addComponent(splitBatch, 40, 40, 40)
							.addComponent(help, 40, 40, 40)
						)
						.addComponent(replaceNext, 140, 140, 140)
						.addGroup(layout.createSequentialGroup()
							.addComponent(replaceAll, 90, 90, 140)
							.addComponent(replaceBatch, 40, 40, 40)
						)
						.addComponent(cancel, 90, 90, 240)
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
						.addComponent(help)
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
			setLocationRelativeTo(AnalysisTool.this);
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
			switch(this.mode=mode) {
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
			super(AnalysisTool.this, "Go To Tuple", true);
			
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
			setLocationRelativeTo(AnalysisTool.this);
			setResizable(false);
		}
		
		@Override public void requestFocus() {
			number.requestFocus();
		}
		
		public void setNumber(int number) { this.number.setValue(number); }
		public int getNumber() { return ((Number)number.getValue()).intValue(); }
	}
	
	public static void main(final String[] args) {
		// Set to use system proxies
		safeSetSystemProperty("java.net.useSystemProxies",Boolean.TRUE.toString());
		/*try {
			List<Proxy> proxies = ProxySelector.getDefault().select(new URI("http://www.hs-mannheim.de/"));
			if(!proxies.isEmpty()) {
				System.setProperty("java.net.useSystemProxies", "false");
				Proxy proxy = proxies.get(0); InetSocketAddress address = (InetSocketAddress)proxy.address();
				switch(proxy.type()) {
				case DIRECT: break;
				case HTTP:
					System.setProperty("java.net.useSystemProxies", "false");
		      System.setProperty("http.proxyHost", address.getHostName());
		      System.setProperty("http.proxyPort", Integer.toString(address.getPort()));
		      break;
				case SOCKS:
					System.setProperty("java.net.useSystemProxies", "false");
					System.setProperty("socksProxyHost", address.getHostName());
		      System.setProperty("socksProxyPort", Integer.toString(address.getPort()));
		      break;
				}
			}
		} catch (URISyntaxException e) { /* nothing to do here *//* }*/
		
		// Register resource:// & help:// protocols for help
		try {
			URL.setURLStreamHandlerFactory(new URLStreamHandlerFactory() {
				public URLStreamHandler createURLStreamHandler(String protocol) {
					switch(protocol) {
					case "resource":
						return new URLStreamHandler() {
							protected URLConnection openConnection(URL url) throws IOException {
								String path = url.getPath();
								return Optional.ofNullable(Utilities.getResource(path.startsWith("/")?
									path.substring(1):path)).orElseThrow(()->new IOException("Resource not found")).openConnection();
							}
						};
					case "help":
						return new URLStreamHandler() {
							protected URLConnection openConnection(URL url) throws IOException {
								return new URLConnection(url) { public void connect() throws IOException { /* nothing to do here */ } };
							}
						};
					default: return null; }
				}
			});
		} catch(SecurityException e) { /* nothing to do here unfortunately */ }
		
		// Prepare Look and Feel
		Guitilities.prepareLookAndFeel();
		
		// Open AnalysisTool or BatchTool
		SwingUtilities.invokeLater(new Runnable() {
			@Override public void run() {
				if(args.length>=1)
					switch(args[0]) {
					case "-open":
						if(args.length>=2) {
							if(args[1].endsWith(".bda")) {
								BDATool bda = new BDATool();
								bda.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
								bda.openFile(new File(args[1]));
								bda.setVisible(true);
							} else {
								AnalysisTool tool = new AnalysisTool();
								tool.openFile(new File(args[1]));
								tool.setVisible(true);
							}
						}	break;
					default: System.out.println(String.format("Unknown argument \"%s\"", args[0])); }
				else new AnalysisTool().setVisible(true);
			}
		});
	}
}