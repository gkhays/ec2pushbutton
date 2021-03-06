package org.pushbutton.aws.gui;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.pushbutton.aws.gui.components.ConfigurationFrame;
import org.pushbutton.utils.Utils;

public class InstancesGui extends ConfigurationFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8709584604938742735L;

	// TODO - I intend for the order to be:
	// ID, AMI, Type, State, Monitoring, DNS Name, IP Address
	// But instead I am getting:
	// ID, AMI, Type, IP Address, DNS Name, State, Monitoring
//	private static final List<String> headers = Arrays.asList("ID", "AMI",
//			"Type", "State", "Monitoring", "DNS Name", "IP Address");
	private static final List<String> headers = Arrays.asList("ID", "AMI",
			"Type", "IP Address", "DNS Name", "State", "Monitoring");

	public InstancesGui(List<Map<String,String>> list) {
		setBounds(new Rectangle(100, 100, 600, 300));
		setTitle("EC2 Instances");
		setLocationRelativeTo(null);
		DefaultTableModel model = new DefaultTableModel();
		for (String title : headers) {
			model.addColumn(title);
		}
		
		for (Map<String, String> map : list) {
			int index = 0;
			String[] row = new String[map.size()];
			for (Map.Entry<String, String> entry : map.entrySet()) {
				// TODO - The values should come out of the map in the order in
				// which they were inserted. Otherwise, look at a LinkedHashMap
				// or implement Comparable.
				row[index++] = entry.getValue();
				
			}
			model.addRow(row);
		}
		
		this.table.setModel(model);
		TableColumnModel tcm = this.table.getColumnModel();
		tcm.getColumn(5).setCellRenderer(new IconTextCellRenderer());
		
		btnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				InstancesGui.this.setVisible(false);
			}
		});
		
		// We don't need the cancel button.
		this.btnCancel.setEnabled(false);
		this.btnCancel.setVisible(false);
	}
	
	private class IconTextCellRenderer extends DefaultTableCellRenderer {
		private static final long serialVersionUID = -1367316137984216161L;
		public Component getTableCellRendererComponent(
				JTable table,
				Object value, 
				boolean isSelected, 
				boolean hasFocus, 
				int row,
				int column) {
			super.getTableCellRendererComponent(table, value, isSelected,
					hasFocus, row, column);
			String state;
			if (value == null) {
				state = "";
			} else {
				state = value.toString();
			}
			setText(state);
			if (state.equals("running")) {
				setIcon(Utils.getIconImage("/assets/image/StatusGreen.png"));
			} else {
				setIcon(Utils.getIconImage("/assets/image/StatusRed.png"));
			}
			return this;
		}
	}

}
