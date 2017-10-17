package org.pushbutton.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import org.pushbutton.aws.gui.LoginForm;

import com.amazonaws.SdkClientException;
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
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.model.StartInstancesResult;
import com.amazonaws.services.ec2.model.StopInstancesRequest;
import com.amazonaws.services.ec2.model.StopInstancesResult;

public class AWSConnector {
	
	private AmazonEC2 ec2;
	
	public AWSConnector() {		
		try {
			initialize();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Map<String, String> getInstance(String id) throws Exception {
		DescribeInstancesRequest request = new DescribeInstancesRequest().withInstanceIds(id);
		DescribeInstancesResult response = ec2.describeInstances(request);
		
		List<Reservation> reservations = response.getReservations();
		assert(reservations.size() == 1);
		
		List<Instance> instances = reservations.get(0).getInstances();
		assert(instances.size() == 1);
		
		return instanceToMap(instances.get(0));
	}
	
	public List<Map<String, String>> getInstanceList() {
		boolean done = false;
		List<Map<String, String>> instanceList = new ArrayList<Map<String, String>>();
		while (!done) {
			DescribeInstancesRequest request = new DescribeInstancesRequest();
			DescribeInstancesResult response = ec2.describeInstances(request);
			
			for (Reservation reservation : response.getReservations()) {
				for (Instance instance : reservation.getInstances()) {
					instanceList.add(instanceToMap(instance));
				}
			}

			// This is overkill since I don't expect a ton of instances. But if
			// we find a bunch, we will page through 'em! :)
			request.setNextToken(response.getNextToken());

			if (response.getNextToken() == null) {
				done = true;
			}
		}
		
		return instanceList;
	}

	// TODO - Implement an instance state change converter and return the values
	// in a list.
	public void startInstance(String id) {
		StartInstancesRequest request = new StartInstancesRequest()
				.withInstanceIds(id);
		StartInstancesResult result = ec2.startInstances(request);
		result.getStartingInstances();
	}
	
	// TODO - As in startInstance above, keep track of stopping instances.
	public void stopInstance(String id) {
		StopInstancesRequest request = new StopInstancesRequest()
				.withInstanceIds(id);
		StopInstancesResult result = ec2.stopInstances(request);
		result.getStoppingInstances();
	}
	
	private void doLogin(File configFile) {
		LoginForm login = new LoginForm(configFile);
		login.setModal(true);
		login.setVisible(true);
	}

	/**
	 * @param configFile
	 * @throws SdkClientException
	 */
	private void getClient(File configFile) throws SdkClientException {
		ProfilesConfigFile profiles = new ProfilesConfigFile(configFile);
		AWSCredentialsProvider provider = new ProfileCredentialsProvider(
				profiles, "default");
		
		try {
			// Attempting to retrieve the credentials will confirm a default
			// profile exists in the Amazon credential cache.
			provider.getCredentials();
		} catch (IllegalArgumentException e) {
			String noProfile = "No AWS profile named 'default'";
			String fileNotFound = "AWS credential profiles file not found";
			if (e instanceof IllegalArgumentException) {
				if (e.getMessage().equals(noProfile)
						|| e.getMessage().startsWith(fileNotFound)) {
					doLogin(configFile);
					profiles = new ProfilesConfigFile(configFile);
					provider = new ProfileCredentialsProvider(profiles,
							"default");
				}
			} else {
				JOptionPane.showMessageDialog(null, e.getMessage(),
						"Login Error", JOptionPane.WARNING_MESSAGE);
			}
		}
		
		AmazonEC2ClientBuilder builder = AmazonEC2ClientBuilder.standard()
				.withCredentials(provider).withRegion(Regions.US_EAST_1);
		ec2 = builder.build();
	}
	
	private void initialize() throws IOException {
		String home = System.getProperty("user.home");
		File configFile = new File(home, ".aws/credentials");
		
		if (!configFile.exists()) {
			configFile.getParentFile().mkdirs();
			configFile.createNewFile();
			doLogin(configFile);
		}
		
		try {
			getClient(configFile);
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	private Map<String, String> instanceToMap(Instance instance) {
		Map<String, String> instanceMap = new HashMap<String, String>();
		instanceMap.put("instanceId", instance.getInstanceId());
		instanceMap.put("imageId", instance.getImageId());
		instanceMap.put("instanceType", instance.getInstanceType());
		instanceMap.put("state", instance.getState().getName());
		instanceMap.put("monitoring", instance.getMonitoring().getState());
		instanceMap.put("dnsName", instance.getPublicDnsName());
		instanceMap.put("ipAddress", instance.getPublicIpAddress());
		return instanceMap;
	}
	
}
