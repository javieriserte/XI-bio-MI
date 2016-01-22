package org.jiserte.mi.mimatrixviewer.datastructures;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Track {
  //////////////////////////////////////////////////////////////////////////////
  // Instance variables
  private String description;
  private double[] data;
  //////////////////////////////////////////////////////////////////////////////

  //////////////////////////////////////////////////////////////////////////////
  // Class methods
  public static Track fromFile(File file, String description) throws IOException{
    List<Double> value = new ArrayList<>();
    BufferedReader br = new BufferedReader(new FileReader(file));
    String cl = null;
    while ( (cl = br.readLine()) != null) {
      value.add( Double.valueOf(cl));
    }
    br.close();
    double[] d = new double[value.size()];
    for (int i = 0; i<value.size(); i++) {
      d[i] = value.get(i);
    }
    return new Track(description, d);
  }
  //////////////////////////////////////////////////////////////////////////////
  
  //////////////////////////////////////////////////////////////////////////////
  // Constructor
  public Track(String description, double[] data) {
    super();
    this.description = description;
    this.data = data;
  }
  //////////////////////////////////////////////////////////////////////////////

  //////////////////////////////////////////////////////////////////////////////
  // Public interface
  public String getDescription() {
    return description;
  }
  public void setDescription(String description) {
    this.description = description;
  }
  public double[] getData() {
    return data;
  }
  public int getSize() {
    return this.getData().length;
  }
  //////////////////////////////////////////////////////////////////////////////

}
