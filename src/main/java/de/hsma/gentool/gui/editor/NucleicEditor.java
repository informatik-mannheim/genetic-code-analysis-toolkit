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
package de.hsma.gentool.gui.editor;

import static de.hsma.gentool.Utilities.*;
import static de.hsma.gentool.gui.helper.Guitilities.*;
import static de.hsma.gentool.nucleic.Acid.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.regex.Pattern;
import javax.swing.Icon;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Position;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;
import org.reflections.Reflections;
import de.hsma.gentool.Documented;
import de.hsma.gentool.Help;
import de.hsma.gentool.Option;
import de.hsma.gentool.Utilities;
import de.hsma.gentool.Utilities.ArrayComparator;
import de.hsma.gentool.gui.editor.NucleicListener.NucleicEvent;
import de.hsma.gentool.gui.editor.display.GraphDisplay;
import de.hsma.gentool.gui.helper.AttachedScrollPane;
import de.hsma.gentool.gui.helper.BetterGlassPane;
import de.hsma.gentool.gui.helper.VerticalLabelUI;
import de.hsma.gentool.gui.helper.WrapEditorKit;
import de.hsma.gentool.nucleic.Acid;
import de.hsma.gentool.nucleic.Tuple;

public class NucleicEditor extends JRootPane {
	public static enum EditorMode { SEQUENCE,SET }
	
	protected static final Pattern LINE_PATTERN = Pattern.compile("^(([A-Z_]+): *)?([A-Z]{3})( +([#%+\\-]?)(\\d+)| +([A-Z_]+))?( *//.*)?$"); //^(([A-Z_]+): *)?([A-Z]{3})( +([#%]?)(\d+)| +([A-Z_]+))?( *//.*)?$
	
	private static final long serialVersionUID = 1l;
	
	protected NucleicDocument document;
	protected UndoManager undo = new UndoManager();
	protected UndoableEditListener edit;
	
	protected EditorMode mode = EditorMode.SEQUENCE;
	
	private NavigableMap<Position,Tuple> tuples;
	
	private JTextPane textPane;
	private NumberPanel numberPanel;
	
	private JTabbedPane displayPane;
	private List<JLabel> displayLabels;
	private List<JRootPane> displayPanes;
	private List<NucleicDisplay> displays;
	private MouseAdapter displayResize;
	private HelpDisplay displayHelp;
	private int displaySize = 320;

	private JPanel optionPanel;
	private List<Option> options;
	private Option.Component optionLength, optionAcid, optionMode;
	
	private int cleanHash = EMPTY.hashCode();
	
