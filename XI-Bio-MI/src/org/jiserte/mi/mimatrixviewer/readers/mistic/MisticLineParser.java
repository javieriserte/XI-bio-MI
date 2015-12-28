package org.jiserte.mi.mimatrixviewer.readers.mistic;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import org.jiserte.mi.mimatrixviewer.readers.CovariationDataParser;
import org.jiserte.mi.mimatrixviewer.readers.IncorrectFormatException;

public class MisticLineParser implements CovariationDataParser<MisticLineData> {
  //////////////////////////////////////////////////////////////////////////////
  // Class constants
  /**
   * Pattern to match a comment line in mistic data
   */
  public static final String COMMENT_REGEX = "^\\s*#.+$"; 
  /**
   *  Pattern to match the output from Mistic<br>
   *  Sample Data:<br>
   *  <pre>
   *  1 M 2 A 12.182070
   *  </pre>
   */
  public final static String MISTIC_FORMAT_LINE = "\\d+\\t[A-Z\\-]\\t\\d+\\t[A-Z\\-]\\t-*\\d+\\.?\\d*[eE]*[-+]*\\d*$";  
  //////////////////////////////////////////////////////////////////////////////
  
  @Override
  public MisticLineData readLine(String line) throws IncorrectFormatException {

    Pattern commentPattern = Pattern.compile(COMMENT_REGEX);
    Pattern misticFormatPattern = Pattern.compile(MISTIC_FORMAT_LINE);
    Matcher m = commentPattern.matcher(line);
    if ( m.matches() ) {
      return new MisticLineData(); // This is an empty data line
    }
    m = misticFormatPattern.matcher(line);
    if (m.matches()) {
      String[] data = line.split("\\s+");
      return new MisticLineData(
          Integer.valueOf(data[0]),
          Integer.valueOf(data[2]),
          data[1].charAt(0), 
          data[3].charAt(0),
          Double.valueOf(data[4]));      
    }
    
    throw new IncorrectFormatException();
    
  }

}
