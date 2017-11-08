package org.pushbutton.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;

public class SettingsManager {

	private static final String PROPERTY_FILE = "app.properties";
	
	private static Properties properties = new Properties();
	
	private SettingsManager() {}
	
	static {
		InputStream in = null;
		
		try {
			try {
				in = new FileInputStream(PROPERTY_FILE);
			} catch (FileNotFoundException e) {
				File file = new File(PROPERTY_FILE);
				if (!file.exists()) {
					file.createNewFile();
				}
				in = new FileInputStream(PROPERTY_FILE);
			}
			properties.load(in);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			// TODO Throw up an error dialog
			e.printStackTrace();
		}
	}
	
	public static Properties getDefaultProperties() {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		InputStream in = classLoader.getResourceAsStream(PROPERTY_FILE);
		Properties defaultProps = new Properties();

		try {
			defaultProps.load(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return defaultProps;
	}
	
	public static Properties getProperties() {
		return properties;
	}
	
	public static void saveProperties() throws IOException {
		OutputStream out = new FileOutputStream(PROPERTY_FILE);
		properties.store(out, "Saved by the settings form.");
	}
	
}
