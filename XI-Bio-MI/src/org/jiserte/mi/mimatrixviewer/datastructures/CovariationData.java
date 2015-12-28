package org.jiserte.mi.mimatrixviewer.datastructures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CovariationData {

  // ///////////////////////////////////////////////////////////////////////////
  // Class Constants
  public static final double UNDEFINED = Double.NEGATIVE_INFINITY;
  // ///////////////////////////////////////////////////////////////////////////

  // ///////////////////////////////////////////////////////////////////////////
  // Instance Variables
  private List<String> covariatonMethod;
  private char[] referenceSequence;
  private List<CovariationMatrix> matrices;
  private Map<Integer, String> proteinNames;
  private Map<Integer, Integer> proteinLengths;
  private int selectedMethodIndex;
  private int numberOfElements;
  private String title;

  // ///////////////////////////////////////////////////////////////////////////

  // ///////////////////////////////////////////////////////////////////////////
  // Constructor
  public CovariationData(String covariatonMethod, char[] referenceSequence,
      CovariationMatrix matrix) {
    super();
    this.covariatonMethod = new ArrayList<String>();
    this.covariatonMethod.add(covariatonMethod);
    this.referenceSequence = referenceSequence;
    this.matrices = new ArrayList<>();
    this.matrices.add(matrix);
    this.numberOfElements = matrix.getSize() * (matrix.getSize() - 1 ) /2;
    this.proteinLengths = new HashMap<>();
    this.proteinNames = new HashMap<>();
    this.selectedMethodIndex = 0;
    this.setTitle("");
  }

  // ///////////////////////////////////////////////////////////////////////////

  // ///////////////////////////////////////////////////////////////////////////
  // Public interface
  public void addMatrix(String method, CovariationMatrix matrix) {
    this.matrices.add(matrix);
    this.covariatonMethod.add(method);
  }

  public String getCovariatonMethod() {
    return covariatonMethod.get(this.selectedMethodIndex);
  }

  public char[] getReferenceSequence() {
    return referenceSequence;
  }

  public CovariationMatrix getMatrix() {
    return matrices.get(this.selectedMethodIndex);
  }

  public String getProteinName(int index) {
    return this.proteinNames.get(index);
  }

  public void setProteinName(int index, String name) {
    this.proteinNames.put(index, name);
  }
  
  public void setProteinNames(String[] names) {
    int counter=1;
    for (String name : names) {
      this.proteinNames.put(counter, name);
      counter++;
    }
  }

  public int getProteinLength(int index) {
    return this.proteinLengths.get(index);
  }

  public void setProteinLength(int index, int length) {
    this.proteinLengths.put(index, length);
  }
  
  public void setProteinLengths(int[] lengths) {
    int counter=1;
    for (int len : lengths) {
      this.proteinLengths.put(counter, len);
      counter++;
    }
  }

  public int setSelectedIndex(int newMethodIndex) {
    if (newMethodIndex >= 0 && newMethodIndex < this.matrices.size()) {
      this.selectedMethodIndex = newMethodIndex;
    }
    return this.selectedMethodIndex;
  }

  public int getMatrixSize() {
    if (!matrices.isEmpty()) {
      return this.matrices.get(this.selectedMethodIndex).getSize();
    } else {
      return 0;
    }
  }
  
  public int getNumberOfElements() {
    return this.numberOfElements;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }
  
  public int getNumberOfProteins() {
    return this.proteinLengths.size();
  }

  // ///////////////////////////////////////////////////////////////////////////

}
