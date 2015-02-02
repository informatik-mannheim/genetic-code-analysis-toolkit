package de.hsma.gentool.gui.editor;

import static de.hsma.gentool.Utilities.*;
import static de.hsma.gentool.gui.helper.Guitilities.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.NavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.regex.Pattern;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Position;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;
import de.hsma.gentool.gui.editor.NucleicListener.NucleicEvent;
import de.hsma.gentool.gui.helper.AttachedScrollPane;
import de.hsma.gentool.nucleic.Compound;
import de.hsma.gentool.nucleic.Tuple;

public class NucleicEditor extends JRootPane {
	protected static final Pattern LINE_PATTERN = Pattern.compile("^(([A-Z_]+): *)?([A-Z]{3})( +([#%+\\-]?)(\\d+)| +([A-Z_]+))?( *//.*)?$"); //^(([A-Z_]+): *)?([A-Z]{3})( +([#%]?)(\d+)| +([A-Z_]+))?( *//.*)?$
	
	private static final long serialVersionUID = 1l;
	
	protected NucleicDocument document;
	protected UndoManager undo = new UndoManager();
	protected UndoableEditListener edit;
	
	private JTextPane textPane;
	private NumberDisplay numbers;
	private CompoundDisplay compounds;
	
	protected NavigableMap<Position, Tuple> tuples;
	
	private int cleanHash = EMPTY.hashCode();
	
