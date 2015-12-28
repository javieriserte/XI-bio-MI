package org.jiserte.mi.mimatrixviewer.readers.mistic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jiserte.mi.mimatrixviewer.datastructures.CovariationData;
import org.jiserte.mi.mimatrixviewer.datastructures.CovariationMatrix;
import org.jiserte.mi.mimatrixviewer.readers.IncorrectFormatException;
import org.jiserte.mi.mimatrixviewer.readers.MethodReader;

public class MisticReader implements MethodReader {
  public static final String METHOD_ID = "MI";

  @Override
  public CovariationData read(File file) throws FileNotFoundException,
      IncorrectFormatException {

    BufferedReader br = new BufferedReader(new FileReader(file));
    MisticLineParser parser = new MisticLineParser();
    List<MisticLineData> datum = new ArrayList<MisticLineData>();

    // //////////////////////////////////////////////////////////////////////////
    // Read data from file
    String currentLine = null;
    try {
      while ((currentLine = br.readLine()) != null) {
        MisticLineData data = parser.readLine(currentLine);
        if (!data.isEmpty()) {
          datum.add(data);
        }
      }
      br.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    // /////////////////////////////////////////////////////////////////////////

    // /////////////////////////////////////////////////////////////////////////
    // Fill the matrix
    int matrixSize = (int) (Math.sqrt(datum.size() * 8 + 1) + 1) / 2;
    CovariationMatrix matrix = new CovariationMatrix(matrixSize);

    for (MisticLineData data : datum) {
      if (data.getValue()>-900) {
        matrix.setValue(data.getP1(), data.getP2(), data.getValue());
      } else {
        matrix.setValue(data.getP1(), data.getP2(), Double.MIN_VALUE);
      }
    }
    // /////////////////////////////////////////////////////////////////////////

    // /////////////////////////////////////////////////////////////////////////
    // Get the sequence reference
    char[] refseq = new char[matrixSize];
    Arrays.fill(refseq, (char) 0);
    int remainder = matrixSize;
    int index = 0;
    while (remainder > 0 || index < datum.size()) {
      char c1 = datum.get(index).getC1();
      char c2 = datum.get(index).getC2();
      int p1 = datum.get(index).getP1();
      int p2 = datum.get(index).getP2();
      if (refseq[p1 - 1] == (char) 0) {
        refseq[p1 - 1] = c1;
        remainder--;
      }
      if (refseq[p2 - 1] == (char) 0) {
        refseq[p2 - 1] = c2;
        remainder--;
      }
      index++;
    }
    // /////////////////////////////////////////////////////////////////////////

    // /////////////////////////////////////////////////////////////////////////
    // Build the covariation data object to return
    CovariationData covData = new CovariationData(METHOD_ID, refseq, matrix);
    covData.setTitle(file.getName());
    return covData;
    // /////////////////////////////////////////////////////////////////////////
  }

}
