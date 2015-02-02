package de.hsma.gentool;

import static de.hsma.gentool.gui.helper.Guitilities.*;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public interface Configurable {
	public Option[] getOptions();

	default public Map<Option,Object> getPreferences() { return readPreferences(); }
	default public void setPreferences(Map<Option,Object> preferences) {
		try { storePreferences(preferences); }
		catch(BackingStoreException e) { /* nothing to do here */ }
	}
	
	default public Map<Option,Object> readPreferences() {
		Preferences preferences = Preferences.userNodeForPackage(getClass());
		Map<Option,Object> values = new LinkedHashMap<>();
		for(Option option:getOptions())
			values.put(option, option.readPreference(preferences));
		return values;
	}
	default public void storePreferences(Map<Option,Object> values) throws BackingStoreException {
		Preferences preferences = Preferences.userNodeForPackage(getClass());
		for(Option option:getOptions())
			option.storePreference(preferences, values.get(option));
		preferences.flush();
	}
	
	default public Map<Option,Object> showPreferencesDialog(Component parent,String title) {
		final Option[] options = getOptions();
		if(options==null||options.length==0)
			return null;
		
		final JDialog dialog = createDialog(parent,title);
		final Map<Option,Object> values = getPreferences();
		final Map<Option,Option.Component> components = new HashMap<Option,Option.Component>();
		
		int grid = 0;
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(new EmptyBorder(10,10,10,10));
		
		Option.Component component;
		for(Option option:options) {
			components.put(option,component = option.new Component(values.get(option)));
			addGridPairLine(panel,grid++,new JLabel(option.label+":"),component,new JLabel(String.format("(Default: %s)", option.value.toString())));
		}
		dialog.add(panel,BorderLayout.CENTER);
		
		JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		footer.setPreferredSize(new Dimension(footer.getPreferredSize().width,40));
		footer.add(new JButton(new AbstractAction("Okay") {
			private static final long serialVersionUID = 1l;
			@Override public void actionPerformed(ActionEvent event) {
				for(Option option:options)
					values.put(option,components.get(option).getValue());
				setPreferences(values);
				dialog.dispose();
			}
		}));
		footer.add(new JButton(new AbstractAction("Cancel") {
			private static final long serialVersionUID = 1l;
			@Override	public void actionPerformed(ActionEvent event) { dialog.dispose(); }
		}));
		dialog.add(footer,BorderLayout.SOUTH);
		
		dialog.pack(); dialog.setSize(new Dimension(400,dialog.getHeight()));
		dialog.setVisible(true);
		
		return values;
	}
}