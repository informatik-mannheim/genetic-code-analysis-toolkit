/*
 * Copyright [2016] [Mannheim University of Applied Sciences]
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
package bio.gcat;

import static bio.gcat.Utilities.EMPTY;
import static bio.gcat.Utilities.SPACE;
import static bio.gcat.gui.helper.Guitilities.setBoxLayout;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.beans.VetoableChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import javax.swing.filechooser.FileFilter;
import javax.swing.text.NumberFormatter;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.ObjectArrays;

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
	
	@Repeatable(Annotations.class)
	@Target({ElementType.TYPE}) @Retention(RetentionPolicy.RUNTIME)
	public @interface Annotation {
		String key(); String label(); Type type();
		String value() default EMPTY;
	}
	
	@Target({ElementType.TYPE}) @Retention(RetentionPolicy.RUNTIME)
	public @interface Annotations { Annotation[] value(); }
	
	public final String key,label;
	public final Type type;
	public final Object value;
	
	public final Number minimum, maximum, step;
	
	public final Object[] options;
	public final String[] labels;
	
	public final FileFilter filter;
	
	public Parameter(String key, String label, Type type) {
		this(key, label, type, type.initialValue());
	}
	public Parameter(String key, String label, Type type, Object value) {
		this.key = key; this.label = label; this.type = type; this.options = this.labels = null; this.filter = null;
		this.value = type.checkType(value)?value:type.initialValue();
		this.minimum = Short.MIN_VALUE; this.maximum = Short.MAX_VALUE; this.step = 1;
	}
	
	public Parameter(String key, String label, String value) { this(key,label,Type.TEXT,value); }
	
	public Parameter(String key, String label, int value) { this(key,label,Type.NUMBER,value); }
	public Parameter(String key, String label, int minimum, int maximum) { this(key,label,minimum,minimum,maximum); }
	public Parameter(String key, String label, int minimum, int value, int maximum) { this(key,label,minimum,value,maximum,1); }
	public Parameter(String key, String label, int minimum, int value, int maximum, int step) {
		this.key = key; this.label = label; this.type = Type.NUMBER; this.options = this.labels = null; this.filter = null;
		this.value = value; this.minimum = minimum; this.maximum = maximum; this.step = step;
	}
	
	public Parameter(String key, String label, double value) { this(key,label,Type.DECIMAL,value); }
	public Parameter(String key, String label, double minimum, double maximum) { this(key,label,minimum,minimum,maximum); }
	public Parameter(String key, String label, double minimum, double value, double maximum) { this(key,label,minimum,value,maximum,.1); }
	public Parameter(String key, String label, double minimum, double value, double maximum, double step) {
		this.key = key; this.label = label; this.type = Type.DECIMAL; this.options = this.labels = null; this.filter = null;
		this.value = value; this.minimum = minimum; this.maximum = maximum; this.step = step;		
	}
	
	public Parameter(String key, String label, boolean value) { this(key,label,Type.BOOLEAN,value); }

	public Parameter(String key, String label, Object[] options) { this(key,label,options!=null&&options.length>0?options[0]:null,options); }
	public Parameter(String key, String label, Object value, Object[] options) {
		this(key,label,value,options,(String[])Iterables.toArray(Iterables.transform(Arrays.asList(options),new Function<Object,String>() {
			@Override public String apply(Object option) { return option!=null?option.toString():null; }
		}),String.class)); 
	}
	public Parameter(String key, String label, Object[] options, String... labels) { this(key,label,options!=null&&options.length>0?options[0]:null,options,labels); }
	public Parameter(String key, String label, Object value, Object[] options, String... labels) {
		this.key = key; this.label = label; this.type = Type.LIST; this.options = options; this.labels = labels; this.filter = null;
		this.value = value; this.minimum = this.maximum = this.step = null;
	}
	
	public Parameter(String key, String label, File value) { this(key,label,Type.FILE,value); }
	public Parameter(String key, String label, FileFilter filter) {
		this.key = key; this.label = label; this.type = Type.FILE; this.options = this.labels = null; this.filter = filter;
		this.value = null; this.minimum = this.maximum = this.step = null;
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
				component = new FileComponent(filter);
				break;
			} add(component, BorderLayout.CENTER);
			if(component instanceof JComponent)
				((JComponent)component).setToolTipText(label);
		}

		public java.awt.Component getComponent() { return component; }
		
		public void setValue(Object value) {
			switch(type) {
			case TEXT: ((JTextField)component).setText((String)value); break;
			case NUMBER: case DECIMAL: ((JSpinner)component).setValue(value); break;
			case BOOLEAN: ((JCheckBox)component).setSelected((Boolean)value); break;	
			case LIST: ((JComboBox<?>)component).setSelectedItem(value); break;
			case FILE: ((FileComponent)component).setFile((File)value); break; }
		}
		public Object getValue() {
			switch(type) {
			case TEXT: return ((JTextField)component).getText();
			case NUMBER: case DECIMAL: return ((JSpinner)component).getValue();
			case BOOLEAN: return ((JCheckBox)component).isSelected();					
			case LIST: return ((JComboBox<?>)component).getSelectedItem();
			case FILE: try { return ((FileComponent)component).openFile(); } catch (IOException e) { return null; } }
			return null;
		}
		
		public void addVetoableChangeListener(VetoableChangeListener listener) {
			switch(type) {
			case TEXT: ((JTextField)component).addVetoableChangeListener(listener); break;
			case NUMBER: case DECIMAL: ((JSpinner)component).addVetoableChangeListener(listener); break;
			case BOOLEAN: ((JCheckBox)component).addVetoableChangeListener(listener); break;	
			case LIST: ((JComboBox<?>)component).addVetoableChangeListener(listener); break;
			case FILE: ((FileComponent)component).addVetoableChangeListener(listener); break; }
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
	
	public static String GET_PARAMETERS = "getParameters";
	public static Parameter[] getParameters(Class<?> parameterized) {
		if(parameterized.isAnnotationPresent(Annotation.class))
			return getParameters(parameterized.getAnnotationsByType(Annotation.class));
		else if(parameterized.isAnnotationPresent(Annotations.class))
			return getParameters(parameterized.getAnnotation(Annotations.class).value());
		else try {
			Object parameters = parameterized.getMethod(GET_PARAMETERS).invoke(null);
			return parameters instanceof Parameter[]?(Parameter[])parameters:null;
		} catch(NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | SecurityException e) { return null; }
	}
	private static Parameter[] getParameters(Annotation[] annotations) {
		Parameter[] parameters = new Parameter[annotations.length];
		for(int annotation=0;annotation<annotations.length;annotation++)
			parameters[annotation] = getParameter(annotations[annotation]);
		return parameters;
	}
	private static Parameter getParameter(Annotation annotation) {
		String value, values[]; Matcher matcher;
		if((value=annotation.value()).isEmpty())
			return new Parameter(annotation.key(),annotation.label(),annotation.type());
		else switch(annotation.type()) {
			case TEXT: return new Parameter(annotation.key(),annotation.label(),annotation.value());
			case NUMBER:
				try {
					Class<?>[] types = new Class<?>[2+(values=value.split(",")).length];
					Arrays.fill(types,int.class); types[0] = types[1] = String.class;
					return Parameter.class.getConstructor(types).newInstance(
						ObjectArrays.concat(new Object[]{annotation.key(),annotation.label()},
							Arrays.stream(values).map(input->Integer.parseInt(input)).toArray(), Object.class));
				}	catch(Exception e) { throw new RuntimeException(e); }
			case DECIMAL:
				try {
					Class<?>[] types = new Class<?>[2+(values=value.split(",")).length];
					Arrays.fill(types,double.class); types[0] = types[1] = String.class;
					return Parameter.class.getConstructor(types).newInstance(
						ObjectArrays.concat(new Object[]{annotation.key(),annotation.label()},
							Arrays.stream(values).map(input->Double.parseDouble(input)).toArray(), Object.class));
				}	catch(Exception e) { throw new RuntimeException(e); }
			case BOOLEAN:
				return new Parameter(annotation.key(),annotation.label(),Boolean.parseBoolean(value));
			case LIST:
				return new Parameter(annotation.key(),annotation.label(),value.split(","));
			case FILE:
				if((matcher=Pattern.compile(".* \\(\\*\\.(\\w+)\\)").matcher(value)).matches())
					return new Parameter(annotation.key(),annotation.label(),new FileFilter() {
						@Override	public String getDescription() { return value; }
						@Override	public boolean accept(File file) { return file.isDirectory()||file.getName().toLowerCase().endsWith('.'+matcher.group(1)); }
					});
				else return new Parameter(annotation.key(),annotation.label(),new File(value));
			default: return null;
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
				@Override public void actionPerformed(ActionEvent event) {
					if(chooser.showOpenDialog(FileComponent.this)==JFileChooser.APPROVE_OPTION)
						display.setText(chooser.getSelectedFile().getName());
				}
			}));
		}
		public FileComponent(FileFilter filter) {
			this(); chooser.setFileFilter(filter);
		}
		
		public void addActionListener(ActionListener listener) { if(chooser!=null) chooser.addActionListener(listener); }
		@SuppressWarnings("unused") public void removeActionListener(ActionListener listener) { if(chooser!=null) chooser.removeActionListener(listener); }
		@SuppressWarnings("unused") public ActionListener[] getActionListeners() { return chooser!=null?chooser.getActionListeners():null; }
  
		public void setFile(File file) {
			if(chooser!=null) chooser.setSelectedFile(file);
			display.setText(file!=null?file.getName():null);
		}
		public InputStream openFile() throws IOException {
			File selected = chooser.getSelectedFile();
			return selected!=null?new FileInputStream(selected):null;
		}
	}
}