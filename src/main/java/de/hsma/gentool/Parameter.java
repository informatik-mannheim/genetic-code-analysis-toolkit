package de.hsma.gentool;

import static de.hsma.gentool.Utilities.*;
import static de.hsma.gentool.gui.helper.Guitilities.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.io.File;
import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeListener;
import javax.swing.text.NumberFormatter;
import scala.actors.threadpool.Arrays;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;

public class Parameter {
	public enum Type {
		TEXT(String.class),
		BOOLEAN(Boolean.class),
		DECIMAL(Double.class),
		NUMBER(Number.class),
		LIST(Object.class),
		FILE(File.class);
		private Class<?> typeClass;
		private Type(Class<?> typeClass) { this.typeClass = typeClass; }
		public boolean checkType(Object object) {
			return this==LIST||typeClass.isInstance(object);
		}
		public Object initialValue() {
			try { return typeClass.newInstance(); } 
			catch(InstantiationException | IllegalAccessException e) {
				return null; /* e.g. for file */ }
		}
		public static Type forClass(Class<?> typeClass) {
			for(Type type:Type.values())
				if(type.typeClass.equals(typeClass))
					return type;
			throw new IllegalArgumentException();
		}
	}
	
	public final String key,label;
	public final Type type;
	public final Object value;
	
	public final Number minimum, maximum, step;
	
	public final Object[] options;
	public final String[] labels;
	
	public Parameter(String key, String label, Type type) {
		this(key, label, type, type.initialValue());
	}
	public Parameter(String key, String label, Type type, Object value) {
		this.key = key; this.label = label; this.type = type; this.options = this.labels = null;
		this.value = type.checkType(value)?value:type.initialValue();
		this.minimum = Short.MIN_VALUE; this.maximum = Short.MAX_VALUE; this.step = 1;
	}
	
	public Parameter(String key, String label, int minimum, int maximum) { this(key,label,minimum,minimum,maximum); }
	public Parameter(String key, String label, int value, int minimum, int maximum) { this(key,label,value,minimum,maximum,1); }
	public Parameter(String key, String label, int value, int minimum, int maximum, int step) {
		this.key = key; this.label = label; this.type = Type.NUMBER; this.options = this.labels = null;
		this.value = value; this.minimum = minimum; this.maximum = maximum; this.step = step;
	}
	public Parameter(String key, String label, double minimum, double maximum) { this(key,label,minimum,minimum,maximum); }
	public Parameter(String key, String label, double value, double minimum, double maximum) { this(key,label,value,minimum,maximum,.1); }
	public Parameter(String key, String label, double value, double minimum, double maximum, double step) {
		this.key = key; this.label = label; this.type = Type.DECIMAL; this.options = this.labels = null;
		this.value = value; this.minimum = minimum; this.maximum = maximum; this.step = step;		
	}
	public Parameter(String key, String label, boolean value) {
		this.key = key; this.label = label; this.type = Type.BOOLEAN; this.options = this.labels = null;
		this.value = value; this.minimum = this.maximum = this.step = null;
	}
	public Parameter(String key, String label, Object[] options) { this(key,label,options!=null&&options.length>0?options[0]:null,options); }
	@SuppressWarnings("unchecked") public Parameter(String key, String label, Object value, Object[] options) {
		this(key,label,value,options,(String[])Iterables.toArray(Iterables.transform(Arrays.asList(options),new Function<Object,String>() {
			@Override public String apply(Object option) { return option!=null?option.toString():null; }
		}),String.class)); 
	}
	public Parameter(String key, String label, Object[] options, String... labels) { this(key,label,options!=null&&options.length>0?options[0]:null,options,labels); }
	public Parameter(String key, String label, Object value, Object[] options, String... labels) {
		this.key = key; this.label = label; this.type = Type.LIST; this.options = options; this.labels = labels;
		this.value = value; this.minimum = this.maximum = this.step = null;
	}
	
	public class Component extends JComponent {
		private static final long serialVersionUID = 1l;
		private java.awt.Component component;
		
		public Component() { this(value); }
		public Component(Object uncheckedValue) {
			super(); setLayout(new BorderLayout());
			final Object value = type.checkType(uncheckedValue)?uncheckedValue:type.initialValue();
			switch(type) {
			case TEXT:
				((JTextField)(component = new JTextField())).setText((String)value);
				break;
			case NUMBER: case DECIMAL:			
				((JSpinner)(component = new JSpinner(Type.NUMBER.equals(type)?new SpinnerNumberModel(((Number)value).intValue(),minimum.intValue(),maximum.intValue(),step.intValue()):new SpinnerNumberModel(((Double)value).doubleValue(),minimum.doubleValue(),maximum.doubleValue(),step.doubleValue())))).setValue(value);
				((NumberFormatter)((JSpinner.NumberEditor)((JSpinner)component).getEditor()).getTextField().getFormatter()).setAllowsInvalid(false);
				break;
			case BOOLEAN:
				((JCheckBox)(component = new JCheckBox(label))).setSelected(value!=null?(Boolean)value:false);  						
				break;
			case LIST:
				((JComboBox<?>)(component = new JComboBox<Object>(options) {
					private static final long serialVersionUID = 1l;
					@Override public int getSelectedIndex() {
						Object selected = dataModel.getSelectedItem();	
						for(int index=0;index<dataModel.getSize();index++) {
							Object object = dataModel.getElementAt(index);
							if(object==selected||(object!=null&&object.equals(selected)))
								return index;
						}
						return -1;
			    }
				})).setRenderer(new DefaultListCellRenderer() {
					private static final long serialVersionUID = 1l;
					@Override public java.awt.Component getListCellRendererComponent(JList<?> list,Object value,int index,boolean isSelected,boolean cellHasFocus) {
						super.getListCellRendererComponent(list,value,index,isSelected,cellHasFocus);
						if(index==-1) index = ((JComboBox<?>)component).getSelectedIndex();
						if(index==-1) index = list.getSelectedIndex();
						setText(!labels[index].isEmpty()?labels[index]:SPACE);
						return this;
					}
				});
				((JComboBox<?>)component).setSelectedItem(value);
				break;
			case FILE:
				((FileComponent)(component = new FileComponent())).setFile((File)value);
				break;
			} add(component, BorderLayout.CENTER);
		}