	public NucleicEditor() {
		setLayout(new BorderLayout());
		new BetterGlassPane(this);
		getGlassPane().setVisible(true);
		
		textPane = new JTextPane() {
			private static final long serialVersionUID = 1l;
			private final Color lineColor = new Color(0,0,0,64);
			@Override public void paint(Graphics graphics) {
				super.paint(graphics);
				int defaultTupleLength = getDefaultTupleLength();
				if(defaultTupleLength>0) {
					graphics.setColor(lineColor);
					int width=getFontMetrics(getFont()).charWidth('0'), tupleWidth = width*defaultTupleLength;
					for(int left=getInsets().left+width/2+tupleWidth; left<textPane.getWidth(); left += tupleWidth + width)
						graphics.drawLine(left, 0, left, textPane.getHeight());
				}
			}
		};
		textPane.setEditorKit(new WrapEditorKit());
		textPane.setFont(new Font(Font.MONOSPACED,Font.PLAIN,14));
		textPane.setForeground(Color.BLACK);
		textPane.addKeyListener(new KeyAdapter() {
			@Override public void keyTyped(KeyEvent event) {
				char key = event.getKeyChar();
				if(key>='a'||key<='z')
					event.setKeyChar(Character.toUpperCase(key));
			}
		});
		textPane.addComponentListener(new ComponentAdapter() {
			@Override public void componentResized(ComponentEvent event) {
				if(numberPanel!=null) numberPanel.repaint();
			}
		});
		textPane.setDocument(document=new NucleicDocument());
		document.addUndoableEditListener(edit=new UndoableEditListener());
		document.setDefaultTupleLength(3);
		
		tuples = new ConcurrentSkipListMap<Position, Tuple>(new Comparator<Position>() {
			@Override public int compare(Position positionA, Position positionB) {
				return Integer.compare(positionA.getOffset(), positionB.getOffset());
			}
		})/* {
			private static final long serialVersionUID = 1l;
			@Override public Tuple put(Position position, Tuple tuple) {
				Tuple old = super.put(position, tuple);
				fireTuplePut(tuple);
				return old;
			}
			@Override public Tuple remove(Object key) {
				Tuple removed = super.remove(key);
				fireTupleRemoved(removed);
				return removed;
			}
		}*/;
		try {
			tuples.put(document.createPosition(0), new Tuple());
		} catch(BadLocationException e) { /** nothing to do here */ }
		document.addDocumentListener(new DocumentListener() {
			@Override public void insertUpdate(DocumentEvent event) {
				try {
					Document document = event.getDocument(); Position next;
					int endOfText = document.getLength(), offset = event.getOffset(), endOffset = offset+event.getLength(), tupleOffset = tupleOffset(document, offset);
					while(tupleOffset!=-1&&tupleOffset!=endOfText&&tupleOffset<=endOffset) {
						int nextTupleOffset = nextTupleOffset(document, tupleOffset);
						tuples.put(document.createPosition(tupleOffset), new Tuple(document.getText(tupleOffset, (nextTupleOffset!=-1?nextTupleOffset-1:endOfText)-tupleOffset)));
						if(offset!=0&&offset==tupleOffset&&(next=tuples.higherKey(fixPosition(tupleOffset)))!=null)
							tuples.remove(next);
						tupleOffset = nextTupleOffset;
					}
				} catch(BadLocationException e) { /** nothing to do here */ }
				fireTuplesInsert();
			}
			@Override public void removeUpdate(DocumentEvent event) {
				try {
					Document document = event.getDocument();
					int offset = event.getOffset(), tupleOffset = tupleOffset(document, offset), nextTupleOffset = nextTupleOffset(document, tupleOffset);
					String tuple = document.getText(tupleOffset, (nextTupleOffset!=-1?nextTupleOffset-1:document.getLength())-tupleOffset);
					for(Position position:tuples.subMap(fixPosition(tupleOffset), fixPosition(tupleOffset+tuple.length()+1)).keySet().toArray(new Position[0]))
						tuples.remove(position);
					tuples.put(document.createPosition(tupleOffset), new Tuple(tuple));
				} catch(BadLocationException e) { /** nothing to do here */ }
				fireTuplesRemoved();
			}
			@Override public void changedUpdate(DocumentEvent event) {
				/** nothing to do here (attribute change) */
			}
			
			private int tupleOffset(Document document, int offset) throws BadLocationException {
				while(offset!=0&&!SPACE.equals(document.getText(offset-1,1)))
					offset--;
				return offset;
			}
			private int nextTupleOffset(Document document, int offset) throws BadLocationException {
				do {
					if(offset==document.getLength())
						return -1;
				}	while(!SPACE.equals(document.getText(offset++,1)));
				return offset;
			}
		});
		
		addNucleicListener(new NucleicAdapter() {
			@Override public void tuplesInsert(NucleicEvent event) {
				if(mode==EditorMode.SET) {
					int defaultTupleLength = getDefaultTupleLength();
					List<Tuple> tuples = new ArrayList<>();
					for(Tuple tuple:event.getTuples())
						if(tuple.length()==defaultTupleLength) {
							if(!tuples.contains(tuple))
								tuples.add(tuple);
						} else tuples.add(tuple);
					if(tuples.size()!=event.getTuples().size())
						SwingUtilities.invokeLater(new Runnable() {
							@Override public void run() { setTuples(tuples); }
						});
				}
			}
		});
		
		AttachedScrollPane scrollPane = new AttachedScrollPane(textPane, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setRowHeaderView(numberPanel = new NumberPanel()); addNucleicListener(numberPanel);
		scrollPane.setRowFooterView(displayPane = new JTabbedPane(JTabbedPane.RIGHT) {
			private static final long serialVersionUID = 1l;
			@Override public Dimension getPreferredSize() {
				Dimension size = new Dimension(super.getPreferredSize());
				if(getSelectedIndex()==0)
					size.width -= 69; //bug to reduce size to minimum
				return size;
			}
		});
		scrollPane.setBorder(null);
		
		displayPane.setUI(new BasicTabbedPaneUI() {
			@Override protected Insets getTabAreaInsets(int tabPlacement) {
				int y = 10; if(scrollPane.getRowFooter().getViewPosition().y!=0)
					y += scrollPane.getViewport().getViewPosition().y;
				return new Insets(y,0,2,3);
			}
			@Override protected Insets getTabInsets(int tabPlacement,int tabIndex) { return tabIndex!=0?new Insets(20,3,20,3):new Insets(0,3,10,3); }
			@Override protected Insets getContentBorderInsets(int tabPlacement) { return new Insets(0,0,0,super.getContentBorderInsets(tabPlacement).right); }			
			
			@Override protected void paintTabArea(Graphics graphics,int tabPlacement,int selectedIndex) {
				((TabbedPaneLayout)displayPane.getLayout()).layoutContainer(null);
				super.paintTabArea(graphics,tabPlacement,selectedIndex);
			}
			
			@Override protected void paintContentBorderRightEdge(Graphics graphics,int tabPlacement,int selectedIndex,int x,int y,int w,int h) {
				super.paintContentBorderRightEdge(graphics,tabPlacement,selectedIndex!=0?selectedIndex:-1,x,y,w,h);
			}
			@Override protected void paintTabBorder(Graphics graphics,int tabPlacement,int tabIndex,int x,int y,int w,int h,boolean isSelected) {
				if(tabIndex!=0) super.paintTabBorder(graphics,tabPlacement,tabIndex,x,y,w,h,isSelected);
			}
			@Override protected void paintFocusIndicator(Graphics graphics,int tabPlacement,Rectangle[] rects,int tabIndex,Rectangle iconRect,Rectangle textRect,boolean isSelected) {
				if(tabIndex!=0) super.paintFocusIndicator(graphics,tabPlacement,rects,tabIndex,iconRect,textRect,isSelected);
			}
			@Override protected int getTabLabelShiftX(int tabPlacement,int tabIndex,boolean isSelected) {
				return tabIndex!=0?super.getTabLabelShiftX(tabPlacement,tabIndex,isSelected):0;
			}
			
			// do only paint right border edge
			@Override protected void paintContentBorderLeftEdge(Graphics g,int tabPlacement,int selectedIndex,int x,int y,int w,int h) {}
			@Override protected void paintContentBorderBottomEdge(Graphics g,int tabPlacement,int selectedIndex,int x,int y,int w,int h) {}
			@Override protected void paintContentBorderTopEdge(Graphics g,int tabPlacement,int selectedIndex,int x,int y,int w,int h) {}
		});
		displayPane.addChangeListener(new ChangeListener() {
			private int currentTabIndex = -1,previousTabIndex;
			@Override public void stateChanged(ChangeEvent event) {
				previousTabIndex = currentTabIndex;
        currentTabIndex = displayPane.getSelectedIndex();
				if(previousTabIndex!=-1) displayPanes.get(previousTabIndex).getContentPane().removeAll();
				if(currentTabIndex!=-1) displayPanes.get(currentTabIndex).getContentPane().add((Component)displays.get(currentTabIndex));
			}
		});
		
		displayResize = new MouseAdapter() {
			private int dragX;
			@Override public void mouseMoved(MouseEvent event) {
				Component component = (Component)event.getSource();
				if(event.getX()>2) {
					if(component.getCursor().getType()!=Cursor.DEFAULT_CURSOR)
						component.setCursor(Cursor.getDefaultCursor());
				} else {
					if(component.getCursor().getType()!=Cursor.W_RESIZE_CURSOR)
						component.setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
				}
			}
			@Override public void mousePressed(MouseEvent event) {
				dragX = event.getX()<=2?event.getX():-1;
			}
			@Override public void mouseReleased(MouseEvent event) {
				if(dragX!=-1) {
					displaySize = Math.max(100,displaySize+(dragX-event.getX()));
					displayPane.revalidate();
				}
			}
		};
		
		displayLabels = new ArrayList<>(); displayPanes = new ArrayList<>();
		displays = new ArrayList<NucleicDisplay>();		
		addDisplay(new NoDisplay());
		for(Class<? extends NucleicDisplay> displayClass:new Reflections(NucleicDisplay.class.getPackage().getName()).getSubTypesOf(NucleicDisplay.class))
			if(!NoDisplay.class.equals(displayClass)&&!HelpDisplay.class.equals(displayClass)) try {
				addDisplay(displayClass.getConstructor(new Class[]{NucleicEditor.class}).newInstance(this));
			}	catch(Exception e) { e.printStackTrace(); }
		addDisplay(displayHelp=new HelpDisplay());
		this.addComponentListener(new ComponentAdapter() {			
			@Override public void componentResized(ComponentEvent e) {
				NucleicDisplay display = displays.get(displayPane.getSelectedIndex());
				if(display.hasPreferredSize())
					display.setPreferredSize();
			}
		});
		displayPane.setSelectedIndex(displayPane.indexOfTab(GraphDisplay.LABEL));
		
		optionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		optionPanel.setBorder(new EmptyBorder(1,0,0,0) { //paint the border to the edge of NumberPanel
			private static final long serialVersionUID = 1l;
			@Override public void paintBorder(java.awt.Component component, Graphics graphics, int x, int y, int width, int height) {
				Color oldColor = graphics.getColor();
				graphics.setColor(Color.GRAY);
				graphics.drawLine((numberPanel!=null?numberPanel.getPreferredSize().width:1)-1,0,width,0);
				graphics.setColor(oldColor);
			}
		});
		
		(optionLength=addOption(new Option("tupleLength", "Tuple Length", getDefaultTupleLength(), 0, 10, 1))).addChangeListener(new ChangeListener() {
			@Override public void stateChanged(ChangeEvent event) {
				int oldValue = getDefaultTupleLength(), newValue = (Integer)((JSpinner)event.getSource()).getValue();
				if(EditorMode.SET.equals(getMode())&&newValue!=0&&newValue<oldValue&&
					JOptionPane.showOptionDialog(NucleicEditor.this,"<html><b>Warning:</b> Reducing the tuple length in set editor mode, might lead\nto a loss of tuples, because duplicate tuples are beeing removed\nimmediately after the conversion was performed.","Change default tuple length.",JOptionPane.OK_CANCEL_OPTION,JOptionPane.WARNING_MESSAGE,null,new String[]{"Continue","Cancel"},JOptionPane.CANCEL_OPTION)!=JOptionPane.OK_OPTION) {
					((JSpinner)event.getSource()).setValue(oldValue);
				} else setDefaultTupleLength(newValue);
			}
		});
		(optionAcid=addOption(new Option("acid", "Default Acid", getDefaultAcid(), new Acid[]{null,RNA,DNA}, EMPTY, RNA.name(), DNA.name()))).addItemListener(new ItemListener() {
			@Override public void itemStateChanged(ItemEvent event) {
				setDefaultAcid(event.getStateChange()==ItemEvent.SELECTED?(Acid)event.getItem():null);
			}
		});
		(optionMode=addOption(new Option("editorMode", "Editor Mode", EditorMode.SEQUENCE, new EditorMode[]{EditorMode.SEQUENCE,EditorMode.SET}, "Sequence", "Set"))).addItemListener(new ItemListener() {
			@Override public void itemStateChanged(ItemEvent event) {
				if(event.getStateChange()==ItemEvent.SELECTED)
					setMode((EditorMode)event.getItem());
			}
		});
		
		this.add(scrollPane, BorderLayout.CENTER);
		this.add(optionPanel, BorderLayout.SOUTH);
	}

	public List<NucleicDisplay> getDisplays() { return Collections.unmodifiableList(displays); }
	public void addDisplay(NucleicDisplay display) {
		if(!(display instanceof Component))
			throw new IllegalArgumentException();
		
		JLabel label = new JLabel(display.getLabel(), display.getIcon(), SwingConstants.LEFT);
		label.setUI(new VerticalLabelUI(true));
		label.setIconTextGap(8);
		
		JRootPane pane = new JRootPane() {
			private static final long serialVersionUID = 1l;
			@Override public Dimension getPreferredSize() {
				if(displayPane.getSelectedComponent()==this) {
					if(display.hasPreferredSize()) {
						display.setPreferredSize();
						return ((Component)display).getPreferredSize();
					} else return new Dimension(displaySize,0);
				} else return new Dimension();
			}
		};
		pane.getContentPane().setLayout(new BorderLayout());
		if(!display.hasPreferredSize()) {
			BetterGlassPane glassPane = new BetterGlassPane(pane);
			glassPane.addMouseListener(displayResize);
			glassPane.addMouseMotionListener(displayResize);
			glassPane.setVisible(true);
		}
		
		displays.add(display); displayLabels.add(label); displayPanes.add(pane);
		displayPane.addTab(display.getLabel(),display.getIcon(),pane);
		displayPane.setTabComponentAt(displayPane.getTabCount()-1,label);
		if(display instanceof NucleicListener)
			addNucleicListener((NucleicListener)display);
	}
	public void toggleDisplay(Class<? extends NucleicDisplay> display) {
		for(int index=0;index<displays.size();index++)
			if(display.isInstance(displays.get(index))) {
				displayPane.setSelectedIndex(index); return; }
	}
	public void toggleHelp() { toggleDisplay(HelpDisplay.class); }
	public void toggleHelpIndex() { displayHelp.showIndex(); toggleHelp(); }
	public void toggleHelpPage(String... page) { displayHelp.showPage(page); toggleHelp();		 }
	public void toggleHelpPage(Documented documented) { toggleHelpPage(Utilities.add(documented.category(), documented.title())); }
	
	public List<Option> getOptions() { return Collections.unmodifiableList(options); }
	public Option.Component addOption(Option option) {
		Option.Component component;
		optionPanel.add(new JLabel(option.label+":"));
		optionPanel.add(component=option.new Component() {
			private static final long serialVersionUID = 1l;
			private boolean update; //prevent loop with event listeners of option
			@Override public void setValue(Object value) {
				if(update) return; else update = true;
				super.setValue(value);
				update = false;
			}
		}); return component;
		
	}
	
	public JTextPane getTextPane() { return textPane; }
	public JPanel getNumberPanel() { return numberPanel; }
	public NucleicDocument getDocument() { return document; }
	
	public String getText() { return textPane.getText(); }
	public String getText(int offset) throws BadLocationException { return textPane.getText(offset, document.getLength()-offset); }
	public String getText(int offset, int length) throws BadLocationException { return textPane.getText(offset, length); }
	public void setText(String text) { edit.startTransaction(); textPane.setText(text); edit.commitTransaction(); }
	
	public Collection<Tuple> getTuples() { return Collections.unmodifiableCollection(tuples.values()); }
	public List<Tuple> getTupleList() {
		List<Tuple> list = new ArrayList<>(tuples.values());
		int last = list.size()-1;
		if(last>=0&&list.get(last).length()==0)
			list.remove(last);
		return Collections.unmodifiableList(list);
	}
	public NavigableMap<Position,Tuple> getTupleMap() { return tuples; }
	public void setTuples(Collection<Tuple> tuples) {
		invokeAppropriate(new Runnable() {
			@Override public void run() { edit.startTransaction(); textPane.setText(null); appendTuples(tuples); edit.commitTransaction(); }
		});
	}
	public void appendTuples(Collection<Tuple> tuples) {
		invokeAppropriate(new Runnable() { @Override public void run() {
			edit.startTransaction();
			try {
				int length = document.getLength();
				if(length!=0&&!SPACE.equals(document.getText(length-1,1)))
					document.insertString(length++,SPACE,null);
				document.insertString(length, Tuple.joinTuples(tuples), null);
			} catch(BadLocationException e) { /* nothing to do here */ }
			  finally { edit.commitTransaction(); }
		}});
	}
	
	public int getDefaultTupleLength() { return document.getDefaultTupleLength(); }
	public void setDefaultTupleLength(int defaultTupleLength) {
		optionLength.setValue(defaultTupleLength);
		document.setDefaultTupleLength(defaultTupleLength);
		document.adaptText(); repaint();
	}
	
	public Acid getDefaultAcid() { return document.getDefaultAcid(); }
	public void setDefaultAcid(Acid defaultAcid) {
		optionAcid.setValue(defaultAcid);
		document.setDefaultAcid(defaultAcid);
		if(defaultAcid!=null) document.adaptText();
	}
	
	public EditorMode getMode() { return mode; }
	public void setMode(EditorMode mode) {
		optionMode.setValue(mode);
		this.mode = mode; document.adaptText();
	}
	
	public void setDirty() { cleanHash = -1; }
	public void setClean() { cleanHash = textPane.getText().hashCode(); }
	public boolean isDirty() { return cleanHash != textPane.getText().hashCode(); }
	
	public boolean canUndo() { return undo.canUndo(); }
	public void undoEdit() {
		try { undo.undo(); fireTuplesChanged(); }
		catch(CannotUndoException e) {
			/* nothing to do here */
		}
	}
	public boolean canRedo() { return undo.canRedo(); }
	public void redoEdit() {
		try { undo.redo(); fireTuplesChanged(); }
		catch(CannotRedoException e) {
			/* nothing to do here */
		}
	}
	
	@Override public void requestFocus() { textPane.requestFocus(); }

  public void addNucleicListener(NucleicListener listener) {
    listenerList.add(NucleicListener.class, listener);
  }
  public void removeNucleicListener(NucleicListener listener) {
  	listenerList.remove(NucleicListener.class, listener);
  }
  public NucleicListener[] getNucleicListeners() {
  	return listenerList.getListeners(NucleicListener.class);
  }
  protected void fireTuplesInsert() {
  	NucleicEvent event = null;
  	Object[] listeners = listenerList.getListenerList();
  	for(int index = listeners.length-2;index>=0;index-=2)
  		if(listeners[index]==NucleicListener.class)
  			((NucleicListener)listeners[index+1]).tuplesInsert(event!=null?event:(event=new NucleicEvent(NucleicEditor.this,getTuples())));
  }
  protected void fireTuplesRemoved() {
  	NucleicEvent event = null;
  	Object[] listeners = listenerList.getListenerList();
  	for(int index = listeners.length-2;index>=0;index-=2)
  		if(listeners[index]==NucleicListener.class)
  			((NucleicListener)listeners[index+1]).tuplesRemoved(event!=null?event:(event=new NucleicEvent(NucleicEditor.this,getTuples())));
  }
  protected void fireTuplesChanged() {
  	NucleicEvent event = null;
  	Object[] listeners = listenerList.getListenerList();
  	for(int index = listeners.length-2;index>=0;index-=2)
  		if(listeners[index]==NucleicListener.class)
  			((NucleicListener)listeners[index+1]).tuplesChanged(event!=null?event:(event=new NucleicEvent(NucleicEditor.this,getTuples())));
  }

	class NumberPanel extends JPanel implements NucleicListener {
		private static final long serialVersionUID = 1l;
		
		private int border,digits;

		public NumberPanel() { this(0); }
		public NumberPanel(int digits) {
			setFont(textPane.getFont());
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
			setPreferredSize();
		}

		public int getDigits() { return digits; }
		public void adaptDigits() { setDigits(Math.max(String.valueOf(tuples.size()).length(), 3)); }
		public void setDigits(int digits) {
			digits = Math.max(3,digits);
			if(this.digits!=digits) {
				this.digits = digits;
				setPreferredSize();
			}
		}

		public void setPreferredSize() {
			Insets insets = getInsets();
			Dimension dimension = getPreferredSize();
			dimension.setSize(insets.left+insets.right+(getFontMetrics(getFont()).charWidth('0')*(digits*2+1)), Integer.MAX_VALUE-Short.MAX_VALUE);
			setPreferredSize(dimension);
			setSize(dimension);
			if(optionPanel!=null) //border is painted to the edge of NumberPanel, so repaint the OptionPanel
				optionPanel.repaint();
		}

		@Override public void paintComponent(Graphics graphics) {
			super.paintComponent(graphics);
			
			Insets insets = getInsets();
			FontMetrics metrics = textPane.getFontMetrics(textPane.getFont());
			int width = getSize().width-insets.left-insets.right;
			
			Rectangle clip = graphics.getClipBounds();
			int startOffset = textPane.viewToModel(new Point(0,clip.y)), endOffset = textPane.viewToModel(new Point(0,clip.y+clip.height)),
			  tuplesBehind = tuplesToRow(startOffset);
			
			try {
				while(startOffset<=endOffset) {
					graphics.setColor(getForeground());
					int tuplesInRow = tuplesInRow(startOffset);
					String tupleNumber = tupleNumber(tuplesBehind, tuplesInRow);
					graphics.drawString(tupleNumber,width-metrics.stringWidth(tupleNumber)+insets.left, getOffsetY(startOffset,metrics));
					startOffset = javax.swing.text.Utilities.getRowEnd(textPane, startOffset)+1;
					tuplesBehind += tuplesInRow;
				}
			} catch(BadLocationException e) { /** nothing to do here */  }
		}
		
		protected int tuplesToRow(int offset) {
			try {
				//text version: return countOccurences(textPane.getText(0, javax.swing.text.Utilities.getRowStart(textPane, offset)), ' ')+1;
				return tuples.subMap(fixPosition(0), fixPosition(javax.swing.text.Utilities.getRowStart(textPane, offset))).size();
			} catch(BadLocationException e) { return -1; }
		}
		protected int tuplesInRow(int offset) {
			try {
				int rowStartOffset = javax.swing.text.Utilities.getRowStart(textPane, offset), rowEndOffset = javax.swing.text.Utilities.getRowEnd(textPane, rowStartOffset);
				//text version: return countOccurences(textPane.getText(rowStartOffset, rowEndOffset-rowStartOffset), ' ')+1;
				return tuples.subMap(fixPosition(rowStartOffset), fixPosition(rowEndOffset+1)).size();
			}	catch(BadLocationException e) { return -1; }
		}
		protected String tupleNumber(int tuplesBehind, int tuplesInRow) {
			final String unknownNumber = new String(new char[digits]).replace("\0","?"),
			  startNumber = tuplesBehind!=-1?String.format("%0"+digits+"d", tuplesInRow!=0?tuplesBehind+1:tuplesBehind):unknownNumber,
			  endNumber = tuplesBehind!=-1&&tuplesInRow!=-1?String.format("%0"+digits+"d", tuplesBehind+tuplesInRow):unknownNumber;
			return startNumber+"-"+endNumber;
		}
		private int getOffsetY(int rowOffset,FontMetrics metrics) throws BadLocationException {
			Rectangle rectangle = textPane.modelToView(rowOffset);
			return (rectangle.y+rectangle.height)-metrics.getDescent();
		}

		@Override public void tuplesRemoved(NucleicEvent event) { tuplesInsert(event); }
		@Override public void tuplesInsert(NucleicEvent event) {
			invokeAppropriate(new Runnable() {
				public void run() { adaptDigits(); repaint(); }
			});
		}
		@Override public void tuplesChanged(NucleicEvent event) { /* undoable change, nothing to do here */ }
	}
	
	class UndoableEditListener implements javax.swing.event.UndoableEditListener  {
		private CompoundEdit edit;
		
		@Override public void undoableEditHappened(UndoableEditEvent event) {
			UndoableEdit edit = event.getEdit();
			if(!inTransaction()) {
				undo.addEdit(edit);
				fireTuplesChanged();
			} else this.edit.addEdit(edit);
		}
		
		public boolean inTransaction() { return edit!=null; }
		public void startTransaction() {
			if(!inTransaction())
				edit = new CompoundEdit();
		}
		public void commitTransaction() {
			if(inTransaction()) {
				edit.end(); undo.addEdit(edit); edit = null;
				fireTuplesChanged();
			}
		}
	}
	
	class NoDisplay extends Component implements NucleicDisplay {
		private static final long serialVersionUID = 1l;

		@Override public void paint(Graphics graphics) {
			super.paint(graphics);
			graphics.setColor(Color.RED);
			graphics.fillRect(0,0,getWidth(),getHeight());
		}
		
		@Override public String getLabel() { return null; }
		@Override public Icon getIcon() { return getImage("application_side_contract"); }
		
		@Override public boolean hasPreferredSize() { return true; }
		@Override public void setPreferredSize() { setPreferredSize(new Dimension(0,0)); }
	}
	
	class HelpDisplay extends JScrollPane implements NucleicDisplay {
		private static final long serialVersionUID = 1;
		
		private JEditorPane editor;
		
		private String index;
		private Map<String[],String> pages;
		
		public HelpDisplay() {
			setBorder(DEFAULT_DISPLAY_BORDER);
			setViewportView(editor=new JEditorPane());
			editor.setBorder(new MatteBorder(0,20,0,0,Color.WHITE));
			editor.setEditorKit(new HTMLEditorKit());
			editor.setEditable(false);
			editor.addHyperlinkListener(new HyperlinkListener() {
				public void hyperlinkUpdate(HyperlinkEvent event) {
					URL url = event.getURL();
					if(event.getEventType()!=HyperlinkEvent.EventType.ACTIVATED||url==null)
						return;
					String path = url.getPath();
					switch(url.getProtocol()) {
					case "help": showPage((path.startsWith("/")?path.substring(1):path).split("/")); break;
					default: if(Desktop.isDesktopSupported())
						try { Desktop.getDesktop().browse(url.toURI()); } 
						catch(IOException|URISyntaxException e) { /* nothing to do here */ }
					}
				}
			});
			
			pages = new TreeMap<>(new ArrayComparator<>());
			for(Class<?> documentedClass:new Reflections(Documented.class.getPackage().getName()).getTypesAnnotatedWith(Documented.class)) {
				Documented documented = documentedClass.getAnnotation(Documented.class);
				pages.put(Utilities.add(documented.category(), documented.title()), documented.resource());
			} pages.putAll(Help.GENERAL_HELP_PAGES);
			
			index = buildIndex(); showIndex();
		}
		
		@Override public String getLabel() { return "Help"; }
		@Override public Icon getIcon() { return getImage("help"); }
		
		@Override public boolean hasPreferredSize() { return false; }
		@Override public void setPreferredSize() { /* nothing to do here */ }
		
		public void showIndex() { editor.setText(index); editor.setCaretPosition(0); }
		public void showPage(String... page) {
			if(page!=null&&page.length!=0) {
				Entry<String[],String> entry = pages.entrySet().stream().filter(candidate->Arrays.equals(candidate.getKey(),page)).findFirst().orElse(null);
				if(entry!=null) {
					editor.setText(buildPage(page,entry.getValue()));
					editor.setCaretPosition(0);
				} else showIndex();
			} else showIndex();
		}
		
		protected String buildIndex() {			
			String[] lastPage = new String[0];
			StringBuilder builder = new StringBuilder("<html><h1>GenTool Help</h1><h2>Contents</h2><ul>");
			for(String[] page:pages.keySet()) {
				int sameDepth;
				for(sameDepth=0;sameDepth<page.length&&sameDepth<lastPage.length;sameDepth++)
					if(!page[sameDepth].equals(lastPage[sameDepth]))
						break;
				for(int goBack=lastPage.length-sameDepth;goBack>1;goBack--)
					builder.append("</ul></li>");
				for(int newCategory=sameDepth;newCategory<page.length-1;newCategory++)
					builder.append(String.format("<li>%s<ul>",page[newCategory]));
				builder.append(String.format("<li><a href=\"help:///%s\">%s</a></li>",String.join("/",page),page[page.length-1]));
				lastPage = page;
			}
			for(int doClose=lastPage.length;doClose>1;doClose--)
				builder.append("</ul></li>");
			builder.append("</ul>");
			return builder.toString();
		}
		protected String buildPage(String[] page, String resource) {
			StringBuilder builder = new StringBuilder("<html><p><a href=\"help://\">GenTool Help</a>"), link = new StringBuilder();
			for(String name:page) builder.append(String.format(" &gt; <a href=\"help://%s\">%s</a>",
				link.append('/').append(name).toString(), name));			
			builder.append(String.format("<h1>%s</h1>",page[page.length-1]));
			try {
				builder.append(new String(readStream(Utilities.getResource(resource).openStream()), "UTF-8")
					.replaceAll("(src|href)=\"((?!.*?://|/).*?)\"", String.format("$1=\"resource:///%s/$2\"", resource.contains("/")?resource.substring(0,resource.lastIndexOf('/')):resource)));
			} catch(NullPointerException|IOException e) { System.err.println("Can't show help resource "+resource); e.printStackTrace(); }
			return builder.toString();
		}
	}
}