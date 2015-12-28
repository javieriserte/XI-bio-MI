package org.jiserte.mi.mimatrixviewer.readers;

import java.io.File;
import java.io.FileNotFoundException;

import org.jiserte.mi.mimatrixviewer.datastructures.CovariationData;

public interface MethodReader {

  public CovariationData read(File file) throws FileNotFoundException, IncorrectFormatException;
  
}
