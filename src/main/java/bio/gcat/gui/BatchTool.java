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

import static bio.gcat.Utilities.*;
import static bio.gcat.batch.Action.TaskAttribute.*;
import static bio.gcat.gui.helper.Guitilities.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.biojava3.core.sequence.AccessionID;
import org.biojava3.core.sequence.DNASequence;
import org.biojava3.core.sequence.io.FastaReaderHelper;
import org.biojava3.core.sequence.io.FastaWriterHelper;
import org.biojava3.core.sequence.template.Sequence;
import bio.gcat.Option;
import bio.gcat.Parameter;
import bio.gcat.Utilities.FileNameExtensionFileChooser;
import bio.gcat.batch.Action;
import bio.gcat.batch.Action.TaskAttribute;
import bio.gcat.batch.Batch;
import bio.gcat.batch.Batch.Result;
import bio.gcat.gui.helper.AttachedScrollPane;
import bio.gcat.gui.helper.CollectionListModel;
import bio.gcat.gui.helper.Guitilities;
import bio.gcat.nucleic.Tuple;
import bio.gcat.operation.Operation;
import bio.gcat.operation.analysis.Analysis;
import bio.gcat.operation.split.Split;
import bio.gcat.operation.test.Test;
import bio.gcat.operation.transformation.Transformation;
import com.google.common.collect.EnumMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multiset.Entry;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

public class BatchTool extends JFrame implements ActionListener, ListDataListener, ListSelectionListener {
	private static final long serialVersionUID = 1l;

	public static final String GENETIC_EXTENSION = "genb";
	public static final FileNameExtensionFilter
		GENETIC_EXTENSION_FILTER = new FileNameExtensionFilter("Genetic Sequences (*."+GENETIC_EXTENSION+",*."+AnalysisTool.GENETIC_EXTENSION+")", GENETIC_EXTENSION, AnalysisTool.GENETIC_EXTENSION),
		FASTA_EXTENSION_FILTER = new FileNameExtensionFilter("FASTA Files (*.fasta;*.fna,*.fa)","fasta","fna","fa"),
		TEXT_EXTENSION_FILTER = new FileNameExtensionFilter("Text Documents (*.txt)", "txt");
	
	private static final String
		ACTION_IMPORT = "import",
		ACTION_EXPORT = "export",
		ACTION_CLOSE = "exit",
		ACTION_ACTION_ADD = "action_add",
		ACTION_ACTION_EDIT = "action_edit",
		ACTION_ACTION_REMOVE = "action_remove",
		ACTION_ACTIONS_CLEAR = "actions_clear",
		ACTION_ACTION_MOVE_UP = "action_move_up",
		ACTION_ACTION_MOVE_DOWN = "action_move_down",
		ACTION_SEQUENCES_EDIT = "sequences_edit",
		ACTION_SEQUENCES_REMOVE = "sequences_remove",
		ACTION_SEQUENCES_CLEAR = "sequences_clear",
		ACTION_EXECUTE = "execute",
		ACTION_PREFERENCES = "preferences",
		ACTION_ABOUT = "about";
	
	private static final Parameter
		PARAMETER_TEST_CRITERIA = new TaskAttributeParameter(TEST_CRITERIA, "Test Criteria", TEST_CRITERIA_BREAK_IF_FALSE,
			new Boolean[]{TEST_CRITERIA_BREAK_IF_FALSE,TEST_CRITERIA_BREAK_IF_TRUE,TEST_CRITERIA_NEVER_BREAK},
			"Break If False", "Break If True", "Never Break");
	
