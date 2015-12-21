package org.jiserte.mi.mimatrixviewer.matrixview_new.colors;

import java.awt.Color;

public interface ColoringStrategy {

	public Color getColor(double value);
	
	public boolean isValidValue(double value);
	
	public double min();
	
	public double max();
	
}
