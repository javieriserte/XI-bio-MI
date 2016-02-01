package org.jiserte.mi.mimatrixviewer.datastructures;

import java.util.Arrays;

public class CovariationMatrix {
  
  public static final double UNDEFINED = Double.NEGATIVE_INFINITY; 
  
  private double     [] covariationValues;
  private double        minValue;
  private boolean       recalcMinMax;
  private double        maxValue;
  private int size;
  
  
  public CovariationMatrix(int size) {
    super();
    this.setSize(size);
    
    int nPos = this.getNumberOfpositions(size);
    
    this.setValues( new double[nPos]);
    this.setRecalculateMinAndMax(true);
        
  }


  private void setRecalculateMinAndMax(boolean b) {
    this.recalcMinMax = b;
    
  }


  private void setValues(double[] doubles) {
    this.covariationValues = doubles; 
  }


  private void setSize(int size) {
     this.size = size;
  }


  private int getNumberOfpositions(int size) {
    return (int) (size * (size - 1 ) / 2);
    
  }
  
  public void setValue(int nominalPosX, int nominalPosY, double value) {
    
    int x = Math.min(nominalPosX, nominalPosY) - 1 ;
    int y = Math.max(nominalPosX, nominalPosY) - 1 ;
    int arrayPos = this.translatePositionsFromMatrixToArray(x,y);
    
    this.getValues()[arrayPos] = value;
    this.setRecalculateMinAndMax(true);
    
  }
  
  public double getValue(int nominalPosX, int nominalPosY) {
    
    int x = Math.min(nominalPosX, nominalPosY) - 1 ;
    int y = Math.max(nominalPosX, nominalPosY) - 1 ;
    if (x == y) return CovariationMatrix.UNDEFINED;
    int arrayPos = this.translatePositionsFromMatrixToArray(x,y);
    
    return this.getValues()[arrayPos];
    
  }
  
  /**
   * Assumes that absoluteX < absoluteY
   * @param absolutePosX
   * @param absolutePosY
   * @return
   */
  private int translatePositionsFromMatrixToArray(int absolutePosX,
      int absolutePosY) {
    return (int) (absolutePosY * (absolutePosY - 1) / 2) + absolutePosX;
  }

  private double[] getValues() {
    return this.covariationValues;
    
  }
  
  public void findMinAndMax() {
    double currentMin=Double.POSITIVE_INFINITY;
    double currentMax=Double.NEGATIVE_INFINITY;
    for (double currentValue : this.getValues()) {
      currentMin = Math.min(currentMin, currentValue);
      currentMax = Math.max(currentMax, currentValue);
    }
    this.setMin(currentMin);
    this.setMax(currentMax);
    this.setRecalculateMinAndMax(false);
  }

  private void setMax(double max) {
    this.maxValue = max;
    
  }


  private void setMin(double min) {
    this.minValue = min;
  }
  
  public double getMin() {
    boolean recalc = this.getRecalculateMinAndMax();
    if (recalc) {
      this.findMinAndMax();
    }
    return this.minValue;
  }

  private boolean getRecalculateMinAndMax() {
    return this.recalcMinMax;
  }


  public double getMax() {
    boolean recalc = this.getRecalculateMinAndMax();
    if (recalc) {
      this.findMinAndMax();
    }
   return this.maxValue;
  }
  
  
  public int getSize() {
    return size;
  }
  
  
  public double[] getCopyOfValues() {
    return Arrays.copyOf(this.covariationValues, this.covariationValues.length);
  }
}