	protected final ListeningExecutorService service = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(Math.max(1,Runtime.getRuntime().availableProcessors()-1)));
	protected final Split.Pick
		splitPickAll = new Split.Pick() {
			@Override public Collection<Tuple> pick(List<Collection<Tuple>> split) {
				     if(split==null||split.isEmpty()) return null;
				else if(split.size()==1) return split.get(0);
				for(Collection<Tuple> tuples:split.subList(1,split.size()))
					BatchTool.this.addSequence(tuples);
				return split.get(0);
			}
		},
		splitPickRandom = new Split.Pick() {
			private final Random random = new Random();
			@Override public Collection<Tuple> pick(List<Collection<Tuple>> split) {
				     if(split==null||split.isEmpty()) return null;
				else if(split.size()==1) return split.get(0);
				int pick = random.nextInt(split.size());
				for(int index=0;index<split.size();index++)
					if(index!=pick&&random.nextBoolean())
						BatchTool.this.addSequence(split.get(index));						
				return split.get(pick);
			}
		};
		
	private final Parameter
		parameterSplitPick = new TaskAttributeParameter(SPLIT_PICK, "Split Pick", SPLIT_PICK_FIRST,
			new Split.Pick[]{SPLIT_PICK_FIRST,SPLIT_PICK_LAST,SPLIT_PICK_ANY,splitPickAll,splitPickRandom},
			"First","Last","Any","All","Random");
	
	private JMenuBar menubar;
	private JMenu[] menu;
	private JPanel toolbars, bottom;
	private JToolBar[] toolbar;
	private JLabel status;
	private JButton cancel;
	
	private ActionPanel actionPanel;
	private SequenceList sequenceList;
	private NumberDisplay numbers;
	
	private List<ListenableFuture<Result>> futures = Collections.synchronizedList(new LinkedList<>());;
	
	public BatchTool() {		
		super("Batch Tool - "+AnalysisTool.NAME);
		setIconImage(getImage("calculator"));
		setMinimumSize(new Dimension(660,400));
		setPreferredSize(new Dimension(1020,400));
		setSize(getPreferredSize());
		setLocationByPlatform(true);
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

		menubar = new JMenuBar(); menu = new JMenu[4];
		menubar.add(menu[0] = new JMenu("File"));
		menubar.add(menu[1] = new JMenu("Edit"));
		menubar.add(menu[2] = new JMenu("Window"));
		menubar.add(menu[3] = new JMenu("Help"));
		setJMenuBar(menubar);
		
		menu[0].add(createMenuItem("Import...", "table_go", KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.CTRL_DOWN_MASK), ACTION_IMPORT, this));
		menu[0].add(createMenuItem("Export...", "table_save", ACTION_EXPORT, this));
		menu[0].add(createMenuItem("Execute", "table_lightning", KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.CTRL_DOWN_MASK), ACTION_EXECUTE, this));
		menu[0].add(createSeparator());
		menu[0].add(createMenuItem("Close Window", "cross", ACTION_CLOSE, this));
		menu[1].add(createMenuText("Actions:"));
		menu[1].add(createMenuItem("Add", KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK), ACTION_ACTION_ADD, this));
		menu[1].add(createMenuItem("Edit...", KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_DOWN_MASK), ACTION_ACTION_EDIT, this));
		menu[1].add(createMenuItem("Remove", KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), ACTION_ACTION_REMOVE, this));
		menu[1].add(createMenuItem("Clear", ACTION_ACTIONS_CLEAR, this));
		menu[1].add(seperateMenuItem(createMenuItem("Move Up", KeyStroke.getKeyStroke(KeyEvent.VK_UP, InputEvent.CTRL_DOWN_MASK), ACTION_ACTION_MOVE_UP, this)));
		menu[1].add(createMenuItem("Move Down", KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, InputEvent.CTRL_DOWN_MASK), ACTION_ACTION_MOVE_DOWN, this));
		menu[1].add(createSeparator());
		menu[1].add(createMenuText("Sequences:"));
		menu[1].add(createMenuItem("Edit", ACTION_SEQUENCES_EDIT, this));
		menu[1].add(createMenuItem("Remove", "table_row_delete", KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), ACTION_SEQUENCES_REMOVE, this));
		menu[1].add(createMenuItem("Clear", ACTION_SEQUENCES_CLEAR, this));
		menu[2].add(createMenuItem("Preferences", ACTION_PREFERENCES, this));
		menu[3].add(createMenuItem("About Batch Tool", "calculator", ACTION_ABOUT, this));
		for(String action:new String[]{ACTION_ACTION_EDIT,ACTION_ACTION_REMOVE,ACTION_ACTIONS_CLEAR,ACTION_ACTION_MOVE_UP,ACTION_ACTION_MOVE_DOWN,ACTION_SEQUENCES_EDIT,ACTION_SEQUENCES_REMOVE,ACTION_SEQUENCES_CLEAR})
			getMenuItem(menubar,action).setEnabled(false);
		getMenuItem(menubar,ACTION_PREFERENCES).setEnabled(false);
		registerKeyStroke(getRootPane(),KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0),"remove",new ActionListener() {
			@Override public void actionPerformed(ActionEvent event) {
				 if(actionPanel.list.hasFocus()) removeAction();
				else if(sequenceList.hasFocus()) removeSequences();
			}
		});
		
		JButton execute = createToolbarButton("Execute Batch", "table_lightning", ACTION_EXECUTE, this);
		execute.setText(execute.getToolTipText());
		execute.setFont(execute.getFont().deriveFont(Font.ITALIC));
		
		toolbar = new JToolBar[1];
		toolbar[0] = new JToolBar("File");
		toolbar[0].add(createToolbarButton("Import", "table_go", ACTION_IMPORT, this));
		toolbar[0].add(createToolbarButton("Export", "table_save", ACTION_EXPORT, this));
		toolbar[0].addSeparator();
		toolbar[0].add(execute);
		
		toolbars = new JPanel(new FlowLayout(FlowLayout.LEADING));
		for(JToolBar toolbar:toolbar)
			toolbars.add(toolbar);
		add(toolbars,BorderLayout.NORTH);
		
		AttachedScrollPane scroll = new AttachedScrollPane(sequenceList=new SequenceList(), JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroll.setRowHeaderView(numbers = new NumberDisplay());
		sequenceList.getModel().addListDataListener(numbers);
		sequenceList.getModel().addListDataListener(this);
		sequenceList.addListSelectionListener(this);
		
		add(createSplitPane(JSplitPane.HORIZONTAL_SPLIT,false,true,360,0.195,
			new JScrollPane(actionPanel=new ActionPanel()),scroll), BorderLayout.CENTER);
		actionPanel.list.getModel().addListDataListener(this);
		actionPanel.list.addListSelectionListener(this);
		
		add(bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT)),BorderLayout.SOUTH);
		
		status = new JLabel();
		status.setBorder(new EmptyBorder(0,5,0,5));
		status.setHorizontalAlignment(JLabel.RIGHT);
		bottom.add(status);
		
		cancel = new JButton(getImageIcon("cancel"));
		cancel.setFocusable(false); cancel.setBorderPainted(false);
		cancel.setContentAreaFilled(false); cancel.setBorder(EMPTY_BORDER);
		cancel.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent event) {
				for(ListenableFuture<Result> future:new ArrayList<ListenableFuture<Result>>(futures))
					future.cancel(true);
			}
		});
		bottom.add(cancel);
		
		updateStatus();
	}
	
	public void addOperation(Class<? extends Operation> operation) {
		actionPanel.addOperation(operation);
	}
	public void addOperation(Class<? extends Operation> operation,Object... values) {
		actionPanel.addOperation(operation,values);
	}
	
	public void addSequence(Collection<Tuple> sequence) {
		if(sequenceList.countSequences()==0)
			sequenceList.setDefaultTupleLength(Tuple.tuplesLength(sequence));
		sequenceList.addSequence(sequence); updateStatus();
	}
	public void addSequences(List<Collection<Tuple>> sequences) {
		sequenceList.addSequences(sequences); updateStatus();
	}
	
	@Override public void actionPerformed(ActionEvent event) {
		String action;
		switch(action=event.getActionCommand()) {
		case ACTION_IMPORT: importSequences(); break;
		case ACTION_EXPORT: exportSequences(); break;
		case ACTION_CLOSE: hideDialog(); break;
		case ACTION_ACTION_ADD: addAction(); break;
		case ACTION_ACTION_EDIT: editAction(); break;
		case ACTION_ACTION_REMOVE: removeAction(); break;
		case ACTION_ACTIONS_CLEAR: clearActions(); break;
		case ACTION_ACTION_MOVE_UP: moveAction(true); break;
		case ACTION_ACTION_MOVE_DOWN: moveAction(false); break;
		case ACTION_SEQUENCES_EDIT: editSequences(); break;
		case ACTION_SEQUENCES_REMOVE: removeSequences(); break;
		case ACTION_SEQUENCES_CLEAR: clearSequences(); break;
		case ACTION_EXECUTE: executeBatch(); break;
		case ACTION_ABOUT: showAbout(); break; 
		default: System.err.println(String.format("Action %s not implemented.", action)); }
	}
	
	@Override public void intervalRemoved(ListDataEvent event) { contentsChanged(event); }
	@Override public void intervalAdded(ListDataEvent event) { contentsChanged(event); }
	@Override public void contentsChanged(ListDataEvent event) {
		enableActionMenus(); enableSequenceMenus();
	}
	@Override public void valueChanged(ListSelectionEvent event) {
		enableActionMenus(); enableSequenceMenus();
	}

	public void addAction() { actionPanel.addAction(); }
	public void editAction() { actionPanel.editAction(); }
	public void removeAction() { actionPanel.removeAction(); }
	public void clearActions() { actionPanel.clearActions(); }
	public void moveAction(boolean up) { actionPanel.moveAction(up); }
	private void enableActionMenus() {
		JList<Action> list = actionPanel.list;
		ListModel<Action> actions = list.getModel();
		int size = actions.getSize(), index = list.getSelectedIndex();
		boolean filled = size!=0,
			selected = filled&&index!=-1;
		if(selected&&index<size) {
			Parameter[] parameters = getParameters(actions.getElementAt(index).getOperation());
			getMenuItem(menubar,ACTION_ACTION_EDIT).setEnabled(parameters!=null&&parameters.length!=0);
		} else getMenuItem(menubar,ACTION_ACTION_EDIT).setEnabled(false);
		getMenuItem(menubar,ACTION_ACTION_MOVE_UP).setEnabled(index>0);
		getMenuItem(menubar,ACTION_ACTION_MOVE_DOWN).setEnabled(selected&&index<size-1);
		getMenuItem(menubar,ACTION_ACTION_REMOVE).setEnabled(selected);
		getMenuItem(menubar,ACTION_ACTIONS_CLEAR).setEnabled(filled);
	}

	public void importSequences() {
		JFileChooser chooser = new FileNameExtensionFileChooser(GENETIC_EXTENSION_FILTER,FASTA_EXTENSION_FILTER,TEXT_EXTENSION_FILTER);
		chooser.setDialogTitle("Open");
		if(chooser.showOpenDialog(this)!=JFileChooser.APPROVE_OPTION)
			return;
		File file = chooser.getSelectedFile();
		
		clearSequences();
		if(chooser.getFileFilter()==FASTA_EXTENSION_FILTER||FASTA_EXTENSION_FILTER.accept(file)) {
			try {
				for(DNASequence seuqence:FastaReaderHelper.readFastaDNASequence(file).values())
					addSequence(Tuple.splitTuples(seuqence.toString()));
			} catch(Error | Exception e) {
				JOptionPane.showMessageDialog(BatchTool.this,"Could not import FASTA file:\n"+e.getMessage(),"Import File",JOptionPane.WARNING_MESSAGE);
				e.printStackTrace();
			}
		} else try(BufferedReader reader = new BufferedReader(new FileReader(file))) {
			String line; while((line=reader.readLine())!=null)
				addSequence(Tuple.splitTuples(Tuple.tupleString(line)));
		}	catch(IOException e) {
			JOptionPane.showMessageDialog(BatchTool.this,"Could not import file:\n"+e.getMessage(),"Import File",JOptionPane.WARNING_MESSAGE);
			e.printStackTrace();
		}
	}
	public void exportSequences() {
		JFileChooser chooser = new FileNameExtensionFileChooser(false,GENETIC_EXTENSION_FILTER,FASTA_EXTENSION_FILTER,TEXT_EXTENSION_FILTER);
		chooser.setDialogTitle("Save As");
		if(chooser.showSaveDialog(this)!=JFileChooser.APPROVE_OPTION)
			return;
		File file = chooser.getSelectedFile();
		
		if(chooser.getFileFilter()==FASTA_EXTENSION_FILTER||FASTA_EXTENSION_FILTER.accept(file)) {
			try {
				int sequenceNumber = 0;
				Collection<Sequence<?>> sequences = this.sequenceList.getSequences().stream().map(tuples->new DNASequence(Tuple.joinTuples(tuples,EMPTY))).collect(Collectors.toList());
				for(Sequence<?> sequence:sequences)
					((DNASequence)sequence).setAccession(new AccessionID(Integer.toString(++sequenceNumber)));
				FastaWriterHelper.writeSequences(new FileOutputStream(file),sequences);
			} catch(Error | Exception e) {
				JOptionPane.showMessageDialog(BatchTool.this,"Could not export to FASTA file:\n"+e.getMessage(),"Export File",JOptionPane.WARNING_MESSAGE);
				e.printStackTrace();
			}
			
		} else try(PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(chooser.getSelectedFile())))) {
			for(Collection<Tuple> sequence:this.sequenceList.getSequences())
				writer.println(Tuple.joinTuples(sequence));
		}	catch(IOException e) {
			JOptionPane.showMessageDialog(BatchTool.this,"Could not export to file:\n"+e.getMessage(),"Export File",JOptionPane.WARNING_MESSAGE);
			e.printStackTrace();
		}
	}
	
	public void editSequences() {
		for(SequenceListItem item:sequenceList.getSelectedValuesList()) {
			AnalysisTool tool = new AnalysisTool();
			tool.setTuples(item.tuples);
			tool.setVisible(true);
		}
	}
	public void removeSequences() { sequenceList.removeSequences(); updateStatus(); }
	public void clearSequences() { sequenceList.clearSequences(); updateStatus(); }
	
	private void enableSequenceMenus() {
		JList<SequenceListItem> list = sequenceList;
		ListModel<SequenceListItem> items = list.getModel();
		int size = items.getSize(), index = list.getSelectedIndex();
		boolean filled = size!=0,
			selected = filled&&index!=-1;
		getMenuItem(menubar,ACTION_SEQUENCES_EDIT).setEnabled(selected);
		getMenuItem(menubar,ACTION_SEQUENCES_REMOVE).setEnabled(selected);
		getMenuItem(menubar,ACTION_SEQUENCES_CLEAR).setEnabled(filled);
	}

	public void executeBatch() {
		SequenceListModel model = (SequenceListModel)sequenceList.getModel();
		final Batch batch = new Batch(actionPanel.getActions());
		for(final SequenceListItem item:new ArrayList<>(model)) {
			item.status = Status.IDLE;
			model.change(item);
			
			ListenableFuture<Result> future = batch.submit(item.tuples,service,new Function<Collection<Tuple>,Boolean>() {
				@Override public Boolean apply(Collection<Tuple> tuples) {
					item.status = Status.ACTIVE; model.change(item);
					return true;
				}
			});
			futures.add(future);
			updateStatus();
			
			Futures.addCallback(future, new FutureCallback<Result>() {
				@Override public void onSuccess(Result result) {
					item.tuples = result.getTuples();
					item.result = result;
					item.status = Status.SUCCESS;
					model.change(item);
					
					futures.remove(future);
					updateStatus();
				}
				@Override public void onFailure(Throwable thrown) {
					item.status = Status.FAILURE;
					model.change(item);
					
					futures.remove(future);
					updateStatus();
				}
			});
		}
	}
	
	public void showAbout() { AnalysisTool.showAbout(this); }
	public void hideDialog() { setVisible(false); }
	
	protected void updateStatus() {
		invokeAppropriate(new Runnable() {
			@Override public void run() {
				StringBuilder text = new StringBuilder("<html>");
				final String seperator = "<span style=\"color:#808080\"> | </span>";
				int count = sequenceList.countSequences();
				text.append(count).append(SPACE).append(count==1?"Sequence":"Sequences").append(seperator);				
				for(Entry<Status> status:sequenceList.countSequenceStatus().entrySet()) {
					Color color = status.getElement().color.darker();
					text.append("<span style=\"color:rgb("+color.getRed()+","+color.getGreen()+","+color.getBlue()+")\">")
						.append(status.getCount()).append(SPACE).append(firstToUpper(status.getElement().toString())).append("</span>").append(seperator);
				}
				if(futures.size()>0) {
					status.setText(text.append("Computing...").toString());
					cancel.setVisible(true);
				} else {
					status.setText(text.append("Ready").toString());
					cancel.setVisible(false);
				}
			}
		});
	}
	
	private Parameter[] getParameters(Class<? extends Operation> operation) {
		Parameter[] parameters = Operation.getParameters(operation);
		if(Test.class.isAssignableFrom(operation)) {
			parameters = parameters!=null?Arrays.copyOf(parameters,parameters.length+1):new Parameter[1];
			parameters[parameters.length-1] = PARAMETER_TEST_CRITERIA;
		} else if(Split.class.isAssignableFrom(operation)) {
			parameters = parameters!=null?Arrays.copyOf(parameters,parameters.length+1):new Parameter[1];
			parameters[parameters.length-1] = parameterSplitPick;
		}
		return parameters;
	}

	static class TaskAttributeParameter extends Parameter {
		public final TaskAttribute attribute;
		public TaskAttributeParameter(TaskAttribute attribute,String label,Type type) {
			super(attribute.getName(),label,type);
			this.attribute = attribute;
		}
		public TaskAttributeParameter(TaskAttribute attribute,String label,Type type,Object value) {
			super(attribute.getName(),label,type,value);
			this.attribute = attribute;
		}
		public TaskAttributeParameter(TaskAttribute attribute,String label,int value,int minimum,int maximum,int step) {
			super(attribute.getName(),label,minimum,value,maximum,step);
			this.attribute = attribute;
		}
		public TaskAttributeParameter(TaskAttribute attribute,String label,double value,double minimum,double maximum,double step) {
			super(attribute.getName(),label,minimum,value,maximum,step);
			this.attribute = attribute;
		}
		public TaskAttributeParameter(TaskAttribute attribute,String label,boolean value) {
			super(attribute.getName(),label,value);
			this.attribute = attribute;
		}
		public TaskAttributeParameter(TaskAttribute attribute,String label,Object value,Object[] options,String... labels) {
			super(attribute.getName(),label,value,options,labels);
			this.attribute = attribute;
		}
	}
	
	class ActionPanel extends JPanel {
		private static final long serialVersionUID = 1l;
		
		private DefaultListModel<Action> actions;
		private JList<Action> list;
		
		private JButton add,edit,up,down,remove,clear;
		
		public ActionPanel() {
			GroupLayout layout = Guitilities.setGroupLayout(this);
			
			(actions = new DefaultListModel<>()).addListDataListener(new ListDataListener() {
				@Override public void intervalRemoved(ListDataEvent event) { contentsChanged(event); }
				@Override public void intervalAdded(ListDataEvent event) { contentsChanged(event); }
				@Override public void contentsChanged(ListDataEvent event) {
					enableActionButtons();
				}
			});
			
			(list = new JList<>(actions)).addListSelectionListener(new ListSelectionListener() {
				@Override public void valueChanged(ListSelectionEvent event) {
					enableActionButtons();
				}
			});
			list.addMouseListener(new MouseAdapter() {
				@Override public void mouseClicked(MouseEvent event) {
					if(event.getClickCount()==2&&list.locationToIndex(event.getPoint())!=-1)						
						editAction();
				}
			});
			list.addKeyListener(new KeyAdapter() {
				@Override public void keyPressed(KeyEvent event) {
					ActionListener action = BatchTool.this.getRootPane().getActionForKeyStroke(KeyStroke.getKeyStrokeForEvent(event));
					if(action!=null) { action.actionPerformed(new ActionEvent(list,event.getID(),EMPTY)); event.consume(); }
				}
			});
			
			list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			list.setCellRenderer(new ActionListCellRenderer());
			JScrollPane listScroll = new JScrollPane(list,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			
			(add=new JButton("Add")).addActionListener(new ActionListener() {
				@Override public void actionPerformed(ActionEvent e) { addAction(); }
			});
			add.setMnemonic(KeyEvent.VK_A);
			(edit=new JButton("Edit...")).addActionListener(new ActionListener() {
				@Override public void actionPerformed(ActionEvent e) { editAction(); }
			});
			edit.setMnemonic(KeyEvent.VK_E);
			(up=new JButton("Up")).addActionListener(new ActionListener() {
				@Override public void actionPerformed(ActionEvent e) { moveAction(true); up.requestFocus(); }
			});
			(down=new JButton("Down")).addActionListener(new ActionListener() {
				@Override public void actionPerformed(ActionEvent e) { moveAction(false); down.requestFocus(); }
			});
			(remove=new JButton("Remove")).addActionListener(new ActionListener() {
				@Override public void actionPerformed(ActionEvent event) { removeAction(); remove.requestFocus(); }
			});
			(clear=new JButton("Clear")).addActionListener(new ActionListener() {
				@Override public void actionPerformed(ActionEvent e) { clearActions(); }
			});
			
			enableActionButtons();

			layout.setHorizontalGroup(
				layout.createSequentialGroup()
					.addComponent(listScroll, 200, 200, GroupLayout.DEFAULT_SIZE)
					.addGroup(layout.createParallelGroup(Alignment.LEADING)
						.addComponent(add)
						.addComponent(edit)
						.addComponent(remove)
						.addComponent(clear)
						.addComponent(up)
						.addComponent(down)
					)
			);
			layout.linkSize(SwingConstants.HORIZONTAL,add,edit,remove,clear,up,down);
			
			layout.setVerticalGroup(
				layout.createParallelGroup()
					.addComponent(listScroll)
					.addGroup(layout.createSequentialGroup()
						.addComponent(add)
						.addComponent(edit)
						.addComponent(remove)
						.addComponent(clear)
						.addGap(15)
						.addComponent(up)
						.addComponent(down)
					)
			);
		}
		
		public List<Action> getActions() { return Collections.list(actions.elements()); }
		
		public void addAction() {
			JDialog dialog = new JDialog(BatchTool.this,true);
			
			dialog.setTitle("Add Operation");
			dialog.setLocationRelativeTo(add);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			
			GroupLayout layout = new GroupLayout(dialog.getContentPane());
			dialog.getContentPane().setLayout(layout);
			layout.setAutoCreateGaps(true);
			layout.setAutoCreateContainerGaps(true);
			
			JLabel label = new JLabel("Operation:");
			
			final @SuppressWarnings("unchecked") JComboBox<Class<? extends Operation>> combo = new JComboBox<>(Operation.getOperations().toArray((Class<? extends Operation>[])new Class[0]));
			combo.setRenderer(new OperationListCellRenderer());
			
			JButton add = new JButton("Add");
			add.addActionListener(new ActionListener() {
				@Override public void actionPerformed(ActionEvent e) {
					addOperation(combo.getModel().getElementAt(combo.getSelectedIndex()));
					dialog.dispose();
				}
			});
			combo.addKeyListener(new KeyAdapter() {				
				@Override public void keyPressed(KeyEvent event) {
					if(event.getKeyCode()==KeyEvent.VK_ENTER)
						add.getActionListeners()[0].actionPerformed(null);
				}
			});
			JButton cancel = new JButton("Cancel");
			cancel.addActionListener(new ActionListener() {
				@Override public void actionPerformed(ActionEvent e) {
					dialog.dispose();
				}
			});
			
			layout.setHorizontalGroup(
				layout.createSequentialGroup()
					.addComponent(label)
					.addGroup(layout.createParallelGroup(Alignment.TRAILING)
							.addComponent(combo, 185, 185, GroupLayout.DEFAULT_SIZE)
							.addGroup(layout.createSequentialGroup()
								.addComponent(add)	
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
						.addComponent(add)
						.addComponent(cancel)
					)
			);
			
			dialog.pack();
			dialog.setSize(new Dimension(dialog.getWidth()+100,dialog.getHeight()));
			dialog.setResizable(false);
			dialog.setVisible(true);
		}
		public void addAction(Action action) {
			actions.addElement(action);
			list.setSelectedIndex(actions.size()-1);
			list.requestFocus();
		}
		
		public void addOperation(Class<? extends Operation> operation) {
			addAction(new Action(operation));
		}
		public void addOperation(Class<? extends Operation> operation,Object... values) {
			addAction(new Action(operation,values));
		}

		public void editAction() {
			JDialog dialog = new JDialog(BatchTool.this,true);
			
			dialog.setTitle("Edit Operation");
			dialog.setLocationRelativeTo(edit);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			
			final Action action = list.getSelectedValue(); if(action==null) return;
			final Parameter[] parameters = getParameters(action.getOperation());
			if(parameters==null||parameters.length==0) return;
			final Map<Parameter,Option.Component> components = new HashMap<>();
			
			JPanel panel = new JPanel(new SpringLayout());
			if(parameters!=null) for(int parameter=0;parameter<parameters.length;parameter++) {
		    JLabel label = new JLabel(parameters[parameter].label+":", JLabel.TRAILING);
		    Option.Component component = parameters[parameter].new Component((parameters[parameter] instanceof TaskAttributeParameter)?
		    	action.attributes.get(((TaskAttributeParameter)parameters[parameter]).attribute):action.values[parameter]
		    );
		    
		    label.setLabelFor(component);
		    panel.add(label); panel.add(component);
		    components.put(parameters[parameter],component);
			}
			Guitilities.makeCompactGrid(panel, parameters.length, 2, 6, 6, 6, 6);
			dialog.add(panel, BorderLayout.CENTER);

			JButton set = new JButton("Set");
			set.addActionListener(new ActionListener() {
				@Override public void actionPerformed(ActionEvent e) {
					Action action = actions.getElementAt(list.getSelectedIndex());
					List<Object> values = new ArrayList<>(parameters!=null?parameters.length:0);
					if(parameters!=null) for(Parameter parameter:parameters) {
						Object value = components.get(parameter).getValue();
						if(parameter instanceof TaskAttributeParameter)
							action.attributes.put(((TaskAttributeParameter)parameter).attribute,value);
						else values.add(value);
					}
					action.values = values.toArray();
					actions.set(list.getSelectedIndex(),action);
					dialog.dispose();
				}
			});
			JButton cancel = new JButton("Cancel");
			cancel.addActionListener(new ActionListener() {
				@Override public void actionPerformed(ActionEvent e) {
					dialog.dispose();
				}
			});
			
			JPanel bottom = new JPanel(new FlowLayout(FlowLayout.TRAILING));
			bottom.add(set);
			bottom.add(cancel);
			dialog.add(bottom, BorderLayout.SOUTH);
			
			dialog.pack(); dialog.setSize(300, dialog.getHeight());
			dialog.setResizable(false);
			dialog.setVisible(true);
		}
		public void moveAction(boolean up) {
			int index = list.getSelectedIndex(), move = index+(up?-1:1);
			if(move<0||move>=list.getModel().getSize()) return;
			actions.add(move, actions.remove(index));
			list.setSelectedIndex(move);
		}
		public void removeAction() {
			int index = list.getSelectedIndex();
			if(index!=-1) {
				actions.remove(index);
				if(!actions.isEmpty()&&index<=actions.size())
					list.setSelectedIndex(index!=0?index-1:0);
			}
		}
		public void clearActions() { actions.clear(); }
		
		private void enableActionButtons() {
			int size = actions.getSize(),
				index = list.getSelectedIndex();
			boolean selected = index!=-1,
			  filled = size!=0;
			if(selected) {
				Parameter[] parameters = getParameters(actions.getElementAt(index).getOperation());
				edit.setEnabled(parameters!=null&&parameters.length!=0);
			} else edit.setEnabled(false);
			up.setEnabled(index>0);
			down.setEnabled(selected&&index<size-1);
			remove.setEnabled(selected);
			clear.setEnabled(filled);
		}
		
		private class OperationListCellRenderer implements ListCellRenderer<Class<? extends Operation>> {
			private final ImageIcon
				transformationIcon = getImageIcon("wand"),
				testIcon = getImageIcon("magnifier"),
				analysisIcon = getImageIcon("chart_curve"),
				splitIcon = getImageIcon("cut_red");
			
			private JLabel label = new JLabel();
			
			@Override public Component getListCellRendererComponent(JList<? extends Class<? extends Operation>> list,Class<? extends Operation> operation,int index,boolean isSelected,boolean cellHasFocus) {
				return getListCellRendererComponentGeneric(list,operation,index,isSelected,cellHasFocus);
			}
			
			protected Component getListCellRendererComponentGeneric(JList<?> list,Class<? extends Operation> operation,int index,boolean isSelected,boolean cellHasFocus) {
		    label.setText(Operation.getName(operation));
		    
		    if(Transformation.class.isAssignableFrom(operation))
		    	label.setIcon(transformationIcon);
		    else if(Test.class.isAssignableFrom(operation))
		    	label.setIcon(testIcon);
		    else if(Analysis.class.isAssignableFrom(operation))
		    	label.setIcon(analysisIcon);
		    else if(Split.class.isAssignableFrom(operation))
		    	label.setIcon(splitIcon);
		    else label.setIcon(null);
		    
		    if(isSelected) {
		    	label.setBackground(list.getSelectionBackground());
		    	label.setForeground(list.getSelectionForeground());
		    } else {
		    	label.setBackground(list.getBackground());
		    	label.setForeground(list.getForeground());
		    }
		    label.setEnabled(list.isEnabled());
		    label.setFont(list.getFont().deriveFont(Font.BOLD));
		    label.setOpaque(true);
		    
		    return label;
			}
		}
		
		private class ActionListCellRenderer implements ListCellRenderer<Action> {
			private OperationListCellRenderer renderer = new OperationListCellRenderer();
			
			private final ImageIcon
				testPositiveIcon = getImageIcon("magnifier_zoom_in"),
				testNegativeIcon = getImageIcon("magnifier_zoom_out");
			private final ImageIcon
				splitFirstIcon = getImageIcon("cut_red_first"),
				splitLastIcon = getImageIcon("cut_red_last"),
				splitAnyIcon = getImageIcon("cut_red_any"),
				splitAllIcon = getImageIcon("cut_red_all"),
				splitRandomIcon = getImageIcon("cut_red_random");
			
			@Override public Component getListCellRendererComponent(JList<? extends Action> list,Action action,int index,boolean isSelected,boolean cellHasFocus) {
				Class<? extends Operation> operation = action.getOperation();
				JLabel label = (JLabel)renderer.getListCellRendererComponentGeneric(list,operation,index,isSelected,cellHasFocus);
				
				Boolean testCriteria = (Boolean)action.attributes.get(TEST_CRITERIA);
				if(Test.class.isAssignableFrom(operation)&&testCriteria!=null)
					label.setIcon((Boolean)testCriteria?testPositiveIcon:testNegativeIcon);
				else if(Split.class.isAssignableFrom(operation)) {
					Split.Pick pick = (Split.Pick)action.attributes.get(SPLIT_PICK);
					if(SPLIT_PICK_FIRST.equals(pick))
						label.setIcon(splitFirstIcon);
					else if(SPLIT_PICK_LAST.equals(pick))
						label.setIcon(splitLastIcon);
					else if(SPLIT_PICK_ANY.equals(pick))
						label.setIcon(splitAnyIcon);
					else if(splitPickAll.equals(pick))
						label.setIcon(splitAllIcon);
					else if(splitPickRandom.equals(pick))
						label.setIcon(splitRandomIcon);
				}
				
	      return label;
			}
		}
	}
	
	enum Status {
		IDLE(new Color(107,193,255)),
		ACTIVE(new Color(45,101,255)),
		SUCCESS(new Color(183,255,66)),
		FAILURE(new Color(255,75,15)),
		EXCEPTION(new Color(255,0,25));
		
		public final Color color;
		private Status(Color color) {
			this.color = color;
		}
	}
	static class SequenceListModel extends CollectionListModel<SequenceListItem> {
		private static final long serialVersionUID = 1l;
	}
	static class SequenceListItem {
		public Collection<Tuple> tuples;
		public Status status;
		public Result result;
		
		public SequenceListItem(Collection<Tuple> tuples) { this.tuples = new ArrayList<>(tuples); }
	}
	class SequenceList extends JList<SequenceListItem> implements ListCellRenderer<SequenceListItem> {
		private static final long serialVersionUID = 1l;
		
		private final Color lineColor = new Color(0,0,0,64);
		
		protected SequenceListModel items;
		private JLabel label = new JLabel();
		
		private int defaultTupleLength;
		
		public SequenceList() {
			super(new SequenceListModel());
			items = (SequenceListModel)getModel();
			setCellRenderer(this);
			setFont(new Font(Font.MONOSPACED,Font.PLAIN,12));
		}
		
		@Override public Component getListCellRendererComponent(JList<? extends SequenceListItem> list,SequenceListItem item,int index,boolean isSelected,boolean cellHasFocus) {
	    label.setText(Tuple.joinTuples(item.tuples));
	    
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
		
		public int getDefaultTupleLength() { return defaultTupleLength; }
		public void setDefaultTupleLength(int defaultTupleLength) {
			this.defaultTupleLength = defaultTupleLength; repaint();
		}
		
		public int countSequences() { return items.getSize(); }
		public int countSequences(Status status) {
			int count = 0;
			for(SequenceListItem item:items)
				if(item.status.equals(status))
					count++;
			return count;
		}
		public Multiset<Status> countSequenceStatus() {
			Multiset<Status> status = EnumMultiset.create(Status.class);
			for(SequenceListItem item:items)
				if(item.status!=null) status.add(item.status);
			return status;
		}
		public List<Collection<Tuple>> getSequences() {
      List<Collection<Tuple>> sequences = new ArrayList<>();
      for(SequenceListItem item:items)
      	sequences.add(item.tuples);
      return sequences;
    }
		public Collection<Tuple> getSequenceAt(int index) {
			return items.getElementAt(index).tuples;
		}
		public void addSequence(Collection<Tuple> sequence) { items.add(new SequenceListItem(sequence)); }
		public void addSequences(List<Collection<Tuple>> sequences) {
			for(Collection<Tuple> sequence:sequences)
				addSequence(sequence);
		}
		public void removeSequences() {
			int adapt = 0; for(int index:getSelectedIndices())
				items.remove(index-adapt++);
		}
		public void clearSequences() { items.clear(); }
		
		@Override public void paint(Graphics graphics) {
			super.paint(graphics);
			if(defaultTupleLength>0) {
				graphics.setColor(lineColor);
				int width=getFontMetrics(getFont()).charWidth('0'), tupleWidth = width*defaultTupleLength;
				for(int left=getInsets().left+width/2+tupleWidth; left<getWidth(); left += tupleWidth + width)
					graphics.drawLine(left, 0, left, getHeight());
			}
		}
	}
	
	class NumberDisplay extends JPanel implements ListDataListener {
		private static final long serialVersionUID = 1l;

		private int border,digits;
		
		public NumberDisplay() { this(0); }
		public NumberDisplay(int digits) {
			setFont(sequenceList.getFont());
			setBorderSpacing(5);
			if(digits>0)
				setDigits(digits);
			else adaptDigits();
		}
		
		public int getBorderSpacing() { return border; }
		public void setBorderSpacing(int borderSpading) {
			this.border = borderSpading;
			Border inner = new EmptyBorder(0,borderSpading,0,borderSpading);
			setBorder(new CompoundBorder(new MatteBorder(0,0,0,2,Color.GRAY),inner));
			adaptWidth();
		}

		public int getDigits() { return digits; }
		public void adaptDigits() { setDigits(Math.max(String.valueOf(sequenceList.getModel().getSize()).length(), 3)); }
		public void setDigits(int digits) {
			digits = Math.max(3,digits);
			if(this.digits!=digits) {
				this.digits = digits;
				adaptWidth();
			}
		}
		
		private void adaptWidth() {
			Insets insets = getInsets();
			Dimension dimension = getPreferredSize();
			dimension.setSize(insets.left+insets.right+(getFontMetrics(getFont()).charWidth('0')*(digits*2+1)), Integer.MAX_VALUE-Short.MAX_VALUE);
			setPreferredSize(dimension);
			setSize(dimension);
		}

		@Override public void paintComponent(Graphics graphics) {
			super.paintComponent(graphics);
			
			Insets insets = getInsets();
			FontMetrics metrics = sequenceList.getFontMetrics(sequenceList.getFont());
			int width = getSize().width-insets.left-insets.right;
			
			Rectangle clip = graphics.getClipBounds();
			int startIndex = sequenceList.locationToIndex(new Point(0,clip.y)), endIndex = sequenceList.locationToIndex(new Point(0,clip.y+clip.height));
			if(startIndex!=-1) for(int index=startIndex;index<=endIndex;index++) {
				int indexY = sequenceList.indexToLocation(index).y, height = metrics.getHeight();
				
				graphics.setColor(getBackground());
				try {
					SequenceListItem item = sequenceList.getModel().getElementAt(index);
					if(item.status!=null) graphics.setColor(item.status.color);
				} catch(IndexOutOfBoundsException e) { /* nothing to do here */ }
				graphics.fillRect(0,indexY,getWidth(),height);			
				
				graphics.setColor(getForeground());
				String indexText = Integer.toString(index+1);
				graphics.drawString(indexText,width-metrics.stringWidth(indexText)+insets.left,indexY+metrics.getHeight()-metrics.getDescent());
			}
		}
		
		@Override public void intervalAdded(ListDataEvent event) { contentsChanged(event); }
		@Override public void intervalRemoved(ListDataEvent event) { contentsChanged(event); }
		@Override public void contentsChanged(ListDataEvent event) {
			invokeAppropriate(new Runnable() {
				public void run() { adaptDigits(); repaint(); }
			});
		}
	}
}