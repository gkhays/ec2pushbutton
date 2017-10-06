package org.pushbutton.aws.gui.components;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JToolTip;
import javax.swing.border.Border;

import org.pushbutton.utils.Utils;

import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

public class FooterPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3921870443019118823L;
	
	public enum Status {
		ONLINE, OFFLINE, PARTIAL, UNKNOWN
	}
	
	private JLabel statusLED;
	private JLabel ipAddress;

	public FooterPanel() {
		setToolTipText("Server IP Address");
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, 1.0, 1.0};
		gridBagLayout.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		statusLED = new JLabel(
				org.pushbutton.utils.Utils
						.getIconImage("/assets/image/StatusWhite.png")) {
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
		
		GridBagConstraints gbc_lblStatusLED = new GridBagConstraints();
		gbc_lblStatusLED.insets = new Insets(0, 0, 0, 5);
		gbc_lblStatusLED.gridx = 0;
		gbc_lblStatusLED.gridy = 0;
		add(statusLED, gbc_lblStatusLED);
		
		JLabel lblInstanceStatus = new JLabel("AWS Instance Status");
		GridBagConstraints gbc_lblInstanceStatus = new GridBagConstraints();
		gbc_lblInstanceStatus.gridx = 1;
		gbc_lblInstanceStatus.gridy = 0;
		add(lblInstanceStatus, gbc_lblInstanceStatus);
		
		ipAddress = new JLabel("localhost");
		GridBagConstraints gbcIPAddress = new GridBagConstraints();
		gbcIPAddress.gridx = 2;
		gbcIPAddress.gridy = 0;
		add(ipAddress, gbcIPAddress);
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
                break;
            case OFFLINE:
            	statusLED.setToolTipText("AWS instance is offline");
            	statusLED.setIcon(Utils.getIconImage("/assets/image/StatusRed.png"));
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
