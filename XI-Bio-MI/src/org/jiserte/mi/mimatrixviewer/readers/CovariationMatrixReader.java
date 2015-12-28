package org.jiserte.mi.mimatrixviewer.readers;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.jiserte.mi.mimatrixviewer.datastructures.CovariationData;
import org.jiserte.mi.mimatrixviewer.readers.mistic.MisticReader;
import org.jiserte.mi.mimatrixviewer.readers.misticfull.MisticFullReader;

/**
 * A generic reader of covariation data
 * @author javier
 *
 */
public class CovariationMatrixReader {
  // ///////////////////////////////////////////////////////////////////////////
  // Instance variables
  private List<MethodReader> methodReaders;
  // ///////////////////////////////////////////////////////////////////////////

  // ///////////////////////////////////////////////////////////////////////////
  // Constructor
  public CovariationMatrixReader() {
    super();
    this.methodReaders = new ArrayList<MethodReader>();
    this.methodReaders.add(new MisticReader());
    this.methodReaders.add(new MisticFullReader());
  }
  // ///////////////////////////////////////////////////////////////////////////
  
  // ///////////////////////////////////////////////////////////////////////////
  // Public Interface
  public CovariationData read(File file) throws FileNotFoundException {

    CovariationData data = null;
    for (MethodReader reader : this.methodReaders) {
      boolean success = true;
      try {
        data = reader.read(file);
      } catch (IncorrectFormatException e) {
        success = false;
      }
      if (success) {
        return data;
      }
    }
    return null;
  }
  // ///////////////////////////////////////////////////////////////////////////


  
}
