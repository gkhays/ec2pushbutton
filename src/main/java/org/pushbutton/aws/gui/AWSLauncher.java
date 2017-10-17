package org.pushbutton.aws.gui;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import java.awt.GridBagLayout;

import javax.swing.JButton;

import org.pushbutton.aws.App;
import org.pushbutton.aws.IdChangeListener;
import org.pushbutton.aws.gui.components.ColorMenuBar;
import org.pushbutton.aws.gui.components.FooterPanel;
import org.pushbutton.aws.gui.components.FooterPanel.Status;
import org.pushbutton.utils.AWSConnector;
import org.pushbutton.utils.SettingsManager;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.model.StartInstancesResult;
import com.amazonaws.services.ec2.model.StopInstancesRequest;
import com.amazonaws.services.ec2.model.StopInstancesResult;

import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import java.awt.event.InputEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AWSLauncher extends JFrame implements IdChangeListener {	
	
	public static final Color TXT_COLOR = Color.WHITE;
	public static final String APP_NAME = "EC2 Push Button";
	
	private static final long serialVersionUID = -5754438927656452678L;
	private static final Color BG_COLOR = Color.DARK_GRAY;
	
	private JPanel contentPane;
	private JButton btnStart;
	private JButton btnStop;
	private ColorMenuBar menuBar;
	private JMenu mnOptions;
	private JMenuItem mntmSettings;
	private JMenuItem mntmEcInstances;
	private FooterPanel footerPanel;
	
	private boolean awsInstanceUp = false;
	private String instanceId;
	private String ipAddress;
	private AWSConnector connector;

	/**
	 * Create the frame.
	 * @param instanceId
	 */
	public AWSLauncher(String id) {
		this.instanceId = id;		
		connector = new AWSConnector();
		
		setTitle(APP_NAME + " (" + instanceId + ")");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);		
		
		createMenuBar();		
		createContentPane();
		startListeners();
		
		try {
			checkInstanceAlreadyRunning();
		} catch (Exception e) {
			StringBuilder builder = new StringBuilder();
			builder.append("<html><body><p style='width: 300px;'>");
			builder.append("The instance ID is not correct. " + e.getMessage());
			builder.append("</p></body></html>");
			String message = builder.toString();
			JOptionPane.showMessageDialog(this, message, "Instance Error",
					JOptionPane.ERROR_MESSAGE);
		}
		
		App.splash.close();
	}

	private void createContentPane() {
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		contentPane.setBackground(BG_COLOR);
		
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.CENTER);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{0, 0, 0};
		gbl_panel.rowHeights = new int[]{0, 0};
		gbl_panel.columnWeights = new double[]{1.0, 1.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		panel.setBackground(BG_COLOR);
		
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
		btnStop.setForeground(TXT_COLOR);
		GridBagConstraints gbc_btnStop = new GridBagConstraints();
		gbc_btnStop.gridx = 1;
		gbc_btnStop.gridy = 0;
		panel.add(btnStop, gbc_btnStop);
		btnStop.setEnabled(false);
		
		footerPanel = new FooterPanel();
		footerPanel.updateStatus(getInstanceStatus());
		footerPanel.setBackground(BG_COLOR);
		getContentPane().add(footerPanel, BorderLayout.SOUTH);
	}

	private void createMenuBar() {
		menuBar = new ColorMenuBar();
		menuBar.setColor(BG_COLOR);
		setJMenuBar(menuBar);
		
		mnOptions = new JMenu("Options");
		mnOptions.setMnemonic(KeyEvent.VK_O);
		mnOptions.setForeground(TXT_COLOR);
		menuBar.add(mnOptions);
		
		mntmSettings = new JMenuItem("Settings");
		mntmSettings.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
				InputEvent.CTRL_MASK));
		mnOptions.add(mntmSettings);

		mntmEcInstances = new JMenuItem("EC2 Instances");
		mntmEcInstances.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E,
				InputEvent.CTRL_MASK));
		mnOptions.add(mntmEcInstances);
	}

	public void checkInstanceAlreadyRunning() throws Exception {
		Map<String, String> instance = connector.getInstance(instanceId);
		
		String state = instance.get("state");
		if (state.equals("running")) {
			awsInstanceUp = true;
			footerPanel.updateStatus(getInstanceStatus());
			footerPanel.setIPAddress(instance.get("ipAddress"));

			// If its already running toggle the start and stop buttons.
			btnStart.setEnabled(!btnStart.isEnabled());
			btnStop.setEnabled(!btnStop.isEnabled());
		}
	}

	public void instanceChanged(String id) {
		this.instanceId = id;
		this.setTitle(APP_NAME + " (" + instanceId + ")");
	}
	
	public void startListeners() {
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				btnStart.setEnabled(false);				
				
				connector.startInstance(instanceId);
				
				// Poll for status change and update our indicators.
				checkStatus("running");
				
				awsInstanceUp = true;
				footerPanel.setIPAddress("pending...");
				footerPanel.updateStatus(getInstanceStatus());		
				btnStop.setEnabled(true);
			}
		});
		
		btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnStop.setEnabled(false);				
				
				// TODO - Should we check to make sure it's stopping?
				connector.stopInstance(instanceId);
				
				checkStatus("stopping");
				
				awsInstanceUp = false;
				footerPanel.updateStatus(getInstanceStatus());
				btnStart.setEnabled(true);
			}
		});
		
		mntmSettings.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SettingsGui settings = new SettingsGui();
				settings.addListener(AWSLauncher.this);
				settings.setVisible(true);
			}
		});
		
		mntmEcInstances.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				InstancesGui instances = new InstancesGui(connector.getInstanceList());
				instances.setVisible(true);
			}
		});		
	}

	public void setInstance(String instanceId) {
		this.instanceId = instanceId;
	}

	private void checkStatus(String state) {
		// In progress states are: pending, shutting-down, and stopping. Whereas
		// complete states are running, terminated, and stopped.
		final String myState = state;
		App.TASKPOOL.execute(new Runnable() {
			public void run() {
				loopUntilStateChanges(myState);
			}
		});

		awsInstanceUp = true;
		footerPanel.updateStatus(getInstanceStatus());
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
	
	private void loopUntilStateChanges(String targetState) {
		boolean success = false;
		while (!success) {
			try {
				if (connector.getInstance(instanceId).get("state")
						.equals(targetState)) {
					ipAddress = connector.getInstance(instanceId).get(
							"ipAddress");
					success = true;
					break;
				}
			} catch (Exception e) {
				return;
			}
		}

		// TODO - Maybe a Future<T> or Callable<T> so we don't have to depend on
		// side effects.
		footerPanel.setIPAddress(ipAddress);
		setCursor(Cursor.getDefaultCursor());
	}

}
