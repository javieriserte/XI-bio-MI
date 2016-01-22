package org.jiserte.mi.mimatrixviewer.datastructures;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import collections.rangemap.RangeMap;
import datatypes.range.Range;
import pair.Pair;

public class CovariationData extends Observable{

  // ///////////////////////////////////////////////////////////////////////////
  // Class Constants
  public static final double UNDEFINED = Double.NEGATIVE_INFINITY;
  public static final int TRACKS_CHANGED = 1;
  public static final int PROTEIN_LENGTHS_CHANGED = 2;
  public static final int MSA_CHANGED = 2;  
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
  private List<Track> tracks;
  private List<Pair<Integer, Integer>> positives;
  private String title;
  private RangeMap<Integer, Integer> proteinMap;
  private List<Pair<String,String>> msa;
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
    Range<Integer> range = new Range<Integer>(1, matrix.getSize(), true, true);
    this.proteinMap = new RangeMap<>();
    this.proteinMap.put(range, 1);
    this.selectedMethodIndex = 0;
    this.tracks = new ArrayList<>();
    this.setTitle("");
    this.positives = new ArrayList<>();
    this.msa = new ArrayList<>();
      
  }
  // ///////////////////////////////////////////////////////////////////////////

  // ///////////////////////////////////////////////////////////////////////////
  // Public interface
  public int getProteinNumberFromNominalPosition (int nominalPosition) {
    if (nominalPosition >0 && nominalPosition <= this.getMatrixSize()) {
      return this.proteinMap.get(nominalPosition);
    }
    return 0;
  }
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
    this.setChanged();
    this.notifyObservers(PROTEIN_LENGTHS_CHANGED);
  }
  
  public void setProteinLengths(int[] lengths) {
    int counter=1;
    for (int len : lengths) {
      this.proteinLengths.put(counter, len);
      counter++;
    }
    this.setChanged();
    this.notifyObservers(PROTEIN_LENGTHS_CHANGED);
  }
  
  public void updateProteinMap() {
    List<Integer> proteinIds = new ArrayList<>();
    proteinIds.addAll(proteinLengths.keySet());
    Collections.sort(proteinIds);
    this.proteinMap = new RangeMap<>();
    int sum = 0;
    for (int i: proteinIds) {
      Range<Integer> currentRange = new Range<Integer>(sum+1, 
          sum + this.getProteinLength(i), true, true);
      sum=sum + this.getProteinLength(i);
      this.proteinMap.put(currentRange, i);
    }
  }
  
  public boolean isIntraProteinPair(int nominalX, int nominalY ) {
    return this.proteinMap.get(nominalX) == this.proteinMap.get(nominalY);
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
  
  public void addTrack(Track track){
    if (track.getSize() == this.getMatrixSize()) {
      this.tracks.add(track);
      this.setChanged();
      this.notifyObservers(TRACKS_CHANGED);
    }
  }  
  public void removeTrack(int index) {
    // TODO add code to remove track
  }

  public int getTrackCount() {
    return this.tracks.size();
  }
  
  public Track getTrack(int index) {
    return this.tracks.get(index);
  }
  
  public void addMsa(List<Pair<String,String>> msa){
    this.msa = msa;
    this.setChanged();
    this.notifyObservers(MSA_CHANGED);
  }
  // ///////////////////////////////////////////////////////////////////////////

}
