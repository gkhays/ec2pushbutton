package org.pushbutton.aws;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;

import org.pushbutton.aws.gui.AWSLauncher;
import org.pushbutton.aws.gui.SplashScreen;
import org.pushbutton.utils.SettingsManager;

public class App {

	/**
	 * Background task runner.
	 */
	public static final ExecutorService TASKPOOL = Executors.newFixedThreadPool(2);
	
	public static final SplashScreen splash = new SplashScreen();
	
	private String instanceId;
	private AWSLauncher launcherFrame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				splash.setVisible(true);
			}
		});
		
		// Set the look and feel.
		try {
			setLaF();
			modifyLaF();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					App window = new App();
					window.launcherFrame.setVisible(true);
					window.launcherFrame.setResizable(false);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		//splash.close();
	}

	/**
	 * Create the application.
	 */
	public App() {
		initialize();
	}


	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {		
		instanceId = SettingsManager.getDefaultProperties().getProperty("instanceId");
		launcherFrame = new AWSLauncher(instanceId);
		launcherFrame.setBounds(100, 100, 450, 300);
		launcherFrame.setLocationRelativeTo(null);
		launcherFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	/**
	 * Set the look and feel to Nimbus.
	 * 
	 * @throws Exception
	 */
	private static void setLaF() throws Exception {
		for (UIManager.LookAndFeelInfo info : UIManager
				.getInstalledLookAndFeels()) {
			if (info.getName().equalsIgnoreCase("nimbus")) {
				UIManager.setLookAndFeel(info.getClassName());
			}
		}
	}
	
	private static void modifyLaF() throws Exception {
		ToolTipManager.sharedInstance().setDismissDelay(15000);
		ToolTipManager.sharedInstance().setInitialDelay(50);
		//UIManager.put("FileChooser.readOnly", Boolean.TRUE);
		UIManager.put("ScrollBar.minimumThumbSize", new Dimension(50, 50));
	}

}
