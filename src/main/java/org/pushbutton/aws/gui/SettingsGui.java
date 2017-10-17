package org.pushbutton.aws.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import org.pushbutton.aws.gui.components.ConfigurationFrame;
import org.pushbutton.utils.SettingsManager;

public class SettingsGui extends ConfigurationFrame {
	
	private static final long serialVersionUID = 670290632895590534L;
	
	private DefaultTableModel model;
	private Properties properties;
	private Map<String, String> changedEntries;

	public SettingsGui() {
		setTitle("Configuration Settings");
		btnOk.setToolTipText("Save property changes");
		this.properties = SettingsManager.getProperties();
		changedEntries = new HashMap<String, String>();
		model = new DefaultTableModel();
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
				int row = e.getFirstRow();
				int col = e.getColumn();
				String key = model.getValueAt(row, 0).toString();
				String val = model.getValueAt(row, col).toString();
				changedEntries.put(key, val);
			}
		});

		btnOk.setText("Save");
		btnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveTable();				
				SettingsGui.this.setVisible(false);
			}
		});
		
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SettingsGui.this.setVisible(false);
			}			
		});
	}
	
	private void saveTable() {
		for (Map.Entry<String, String> entry : changedEntries.entrySet()) {
			SettingsManager.getProperties().setProperty(
					entry.getKey(), entry.getValue());

			try {
				SettingsManager.saveProperties();
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
