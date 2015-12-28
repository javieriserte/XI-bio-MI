package org.jiserte.mi.mimatrixviewer.readers.mistic;

public class MisticLineData {
  private int p1;
  private int p2;
  private char c1;
  private char c2;
  private double value;
  private boolean empty;
  
  public MisticLineData(int p1, int p2, char c1, char c2, double value) {
    super();
    this.p1 = p1;
    this.p2 = p2;
    this.c1 = c1;
    this.c2 = c2;
    this.value = value;
    this.empty = false;
  }
  public MisticLineData() {
    super();
    this.p1 = 0;
    this.p2 = 0;
    this.c1 = 0;
    this.c2 = 0;
    this.value = 0;
    this.empty = true;
  }

  
  /**
   * @return the p1
   */
  public int getP1() {
    return p1;
  }

  /**
   * @param p1 the p1 to set
   */
  public void setP1(int p1) {
    this.p1 = p1;
  }

  /**
   * @return the p2
   */
  public int getP2() {
    return p2;
  }

  /**
   * @param p2 the p2 to set
   */
  public void setP2(int p2) {
    this.p2 = p2;
  }

  /**
   * @return the c1
   */
  public char getC1() {
    return c1;
  }

  /**
   * @param c1 the c1 to set
   */
  public void setC1(char c1) {
    this.c1 = c1;
  }

  /**
   * @return the c2
   */
  public char getC2() {
    return c2;
  }

  /**
   * @param c2 the c2 to set
   */
  public void setC2(char c2) {
    this.c2 = c2;
  }

  /**
   * @return the value
   */
  public double getValue() {
    return value;
  }

  /**
   * @param value the value to set
   */
  public void setValue(double value) {
    this.value = value;
  }


  /**
   * @return the empty
   */
  public boolean isEmpty() {
    return empty;
  }


  /**
   * @param empty the empty to set
   */
  public void setEmpty(boolean empty) {
    this.empty = empty;
  }
}
