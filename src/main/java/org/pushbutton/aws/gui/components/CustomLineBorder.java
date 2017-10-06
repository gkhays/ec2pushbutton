package org.pushbutton.aws.gui.components;

import java.awt.Color;
import java.awt.Component;
import java.awt.Insets;

import javax.swing.border.LineBorder;

public class CustomLineBorder extends LineBorder {
	
	private static final long serialVersionUID = 586098798049894312L;
	private int insets = 0;

	public CustomLineBorder(int insets, Color color) {
		super(color);
		this.insets = insets;
	}

	public CustomLineBorder(int insets, Color color, int thickness) {
		super(color, thickness);
		this.insets = insets;
	}

	public CustomLineBorder(int insets, Color color, int thickness,
			boolean rounded) {
		super(color, thickness, rounded);
		this.insets = insets;
	}

	@Override
	public Insets getBorderInsets(Component c, Insets insets) {
		return new Insets(this.insets, this.insets, this.insets, this.insets);
	}
}