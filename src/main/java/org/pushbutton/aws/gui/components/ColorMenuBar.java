package org.pushbutton.aws.gui.components;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JMenuBar;

public class ColorMenuBar extends JMenuBar {
	
	private static final long serialVersionUID = 8239469338431980503L;
	
	private Color bgColor;
	
	public void setColor(Color color) {
		bgColor = color;
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(bgColor);
		g.fillRect(0, 0, getWidth(), getHeight());
	}

}
