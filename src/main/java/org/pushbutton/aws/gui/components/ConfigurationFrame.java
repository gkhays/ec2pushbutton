package org.pushbutton.aws.gui.components;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JButton;

public class ConfigurationFrame extends JFrame {

	protected JTable table;
	protected JPanel panel;
	protected JButton btnOk;
	protected JButton btnCancel;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1498937236673965899L;
	private JPanel contentPane;

	/**
	 * Create the frame.
	 */
	public ConfigurationFrame() {
		setResizable(false);		
		setBounds(100, 100, 450, 300);
		setLocationRelativeTo(null);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JScrollPane scrollPane = new JScrollPane();
		contentPane.add(scrollPane, BorderLayout.CENTER);
		
		table = new JTable();
		scrollPane.setViewportView(table);
		
		panel = new JPanel();
		contentPane.add(panel, BorderLayout.SOUTH);
		
		btnOk = new JButton("OK");
		panel.add(btnOk);
		
		btnCancel = new JButton("Cancel");
		panel.add(btnCancel);
	}

}
