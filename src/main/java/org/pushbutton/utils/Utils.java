package org.pushbutton.utils;

import java.awt.Desktop;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class Utils {

	public static BufferedImage getImage(String img) {
		String name;

		if (false == img.startsWith("/assets/image/")) {
			name = "/assets/image/" + img;
		} else {
			name = img;
		}

		if (false == name.endsWith(".png")) {
			name += ".png";
		}

		InputStream in = org.pushbutton.aws.App.class.getResourceAsStream(name);

		try {
			return ImageIO.read(in);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public static ImageIcon getIconImage(String path) {
		URL url = System.class.getResource(path);
		return new ImageIcon(url);
	}

	public static void openBrowser(String URL) {
		if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().browse(new URI(URL));
            } catch (Exception e) {
                // TODO - Add logging.
                e.printStackTrace();
            }
        }
	}
	
	public static void openBrowser(URL URL) {
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().browse(URL.toURI());
            } catch (Exception e) {
                // TODO - Add logging.
                e.printStackTrace();
            }
        }
    }

	public static void openExplorer(File file) {
		if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().open(file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
	}

}
