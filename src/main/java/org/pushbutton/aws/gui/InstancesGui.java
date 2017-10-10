package org.pushbutton.aws.gui;

import java.util.List;

import javax.swing.table.DefaultTableModel;

import org.pushbutton.aws.gui.components.ConfigurationFrame;

import com.amazonaws.services.ec2.model.Instance;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class InstancesGui extends ConfigurationFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8709584604938742735L;
	
	public InstancesGui(List<Instance> list) {
		setBounds(new Rectangle(100, 100, 600, 300));
		setTitle("EC2 Instances");
		DefaultTableModel model = new DefaultTableModel();
		model.addColumn("ID");
		model.addColumn("AMI");
		model.addColumn("Type");
		model.addColumn("State");
		model.addColumn("Monitoring");
		model.addColumn("DNS Name");
		model.addColumn("IP Address");
		
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
		
		btnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				InstancesGui.this.setVisible(false);
			}
		});
		
		// We don't need the cancel button.
		this.btnCancel.setEnabled(false);
		this.btnCancel.setVisible(false);
	}

}
