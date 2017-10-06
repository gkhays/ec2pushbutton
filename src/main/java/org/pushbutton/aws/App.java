package org.pushbutton.aws;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;

import org.pushbutton.aws.gui.AWSLauncher;
import org.pushbutton.aws.gui.Login;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.auth.profile.ProfilesConfigFile;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;

public class App {

	/**
	 * Background task runner.
	 */
	public static final ExecutorService TASKPOOL = Executors.newFixedThreadPool(2);
	
	private AWSLauncher launcherFrame;
	private String instanceId;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
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
					window.authenticate();
					window.launcherFrame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public App() {
		initialize();
	}

	private void authenticate() {
		String home = System.getProperty("user.home");
		File configFile = new File(home, ".aws/credentials");
		
		try {
			AWSCredentialsProvider provider = new ProfileCredentialsProvider(
					new ProfilesConfigFile(configFile), "default");
			AmazonEC2ClientBuilder builder = AmazonEC2ClientBuilder.standard()
					.withCredentials(provider)
					.withRegion(Regions.US_EAST_1);
			AmazonEC2 ec2 = builder.build();
			for (Instance i : getInstances(ec2)) {
				if (i.getInstanceId().equals(instanceId)) {
					launcherFrame.setAwsClient(ec2);
					launcherFrame.setInstance(i);
					launcherFrame.startListeners();
//					launcherFrame.checkStatus();
					break;
				}
			}			
		} catch (Exception ex) {
			if (ex instanceof IllegalArgumentException) {
				String profileNotFound = "AWS credential profiles file not " +
						"found in the given path:";
				if (ex.getMessage().startsWith(profileNotFound)) {
					Login login = new Login(configFile);
					login.setVisible(true);
					
				}
			} else {
				JOptionPane.showMessageDialog(this.launcherFrame, ex.getMessage(),
						"Error", JOptionPane.WARNING_MESSAGE);
			}
		}
	}
	
	private List<Instance> getInstances(AmazonEC2 ec2) {
		boolean done = false;
		List<Instance> instanceList = new ArrayList<Instance>();
		while (!done) {
			DescribeInstancesRequest request = new DescribeInstancesRequest();
			DescribeInstancesResult response = ec2.describeInstances(request);
			
			for (Reservation reservation : response.getReservations()) {
				for (Instance instance : reservation.getInstances()) {
					instanceList.add(instance);
				}
			}

			request.setNextToken(response.getNextToken());

			if (response.getNextToken() == null) {
				done = true;
			}
		}
		return instanceList;
	}
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		Properties properties = new Properties();
		InputStream in = App.class.getResourceAsStream("/app.properties");
		
		try {
			properties.load(in);
			instanceId = properties.getProperty("instanceId");
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this.launcherFrame, e.getMessage(),
					"Error", JOptionPane.WARNING_MESSAGE);
			// TODO - Hard code the instance ID until we have a work-around.
			instanceId = "i-01e6e1abc0e3edc54";
		}
		
		launcherFrame = new AWSLauncher();
		launcherFrame.setBounds(100, 100, 450, 300);
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
