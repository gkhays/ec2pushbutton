package org.pushbutton.aws.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import org.ini4j.Wini;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

public class LoginForm extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7795644787929082513L;
	
	private final JPanel contentPanel = new JPanel();
	private JTextField key;
	private JPasswordField secret;
	
	@SuppressWarnings("unused")
	private File configFile;
	
	public LoginForm(final File configFile) {
		this.configFile = configFile;
		
		setBounds(100, 100, 420, 170);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);

		key = new JTextField();
		key.setFont(new Font("Tahoma", Font.PLAIN, 12));
		key.setBounds(64, 11, 329, 30);
		contentPanel.add(key);
		key.setColumns(10);
		
		secret = new JPasswordField();
		secret.setFont(new Font("Tahoma", Font.PLAIN, 12));
		secret.setBounds(64, 57, 329, 30);
		contentPanel.add(secret);
		
		JLabel keyLabel = new JLabel("Key:");
		keyLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
		keyLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		keyLabel.setLabelFor(key);
		keyLabel.setBounds(10, 14, 44, 14);
		contentPanel.add(keyLabel);
		
		JLabel secretLabel = new JLabel("Secret:");
		secretLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
		secretLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		secretLabel.setLabelFor(secret);
		secretLabel.setBounds(10, 60, 44, 14);
		contentPanel.add(secretLabel);

		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);

		JButton okButton = new JButton("OK");
		okButton.setActionCommand("OK");
		buttonPane.add(okButton);
		getRootPane().setDefaultButton(okButton);

		JButton cancelButton = new JButton("Cancel");
		cancelButton.setActionCommand("Cancel");
		buttonPane.add(cancelButton);
		
		okButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				try {
					if (configFile.exists()) {
						Wini ini = new Wini(configFile);
						ini.add("default", "aws_access_key_id", key.getText());
						ini.add("default", "aws_secret_access_key", new String(
								secret.getPassword()));
						ini.store();
					} else {
						configFile.getParentFile().mkdirs();
						configFile.createNewFile();
					}

					LoginForm.this.setVisible(false);
					LoginForm.this.setModal(false);
					LoginForm.this.dispatchEvent(new WindowEvent(LoginForm.this,
							WindowEvent.WINDOW_CLOSING));
					dispose();
				} catch (IOException ioe) {
					// TODO Auto-generated catch block
					ioe.printStackTrace();
				}
			}
		});
		
		cancelButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				LoginForm.this.setVisible(false);
				LoginForm.this.setModal(false);
				dispose();
			}
			
		});
		
		this.setResizable(false);
		this.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
	}
}
