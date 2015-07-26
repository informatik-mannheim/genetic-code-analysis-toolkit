package de.hsma.gentool.gui;

import static de.hsma.gentool.Utilities.*;
import static de.hsma.gentool.gui.GenBDA.Helper.*;
import static de.hsma.gentool.gui.helper.Guitilities.*;
import static de.hsma.gentool.nucleic.Acid.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.DefaultListCellRenderer;
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
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import net.gumbix.geneticcode.dich.AntiCodonBDA$;
import net.gumbix.geneticcode.dich.BinaryDichotomicAlgorithm;
import net.gumbix.geneticcode.dich.ParityBDA$;
import net.gumbix.geneticcode.dich.RumerBDA$;
import org.apache.commons.math3.util.Pair;
import de.hsma.gentool.Utilities.FileExtensionFileChooser;
import de.hsma.gentool.gui.helper.Guitilities;
import de.hsma.gentool.gui.helper.ListTableModel;
import de.hsma.gentool.nucleic.Acid;
import de.hsma.gentool.nucleic.Base;

public class GenBDA extends JFrame implements ActionListener, ListDataListener, ListSelectionListener {
	private static final long serialVersionUID = 1l;
	
	public static final String FILE_EXTENSION = "bda", FILE_DESCRIPTION = "Binary Dichotomic Algorithm";
	
	private static final String
		ACTION_OPEN = "open",
		ACTION_SAVE_AS = "save_as",
		ACTION_CLOSE = "close",
		ACTION_BDA_ADD = "actions_add",
		ACTION_BDA_EDIT = "actions_edit",
		ACTION_BDA_REMOVE = "actions_remove",
		ACTION_BDAS_CLEAR = "actions_clear",
		ACTION_BDA_MOVE_UP = "actions_move_up",
		ACTION_BDA_MOVE_DOWN = "actions_move_down",
		ACTION_PREFERENCES = "preferences",
		ACTION_ABOUT = "about";
		
	private JMenuBar menubar;
	private JMenu[] menu;
	private JPanel toolbars, bottom;
	private JToolBar[] toolbar;
	private JLabel status;
	
	private BinaryDichotomicAlgorithmPanel bdaPanel;
	private JPanel tablePanel;
	private net.gumbix.geneticcode.dich.ui.JGeneticCodeTable table;
	
