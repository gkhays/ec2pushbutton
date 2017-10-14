package org.pushbutton.aws.gui;

import java.util.Arrays;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import org.pushbutton.aws.gui.components.ConfigurationFrame;
import org.pushbutton.utils.Utils;

import org.pushbutton.aws.gui.components.ConfigurationFrame;

import com.amazonaws.services.ec2.model.Instance;

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class InstancesGui extends ConfigurationFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8709584604938742735L;

	private static final List<String> headers = Arrays.asList("ID", "AMI",
			"Type", "State", "Monitoring", "DNS Name", "IP Address");

	public InstancesGui(List<Instance> list) {
		setBounds(new Rectangle(100, 100, 600, 300));
		setTitle("EC2 Instances");
		DefaultTableModel model = new DefaultTableModel();
		for (String title : headers) {
			model.addColumn(title);
		}
		
		for (Instance i : list) {
			String[] row = { 
					i.getInstanceId(), 
					i.getImageId(), 
					i.getInstanceType(), 
					i.getState().getName(),
					i.getMonitoring().getState(), 
					i.getPublicDnsName(), 
					i.getPublicIpAddress() };
			model.addRow(row);
		}
		
		this.table.setModel(model);
		TableColumnModel tcm = this.table.getColumnModel();
		tcm.getColumn(3).setCellRenderer(new IconTextCellRenderer());
		
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
			String state = value.toString();
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
