package de.hsma.gentool.gui;

import static de.hsma.gentool.Utilities.*;
import static de.hsma.gentool.gui.helper.Guitilities.*;
import static de.hsma.gentool.nucleic.Acid.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.DefaultListCellRenderer;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import net.gumbix.geneticcode.dich.Adenine$;
import net.gumbix.geneticcode.dich.AntiCodonBDA$;
import net.gumbix.geneticcode.dich.BinaryDichotomicAlgorithm;
import net.gumbix.geneticcode.dich.Compound;
import net.gumbix.geneticcode.dich.Cytosine$;
import net.gumbix.geneticcode.dich.Guanine$;
import net.gumbix.geneticcode.dich.ParityBDA$;
import net.gumbix.geneticcode.dich.RumerBDA$;
import net.gumbix.geneticcode.dich.Uracil$;
import net.gumbix.geneticcode.dich.ui.JGeneticCodeTable;
import org.apache.commons.math3.util.Pair;
import de.hsma.gentool.gui.helper.Guitilities;
import de.hsma.gentool.gui.helper.ListTableModel;
import de.hsma.gentool.nucleic.Acid;
import de.hsma.gentool.nucleic.Base;

public class GenBDA extends JFrame implements ActionListener, ListDataListener, ListSelectionListener {
	private static final long serialVersionUID = 1l;
	
	private JMenuBar menubar;
	private JMenu[] menu;
	/*private JPanel toolbars;
	private JToolBar[] toolbar;*/
	
	private BinaryDichotomicAlgorithmPanel bdaPanel;
	private JPanel gcTablePanel;
	private JGeneticCodeTable gcTable;
	
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
		
