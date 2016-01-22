package org.jiserte.mi.mimatrixviewer.datastructures;

import java.util.Comparator;

public class CovariationTriplet {
  //////////////////////////////////////////////////////////////////////////////
  // Instance variables
  private int nominalX;
  private int nominalY;
  private double value;
  //////////////////////////////////////////////////////////////////////////////

  //////////////////////////////////////////////////////////////////////////////
  // Constructor
  public CovariationTriplet(int nominalX, int nominalY, double value) {
    super();
    this.nominalX = nominalX;
    this.nominalY = nominalY;
    this.value = value;
  }
  //////////////////////////////////////////////////////////////////////////////

  //////////////////////////////////////////////////////////////////////////////
  // Public Interface
  public int getNominalX() {
    return nominalX;
  }

  public void setNominalX(int nominalX) {
    this.nominalX = nominalX;
  }

  public int getNominalY() {
    return nominalY;
  }

  public void setNominalY(int nominalY) {
    this.nominalY = nominalY;
  }

  public double getValue() {
    return value;
  }

  public void setValue(double value) {
    this.value = value;
  }
  //////////////////////////////////////////////////////////////////////////////

  //////////////////////////////////////////////////////////////////////////////
  // Class methods
  public static Comparator<CovariationTriplet> getComparator(boolean ascending) {
    
    Comparator<CovariationTriplet> comp = new Comparator<CovariationTriplet>() {
      private boolean orderAscending = ascending;
      @Override
      public int compare(CovariationTriplet o1, CovariationTriplet o2) {
        double d = o1.getValue() - o2.getValue();
        d = d == 0 ? 0 : (int) (d / Math.abs(d));
        d = (orderAscending)? -d : d;
        return (int)d;
      }};
      return comp;
  }
  
}
//////////////////////////////////////////////////////////////////////////////
