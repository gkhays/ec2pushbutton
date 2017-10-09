package org.pushbutton.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;

public class SettingsManager {

	private static URL url;
	private static Properties properties = new Properties();
	
	private SettingsManager() {}
	
	static {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		InputStream in = classLoader.getResourceAsStream("app.properties");
		url = classLoader.getResource("app.properties");
		
		try {
			properties.load(in);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static Properties getProperties() {
		return properties;
	}
	
	public static void saveProperties() throws URISyntaxException, IOException {
		File file = new File(url.toURI());
		OutputStream out = new FileOutputStream(file);
		properties.store(out, "Saved by the settings form.");
	}
	
}
