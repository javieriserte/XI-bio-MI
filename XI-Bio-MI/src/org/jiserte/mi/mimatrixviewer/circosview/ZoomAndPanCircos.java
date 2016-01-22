package org.jiserte.mi.mimatrixviewer.circosview;

import java.util.Observable;

public class ZoomAndPanCircos extends Observable {

  //////////////////////////////////////////////////////////////////////////////
  // Instance variables
  private double value;
  private double offSetH;
  private double offSetV;
  //////////////////////////////////////////////////////////////////////////////

  //////////////////////////////////////////////////////////////////////////////
  // Constructor
  public ZoomAndPanCircos(double value) {
    super();
    this.value = value;
  }
  //////////////////////////////////////////////////////////////////////////////

  //////////////////////////////////////////////////////////////////////////////
  // Public interface
  public double getValue() {
    return value;
  }

  public double getOffSetH() {
    return offSetH;
  }

  public double getOffSetV() {
    return offSetV;
  }

  public void setValue(double value) {
    this.value = value;
    this.setChanged();
    this.notifyObservers("VALUE_CHANGED");
  }

  public void setOffSetH(double value) {
    this.offSetH = value;
    this.setChanged();
    this.notifyObservers("OFFSETH_CHANGED");
  }

  public void setOffSetV(double value) {
    this.offSetV = value;
    this.setChanged();
    this.notifyObservers("OFFSETV_CHANGED");
  }
  //////////////////////////////////////////////////////////////////////////////

}
