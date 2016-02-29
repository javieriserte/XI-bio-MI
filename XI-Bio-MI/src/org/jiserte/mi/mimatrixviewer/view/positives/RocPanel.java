package org.jiserte.mi.mimatrixviewer.view.positives;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import javax.swing.JButton;
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
  private Observer dataObserver;

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
    this.dataObserver = new Observer() {
      
      @Override
      public void update(Observable o, Object arg) {
        RocPanel.this.updateUI();
      }
    };
    this.data.addObserver(this.dataObserver);
    this.needToRecalculateROC = true;
  }

  protected void paintComponent(Graphics g) {
    super.paintComponent(g);

    if (!this.data.hasPositives()) {
      return;
    }

    this.calculateRoc();
    
    System.out.println("calculates ROC" + Math.random());

    // /////////////////////////////////////////////////////////////////////////
    Graphics2D g2d = (Graphics2D) g;
    
    g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
        RenderingHints.VALUE_RENDER_QUALITY);
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON);
    g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING,
        RenderingHints.VALUE_COLOR_RENDER_QUALITY);
    g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
        RenderingHints.VALUE_FRACTIONALMETRICS_ON);
    g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_OFF);
    g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
        RenderingHints.VALUE_RENDER_SPEED);
    g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING,
        RenderingHints.VALUE_COLOR_RENDER_SPEED);

    int width = this.getWidth();
    int height = this.getHeight();
    int centerX = width / 2;
    int centerY = height / 2;
    int plotWidth = (int) (0.9 * width);
    int plotHeight = (int) (0.9 * height);
    // Draw background
    g2d.setColor(Color.white);
    g2d.fillRect(0, 0, width, height);
    // Draw Plot borders
    g2d.setColor(new Color(160, 160, 240));
    GeneralPath border = new GeneralPath(
        new Rectangle(0, 0, plotWidth, plotHeight));
    for (int i = 1; i < 10; i++) {
      border.moveTo(0, i * plotHeight / 10);
      border.lineTo(plotWidth, i * plotHeight / 10);
      border.moveTo(i * plotWidth / 10, 0);
      border.lineTo(i * plotWidth / 10, plotHeight);
    }
    AffineTransform centerBorder = new AffineTransform();
    centerBorder.translate(centerX - plotWidth / 2, centerY - plotHeight / 2);
    border.transform(centerBorder);

    g2d.draw(border);
    
    int fontSize =  Math.round( (float) ((height-plotHeight) * 0.6 /2) );
    Font font = new Font("Arial", 0,  fontSize );
    g2d.setFont(font);
    g2d.setColor(Color.black);
    for (int i = 0; i<=10; i++) {
      String label = String.format("%.2f", (float)(i*0.1));

      int advance = g2d.getFontMetrics().stringWidth(label);
      AffineTransform labelTr = new AffineTransform();
      labelTr.translate(i * plotWidth /10 -advance/2,  plotHeight + 1.2 * font.getSize());
      
      g2d.drawString(label, centerX - plotWidth / 2 + i * plotWidth /10 -advance/2 ,
          (int) (centerY - plotHeight / 2 + plotHeight + 1.1 * font.getSize()));
      
      g2d.drawString(label, (int) (centerX - plotWidth / 2 - advance - 0.1*font.getSize()),
          (int) (centerY - plotHeight / 2 + plotHeight * (10-i) /10 + fontSize / 2 ));
    }

    // draw Roc

    GeneralPath curve = new GeneralPath();

    boolean firstPoint = true;
    for (Point2D.Double currentPoint : this.rocCurve) {
      if (firstPoint) {
        curve.moveTo(currentPoint.getX() * plotWidth,
            currentPoint.getY() * plotHeight);
      } else {
        curve.lineTo(currentPoint.getX() * plotWidth,
            currentPoint.getY() * plotHeight);
      }
      firstPoint = false;
    }
    GeneralPath curveBg = (GeneralPath) curve.clone();
    curveBg.lineTo(plotWidth, 0);
    curveBg.closePath();

    AffineTransform scaleTr = new AffineTransform();
    scaleTr.translate(0, plotHeight);
    scaleTr.scale(1, -1);
    AffineTransform centerTr = new AffineTransform();
    centerTr.translate(centerX - plotWidth / 2, centerY - plotHeight / 2);
    curve.transform(scaleTr);
    curve.transform(centerTr);
    
    curveBg.transform(scaleTr);
    curveBg.transform(centerTr);

    g2d.setColor(new Color(180, 180, 180,220));
    g2d.fill(curveBg);
    
    g2d.setColor(new Color(100, 100, 100));
    g2d.setStroke(new BasicStroke(1));
    g2d.draw(curve);
    // /////////////////////////////////////////////////////////////////////////

  }

  // ///////////////////////////////////////////////////////////////////////////

  // ///////////////////////////////////////////////////////////////////////////
  // Private Methods
  private void calculateRoc() {
    if (this.needToRecalculateROC) {

      // ///////////////////////////////////////////////////////////////////////
      // Get covariation data and sort them
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

      Arrays.sort(triplets, CovariationTriplet.getComparator(true));

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
          roc.add(
              new Point2D.Double((double) negatives / (double) totalNegatives,
                  (double) positives / (double) totalPositives));
          last_positive = true;
          last_negative = false;
        }

        if ((!isPositive) && (!last_negative)) {
          roc.add(
              new Point2D.Double((double) negatives / (double) totalNegatives,
                  (double) positives / (double) totalPositives));
          last_negative = true;
          last_positive = false;
        }

        positives += isPositive ? 1 : 0;
        negatives += isPositive ? 0 : 1;

      }
      roc.add(new Point2D.Double(1d, 1d));
      this.rocCurve = roc;
      // Calculate the Area under the curve;
      double auc = 0;
      if (this.rocCurve.size() > 0) {
        for (int i = 1; i < this.rocCurve.size(); i++) {
          double deltaX = this.rocCurve.get(i).x - this.rocCurve.get(i - 1).x;
          double avgY = (this.rocCurve.get(i).y + this.rocCurve.get(i - 1).y)
              / 2;
          auc = auc + deltaX * avgY;
        }
      }
      this.auc = auc;

      // ///////////////////////////////////////////////////////////////////////
      this.needToRecalculateROC = false;
    }
  }
  // ///////////////////////////////////////////////////////////////////////////
}
