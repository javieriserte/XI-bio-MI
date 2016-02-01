package org.jiserte.mi.mimatrixviewer.view.positives;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.swing.JPanel;

import org.jiserte.mi.mimatrixviewer.datastructures.CovariationData;
import org.jiserte.mi.mimatrixviewer.datastructures.CovariationTriplet;

import pair.Pair;

public class RocPanel extends JPanel {

  // ///////////////////////////////////////////////////////////////////////////
  // Class constants
  private static final long serialVersionUID = 7674131727277419788L;
  // ///////////////////////////////////////////////////////////////////////////

  // ///////////////////////////////////////////////////////////////////////////
  // Instance variables
  private List<Point2D.Double> rocCurve;
  private double auc;
  private CovariationData data;
  private boolean needToRecalculateROC;

  // ///////////////////////////////////////////////////////////////////////////

  // ///////////////////////////////////////////////////////////////////////////
  // Constructor
  public RocPanel() {
    super();
    this.rocCurve = new ArrayList<>();
    this.auc = 0;
    this.needToRecalculateROC = true;
  }

  // ///////////////////////////////////////////////////////////////////////////

  // ///////////////////////////////////////////////////////////////////////////
  // Public Interface
  public void setData(CovariationData data) {
    this.data = data;
    this.needToRecalculateROC = true;
    System.out.println("Dentro del setData");
  }

  protected void paintComponent(Graphics g) {
    super.paintComponent(g);

    if (!this.data.hasPositives()) {
      return;
    }
    
    this.calculateRoc();

    // /////////////////////////////////////////////////////////////////////////
    // draw Roc
    Graphics2D g2d = (Graphics2D) g;
    int width = this.getWidth();
    int height = this.getHeight();
    int centerX = width / 2;
    int centerY = height / 2;
    int plotWidth = (int) (0.9 * width);
    int plotHeight = (int) (0.9 * height);

    g2d.setColor(Color.gray);
    g2d.setStroke(new BasicStroke(2));
    
    GeneralPath curve = new GeneralPath();
    System.out.println("Cantidad de puntos:" + this.rocCurve.size());
    boolean firstPoint = true;
    for (Point2D.Double currentPoint : this.rocCurve) {
      if (firstPoint) {
        curve.moveTo(currentPoint.getX() * plotWidth, currentPoint.getY()
            * plotHeight);
      } else {
        curve.lineTo(currentPoint.getX() * plotWidth, currentPoint.getY()
            * plotHeight);
      }
      firstPoint = false;
      System.out.println(currentPoint);
    }

    AffineTransform scaleTr = new AffineTransform();
    scaleTr.translate(1, -1);
    AffineTransform centerTr = new AffineTransform();
    centerTr.translate(centerX, centerY);
    curve.transform(scaleTr);
    curve.transform(centerTr);
    g2d.draw(curve);
    // /////////////////////////////////////////////////////////////////////////

  }

  // ///////////////////////////////////////////////////////////////////////////

  // ///////////////////////////////////////////////////////////////////////////
  // Private Methods
  private void calculateRoc() {
    System.out.println("Need To recalculate Roc:");
    if (this.needToRecalculateROC) {
    System.out.println("Yes");
      // ///////////////////////////////////////////////////////////////////////
      // Get covariation data and sort them
      CovariationTriplet[] triplets = new CovariationTriplet[this.data
          .getNumberOfElements()];

      int tripletCounter = 0;
      for (int i = 1; i <= this.data.getMatrixSize(); i++) {
        for (int j = i + 1; j <= this.data.getMatrixSize(); j++) {
          triplets[tripletCounter] = new CovariationTriplet(i, j, this.data
              .getMatrix().getValue(i, j));
          tripletCounter++;
        }
      }

      Arrays.sort(triplets, CovariationTriplet.getComparator(false));
      // Count positives and negatives. And generates the points of the
      // Roc curve
      List<Point2D.Double> roc = new ArrayList<>();
      boolean last_positive = false;
      boolean last_negative = false;
      Set<Pair<Integer, Integer>> positivesData = this.data.getPositives();
      int totalPositives = positivesData.size();
      int totalNegatives = triplets.length - totalPositives;
      int positives = 0;
      int negatives = 0;
      for (CovariationTriplet triplet : triplets) {
        Pair<Integer, Integer> pair = new Pair<>(triplet.getNominalX(),
            triplet.getNominalY());
        boolean isPositive = positivesData.contains(pair);

        if (isPositive && (!last_positive)) {
          roc.add(new Point2D.Double((double) negatives
              / (double) totalNegatives, (double) positives
              / (double) totalPositives));
          last_positive = true;
          last_negative = false;
        }

        if ( (!isPositive) && (!last_negative)) {
          roc.add(new Point2D.Double((double) negatives
              / (double) totalNegatives, (double) positives
              / (double) totalPositives));
          last_negative = true;
          last_positive = false;
        }

        positives += isPositive ? 1 : 0;
        negatives += isPositive ? 0 : 1;

      }
      roc.add(new Point2D.Double(1d, 1d));
      this.rocCurve = roc;
      // Calcaulte the Area under the curve;
      double auc = 0;
      if (this.rocCurve.size() > 0) {
        for (int i = 1; i < this.rocCurve.size(); i++) {
          double deltaX = this.rocCurve.get(i).x - this.rocCurve.get(i - 1).x;
          double deltaY = this.rocCurve.get(i).y - this.rocCurve.get(i - 1).y;
          auc = auc + deltaX * deltaY;
        }
      }
      this.auc = auc;

      // ///////////////////////////////////////////////////////////////////////
      this.needToRecalculateROC = false;
    }
  }
  // ///////////////////////////////////////////////////////////////////////////
}
