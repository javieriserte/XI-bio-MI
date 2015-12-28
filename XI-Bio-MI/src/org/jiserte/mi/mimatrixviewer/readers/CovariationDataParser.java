package org.jiserte.mi.mimatrixviewer.readers;

public interface CovariationDataParser<T> {
  public T readLine(String line) throws IncorrectFormatException;
}