	public NucleicEditor() {
		this.setLayout(new BorderLayout());
		this.getGlassPane().setVisible(true);
		
		textPane = new JTextPane() {
			private static final long serialVersionUID = 1l;
			private final Color lineColor = new Color(0,0,0,64);
			@Override public void paint(Graphics graphics) {
				super.paint(graphics);
				int defaultTupleLength = document.getDefaultTupleLength();
				if(defaultTupleLength>0) {
					graphics.setColor(lineColor);
					int width=getFontMetrics(getFont()).charWidth('0'), tupleWidth = width*defaultTupleLength;
					for(int left=getInsets().left+width/2+tupleWidth; left<textPane.getWidth(); left += tupleWidth + width)
						graphics.drawLine(left, 0, left, textPane.getHeight());
				}
			}
		};
		textPane.setFont(new Font(Font.MONOSPACED,Font.PLAIN,14));
		textPane.setForeground(Color.BLACK);
		textPane.addKeyListener(new KeyAdapter() {
			@Override public void keyTyped(KeyEvent event) {
				char key = event.getKeyChar();
				if(key>='a'||key<='z')
					event.setKeyChar(Character.toUpperCase(key));
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
		
		AttachedScrollPane scroll = new AttachedScrollPane(textPane, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setRowHeaderView(numbers = new NumberDisplay());
		scroll.setRowFooterView(compounds = new CompoundDisplay());
		for(NucleicListener listener:new NucleicListener[]{numbers,compounds})
			addNucleicListener(listener);
		this.addComponentListener(new ComponentAdapter() {			
			@Override public void componentResized(ComponentEvent e) {
				compounds.adaptWidth();
			}
		});
		
		this.add(scroll, BorderLayout.CENTER);
	}

	public JTextPane getTextPane() { return textPane; }
	public NucleicDocument getDocument() { return document; }
	
	public String getText() { return textPane.getText(); }
	public String getText(int offset) throws BadLocationException { return textPane.getText(offset, document.getLength()-offset); }
	public String getText(int offset, int length) throws BadLocationException { return textPane.getText(offset, length); }
	public void setText(String text) { edit.startTransaction(); textPane.setText(text); edit.commitTransaction(); }
	
	public Collection<Tuple> getTuples() { return Collections.unmodifiableCollection(tuples.values()); }
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

	class NumberDisplay extends JPanel implements NucleicListener {
		private static final long serialVersionUID = 1l;
		
		private int border,digits;

		public NumberDisplay() { this(0); }
		public NumberDisplay(int digits) {
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
			adaptWidth();
		}

		public int getDigits() { return digits; }
		public void adaptDigits() { setDigits(Math.max(String.valueOf(tuples.size()).length(), 3)); }
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
		protected String tupleNumber(int position, int number) {
			final String unknownNumber = new String(new char[digits]).replace("\0","?"),
			  startNumber = position!=-1?String.format("%0"+digits+"d", position+1):unknownNumber,
			  endNumber = position!=-1&&number!=-1?String.format("%0"+digits+"d", position+number):unknownNumber;
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
	
	class CompoundDisplay extends JPanel implements NucleicListener {
		private static final long serialVersionUID = 1l;

		private final Color COLOR_NONPOLAR = new Color(255,231,95),
                           COLOR_POLAR = new Color(179,222,192),
                           COLOR_BASIC = new Color(187,191,224),
                          COLOR_ACIDIC = new Color(248,183,211),
                         COLOR_SPECIAL = new Color(176,176,176);
		private final int defaultCharWidth;
		
		private int border;
		
		public CompoundDisplay() {
			setBackground(Color.WHITE);
			setFont(textPane.getFont());
			FontMetrics metrics = getFontMetrics(getFont());
			defaultCharWidth = metrics.charWidth('0');
			setBorderSpacing(5);
			adaptWidth();
		}
		
		public int getBorderSpacing() { return border; }
		public void setBorderSpacing(int borderSpading) {
			this.border = borderSpading;
			Border inner = new EmptyBorder(0,borderSpading,0,borderSpading);
			setBorder(new CompoundBorder(new MatteBorder(0,1,0,0,Color.LIGHT_GRAY),inner));
			adaptWidth();
		}
		public void adaptWidth() {
			Insets insets = getInsets();
			Dimension dimension = getPreferredSize();
			dimension.setSize(insets.left+insets.right+(NucleicEditor.this.getWidth()-NucleicEditor.this.numbers.getWidth())/2, Integer.MAX_VALUE-Short.MAX_VALUE);
			setPreferredSize(dimension);
			setSize(dimension);
		}

		@Override public void paintComponent(Graphics graphics) {
			super.paintComponent(graphics);
			
			Rectangle clip = graphics.getClipBounds();
			FontMetrics metrics = textPane.getFontMetrics(textPane.getFont());
			int startOffset = textPane.viewToModel(new Point(0,clip.y)), endOffset = textPane.viewToModel(new Point(0,clip.y+clip.height));
			
			try {
				while(startOffset<=endOffset) {
					graphics.setColor(getForeground());
					drawCompounds(graphics, tuplesInRow(startOffset), getOffsetY(startOffset, metrics));
					startOffset = javax.swing.text.Utilities.getRowEnd(textPane, startOffset)+1;
				}
			} catch(BadLocationException e) { /** nothing to do here */  }
		}
		
		protected void drawCompounds(Graphics graphics, Tuple[] tuples, int y) {
			int x = getInsets().left;
			for(Tuple tuple:tuples) {
				String text; Color color = COLOR_SPECIAL;
				if(tuple!=null) {
					Compound compound = tuple.getCompound();
					if(compound!=null) {
						text = compound.abbreviation;
						switch(compound.property) {
						case NONPOLAR: color = COLOR_NONPOLAR; break;
						case POLAR: color = COLOR_POLAR; break;
						case BASIC: color = COLOR_BASIC; break;
						case ACIDIC: color = COLOR_ACIDIC; break; }
					} else if(Compound.isStart(tuple)) text = "STA";
						else if(Compound.isStop(tuple)) text = "STP";
						else text = "UNK";
				} else text = "ERR";
				
				// adapt text length to tuple length
				int defaultTupleLength = document.getDefaultTupleLength();
				if(defaultTupleLength>0&&defaultTupleLength<text.length())
					text = text.substring(0,defaultTupleLength);
				
				drawString(graphics, text, x, y, color);
				x += (text.length()+1)*defaultCharWidth;
			}
		}
		protected void drawString(Graphics graphics, String string, int x, int y, Color color) {
			FontMetrics metrics = graphics.getFontMetrics();
      Rectangle2D rectangle = metrics.getStringBounds(string, graphics);
      graphics.setColor(color);
      graphics.fillRect(x, y - metrics.getAscent(), (int)rectangle.getWidth(), (int)rectangle.getHeight());
      graphics.setColor(getForeground());
			graphics.drawString(string, x, y);
		}
	
		protected Tuple[] tuplesInRow(int offset) {
			try {
				int rowStartOffset = javax.swing.text.Utilities.getRowStart(textPane, offset), rowEndOffset = javax.swing.text.Utilities.getRowEnd(textPane, rowStartOffset);
				//text version: return Tuple.toArray(textPane.getText(rowStartOffset, rowEndOffset-rowStartOffset));
				return tuples.subMap(fixPosition(rowStartOffset), fixPosition(rowEndOffset+1)).values().toArray(new Tuple[0]);
			}	catch(BadLocationException e) { return null; }
		}
		private int getOffsetY(int rowOffset,FontMetrics metrics) throws BadLocationException {
			Rectangle rectangle = textPane.modelToView(rowOffset);
			return (rectangle.y+rectangle.height)-metrics.getDescent();
		}

		@Override public void tuplesRemoved(NucleicEvent event) { tuplesInsert(event); }
		@Override public void tuplesInsert(NucleicEvent event) {
			invokeAppropriate(new Runnable() {
				public void run() { repaint(); }
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
}