		public java.awt.Component getComponent() { return component; }
		
		public void setValue(Object value) {
			switch(type) {
			case TEXT: ((JTextField)component).setText((String)value);
			case NUMBER: case DECIMAL: ((JSpinner)component).setValue(value);
			case BOOLEAN: ((JCheckBox)component).setSelected((Boolean)value);					
			case LIST: ((JComboBox<?>)component).setSelectedItem(value);
			case FILE: ((FileComponent)component).setFile((File)value); }
		}
		public Object getValue() {
			switch(type) {
			case TEXT: return ((JTextField)component).getText();
			case NUMBER: case DECIMAL: return ((JSpinner)component).getValue();
			case BOOLEAN: return ((JCheckBox)component).isSelected();					
			case LIST: return ((JComboBox<?>)component).getSelectedItem();
			case FILE: return ((FileComponent)component).getFile(); }
			return null;
		}
		
		@SuppressWarnings("incomplete-switch") public void addChangeListener(ChangeListener listener) {
			switch(type) {
			case NUMBER: case DECIMAL: ((JSpinner)component).addChangeListener(listener); break;
			case BOOLEAN: ((JCheckBox)component).addChangeListener(listener); break; }
		}
		@SuppressWarnings("incomplete-switch") public void addItemListener(ItemListener listener) {
			switch(type) {
			case BOOLEAN: ((JCheckBox)component).addItemListener(listener); break;	
			case LIST: ((JComboBox<?>)component).addItemListener(listener); break; }
		}
		@SuppressWarnings("incomplete-switch") public void addActionListener(ActionListener listener) {
			switch(type) {
			case TEXT: ((JTextField)component).addActionListener(listener); break;
			case BOOLEAN: ((JCheckBox)component).addActionListener(listener); break;	
			case LIST: ((JComboBox<?>)component).addActionListener(listener); break;
			case FILE: ((FileComponent)component).addActionListener(listener); break; }
		}
	}
	
	public static Class<?>[] getTypes(Parameter[] parameters) {
		Class<?>[] types = new Class[parameters.length];
		for(int parameter=0;parameter<parameters.length;parameter++)
			types[parameter] = parameters[parameter].type.typeClass;
		return types;
	}
	public static Object[] getValues(Parameter[] parameters) {
		if(parameters==null) return new Object[0];
		Object[] values = new Object[parameters.length];
		for(int parameter=0;parameter<parameters.length;parameter++)
			values[parameter] = parameters[parameter].value;
		return values;
	}
	
	@Override public boolean equals(Object object) {
		if(!(object instanceof Option))
			return false;
		return key.equals(((Option)object).key);
	}
	@Override public int hashCode() {
		return key.hashCode();
	}
	
	private static class FileComponent extends JPanel {
		private static final long serialVersionUID = 1l;
		
		private JTextField display;
		private JFileChooser chooser;
		
		public FileComponent() {
			setBoxLayout(this,BoxLayout.X_AXIS);
			
			chooser = new JFileChooser();
			chooser.setDialogTitle("Open File");
			chooser.setCurrentDirectory(new File(System.getProperty("user.home")));
			
			add(display = new JTextField());
			display.setEditable(false);
			display.setBackground(Color.WHITE);
			
			add(new JButton(new AbstractAction("Choose File") {
				private static final long serialVersionUID = 1l;
				@Override public void actionPerformed(ActionEvent e) {
					if(chooser.showOpenDialog(FileComponent.this)==JFileChooser.APPROVE_OPTION)
						setFile(chooser.getSelectedFile());
				}
			}));
		}

		public void addActionListener(ActionListener listener) { chooser.addActionListener(listener); }
		@SuppressWarnings("unused") public void removeActionListener(ActionListener listener) { chooser.removeActionListener(listener); }
		@SuppressWarnings("unused") public ActionListener[] getActionListeners() { return chooser.getActionListeners(); }
  
		public File getFile() { return chooser.getSelectedFile(); }
		public void setFile(File file) {
			display.setText(file!=null?file.getName():null);
			chooser.setSelectedFile(file);
		}
	}
}