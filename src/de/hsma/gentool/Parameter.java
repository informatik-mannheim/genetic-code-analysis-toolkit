package de.hsma.gentool;

import static de.hsma.gentool.Utilities.*;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeListener;
import javax.swing.text.NumberFormatter;

public class Parameter {
	public enum Type {
		TEXT(String.class),
		BOOLEAN(Boolean.class),
		DECIMAL(Double.class),
		NUMBER(Number.class),
		LIST(Object.class);
		private Class<?> typeClass;
		private Type(Class<?> typeClass) { this.typeClass = typeClass; }
		public boolean checkType(Object object) {
			return this==LIST||typeClass.isInstance(object);
		}
		public Object initialValue() {
			try { return typeClass.newInstance(); } 
			catch(InstantiationException | IllegalAccessException e) { return null; }
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
	public Parameter(String key, String label, int value, int minimum, int maximum, int step) {
		this.key = key; this.label = label; this.type = Type.NUMBER; this.options = this.labels = null;
		this.value = value; this.minimum = minimum; this.maximum = maximum; this.step = step;
	}
	public Parameter(String key, String label, double value, double minimum, double maximum, double step) {
		this.key = key; this.label = label; this.type = Type.DECIMAL; this.options = this.labels = null;
		this.value = value; this.minimum = minimum; this.maximum = maximum; this.step = step;		
	}
	public Parameter(String key, String label, boolean value) {
		this.key = key; this.label = label; this.type = Type.BOOLEAN; this.options = this.labels = null;
		this.value = value; this.minimum = this.maximum = this.step = null;
	}
	public Parameter(String key, String label, Object value, Object[] options, String... labels) {
		this.key = key; this.label = label; this.type = Type.LIST; this.options = options; this.labels = labels;
		this.value = value; this.minimum = this.maximum = this.step = null;
	}
	
	public class Component extends JComponent {
		private static final long serialVersionUID = 1l;
		private java.awt.Component component;
		public Component() { this(value); }
		public Component(Object value) {
			super(); setLayout(new BorderLayout());
			value = type.checkType(value)?value:type.initialValue();
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
			} add(component, BorderLayout.CENTER);
		}

		public Object getValue() {
			switch(type) {
			case TEXT: return ((JTextField)component).getText();
			case NUMBER: case DECIMAL: return ((JSpinner)component).getValue();
			case BOOLEAN: return ((JCheckBox)component).isSelected();					
			case LIST: return ((JComboBox<?>)component).getSelectedItem();
			default: return null; }
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
			case LIST: ((JComboBox<?>)component).addActionListener(listener); break; }
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
}