		/*menu[0].add(createMenuItem("Import...", "table_go.png", KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.CTRL_DOWN_MASK), ACTION_IMPORT, this));
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
		
		add(createSplitPane(JSplitPane.HORIZONTAL_SPLIT,false,true,360,0.195,new JScrollPane(bdaPanel=new BinaryDichotomicAlgorithmPanel()),new JScrollPane(gcTablePanel=new JPanel())), BorderLayout.CENTER);
		((ListTableModel<?>)bdaPanel.table.getModel()).addListDataListener(this);
		bdaPanel.table.getSelectionModel().addListSelectionListener(this);
	}
	
	@Override public void actionPerformed(ActionEvent event) {
		/*String action;
		switch(action=event.getActionCommand()) {
		case ACTION_IMPORT: importSequences(); break;
		case ACTION_EXPORT: exportSequences(); break;
		case ACTION_CLOSE: hideBatch(); break;
		case ACTION_ACTION_ADD: addAction(); break;
		case ACTION_ACTION_EDIT: editAction(); break;
		case ACTION_ACTION_REMOVE: removeAction(); break;
		case ACTION_ACTIONS_CLEAR: clearActions(); break;
		case ACTION_ACTION_MOVE_UP: moveAction(true); break;
		case ACTION_ACTION_MOVE_DOWN: moveAction(false); break;
		case ACTION_SEQUENCES_REMOVE: removeSequences(); break;
		case ACTION_SEQUENCES_CLEAR: clearSequences(); break;
		case ACTION_EXECUTE: executeBatch(); break;
		case ACTION_PREFERENCES: showPreferences(); break;
		case ACTION_ABOUT: showAbout(); break; 
		default: System.err.println(String.format("Action %s not implemented.", action)); }*/
	}
	
	@Override public void intervalRemoved(ListDataEvent event) { contentsChanged(event); }
	@Override public void intervalAdded(ListDataEvent event) { contentsChanged(event); }
	@Override public void contentsChanged(ListDataEvent event) {
		//enableActionMenus(); enableSequenceMenus();
		revalidateGeneticCodeTable();
	}
	@Override public void valueChanged(ListSelectionEvent event) {
		//enableActionMenus(); enableSequenceMenus();
	}
	
	@SuppressWarnings("unchecked") public void revalidateGeneticCodeTable() {		
    scala.collection.immutable.List<?> bdas =
    	scala.collection.JavaConversions.collectionAsScalaIterable(bdaPanel.getBinaryDichotomicAlgorithms()).toList();
    
    net.gumbix.geneticcode.dich.ct.ClassTable classTable = null;
    if(bdas.nonEmpty()) classTable =
    	new net.gumbix.geneticcode.dich.ct.ClassTable(
    		(scala.collection.immutable.List<net.gumbix.geneticcode.dich.Classifier<Object>>)bdas,
				net.gumbix.geneticcode.dich.IUPAC.STANDARD(),
				new net.gumbix.geneticcode.dich.IdAminoAcidProperty(1));
    
    if(gcTable!=null) gcTablePanel.remove(gcTable);
    if(classTable!=null)
    	gcTablePanel.add(gcTable=new net.gumbix.geneticcode.dich.ui.JGeneticCodeTable(classTable),
	    	BorderLayout.CENTER);
    else gcTable = null;
    gcTablePanel.revalidate();
    gcTablePanel.repaint();
	}
	
	class BinaryDichotomicAlgorithmPanel extends JPanel {
		private static final long serialVersionUID = 1l;
		
		private BinaryDichotomicAlgorithmTableModel bdas;
		private JTable table;
		
		private JButton add,edit,up,down,remove,clear;
		
		public BinaryDichotomicAlgorithmPanel() {
			GroupLayout layout = setGroupLayout(this);
			
			(bdas = new BinaryDichotomicAlgorithmTableModel()).addListDataListener(new ListDataListener() {
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
			
			final BinaryDichotomicAlgorithmEditor editor = new BinaryDichotomicAlgorithmEditor();
			
			JButton add = new JButton("Add");
			add.addActionListener(new ActionListener() {
				@Override public void actionPerformed(ActionEvent e) {
					addBinaryDichotomicAlgorithm(editor.getBDA());
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
			
			final BinaryDichotomicAlgorithmEditor editor = new BinaryDichotomicAlgorithmEditor(getSelectedBinaryDichotomicAlgorithm());
			
			JButton add = new JButton("Edit");
			add.addActionListener(new ActionListener() {
				@Override public void actionPerformed(ActionEvent e) {
					bdas.set(table.getSelectedRow(),editor.getBDA());
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
		protected void selectBinaryDichotomicAlgorithm(int index) {
			table.setRowSelectionInterval(index,index);
		}
		
		public List<BinaryDichotomicAlgorithm> getBinaryDichotomicAlgorithms() { return bdas; }
		public BinaryDichotomicAlgorithm getSelectedBinaryDichotomicAlgorithm() {
			return bdas.get(table.getSelectedRow());
		}
		public void addBinaryDichotomicAlgorithm(BinaryDichotomicAlgorithm bda) {
			bdas.add(bda);
			selectBinaryDichotomicAlgorithm(bdas.size()-1);
			table.requestFocus();
		}
		
		private void enableBinaryDichotomicAlgorithmButtons() {
			int size = bdas.size(),
				index = table.getSelectedRow();
			boolean selected = index!=-1,
			  filled = size!=0;
			if(selected&&index<size) {
				/*BinaryDichotomicAlgorithm bda = bdas.get(index);
				edit.setEnabled(isOtherBDA(bda));*/
				edit.setEnabled(true);
			} else edit.setEnabled(false);
			up.setEnabled(index>0);
			down.setEnabled(selected&&index<size-1);
			remove.setEnabled(selected);
			clear.setEnabled(filled);
		}
		
		private class BinaryDichotomicAlgorithmTableModel extends ListTableModel<BinaryDichotomicAlgorithm> {
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
					return getBDAName(bda);
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
		private class BinaryDichotomicAlgorithmEditor extends JPanel {
			private static final long serialVersionUID = 1l;
			
			protected final Acid DEFAULT_ACID = RNA;
			
			private JComboBox<BinaryDichotomicAlgorithm> bdas;
			private JComboBox<Pair<Integer,Integer>> i1i2;
			private JComboBox<Pair<Base,Base>> q1,q2;
			
			public BinaryDichotomicAlgorithmEditor() {
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
						setText(getBDAName((BinaryDichotomicAlgorithm)value));
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
						setBDA(bdas.getItemAt(bdas.getSelectedIndex()));
					}
				});
				bdas.setSelectedIndex(bdas.getItemCount()-1);
			}
			public BinaryDichotomicAlgorithmEditor(BinaryDichotomicAlgorithm bda) {
				this(); setBDA(bda);
			}
			
			public void setBDA(BinaryDichotomicAlgorithm bda) {
				bdas.setSelectedItem(bda);
				if(bda!=null) {
					i1i2.setSelectedItem(new Pair<Integer,Integer>(bda.i1()+1,bda.i2()+1));
					q1.setSelectedItem(toPair(bda.q1())); q2.setSelectedItem(toPair(bda.q2()));
				}
				
				boolean enabled = !isBDAKnown(bda);
				for(JComboBox<?> combo:new JComboBox<?>[]{i1i2,q1,q2})
					combo.setEnabled(enabled);
			}
			public BinaryDichotomicAlgorithm getBDA() {
				BinaryDichotomicAlgorithm bda = bdas.getItemAt(bdas.getSelectedIndex());
				if(bda==null) {
			  	Pair<Integer,Integer> indices = i1i2.getItemAt(i1i2.getSelectedIndex());
			  	bda =new net.gumbix.geneticcode.dich.BinaryDichotomicAlgorithm(indices.getFirst()-1,indices.getSecond()-1,
		  			toTuple(q1.getItemAt(q1.getSelectedIndex())),toSet(q2.getItemAt(q2.getSelectedIndex())));
				} return bda;
			}
		}
	}
	
	private static String getBDAName(BinaryDichotomicAlgorithm bda) {
		if(bda==(Object)RumerBDA$.MODULE$)
			return "Rumer";
		else if(bda==(Object)ParityBDA$.MODULE$)
			return "Partiy";
		else if(bda==(Object)AntiCodonBDA$.MODULE$)
			return "Complementary";
		else return "Other";
	}
	private boolean isBDAKnown(BinaryDichotomicAlgorithm bda) {
		Object bdaObject = bda;
		return bdaObject instanceof RumerBDA$
				|| bdaObject instanceof ParityBDA$
				|| bdaObject instanceof AntiCodonBDA$;
	}
	
	private static Pair<Base,Base> toPair(scala.Tuple2<Compound,Compound> tuple) {
		return new Pair<Base,Base>(toBase(tuple._1),toBase(tuple._2));
	}
	private static Pair<Base,Base> toPair(scala.collection.Set<Compound> set) {
		scala.collection.Iterator<net.gumbix.geneticcode.dich.Compound> compounds =
			set.iterator();
		return new Pair<Base,Base>(toBase(compounds.next()),toBase(compounds.next()));
	}
	private static scala.Tuple2<Compound,Compound> toTuple(Pair<Base,Base> bases) {
		return new scala.Tuple2<Compound,Compound>(toCompound(bases.getFirst()),toCompound(bases.getSecond()));
	}
	private static scala.collection.immutable.Set<Compound> toSet(Pair<Base,Base> bases) {
		scala.collection.immutable.HashSet<net.gumbix.geneticcode.dich.Compound> set =
	  		new scala.collection.immutable.HashSet<>();
  	return set.$plus(toCompound(bases.getFirst())).$plus(toCompound(bases.getSecond()));
	}
	
	private static Base toBase(Compound compound) {
		if(compound.equals(Adenine$.MODULE$))
			return Base.ADENINE;
		else if(compound.equals(Uracil$.MODULE$))
			return Base.URACIL;
		else if(compound.equals(Cytosine$.MODULE$))
			return Base.CYTOSINE;
		else if(compound.equals(Guanine$.MODULE$))
			return Base.GUANINE;
		else return null;
	}
	private static Compound toCompound(Base base) {
		switch(base) {
		case ADENINE: return Adenine$.MODULE$;
		case URACIL: case THYMINE: return Uracil$.MODULE$;
		case CYTOSINE: return Cytosine$.MODULE$;
		case GUANINE: return Guanine$.MODULE$;
		default: return null; }
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
