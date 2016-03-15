package bio.gcat.gui.helper;

import static bio.gcat.gui.helper.Guitilities.getImageIcon;
import static bio.gcat.nucleic.helper.GenBank.DATABASE_NUCLEOTIDE;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import bio.gcat.Utilities.OperatingSystem;
import bio.gcat.nucleic.helper.GenBank;

public class GenBankPicker extends JDialog {
	private static final long serialVersionUID = 1l;

	private JList<Map<String,Object>> list;
	private JLabel select;
	
	private String[] selectedAccessionIds;
	
	public GenBankPicker() {
		super(); initialize(); }
	public GenBankPicker(Dialog owner) {
		super(owner); initialize(); }
	public GenBankPicker(Frame owner) {
		super(owner); initialize(); }
	public GenBankPicker(Window owner) {
		super(owner); initialize(); }
	
	private void initialize() {
		setModal(true);
		setTitle("Search GenBank");
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		
		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		
		JLabel label = new JLabel("Search Term:"); select = new JLabel();
		JButton search = new JButton(), pick = new JButton(), cancel = new JButton("Cancel");
		
		JTextField term = new JTextField();
		term.addKeyListener(new KeyAdapter() {
			@Override public void keyPressed(KeyEvent event) {
				if(event.getKeyCode()==KeyEvent.VK_ENTER)
					search.getAction().actionPerformed(null);
			}
		});
		
		DefaultListModel<Map<String,Object>> model;
		list = new JList<>(model=new DefaultListModel<>());
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
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
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
								JOptionPane.showMessageDialog(GenBankPicker.this, "Could not search GenBank:\n\n"+e.getMessage(), "Search GenBank", JOptionPane.WARNING_MESSAGE);
							}});
						} finally { search.setEnabled(true); search.setText("Search"); }
					}
				}).start();
			}
		});
		
		pick.setAction(new AbstractAction("Pick") {
			private static final long serialVersionUID = 1l;
			@Override public void actionPerformed(ActionEvent event) {
				selectedAccessionIds = list.getSelectedValuesList().stream().map(
					map->map.get("uid").toString()).toArray(size->new String[size]);
				dispose();
			}
		});
		cancel.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				selectedAccessionIds = null; dispose(); }
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
					.addComponent(select)
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
					.addComponent(select)
					.addComponent(pick)
					.addComponent(cancel)
				)
		);
		layout.linkSize(SwingConstants.VERTICAL, label, term, search);
		layout.linkSize(SwingConstants.VERTICAL, pick, cancel, select);
		
		pack(); setMinimumSize(getSize());
		setSize(500, 300);
	}
	
	public boolean pick() {
		setVisible(true); // return true if at least one accession id was picked
		return getSelectedAccessionId()!=null;
	}
	
	public void setSelectionMode(int selectionMode) {
		list.setSelectionMode(selectionMode);
		select.setText(selectionMode==ListSelectionModel.SINGLE_SELECTION?null:
			"(pick multiple sequences by holding the "+(!OperatingSystem.MAC.equals(OperatingSystem.currentOperatingSystem())?"Ctrl":"Command")+" key on your keyboard)");
	}
	
	public String getSelectedAccessionId() {
		return selectedAccessionIds!=null&&selectedAccessionIds.length>0?
			selectedAccessionIds[0]:null;
	}
	public String[] getSelectedAccessionIds() {
		return selectedAccessionIds; }
}
