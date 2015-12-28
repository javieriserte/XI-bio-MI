package org.jiserte.mi.mimatrixviewer.readers.misticfull;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import org.jiserte.mi.mimatrixviewer.readers.CovariationDataParser;
import org.jiserte.mi.mimatrixviewer.readers.IncorrectFormatException;
import org.jiserte.mi.mimatrixviewer.readers.mistic.MisticLineData;

public class MisticFullLineParser implements CovariationDataParser<MisticLineData> {
  //////////////////////////////////////////////////////////////////////////////
  // Class constants
  /**
   * Pattern to match a comment line in mistic data
   */
  public static final String COMMENT_REGEX = "^\\s*#.+$"; 
  /**
   * Pattern to match the output from Mortem Nielsen soft<br>
   * Sample Data:<br>
   * <pre>
   * MI[ 1 G ][ 6 Q ] = -0.029194 0.003595 0.016705 -1.962794
   * </pre>
   */
  public final static String MN_FORMAT_LINE = "MI\\[ \\d+ [A-Z\\-] \\]\\[ \\d+ [A-Z\\-] \\] = -*\\d+\\.?\\d*[eE]*[-+]*\\d* -*\\d+\\.?\\d*[eE]*[-+]*\\d* -*\\d+\\.?\\d*[eE]*[-+]*\\d* -*\\d+\\.?\\d*[eE]*[-+]*\\d*$";
  //////////////////////////////////////////////////////////////////////////////
  
  @Override
  public MisticLineData readLine(String line) throws IncorrectFormatException {

    Pattern commentPattern = Pattern.compile(COMMENT_REGEX);
    Pattern misticFormatPattern = Pattern.compile(MN_FORMAT_LINE);
    Matcher m = commentPattern.matcher(line);
    if ( m.matches() ) {
      return new MisticLineData(); // This is an empty data line
    }
    m = misticFormatPattern.matcher(line);
    if (m.matches()) {
      String[] data = line.split("\\s+");
      return new MisticLineData(
          Integer.valueOf(data[1]),
          Integer.valueOf(data[4]),
          data[2].charAt(0), 
          data[5].charAt(0),
          Double.valueOf(data[11]));      
    }
    
    throw new IncorrectFormatException();
    
  }

}
