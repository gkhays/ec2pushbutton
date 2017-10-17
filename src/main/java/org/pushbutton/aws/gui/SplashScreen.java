package org.pushbutton.aws.gui;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JWindow;

import org.pushbutton.utils.Utils;

/**
 * Shamelessly ripped off from ATLauncher.
 * 
 * @see <a href="https://github.com/ATLauncher/ATLauncher">ATLauncher</a>
 * 
 * @author RyanTheAllmighty
 * @author HaysG
 */
public class SplashScreen extends JWindow {
	
	private static final long serialVersionUID = 1L;
	// TODO - Should probably use an image to which we have copyright, or at
	// least permission.
	private static final BufferedImage img = Utils.getImage("SplashScreen");
	private final ContextMenu CONTEXT_MENU = new ContextMenu();

	public SplashScreen() {
		this.setLayout(null);
		this.setSize(img.getWidth(), img.getHeight());
		this.setLocationRelativeTo(null);
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					CONTEXT_MENU.show(SplashScreen.this, e.getX(), e.getY());
				}
			}
		});
		this.setAlwaysOnTop(false);
	}	
	
	@Override
	public void paint(Graphics g) {
		g.drawImage(img, 0, 0, this.getWidth(), this.getHeight(), null);
	}

	/**
	 * Closes and disposes of the splash screen.
	 */
	public void close() {
		this.setVisible(false);
		this.dispose();
	}
	
	private final class ContextMenu extends JPopupMenu {
		private static final long serialVersionUID = 1L;
		private final JMenuItem FORCE_QUIT = new JMenuItem("Force Quit");
		public ContextMenu() {
			super();
			this.FORCE_QUIT.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					System.exit(0);
				}
			});
			this.add(this.FORCE_QUIT);
		}
	}
	
}
