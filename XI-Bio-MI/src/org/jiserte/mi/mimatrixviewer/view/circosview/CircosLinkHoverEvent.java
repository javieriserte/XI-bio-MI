package org.jiserte.mi.mimatrixviewer.view.circosview;

import org.jiserte.mi.mimatrixviewer.datastructures.CovariationTriplet;

public class CircosLinkHoverEvent {
  private CovariationTriplet triplet;

  public CircosLinkHoverEvent(CovariationTriplet triplet) {
    super();
    this.triplet = triplet;
  }

  public CovariationTriplet getTriplet() {
    return triplet;
  }
  

}
