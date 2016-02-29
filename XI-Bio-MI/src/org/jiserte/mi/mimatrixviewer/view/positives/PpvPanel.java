package org.jiserte.mi.mimatrixviewer.view.positives;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.swing.JPanel;

import org.jiserte.mi.mimatrixviewer.datastructures.CovariationData;
import org.jiserte.mi.mimatrixviewer.datastructures.CovariationTriplet;

import pair.Pair;

public class PpvPanel extends JPanel {

  private List<Point2D.Double> ppvVsScore;
  private double misScore;
  private double maxScore;
  private boolean needToRecalculate;

  private CovariationData data;

  /**
   * 
   */
  private static final long serialVersionUID = -5124327467708956409L;

  @Override
  public void paintComponent(Graphics g) {

    Graphics2D g2d = (Graphics2D) g;

    if (this.needToRecalculate) {
      calculatePpvVsScore();
    }

    int width = this.getWidth();
    int height = this.getHeight();

    g2d.setColor(Color.WHITE);
    g2d.fillRect(0, 0, width, height);

    double plotWidth = 0.7 * width;
    double plotHeight = 0.9 * height;

    // /////////////////////////////////////////////////////////////////////////
    // Dibujar los bordes del plot
    
    g2d.setStroke(new BasicStroke(1));
    g2d.setColor(Color.blue);
    g2d.drawRect(0, 0, (int)plotWidth, (int)plotHeight);
    
    // /////////////////////////////////////////////////////////////////////////
    
    for (Point2D.Double p : this.ppvVsScore) {
      
    }
    

  }

  public void setData(CovariationData data) {

    this.data = data;

    this.needToRecalculate = true;

  }

  private void calculatePpvVsScore() {

    if (needToRecalculate && this.data != null && this.data.hasPositives()) {

      int window = 100;

      CovariationTriplet[] triplets = new CovariationTriplet[this.data
          .getNumberOfElements()];

      int tripletCounter = 0;
      for (int i = 1; i <= this.data.getMatrixSize(); i++) {
        for (int j = i + 1; j <= this.data.getMatrixSize(); j++) {
          triplets[tripletCounter] = new CovariationTriplet(i, j,
              this.data.getMatrix().getValue(i, j));
          tripletCounter++;
        }
      }
      
      this.data.getMatrix().getMin();

      Arrays.sort(triplets, CovariationTriplet.getComparator(true));

      List<Point2D.Double> ppvVsScore = new ArrayList<>();

      Point2D.Double firstPoint = new Point2D.Double(0, 0);

      ppvVsScore.add(firstPoint);

      Set<Pair<Integer, Integer>> positives = this.data.getPositives();

      for (int i = 0; i < window; i++) {
        firstPoint.x += triplets[0].getValue();
        Pair<Integer, Integer> pair = new Pair<>(triplets[i].getNominalX(),
            triplets[i].getNominalY());
        firstPoint.y += positives.contains(pair) ? 1 : 0;
      }

      Point2D.Double currentPoint = new Point2D.Double(firstPoint.x,
          firstPoint.y);

      for (int i = window; i < triplets.length; i++) {
        Pair<Integer, Integer> pairBegin = new Pair<>(
            triplets[i - window].getNominalX(),
            triplets[i - window].getNominalY());

        Pair<Integer, Integer> pairNew = new Pair<>(triplets[i].getNominalX(),
            triplets[i].getNominalY());

        currentPoint = new Point2D.Double(
            currentPoint.x - triplets[i - window].getValue()
                + triplets[i].getValue(),
            currentPoint.y - (positives.contains(pairBegin) ? 1 : 0)
                + (positives.contains(pairNew) ? 1 : 0));

        ppvVsScore.add(currentPoint);
      }

      this.ppvVsScore = ppvVsScore;

    }

  }

}
