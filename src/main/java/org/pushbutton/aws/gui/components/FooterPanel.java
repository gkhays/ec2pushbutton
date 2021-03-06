package org.pushbutton.aws.gui.components;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JToolTip;
import javax.swing.border.Border;

import org.pushbutton.aws.gui.AWSLauncher;
import org.pushbutton.utils.Utils;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

public class FooterPanel extends JPanel {
	
	private static final long serialVersionUID = -3921870443019118823L;
	
	public enum Status {
		ONLINE, OFFLINE, PARTIAL, UNKNOWN
	}
	
	private JLabel statusLED;
	private JTextField ipAddress;
	private JButton btnCopy;

	public FooterPanel() {
		setToolTipText("Server IP Address");
		setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JLabel lblInstanceStatus = new JLabel("AWS Instance Status");
		lblInstanceStatus.setForeground(AWSLauncher.TXT_COLOR);
		add(lblInstanceStatus);
		
		statusLED = new JLabel(
				Utils.getIconImage("/assets/image/StatusWhite.png")) {
			private static final long serialVersionUID = 1L;
			public JToolTip createToolTip() {
				JToolTip tip = super.createToolTip();
				Border border = new CustomLineBorder(5,
						new Color(80, 170, 107), 2);
				tip.setBorder(border);
				return tip;
			}
		};
		statusLED.setBorder(BorderFactory.createEmptyBorder());
		statusLED.setToolTipText("Check AWS Instance Status");
		add(statusLED);
		
		ipAddress = new JTextField("255.255.255.255");
		ipAddress.setToolTipText("Server IP address");
		ipAddress.setEditable(false);
		add(ipAddress);
		
		btnCopy = new JButton("Copy");
		btnCopy.setToolTipText("Copy to clipboard");
		add(btnCopy);
		
		btnCopy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				StringSelection selection = new StringSelection(ipAddress.getText());
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				clipboard.setContents(selection, null);
			}			
		});
	}
	
	public void updateStatus(Status status) {        
		switch (status) {
            case UNKNOWN:
            	statusLED.setToolTipText("Checking AWS Instance Status");
            	statusLED.setIcon(Utils.getIconImage("/assets/image/StatusWhite.png"));
                break;
            case ONLINE:
            	statusLED.setToolTipText("AWS instance is online!");
            	statusLED.setIcon(Utils.getIconImage("/assets/image/StatusGreen.png"));
            	statusLED.setText("Running");
            	statusLED.setForeground(Color.GREEN);
            	ipAddress.setEnabled(true);
                break;
            case OFFLINE:
            	statusLED.setToolTipText("AWS instance is offline");
            	statusLED.setIcon(Utils.getIconImage("/assets/image/StatusRed.png"));
            	statusLED.setText("Stopped");
            	statusLED.setForeground(AWSLauncher.TXT_COLOR);
            	ipAddress.setEnabled(false);
            	// TODO - Is there any point in leaving the copy button enabled?
                break;
            case PARTIAL:
            	statusLED.setToolTipText("AWS instance partial availability");
            	statusLED.setIcon(Utils.getIconImage("/assets/image/StatusYellow.png"));
                break;
            default:
                break;
        }
    }

	public void setIPAddress(String publicIpAddress) {
		ipAddress.setText(publicIpAddress);
	}
	
}
