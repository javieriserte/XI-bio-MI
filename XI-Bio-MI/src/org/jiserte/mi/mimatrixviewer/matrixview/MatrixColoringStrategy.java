package org.jiserte.mi.mimatrixviewer.matrixview;

import java.awt.Color;

import org.jiserte.mi.misticmod.datastructures.MI_Position;

public interface MatrixColoringStrategy {

	public Color getColor(MI_Position p1);
	
}
