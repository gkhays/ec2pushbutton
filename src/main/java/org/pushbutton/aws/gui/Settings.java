package org.pushbutton.aws.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

public class Settings extends Configuration {

	/**
	 * 
	 */
	private static final long serialVersionUID = 670290632895590534L;

	public Settings(Properties properties) {
		setTitle("Configuration Settings");
		DefaultTableModel model = new DefaultTableModel();
		model.addColumn("Property");
		model.addColumn("Value");
		
		Set<Object> keys = properties.keySet();
		SortedSet<Object> sortedKeys = new TreeSet<Object>(keys);
		Iterator<Object> iter = sortedKeys.iterator();
		
		while (iter.hasNext()) {
			String key = iter.next().toString();
			String value = properties.getProperty(key);
			String[] row = { key, value };
			model.addRow(row);
		}
		
		this.table.setModel(model);
		this.table.getModel().addTableModelListener(new TableModelListener() {
			public void tableChanged(TableModelEvent e) {
			}
		});

		btnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Settings.this.setVisible(false);
			}
		});
	}

}
