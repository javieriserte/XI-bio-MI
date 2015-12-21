package org.jiserte.mi.covdataviewer.datastructures;

import java.util.Map;

public class CovariationData {

  //////////////////////////////////////////////////////////////////////////////
  // Class Constants
  public static final double UNDEFINED = Double.NEGATIVE_INFINITY;
  //////////////////////////////////////////////////////////////////////////////
  
  //////////////////////////////////////////////////////////////////////////////
  // Instance Variables
  private String                 covariatonMethod;
  private char[]                 referenceSequence;
  private CovariationMatrix      matrix;
  private Map<Integer, String>   proteinNames;
  private Map<Integer, Integer>  proteinLengths;
  //////////////////////////////////////////////////////////////////////////////

  
  //////////////////////////////////////////////////////////////////////////////
  // Constructor
  public CovariationData(String covariatonMethod, char[] referenceSequence,
      CovariationMatrix matrix) {
    super();
    this.covariatonMethod = covariatonMethod;
    this.referenceSequence = referenceSequence;
    this.matrix = matrix;
    this.proteinLengths = proteinLengths;
    this.proteinNames = proteinNames;
  }
  //////////////////////////////////////////////////////////////////////////////


  //////////////////////////////////////////////////////////////////////////////
  // Public interface
  public String getCovariatonMethod() {
    return covariatonMethod;
  }
  public char[] getReferenceSequence() {
    return referenceSequence;
  }
  public CovariationMatrix getMatrix() {
    return matrix;
  }
  //////////////////////////////////////////////////////////////////////////////
}
