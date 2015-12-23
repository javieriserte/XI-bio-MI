package org.jiserte.mi.mimatrixviewer.matrixview_new;

import java.awt.Rectangle;

public class MatrixAreaChangedEvent {

  ////////////////////////////////////////////////////////////////////////////
  // Class Constants
  public static final int HOVER_AREA = 0; 
  public static final int SELECTED_AREA = 1;
  ////////////////////////////////////////////////////////////////////////////
  
	////////////////////////////////////////////////////////////////////////////
	// Instance variables
	private Rectangle rect ;
	private double[][] values ;
	private char[] hChars ;
	private char[] vChars ;
	private int action;
	////////////////////////////////////////////////////////////////////////////

  ////////////////////////////////////////////////////////////////////////////
    // Constructor
	public MatrixAreaChangedEvent() {
		super();
	}
	////////////////////////////////////////////////////////////////////////////

	////////////////////////////////////////////////////////////////////////////
	public Rectangle getRect() {
		return rect;
	}
	public void setRect(Rectangle rect) {
		this.rect = rect;
	}
	public double[][] getValues() {
		return values;
	}
	public void setValues(double[][] values) {
		this.values = values;
	}
	public char[] gethChars() {
		return hChars;
	}
	public void sethChars(char[] hChars) {
		this.hChars = hChars;
	}
	public char[] getvChars() {
		return vChars;
	}
	public void setvChars(char[] vChars) {
		this.vChars = vChars;
	}  
  public int getAction() {
    return action;
  }
  public void setAction(int action) {
    this.action = action;
  }
	////////////////////////////////////////////////////////////////////////////
}