	public GenBDA() {		
		super("Genetic Code BDA Editor (GenBDA)");
		setIconImage(new ImageIcon(getResource("application_osx_terminal.png")).getImage());
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
		
		menu[0].add(createMenuItem("Open...", "folder-horizontal-open.png", KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK), ACTION_OPEN, this));
		menu[0].add(createMenuItem("Save As...", "disk--arrow.png", KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK), ACTION_SAVE_AS, this));
		menu[0].add(createSeparator());
		menu[0].add(createMenuItem("Close Window", "cross.png", ACTION_CLOSE, this));
		menu[1].add(createMenuText("Binary Dichotomic Algorithm:"));
		menu[1].add(createMenuItem("Add", KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK), ACTION_BDA_ADD, this));
		menu[1].add(createMenuItem("Edit...", KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_DOWN_MASK), ACTION_BDA_EDIT, this));
		menu[1].add(createMenuItem("Remove", KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), ACTION_BDA_REMOVE, this));
		menu[1].add(createMenuItem("Clear", ACTION_BDAS_CLEAR, this));
		menu[1].add(seperateMenuItem(createMenuItem("Move Up", KeyStroke.getKeyStroke(KeyEvent.VK_UP, InputEvent.CTRL_DOWN_MASK), ACTION_BDA_MOVE_UP, this)));
		menu[1].add(createMenuItem("Move Down", KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, InputEvent.CTRL_DOWN_MASK), ACTION_BDA_MOVE_DOWN, this));
		menu[2].add(createMenuItem("Preferences", ACTION_PREFERENCES, this));
		menu[3].add(createMenuItem("About GenBDA", "application_osx_terminal.png", ACTION_ABOUT, this));
		for(String action:new String[]{ACTION_BDA_EDIT,ACTION_BDA_REMOVE,ACTION_BDAS_CLEAR,ACTION_BDA_MOVE_UP,ACTION_BDA_MOVE_DOWN})
			getMenuItem(menubar,action).setEnabled(false);
		getMenuItem(menubar,ACTION_PREFERENCES).setEnabled(false);
		registerKeyStroke(getRootPane(),KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0),"remove",new ActionListener() {
			@Override public void actionPerformed(ActionEvent event) {
				 if(bdaPanel.table.hasFocus())
					 removeBinaryDichotomicAlgorithm();
			}
		});
		
		toolbar = new JToolBar[1];
		toolbar[0] = new JToolBar("File");
		toolbar[0].add(createToolbarButton("Open File", "folder-horizontal-open.png", ACTION_OPEN, this));
		toolbar[0].add(createToolbarButton("Save As File", "disk--arrow.png", ACTION_SAVE_AS, this));
		
		toolbars = new JPanel(new FlowLayout(FlowLayout.LEADING));
		for(JToolBar toolbar:toolbar)
			toolbars.add(toolbar);
		add(toolbars,BorderLayout.NORTH);
		
		add(createSplitPane(JSplitPane.HORIZONTAL_SPLIT,false,true,360,0.195,
			new JScrollPane(bdaPanel=new BinaryDichotomicAlgorithmPanel()),
			new JScrollPane(tablePanel=new JPanel())), BorderLayout.CENTER);
		
		add(bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT)),BorderLayout.SOUTH);
		
		status = new JLabel();
		status.setBorder(new EmptyBorder(0,5,0,5));
		status.setHorizontalAlignment(JLabel.RIGHT);
		bottom.add(status);
		
		((ListTableModel<?>)bdaPanel.table.getModel()).addListDataListener(this);
		bdaPanel.table.getSelectionModel().addListSelectionListener(this);
		
		revalidateGeneticCodeTable();
	}
	
	@Override public void actionPerformed(ActionEvent event) {
		String action;
		switch(action=event.getActionCommand()) {
		case ACTION_OPEN: openFile(); break;
		case ACTION_SAVE_AS: saveFileAs(); break;
		case ACTION_CLOSE: hideDialog(); break;
		case ACTION_BDA_ADD: addBinaryDichotomicAlgorithm(); break;
		case ACTION_BDA_EDIT: editBinaryDichotomicAlgorithm(); break;
		case ACTION_BDA_REMOVE: removeBinaryDichotomicAlgorithm(); break;
		case ACTION_BDAS_CLEAR: clearBinaryDichotomicAlgorithms(); break;
		case ACTION_BDA_MOVE_UP: moveBinaryDichotomicAlgorithm(true); break;
		case ACTION_BDA_MOVE_DOWN: moveBinaryDichotomicAlgorithm(false); break;
		case ACTION_ABOUT: showAbout(); break;
		default: System.err.println(String.format("Action %s not implemented.", action)); }
	}
	
	@Override public void intervalRemoved(ListDataEvent event) { contentsChanged(event); }
	@Override public void intervalAdded(ListDataEvent event) { contentsChanged(event); }
	@Override public void contentsChanged(ListDataEvent event) {
		enableBinaryDichotomicAlgorithmMenus();
		revalidateGeneticCodeTable();
	}
	@Override public void valueChanged(ListSelectionEvent event) {
		enableBinaryDichotomicAlgorithmMenus();
	}
	
	public boolean openFile() {
		JFileChooser chooser = new FileExtensionFileChooser(FILE_EXTENSION,FILE_DESCRIPTION);
		chooser.setDialogTitle("Open");
		if(chooser.showOpenDialog(this)!=JFileChooser.APPROVE_OPTION)
			return false;
		
		return openFile(chooser.getSelectedFile());
	}
	public boolean openFile(File file) {
		try(FileReader reader=new FileReader(file)) {
			bdaPanel.setBinaryDichotomicAlgorithms(readFrom(reader));
			return true;
		}	catch(IOException e) {
			JOptionPane.showMessageDialog(this,"Could not open file:\n"+e.getMessage(),"Open File",JOptionPane.WARNING_MESSAGE);
			return false;
		}
	}
	public boolean saveFileAs() {
		JFileChooser chooser = new FileExtensionFileChooser(FILE_EXTENSION,FILE_DESCRIPTION);
		chooser.setDialogTitle("Save As");
		if(chooser.showSaveDialog(this)==JFileChooser.APPROVE_OPTION)
			   return saveFile(chooser.getSelectedFile());
		else return false;
	}
	public boolean saveFile(File file) {
		try(FileWriter writer = new FileWriter(file)) {
			writeTo(writer,bdaPanel.getBinaryDichotomicAlgorithms());
			return true;
		}	catch(IOException e) {
			JOptionPane.showMessageDialog(this,"Could not save file:\n"+e.getMessage(),"Save File",JOptionPane.WARNING_MESSAGE);
			return false;
		}
	}
	
	public void addBinaryDichotomicAlgorithm() { bdaPanel.addBinaryDichotomicAlgorithm(); }
	public void editBinaryDichotomicAlgorithm() { bdaPanel.editBinaryDichotomicAlgorithm(); }
	public void removeBinaryDichotomicAlgorithm() { bdaPanel.removeBinaryDichotomicAlgorithm(); }
	public void clearBinaryDichotomicAlgorithms() { bdaPanel.clearBinaryDichotomicAlgorithms(); }
	public void moveBinaryDichotomicAlgorithm(boolean up) { bdaPanel.moveBinaryDichotomicAlgorithm(up); }
	private void enableBinaryDichotomicAlgorithmMenus() {
		JTable table = bdaPanel.table;
		javax.swing.table.TableModel bdas = table.getModel();
		int size = bdas.getRowCount(),
			index = table.getSelectedRow();
		boolean selected = index!=-1,
		  filled = size!=0;
		getMenuItem(menubar,ACTION_BDA_EDIT).setEnabled(selected&&index<size); //BinaryDichotomicAlgorithm bda = bdas.get(index); !isPredefined(bda);
		getMenuItem(menubar,ACTION_BDA_MOVE_UP).setEnabled(index>0);
		getMenuItem(menubar,ACTION_BDA_MOVE_DOWN).setEnabled(selected&&index<size-1);
		getMenuItem(menubar,ACTION_BDA_REMOVE).setEnabled(selected);
		getMenuItem(menubar,ACTION_BDAS_CLEAR).setEnabled(filled);
	}
	
	public void showAbout() { GenTool.showAbout(this); }
	public void hideDialog() { setVisible(false); }
	
	@SuppressWarnings("unchecked") private void revalidateGeneticCodeTable() {		
    scala.collection.immutable.List<?> bdas =
    	scala.collection.JavaConversions.collectionAsScalaIterable(bdaPanel.getBinaryDichotomicAlgorithms()).toList();
    
    net.gumbix.geneticcode.dich.ct.ClassTable classTable = null;
    if(bdas.nonEmpty()) classTable =
    	new net.gumbix.geneticcode.dich.ct.ClassTable(
    		(scala.collection.immutable.List<net.gumbix.geneticcode.dich.Classifier<Object>>)bdas,
				net.gumbix.geneticcode.dich.IUPAC.STANDARD(),
				new net.gumbix.geneticcode.dich.IdAminoAcidProperty(1));
    
    if(table!=null) tablePanel.remove(table);
    if(classTable!=null)
    	tablePanel.add(table=new net.gumbix.geneticcode.dich.ui.JGeneticCodeTable(classTable),
	    	BorderLayout.CENTER);
    else table = null;
    tablePanel.revalidate();
    tablePanel.repaint();
    
    int classes = classTable==null?0:classTable.classes().size();
    status.setText((classes==0?"No":classes)+(classes==1?" class":" classes"));
	}
	
	class BinaryDichotomicAlgorithmPanel extends JPanel {
		private static final long serialVersionUID = 1l;
		
		private TableModel bdas;
		private JTable table;
		
		private JButton add,edit,up,down,remove,clear;
		
		public BinaryDichotomicAlgorithmPanel() {
			GroupLayout layout = setGroupLayout(this);
			
			(bdas = new TableModel()).addListDataListener(new ListDataListener() {
				@Override public void intervalRemoved(ListDataEvent event) { contentsChanged(event); }
				@Override public void intervalAdded(ListDataEvent event) { contentsChanged(event); }
				@Override public void contentsChanged(ListDataEvent event) {
					enableBinaryDichotomicAlgorithmButtons();
				}
			});
			
			table = new JTable(bdas);
			table.setRowSelectionAllowed(true);
			DefaultTableCellRenderer tableRenderer = new DefaultTableCellRenderer();
			tableRenderer.setHorizontalAlignment(JLabel.CENTER);
			table.setDefaultRenderer(Object.class,tableRenderer);
			table.setPreferredScrollableViewportSize(table.getPreferredSize());
			table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
			table.getColumn("BDA").setMinWidth(90);
			table.getTableHeader().setReorderingAllowed(false);
			table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
				@Override public void valueChanged(ListSelectionEvent event) {
					enableBinaryDichotomicAlgorithmButtons();
				}
			});
			table.addMouseListener(new MouseAdapter() {
				@Override public void mouseClicked(MouseEvent event) {
					if(event.getClickCount()==2&&table.rowAtPoint(event.getPoint())!=-1)						
						editBinaryDichotomicAlgorithm();
				}
			});
			table.addKeyListener(new KeyAdapter() {
				@Override public void keyPressed(KeyEvent event) {
					ActionListener action = GenBDA.this.getRootPane().getActionForKeyStroke(KeyStroke.getKeyStrokeForEvent(event));
					if(action!=null) { action.actionPerformed(new ActionEvent(table,event.getID(),EMPTY)); event.consume(); }
				}
			});
			
			JScrollPane tableScroll = new JScrollPane(table,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			tableScroll.getViewport().setBackground(Color.WHITE);
			
			(add=new JButton("Add")).addActionListener(new ActionListener() {
				@Override public void actionPerformed(ActionEvent e) { addBinaryDichotomicAlgorithm(); }
			});
			add.setMnemonic(KeyEvent.VK_A);
			(edit=new JButton("Edit...")).addActionListener(new ActionListener() {
				@Override public void actionPerformed(ActionEvent e) { editBinaryDichotomicAlgorithm(); }
			});
			edit.setMnemonic(KeyEvent.VK_E);
			(up=new JButton("Up")).addActionListener(new ActionListener() {
				@Override public void actionPerformed(ActionEvent e) { moveBinaryDichotomicAlgorithm(true); up.requestFocus(); }
			});
			(down=new JButton("Down")).addActionListener(new ActionListener() {
				@Override public void actionPerformed(ActionEvent e) { moveBinaryDichotomicAlgorithm(false); down.requestFocus(); }
			});
			(remove=new JButton("Remove")).addActionListener(new ActionListener() {
				@Override public void actionPerformed(ActionEvent event) { removeBinaryDichotomicAlgorithm(); remove.requestFocus(); }
			});
			(clear=new JButton("Clear")).addActionListener(new ActionListener() {
				@Override public void actionPerformed(ActionEvent e) { clearBinaryDichotomicAlgorithms(); }
			});
			
			enableBinaryDichotomicAlgorithmButtons();

			layout.setHorizontalGroup(
				layout.createSequentialGroup()
					.addComponent(tableScroll, 200, 200, GroupLayout.DEFAULT_SIZE)
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
					.addComponent(tableScroll)
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
		
		public void addBinaryDichotomicAlgorithm() {
			JDialog dialog = new JDialog(GenBDA.this,true);	
			
			dialog.setTitle("Add Binary Dichotomic Algorithm");
			dialog.setLocationRelativeTo(add);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			
			GroupLayout layout = new GroupLayout(dialog.getContentPane());
			dialog.getContentPane().setLayout(layout);
			layout.setAutoCreateGaps(true);
			layout.setAutoCreateContainerGaps(true);
			
			JLabel label = new JLabel("Please select or define a Binary Dichotomic Algorithm:");
			
			final EditorPanel editor = new EditorPanel();
			
			JButton add = new JButton("Add");
			add.addActionListener(new ActionListener() {
				@Override public void actionPerformed(ActionEvent e) {
					addBinaryDichotomicAlgorithm(editor.getBinaryDichotomicAlgorithm());
					dialog.dispose();
				}
			});
			JButton cancel = new JButton("Cancel");
			cancel.addActionListener(new ActionListener() {
				@Override public void actionPerformed(ActionEvent e) {
					dialog.dispose();
				}
			});
			
			layout.setHorizontalGroup(
				layout.createParallelGroup(Alignment.TRAILING)
					.addComponent(label)
					.addComponent(editor)
					.addGroup(layout.createSequentialGroup()
						.addComponent(add)
						.addComponent(cancel)	
					)					
			);
			layout.linkSize(SwingConstants.HORIZONTAL,label,editor);

			layout.setVerticalGroup(
				layout.createSequentialGroup()
					.addComponent(label)
					.addComponent(editor)
					.addGroup(layout.createParallelGroup()
						.addComponent(add)
						.addComponent(cancel)
					)
			);
			
			dialog.pack();
			dialog.setResizable(false);
			dialog.setVisible(true);
		}
		public void editBinaryDichotomicAlgorithm() {
			JDialog dialog = new JDialog(GenBDA.this,true);
			
			dialog.setTitle("Edit Binary Dichotomic Algorithm");
			dialog.setLocationRelativeTo(edit);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			
			GroupLayout layout = new GroupLayout(dialog.getContentPane());
			dialog.getContentPane().setLayout(layout);
			layout.setAutoCreateGaps(true);
			layout.setAutoCreateContainerGaps(true);
			
			JLabel label = new JLabel("Please select another or redefine the Binary Dichotomic Algorithm:");
			
			final EditorPanel editor = new EditorPanel(getSelectedBinaryDichotomicAlgorithm());
			
			JButton modify = new JButton("Modify");
			modify.addActionListener(new ActionListener() {
				@Override public void actionPerformed(ActionEvent e) {
					bdas.set(table.getSelectedRow(),editor.getBinaryDichotomicAlgorithm());
					dialog.dispose();
				}
			});
			JButton cancel = new JButton("Cancel");
			cancel.addActionListener(new ActionListener() {
				@Override public void actionPerformed(ActionEvent e) {
					dialog.dispose();
				}
			});
			
			layout.setHorizontalGroup(
				layout.createParallelGroup(Alignment.TRAILING)
					.addComponent(label)
					.addComponent(editor)
					.addGroup(layout.createSequentialGroup()
						.addComponent(modify)
						.addComponent(cancel)	
					)					
			);
			layout.linkSize(SwingConstants.HORIZONTAL,label,editor);

			layout.setVerticalGroup(
				layout.createSequentialGroup()
					.addComponent(label)
					.addComponent(editor)
					.addGroup(layout.createParallelGroup()
						.addComponent(modify)
						.addComponent(cancel)
					)
			);
			
			dialog.pack();
			dialog.setResizable(false);
			dialog.setVisible(true);
		}
		public void moveBinaryDichotomicAlgorithm(boolean up) {
			int index = table.getSelectedRow(), move = index+(up?-1:1);
			if(move<0||move>=bdas.size()) return;
			bdas.add(move, bdas.remove(index));
			selectBinaryDichotomicAlgorithm(move);
		}
		public void removeBinaryDichotomicAlgorithm() {
			int index = table.getSelectedRow();
			if(index!=-1) {
				bdas.remove(index);
				if(!bdas.isEmpty()&&index<=bdas.size())
					selectBinaryDichotomicAlgorithm(index!=0?index-1:0);
			}
		}
		public void clearBinaryDichotomicAlgorithms() { bdas.clear(); }

		public List<BinaryDichotomicAlgorithm> getBinaryDichotomicAlgorithms() { return bdas; }
		public BinaryDichotomicAlgorithm getSelectedBinaryDichotomicAlgorithm() {
			return bdas.get(table.getSelectedRow());
		}
		
		public void setBinaryDichotomicAlgorithms(Collection<BinaryDichotomicAlgorithm> bdas) { clearBinaryDichotomicAlgorithms(); addBinaryDichotomicAlgorithms(bdas); }
		public void setBinaryDichotomicAlgorithms(BinaryDichotomicAlgorithm... bdas) { clearBinaryDichotomicAlgorithms(); addBinaryDichotomicAlgorithms(bdas); }
		
		public void addBinaryDichotomicAlgorithms(Collection<BinaryDichotomicAlgorithm> bdas) { addBinaryDichotomicAlgorithms(bdas.toArray(new BinaryDichotomicAlgorithm[0])); }
		public void addBinaryDichotomicAlgorithms(BinaryDichotomicAlgorithm... bdas) { for(BinaryDichotomicAlgorithm bda:bdas) addBinaryDichotomicAlgorithm(bda); }
		public void addBinaryDichotomicAlgorithm(BinaryDichotomicAlgorithm bda) {
			bdas.add(bda);
			selectBinaryDichotomicAlgorithm(bdas.size()-1);
			table.requestFocus();
		}
		
		protected void selectBinaryDichotomicAlgorithm(int index) {
			table.setRowSelectionInterval(index,index);
		}
		
		private void enableBinaryDichotomicAlgorithmButtons() {
			int size = bdas.getRowCount(),
				index = table.getSelectedRow();
			boolean selected = index!=-1,
			  filled = size!=0;
			edit.setEnabled(selected&&index<size); //BinaryDichotomicAlgorithm bda = bdas.get(index); !isPredefined(bda);
			up.setEnabled(index>0);
			down.setEnabled(selected&&index<size-1);
			remove.setEnabled(selected);
			clear.setEnabled(filled);
		}
		
		private class TableModel extends ListTableModel<BinaryDichotomicAlgorithm> {
			private static final long serialVersionUID = 1l;
			
			@Override public int getColumnCount() { return 4; }
			@Override public String getColumnName(int column) {
				switch(column) {
				case 0: return "BDA";
				case 1: return "(i\u2081,i\u2082)";
				case 2: return "Q\u2081";
				case 3: return "Q\u2082";
				default: return null; }
			}
			@Override public Object getValueAt(BinaryDichotomicAlgorithm bda,int columnIndex) {
				switch(columnIndex) {
				case 0: //BDA (name)
					return Helper.getName(bda);
				case 1: //(i1,i2)
					return "("+(bda.i1()+1)+","+(bda.i2()+1)+")";
				case 2: //Q1
					return bda.q1().toString();
				case 3: //Q2
					StringBuilder q2 = new StringBuilder(String.valueOf('{'));
					scala.collection.Iterator<net.gumbix.geneticcode.dich.Compound> compounds =
						bda.q2().iterator();
					while(compounds.hasNext()) {
						q2.append(compounds.next().toString());
						if(compounds.hasNext()) q2.append(',');
					} return q2.append('}').toString();
				default:
					return null;
				}
			}
		}
		private class EditorPanel extends JPanel {
			private static final long serialVersionUID = 1l;
			
			protected final Acid DEFAULT_ACID = RNA;
			
			private JComboBox<BinaryDichotomicAlgorithm> bdas;
			private JComboBox<Pair<Integer,Integer>> i1i2;
			private JComboBox<Pair<Base,Base>> q1,q2;
			
			public EditorPanel() {
				setLayout(new GridLayout(2,4));
				add(new JLabel("BDA",JLabel.CENTER));
				add(new JLabel("(i\u2081,i\u2082)",JLabel.CENTER));
				add(new JLabel("Q\u2081",JLabel.CENTER));
				add(new JLabel("Q\u2082",JLabel.CENTER));
				
				add(bdas=new JComboBox<BinaryDichotomicAlgorithm>());
				for(Object bda:new Object[]{RumerBDA$.MODULE$,ParityBDA$.MODULE$,AntiCodonBDA$.MODULE$,null})
					bdas.addItem((BinaryDichotomicAlgorithm)bda);
				bdas.setRenderer(new DefaultListCellRenderer() {
					private static final long serialVersionUID = 1l;
					@Override public Component getListCellRendererComponent(JList<?> list,Object value,int index,boolean isSelected,boolean cellHasFocus) {
						super.getListCellRendererComponent(list,value,index,isSelected,cellHasFocus);
						setText(Helper.getName((BinaryDichotomicAlgorithm)value));
						return this;
					}
				});
				
				add(i1i2=new JComboBox<Pair<Integer,Integer>>());
				for(int i1=1;i1<=3;i1++) for(int i2=1;i2<=3;i2++)
					if(i1!=i2) i1i2.addItem(new Pair<Integer,Integer>(i1,i2));
				i1i2.setRenderer(new DefaultListCellRenderer() {
					private static final long serialVersionUID = 1l;
					@SuppressWarnings("unchecked") @Override public Component getListCellRendererComponent(JList<?> list,Object value,int index,boolean isSelected,boolean cellHasFocus) {
						Pair<Integer,Integer> indices = (Pair<Integer,Integer>)value;
						super.getListCellRendererComponent(list,value,index,isSelected,cellHasFocus);
						setText("("+indices.getFirst()+","+indices.getSecond()+")");
						return this;
					}
				});
				
				Base[] bases = DEFAULT_ACID.bases;
				add(q1=new JComboBox<Pair<Base,Base>>());
				for(int b1=0;b1<bases.length;b1++) for(int b2=0;b2<bases.length;b2++)
					if(b1!=b2) q1.addItem(new Pair<Base,Base>(bases[b1],bases[b2]));
				q1.setRenderer(new DefaultListCellRenderer() {
					private static final long serialVersionUID = 1l;
					@SuppressWarnings("unchecked") @Override public Component getListCellRendererComponent(JList<?> list,Object value,int index,boolean isSelected,boolean cellHasFocus) {
						Pair<Base,Base> bases = (Pair<Base,Base>)value;
						super.getListCellRendererComponent(list,value,index,isSelected,cellHasFocus);
						setText("("+bases.getFirst()+","+bases.getSecond()+")");
						return this;
					}
				});
				
				add(q2=new JComboBox<Pair<Base,Base>>());
				for(int b1=0;b1<bases.length;b1++) for(int b2=b1;b2<bases.length;b2++)
					if(b1!=b2) q2.addItem(new Pair<Base,Base>(bases[b1],bases[b2]));
				q2.setRenderer(new DefaultListCellRenderer() {
					private static final long serialVersionUID = 1l;
					@Override public Component getListCellRendererComponent(JList<?> list,Object value,int index,boolean isSelected,boolean cellHasFocus) {
						@SuppressWarnings("unchecked") Pair<Base,Base> bases = (Pair<Base,Base>)value;
						super.getListCellRendererComponent(list,value,index,isSelected,cellHasFocus);
						setText("{"+bases.getFirst()+","+bases.getSecond()+"}");
						return this;
					}
				});
				
				bdas.addActionListener(new ActionListener() {
					@Override public void actionPerformed(ActionEvent event) {
						setBinaryDichotomicAlgorithm(bdas.getItemAt(bdas.getSelectedIndex()));
					}
				});
				bdas.setSelectedIndex(bdas.getItemCount()-1);
			}
			public EditorPanel(BinaryDichotomicAlgorithm bda) {
				this(); setBinaryDichotomicAlgorithm(bda);
			}
			
			public void setBinaryDichotomicAlgorithm(BinaryDichotomicAlgorithm bda) {
				bdas.setSelectedItem(bda);
				if(bda!=null) {
					i1i2.setSelectedItem(new Pair<Integer,Integer>(bda.i1()+1,bda.i2()+1));
					q1.setSelectedItem(toPair(bda.q1())); q2.setSelectedItem(toPair(bda.q2()));
				}
				
				boolean enabled = !isPredefined(bda);
				for(JComboBox<?> combo:new JComboBox<?>[]{i1i2,q1,q2})
					combo.setEnabled(enabled);
			}
			public BinaryDichotomicAlgorithm getBinaryDichotomicAlgorithm() {
				BinaryDichotomicAlgorithm bda = bdas.getItemAt(bdas.getSelectedIndex());
				if(bda==null) {
			  	Pair<Integer,Integer> indices = i1i2.getItemAt(i1i2.getSelectedIndex());
			  	bda = new net.gumbix.geneticcode.dich.BinaryDichotomicAlgorithm(indices.getFirst()-1,indices.getSecond()-1,
		  			toTuple(q1.getItemAt(q1.getSelectedIndex())),toSet(q2.getItemAt(q2.getSelectedIndex())));
				} return bda;
			}
		}
	}
	
	public static class Helper {
		private static final Pattern BDA_PATTERN = Pattern.compile("\\((\\d),(\\d)\\) \\((\\w),(\\w)\\) \\{(\\w),(\\w)\\}");
		
		public static String toString(Collection<BinaryDichotomicAlgorithm> bdas) {
			StringWriter writer = new StringWriter();
			try { writeTo(writer, bdas); } catch(IOException e) { /* nothing to do here */ }
			return writer.toString();
		}
		public static Collection<BinaryDichotomicAlgorithm> parseString(String string) throws IOException {			
			return readFrom(new StringReader(string));
		}
		
		public static void writeFile(File file,Collection<BinaryDichotomicAlgorithm> bdas) throws IOException {
			try(FileWriter writer=new FileWriter(file)) { writeTo(writer,bdas); }
		}
		public static void writeTo(Writer writer,Collection<BinaryDichotomicAlgorithm> bdas) throws IOException {
			for(BinaryDichotomicAlgorithm bda:bdas) {
				Pair<Base,Base> q1 = toPair(bda.q1()), q2 = toPair(bda.q2());
				writer.write(String.format("(%d,%d) (%C,%C) {%C,%C}\n",
					bda.i1()+1,bda.i2()+1,q1.getFirst().letter,q1.getSecond().letter,q2.getFirst().letter,q2.getSecond().letter));
			}
		}
		
		public static List<BinaryDichotomicAlgorithm> readFile(File file) throws IOException {
			try(FileReader reader=new FileReader(file)) { return readFrom(reader); }
		}
		public static List<BinaryDichotomicAlgorithm> readFrom(Reader reader) throws IOException {
			List<BinaryDichotomicAlgorithm> bdas = new ArrayList<>();
			String line; Matcher matcher; BufferedReader buffer = new BufferedReader(reader);			
			while((line=buffer.readLine())!=null) {
				if(line.isEmpty()) continue;
				else if((matcher=BDA_PATTERN.matcher(line)).matches()) {
			  	bdas.add(new net.gumbix.geneticcode.dich.BinaryDichotomicAlgorithm(Integer.parseInt(matcher.group(1))-1,Integer.parseInt(matcher.group(2))-1,
		  			toTuple(new Pair<Base,Base>(Base.valueOf(matcher.group(3).charAt(0)),Base.valueOf(matcher.group(4).charAt(0)))),
		  			toSet(new Pair<Base,Base>(Base.valueOf(matcher.group(5).charAt(0)),Base.valueOf(matcher.group(6).charAt(0))))));
				} else throw new IOException("Unknown line format for BDA \""+line+"\", expected (%d,%d) (%C,%C) {%C,%C}");
			} return bdas;
		}
		
		public static String getName(BinaryDichotomicAlgorithm bda) {
			if(RumerBDA$.MODULE$.equals(bda))
				return "Rumer";
			else if(ParityBDA$.MODULE$.equals(bda))
				return "Partiy";
			else if(AntiCodonBDA$.MODULE$.equals(bda))
				return "Complementary";
			else return "Other";
		}
		public static boolean isPredefined(BinaryDichotomicAlgorithm bda) {
			return RumerBDA$.MODULE$.equals(bda)
					|| ParityBDA$.MODULE$.equals(bda)
					|| AntiCodonBDA$.MODULE$.equals(bda);
		}
		
		public static Pair<Base,Base> toPair(scala.Tuple2<net.gumbix.geneticcode.dich.Compound,net.gumbix.geneticcode.dich.Compound> tuple) {
			return new Pair<Base,Base>(toBase(tuple._1),toBase(tuple._2));
		}
		public static Pair<Base,Base> toPair(scala.collection.Set<net.gumbix.geneticcode.dich.Compound> set) {
			scala.collection.Iterator<net.gumbix.geneticcode.dich.Compound> compounds =
				set.iterator();
			return new Pair<Base,Base>(toBase(compounds.next()),toBase(compounds.next()));
		}
		public static scala.Tuple2<net.gumbix.geneticcode.dich.Compound,net.gumbix.geneticcode.dich.Compound> toTuple(Pair<Base,Base> bases) {
			return new scala.Tuple2<net.gumbix.geneticcode.dich.Compound,net.gumbix.geneticcode.dich.Compound>(
					toCompound(bases.getFirst()),toCompound(bases.getSecond()));
		}
		public static scala.collection.immutable.Set<net.gumbix.geneticcode.dich.Compound> toSet(Pair<Base,Base> bases) {
			scala.collection.immutable.HashSet<net.gumbix.geneticcode.dich.Compound> set =
		  		new scala.collection.immutable.HashSet<>();
	  	return set.$plus(toCompound(bases.getFirst())).$plus(toCompound(bases.getSecond()));
		}
		
		public static Base toBase(net.gumbix.geneticcode.dich.Compound compound) {
			if(compound.equals(net.gumbix.geneticcode.dich.Adenine$.MODULE$))
				return Base.ADENINE;
			else if(compound.equals(net.gumbix.geneticcode.dich.Uracil$.MODULE$))
				return Base.URACIL;
			else if(compound.equals(net.gumbix.geneticcode.dich.Cytosine$.MODULE$))
				return Base.CYTOSINE;
			else if(compound.equals(net.gumbix.geneticcode.dich.Guanine$.MODULE$))
				return Base.GUANINE;
			else return null;
		}
		public static net.gumbix.geneticcode.dich.Compound toCompound(Base base) {
			switch(base) {
			case ADENINE: return net.gumbix.geneticcode.dich.Adenine$.MODULE$;
			case URACIL: case THYMINE: return net.gumbix.geneticcode.dich.Uracil$.MODULE$;
			case CYTOSINE: return net.gumbix.geneticcode.dich.Cytosine$.MODULE$;
			case GUANINE: return net.gumbix.geneticcode.dich.Guanine$.MODULE$;
			default: return null; }
		}
	}
	
	public static void main(String[] args) {
		Guitilities.prepareLookAndFeel();
		SwingUtilities.invokeLater(new Runnable() {
			@Override public void run() {
				GenBDA bda = new GenBDA();
				bda.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
				bda.setVisible(true);
			}
		});
	}
}
