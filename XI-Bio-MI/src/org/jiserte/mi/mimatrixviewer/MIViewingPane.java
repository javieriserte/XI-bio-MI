package org.jiserte.mi.mimatrixviewer;

import javax.swing.JComponent;

import org.jiserte.mi.mimatrixviewer.datastructures.CovariationData;



public abstract class MIViewingPane extends JComponent{

	private static final long serialVersionUID = -3394871213386920614L;

	public abstract void setData(CovariationData data);
	
	public abstract void forceDrawing();
	
}
