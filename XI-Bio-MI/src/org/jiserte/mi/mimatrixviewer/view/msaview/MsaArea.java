package org.jiserte.mi.mimatrixviewer.view.msaview;


public class MsaArea {
  private int left;
  private int right;
  private int top;
  private int bottom;
  private int selectionMode;
  public static final int ROW_SELECTION_MODE = 1;
  public static final int COLUMN_SELECTION_MODE = 2;
  public static final int AREA_SELECTION_MODE = 3;
  
  
  
  public MsaArea() {
    super();
    this.left = 0;
    this.right = 0;
    this.top = 0;
    this.bottom = 0;
    this.selectionMode = ROW_SELECTION_MODE;
  }
  
  public int getLeft() {
    return left;
  }
  public void setLeft(int left) {
    this.left = left;
  }
  public int getRight() {
    return right;
  }
  public void setRight(int right) {
    this.right = right;
  }
  public int getTop() {
    return top;
  }
  public void setTop(int top) {
    this.top = top;
  }
  public int getBottom() {
    return bottom;
  }
  public void setBottom(int bottom) {
    this.bottom = bottom;
  }
  public int getSelectionMode() {
    return selectionMode;
  }
  public void setSelectionMode(int selectionMode) {
    this.selectionMode = selectionMode;
  }
  public String toString() {
    return "["+this.getLeft() + ", " + this.getTop() + ", "+ this.getRight() + ", "+ this.getBottom() + "] Mode: "+ getSelectionMode();
  }

}
//////////////////////////////////////////////////////////////////////////////