package org.pushbutton.aws.gui;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import java.awt.GridBagLayout;

import javax.swing.JButton;

import org.pushbutton.aws.App;
import org.pushbutton.aws.gui.components.FooterPanel;
import org.pushbutton.aws.gui.components.FooterPanel.Status;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.model.StartInstancesResult;
import com.amazonaws.services.ec2.model.StopInstancesRequest;
import com.amazonaws.services.ec2.model.StopInstancesResult;

public class AWSLauncher extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5754438927656452678L;
	
	private JPanel contentPane;
	private JButton btnStart;
	private JButton btnStop;
	private FooterPanel footerPanel;
	
	private boolean awsInstanceUp = false;
	private AmazonEC2 ec2;
	private Instance awsInstance;

	/**
	 * Create the frame.
	 */
	public AWSLauncher() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.CENTER);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{0, 0, 0};
		gbl_panel.rowHeights = new int[]{0, 0};
		gbl_panel.columnWeights = new double[]{1.0, 1.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		
		btnStart = new JButton("Start");
		btnStart.setToolTipText("Start AWS Instance");
		btnStart.setPreferredSize(new Dimension(200, 200));
		btnStart.setFont(new Font("Arial", Font.BOLD, 40));
		btnStart.setBackground(Color.GREEN);
		btnStart.setForeground(Color.BLUE);
		GridBagConstraints gbc_btnStart = new GridBagConstraints();
		gbc_btnStart.insets = new Insets(0, 0, 0, 5);
		gbc_btnStart.gridx = 0;
		gbc_btnStart.gridy = 0;
		panel.add(btnStart, gbc_btnStart);
		
		btnStop = new JButton("Stop");
		btnStop.setToolTipText("Stop AWS instance");
		btnStop.setPreferredSize(new Dimension(200, 200));
		btnStop.setFont(new Font("Arial", Font.BOLD, 40));
		btnStop.setBackground(Color.RED);
		btnStop.setForeground(Color.WHITE);
		GridBagConstraints gbc_btnStop = new GridBagConstraints();
		gbc_btnStop.gridx = 1;
		gbc_btnStop.gridy = 0;
		panel.add(btnStop, gbc_btnStop);
		btnStop.setEnabled(false);
		
		footerPanel = new FooterPanel();
		footerPanel.updateStatus(getInstanceStatus());
		this.add(footerPanel, BorderLayout.SOUTH);
	}
	
	public void startListeners() {
		final String instanceId = awsInstance.getInstanceId();

		btnStart.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				btnStart.setEnabled(false);
				
				StartInstancesRequest request = new StartInstancesRequest()
						.withInstanceIds(instanceId);
				StartInstancesResult result = ec2.startInstances(request);
				result.getStartingInstances();
				
				// Poll for status change and update our indicators.
				checkStatus();				
				btnStop.setEnabled(true);
			}

		});
		
		btnStop.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				btnStop.setEnabled(false);
				
				StopInstancesRequest request = new StopInstancesRequest()
						.withInstanceIds(instanceId);
				StopInstancesResult result = ec2.stopInstances(request);
				result.getStoppingInstances();
				
				// TODO - Should we check to make sure it's stopping?
				
				awsInstanceUp = false;
				footerPanel.updateStatus(getInstanceStatus());
				btnStart.setEnabled(true);
			}

		});
		
	}

	public void setAwsClient(AmazonEC2 ec2) {
		this.ec2 = ec2;
	}

	public void setInstance(Instance instance) {
		awsInstance = instance;
	}

	private void checkStatus() {
		// In progress states are pending, shutting-down, and stopping. Whereas
		// complete states are running, terminated, and stopped.
		App.TASKPOOL.execute(new Runnable() {
			public void run() {
				loopUntilStateChanges();
			}
		});
	}

	private Status getInstanceStatus() {
		if (awsInstanceUp) {
            return Status.ONLINE;
        } else if (!awsInstanceUp) {
            return Status.OFFLINE;
        } else {
            return Status.PARTIAL;
        }
	}
	
	private void loopUntilStateChanges() {
		boolean success = false;
		String ipAddress = null;
		String targetState = "running";
		String targetInstance = awsInstance.getInstanceId();
		while (!success) {
			DescribeInstancesRequest request = new DescribeInstancesRequest();
			DescribeInstancesResult response = ec2.describeInstances(request);
			
			for (Reservation reservation : response.getReservations()) {
				for (Instance instance : reservation.getInstances()) {
					if (instance.getInstanceId().equals(targetInstance)) {
						if (instance.getState().getName().equals(targetState)) {
							ipAddress = instance.getPublicIpAddress();
							success = true;
							break;
						}
					}
				}
			}
		}
		awsInstanceUp = true;
		footerPanel.updateStatus(getInstanceStatus());
		footerPanel.setIPAddress(ipAddress);
		setCursor(Cursor.getDefaultCursor());
	}

}
