package org.jiserte.mi.mimatrixviewer.circosview;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.JPanel;
import javax.swing.JScrollBar;

import org.jiserte.mi.mimatrixviewer.datastructures.CovariationData;
import org.jiserte.mi.mimatrixviewer.datastructures.CovariationMatrix;
import org.jiserte.mi.mimatrixviewer.datastructures.CovariationTriplet;
import org.jiserte.mi.mimatrixviewer.datastructures.Track;
import org.jiserte.mi.mimatrixviewer.matrixview_new.colors.ColorMapper;
import org.jiserte.mi.mimatrixviewer.matrixview_new.colors.ColoringStrategy;
import org.jiserte.mi.mimatrixviewer.matrixview_new.colors.GradientColoringStrategy;

import datatypes.range.Range;

public class CircosImagePanel extends JPanel {

  /**
   * 
   */
  private static final long serialVersionUID = 4241249433569225898L;
  //////////////////////////////////////////////////////////////////////////////
  // Instance variables
  private ZoomAndPanCircos zoom;
  private CovariationData data;
  private CovariationTriplet[] sortedData;
  private ColorMapper proteinColorMapper;
  private List<ColorMapper> trackColorMappers;
  private Observer dataObserver;
  private Observer zoomObserver;
  private boolean showIntraProteinLinks;
  private boolean showInterProteinLinks;
  private boolean requireFilterData;
  private int maxLinksToShow;
  private BufferedImage circosMap;
  private Map<Integer, CovariationTriplet> tripletsMap;
  private List<CovariationTriplet> highlighted;
  // Listeners
  private List<CircosLinkHoverListener> circosLinkHoverListener;
  // Components
  private JPanel image;
  private JScrollBar hScroolBar;
  private JScrollBar vScroolBar;
  //////////////////////////////////////////////////////////////////////////////

  //////////////////////////////////////////////////////////////////////////////
  // Constructor
  public CircosImagePanel() {
    ColoringStrategy c1 = new GradientColoringStrategy(
        new Range<Double>(0d, 7d, true, true), Color.RED, Color.MAGENTA, true);
    ArrayList<ColoringStrategy> l = new ArrayList<>();
    l.add(c1);
    this.proteinColorMapper = new ColorMapper(l);
    this.trackColorMappers = new ArrayList<>();

    this.zoom = new ZoomAndPanCircos(1);

    this.zoomObserver = new ZoomFactorObserver();
    this.createGUI();
    this.zoom.addObserver(zoomObserver);
    this.setMaxLinksToShow(50);
    this.showInterProteinLinks = true;
    this.showIntraProteinLinks = true;
    this.tripletsMap = new HashMap<>();
    this.highlighted = new ArrayList<>();
    this.circosLinkHoverListener = new ArrayList<>();
  }
  //////////////////////////////////////////////////////////////////////////////

  //////////////////////////////////////////////////////////////////////////////
  // Public interface
  public void addCircosLinkHoverListener(CircosLinkHoverListener l) {
    this.circosLinkHoverListener.add(l);
  }

  public double getOffSetH() {
    return this.zoom.getOffSetH();
  }

  public void setOffSetH(double offSetH) {
    this.zoom.setOffSetH(offSetH);
  }

  public double getOffSetV() {
    return this.getOffSetV();
  }

  public void setOffSetV(double offSetV) {
    this.zoom.setOffSetV(offSetV);
  }

  public void setOffSets(double offsetH, double offsetV) {
    this.zoom.setOffSetH(offsetH);
    this.zoom.setOffSetV(offsetV);
  }

  public double getZoomFactor() {
    return this.zoom.getValue();
  }

  public void setZoomFactor(double zoomFactor) {
    this.zoom.setValue(zoomFactor);
    this.updateUI();
  }

  public List<CovariationTriplet> getHighlighted() {
    return highlighted;
  }

  public void setHighlighted(List<CovariationTriplet> highlighted) {
    this.highlighted = highlighted;
    updateUI();
  }

  public void setData(CovariationData data) {
    this.data = data;

    this.createTrackColorMappers();

    this.dataObserver = new Observer() {
      @Override
      public void update(Observable o, Object arg) {
        int msg = (Integer) arg;
        if (msg == CovariationData.TRACKS_CHANGED) {
          createTrackColorMappers();
          CircosImagePanel.this.updateUI();
        }
        if (msg == CovariationData.PROTEIN_LENGTHS_CHANGED) {
          CircosImagePanel.this.updateUI();
        }
      }
    };

    this.data.addObserver(this.dataObserver);

    this.requireFilterData = true;
  }

  public boolean isShowIntraProteinLinks() {
    return showIntraProteinLinks;
  }

  public void setShowIntraProteinLinks(boolean showIntraProteinLinks) {
    this.showIntraProteinLinks = showIntraProteinLinks;
  }

  public boolean isShowInterProteinLinks() {
    return showInterProteinLinks;
  }

  public void setShowInterProteinLinks(boolean showInterProteinLinks) {
    this.showInterProteinLinks = showInterProteinLinks;
  }

  public int getMaxLinksToShow() {
    return maxLinksToShow;
  }

  public void setMaxLinksToShow(int maxLinksToShow) {
    this.maxLinksToShow = maxLinksToShow;
    this.requireFilterData = true;
    this.updateUI();
  }

  public boolean containsData() {
    return this.data != null;
  }
  //////////////////////////////////////////////////////////////////////////////

  //////////////////////////////////////////////////////////////////////////////
  // Private & protected methods
  private void createGUI() {
    this.setLayout(new BorderLayout());
    this.image = new CircosImage();
    HVScrollsAdjListener hvScrollsAdjListener = new HVScrollsAdjListener();
    this.hScroolBar = new JScrollBar(JScrollBar.HORIZONTAL, 0, 1, 0, 1);
    this.hScroolBar.addAdjustmentListener(hvScrollsAdjListener);
    this.vScroolBar = new JScrollBar(JScrollBar.VERTICAL, 0, 1, 0, 1);
    this.vScroolBar.addAdjustmentListener(hvScrollsAdjListener);
    this.add(this.hScroolBar, BorderLayout.SOUTH);
    this.add(this.vScroolBar, BorderLayout.EAST);
    this.add(this.image, BorderLayout.CENTER);

    this.image.addMouseMotionListener(new MouseMotionListener() {

      @Override
      public void mouseMoved(MouseEvent e) {
        System.out.println();
        if (circosMap != null) {
          int rgb = circosMap.getRGB(e.getX(), e.getY());

          CovariationTriplet t = tripletsMap.get(rgb);
          if (t != null) {
            List<CovariationTriplet> h = new ArrayList<CovariationTriplet>();
            h.add(t);
            CircosImagePanel.this.setHighlighted(h);
            CircosImagePanel.this.notifyListeners(t);
          }

        }
      }

      @Override
      public void mouseDragged(MouseEvent e) {

      }
    });
  }

  private void filterData() {
    if (this.requireFilterData) {
      CovariationMatrix matrix = this.data.getMatrix();

      SortedSet<CovariationTriplet> filtered = new TreeSet<>(
          CovariationTriplet.getComparator(true));

      for (int x = 1; x <= this.data.getMatrixSize(); x++) {
        for (int y = x + 1; y <= this.data.getMatrixSize(); y++) {
          if ((this.data.isIntraProteinPair(x, y) && this.showIntraProteinLinks)
              || (!this.data.isIntraProteinPair(x, y)
                  && this.showInterProteinLinks)) {
            double value = matrix.getValue(x, y);
            CovariationTriplet currentTriplet = new CovariationTriplet(x, y,
                value);
            filtered.add(currentTriplet);
            while (filtered.size() > this.maxLinksToShow) {
              filtered.remove(filtered.last());
            }
          }
        }
      }
      CovariationTriplet[] triplets = new CovariationTriplet[filtered.size()];

      filtered.toArray(triplets);

      this.sortedData = triplets;
      this.requireFilterData = false;
    }
  }

  private void createTrackColorMappers() {
    this.trackColorMappers.clear();
    for (int i = 0; i < data.getTrackCount(); i++) {
      Track currentTrack = data.getTrack(i);

      double max = Double.MIN_VALUE;
      double min = Double.MAX_VALUE;
      for (double d : currentTrack.getData()) {
        max = Math.max(max, d);
        min = Math.min(min, d);
      }
      ColoringStrategy c = new GradientColoringStrategy(
          new Range<Double>(min, max, true, true), Color.red, Color.blue,
          false);
      ArrayList<ColoringStrategy> l = new ArrayList<>();
      l.add(c);
      trackColorMappers.add(new ColorMapper(l));

    }
  }

  private void notifyListeners(CovariationTriplet t) {
    for (CircosLinkHoverListener l : this.circosLinkHoverListener) {
      l.circosHover(new CircosLinkHoverEvent(t));
    }
  }
  //////////////////////////////////////////////////////////////////////////////

  //////////////////////////////////////////////////////////////////////////////
  // Static Methods
  private static List<Point2D> createProteinBandPath(float spacerArc,
      float elementArc, float outRadius, float innerRadius, int length) {
    List<Point2D> proteinBand = new ArrayList<>();
    for (int j = 0; j < length; j++) {
      proteinBand.add(CircosImagePanel
          .getPointAtDegreeAngle(elementArc * j + spacerArc * j, outRadius));
      proteinBand.add(CircosImagePanel.getPointAtDegreeAngle(
          elementArc * (j + 1) + spacerArc * j, outRadius));
    }
    for (int j = length - 1; j >= 0; j--) {
      proteinBand.add(CircosImagePanel.getPointAtDegreeAngle(
          elementArc * (j + 1) + spacerArc * j, innerRadius));
      proteinBand.add(CircosImagePanel
          .getPointAtDegreeAngle(elementArc * j + spacerArc * j, innerRadius));
    }
    return proteinBand;
  }

  private static GeneralPath createClosedPath(List<Point2D> proteinBand) {

    GeneralPath path = new GeneralPath();
    if (!proteinBand.isEmpty()) {
      Point2D firstPoint = proteinBand.get(0);
      proteinBand.remove(0);
      path.moveTo(firstPoint.getX(), firstPoint.getY());
      for (Point2D point : proteinBand) {
        path.lineTo(point.getX(), point.getY());
      }
      path.closePath();
    }
    return path;

  }

  private static Point2D getPointAtDegreeAngle(float elementArc,
      float hipotenuse) {
    return new Point2D.Float(
        hipotenuse * (float) Math.sin((elementArc) * Math.PI / 180),
        -hipotenuse * (float) Math.cos((elementArc) * Math.PI / 180));
  }
  //////////////////////////////////////////////////////////////////////////////

  //////////////////////////////////////////////////////////////////////////////
  // Auxiliary classes
  private class HVScrollsAdjListener implements AdjustmentListener {
    @Override
    public void adjustmentValueChanged(AdjustmentEvent e) {
      int hv = hScroolBar.getValue();
      int vv = vScroolBar.getValue();
      int hmax = hScroolBar.getMaximum();
      int vmax = vScroolBar.getMaximum();
      zoom.setOffSetH((double) hv / (double) hmax);
      zoom.setOffSetV((double) vv / (double) vmax);
    }
  }

  private class CircosImage extends JPanel {
    private static final long serialVersionUID = 1L;

    @Override
    protected void paintComponent(Graphics g) {
      // b:\javier\dropbox\mi_data2
      super.paintComponent(g);
      if (CircosImagePanel.this.containsData()) {

        CircosImagePanel.this.filterData();

        BufferedImage bi = createCircosGraphic();
        ((Graphics2D) g).drawImage(bi, 0, 0, null);

        ////////////////////////////////////////////////////////////////////////
      }
    }

    private BufferedImage createCircosGraphic() {

      ////////////////////////////////////////////////////////////////////////
      // Create the bufferedImage object
      BufferedImage bi = new BufferedImage(this.getWidth(), this.getHeight(),
          BufferedImage.TYPE_INT_RGB);
      Graphics g = bi.getGraphics();
      BufferedImage bim = new BufferedImage(this.getWidth(), this.getHeight(),
          BufferedImage.TYPE_INT_ARGB);
      Graphics gm = bim.getGraphics();
      ////////////////////////////////////////////////////////////////////////

      ////////////////////////////////////////////////////////////////////////
      // Get the data
      CovariationData covData = CircosImagePanel.this.data;
      ZoomAndPanCircos zoomAndPan = CircosImagePanel.this.zoom;
      int matrixSize = covData.getMatrixSize();
      ////////////////////////////////////////////////////////////////////////

      ////////////////////////////////////////////////////////////////////////
      // Define graphical variables
      int height = this.getHeight();
      int width = this.getWidth();
      double offSetH = zoomAndPan.getOffSetH() * width;
      double offSetV = zoomAndPan.getOffSetV() * height;
      float spacerArcTotal = 120;
      float spacerArc = spacerArcTotal / matrixSize;
      float elementArc = (360 - spacerArcTotal) / matrixSize;
      float centerX = width / 2;
      float centerY = height / 2;
      float maxRadius = Math.min(centerX, centerY);
      Graphics2D graphics2d = (Graphics2D) g;
      Graphics2D graphics2dm = (Graphics2D) gm;
      float lastRadio = 0.98f;
      ////////////////////////////////////////////////////////////////////////

      ////////////////////////////////////////////////////////////////////////
      // Set scale transformations
      AffineTransform scale = new AffineTransform();
      scale.scale(zoomAndPan.getValue(), zoomAndPan.getValue());
      // Set translate offset transformation
      AffineTransform offsetTr = new AffineTransform();
      offsetTr.translate(-offSetH, -offSetV);
      // Set translate to center transformation
      AffineTransform centerTr = new AffineTransform();
      centerTr.translate(centerX, centerY);
      // Set all together transformation
      AffineTransform cenScaPanTr = new AffineTransform();
      cenScaPanTr.setToIdentity();
      cenScaPanTr.concatenate(scale);
      cenScaPanTr.concatenate(offsetTr);
      cenScaPanTr.concatenate(centerTr);
      ////////////////////////////////////////////////////////////////////////

      ////////////////////////////////////////////////////////////////////////
      // Set graphic quality options
      graphics2d.setRenderingHint(RenderingHints.KEY_RENDERING,
          RenderingHints.VALUE_RENDER_QUALITY);
      graphics2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
          RenderingHints.VALUE_ANTIALIAS_ON);
      graphics2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING,
          RenderingHints.VALUE_COLOR_RENDER_QUALITY);
      graphics2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
          RenderingHints.VALUE_FRACTIONALMETRICS_ON);
      graphics2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
          RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
      graphics2dm.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
          RenderingHints.VALUE_ANTIALIAS_OFF);
      graphics2dm.setRenderingHint(RenderingHints.KEY_RENDERING,
          RenderingHints.VALUE_RENDER_SPEED);
      graphics2dm.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING,
          RenderingHints.VALUE_COLOR_RENDER_SPEED);
          ////////////////////////////////////////////////////////////////////////

      ////////////////////////////////////////////////////////////////////////
      // Draw background
      graphics2d.setColor(Color.white);
      graphics2d.fillRect(0, 0, width, height);
      graphics2dm.setColor(Color.black);
      graphics2dm.fillRect(0, 0, width, height);
      ////////////////////////////////////////////////////////////////////////

      ////////////////////////////////////////////////////////////////////////
      // Add Refseq
      char[] referenceSequence = covData.getReferenceSequence();
      if (referenceSequence != null
          && referenceSequence.length == covData.getMatrixSize()) {
        Font font = new Font("Arial", 1, 7);
        float h = lastRadio * maxRadius;
        lastRadio = lastRadio - 0.01f;
        int charCounter = 0;
        for (char c : referenceSequence) {
          Shape currentGlyph = font
              .createGlyphVector(graphics2d.getFontRenderContext(),
                  String.valueOf(c))
              .getGlyphOutline(0);
          GeneralPath p = new GeneralPath(currentGlyph);
          AffineTransform at = new AffineTransform();
          at.rotate(charCounter * (elementArc + spacerArc) * Math.PI / 180);
          at.translate(0, -h);
          p.transform(at);
          p.transform(cenScaPanTr);
          graphics2d.setColor(Color.black);
          graphics2d.fill(p);
          charCounter++;
        }
      }
      ////////////////////////////////////////////////////////////////////////

      ////////////////////////////////////////////////////////////////////////
      // Add proteins Bands
      int numberOfProteins = covData.getNumberOfProteins();
      if (numberOfProteins > 1) {
        float h1 = lastRadio * maxRadius;
        lastRadio = lastRadio - 0.01f;
        float h2 = lastRadio * maxRadius;
        lastRadio = lastRadio - 0.02f;

        float accumProteinArc = 0;
        for (int i = 0; i < covData.getNumberOfProteins(); i++) {
          List<Point2D> proteinBand = CircosImagePanel.createProteinBandPath(
              spacerArc, elementArc, h1, h2, covData.getProteinLength(i + 1));
          GeneralPath p = CircosImagePanel.createClosedPath(proteinBand);

          AffineTransform tr = new AffineTransform();
          tr.rotate(accumProteinArc * Math.PI / 180);
          p.transform(tr);
          p.transform(cenScaPanTr);

          graphics2d.setColor(
              CircosImagePanel.this.proteinColorMapper.getColor(i * 31 % 7));
          graphics2d.fill(p);
          graphics2d.setColor(Color.gray);
          graphics2d.setStroke(new BasicStroke(0.5f));
          graphics2d.draw(p);
          accumProteinArc = accumProteinArc
              + covData.getProteinLength(i + 1) * (elementArc + spacerArc);
        }
      }
      ////////////////////////////////////////////////////////////////////////

      ////////////////////////////////////////////////////////////////////////
      // Add Tracks
      for (int i = 0; i < covData.getTrackCount(); i++) {

        float outerRadius = lastRadio * maxRadius;
        lastRadio = lastRadio - 0.02f;
        float innerRadius = lastRadio * maxRadius;
        lastRadio = lastRadio - 0.02f;

        List<Point2D> proteinBand = createProteinBandPath(spacerArc, elementArc,
            outerRadius, innerRadius, 1);
        GeneralPath p = CircosImagePanel.createClosedPath(proteinBand);

        ColorMapper currentColorMapper = CircosImagePanel.this.trackColorMappers
            .get(i);
        Track currentTrack = covData.getTrack(i);

        for (int j = 0; j < matrixSize; j++) {
          GeneralPath p1 = (GeneralPath) p.clone();
          AffineTransform tr = new AffineTransform();
          tr.rotate((j * (elementArc + spacerArc) * Math.PI / 180));
          p1.transform(tr);
          p1.transform(cenScaPanTr);
          double value = currentTrack.getData()[j];
          graphics2d.setColor(currentColorMapper.getColor(value));
          graphics2d.fill(p1);
          graphics2d.setColor(Color.gray);
          graphics2d.setStroke(new BasicStroke(0.5f));
          graphics2d.draw(p1);
        }
      }
      ////////////////////////////////////////////////////////////////////////

      ////////////////////////////////////////////////////////////////////////
      // Add Circos Links
      float outerRadius = lastRadio * maxRadius;
      lastRadio = lastRadio - 0.03f;

      tripletsMap.clear();
      int circosMapColorRBG = -16777216;
      for (int i = 0; i < CircosImagePanel.this.sortedData.length; i++) {

        GeneralPath circosLink = new GeneralPath();
        CovariationTriplet triplet = CircosImagePanel.this.sortedData[i];
        float p1Angle = (triplet.getNominalX() - 1) * (elementArc + spacerArc)
            + elementArc / 2;
        float p2Angle = (triplet.getNominalY() - 1) * (elementArc + spacerArc)
            + elementArc / 2;

        Point2D p1 = CircosImagePanel.getPointAtDegreeAngle(p1Angle,
            outerRadius);
        Point2D p2 = CircosImagePanel.getPointAtDegreeAngle(p2Angle,
            outerRadius);

        circosLink.moveTo(p1.getX(), p1.getY());
        circosLink.quadTo(0, 0, p2.getX(), p2.getY());

        AffineTransform tr = new AffineTransform();
        circosLink.transform(tr);
        circosLink.transform(cenScaPanTr);

        boolean intra = CircosImagePanel.this.data
            .isIntraProteinPair(triplet.getNominalX(), triplet.getNominalY());

        Color linkColor;
        if (!highlighted.contains(triplet)) {
          if (intra) {
            int protIndex = CircosImagePanel.this.data
                .getProteinNumberFromNominalPosition(triplet.getNominalX());
            linkColor = proteinColorMapper.getColor((protIndex - 1) * 31 % 7);
          } else {
            linkColor = Color.blue;
          }
        } else {
          linkColor = Color.orange;
        }

        graphics2d.setColor(linkColor);
        graphics2d.setStroke(new BasicStroke(3));
        graphics2d.draw(circosLink);

        circosMapColorRBG = circosMapColorRBG + 1;
        graphics2dm.setColor(new Color(circosMapColorRBG));
        graphics2dm.setStroke(new BasicStroke(5));
        graphics2dm.draw(circosLink);
        tripletsMap.put(circosMapColorRBG, triplet);

      }
      circosMap = bim;
      return bi;
    }
  }

  private class ZoomFactorObserver implements Observer {
    @Override
    public void update(Observable o, Object arg) {
      ZoomAndPanCircos z = (ZoomAndPanCircos) o;
      String msg = (String) arg;

      //////////////////////////////////////////////////////////////////////////
      // Change scroll bars according to zoom factor
      if (msg.equals("VALUE_CHANGED")) {
        double zoomValue = z.getValue();
        int maxValue = (int) (100 * zoomValue);
        int offsetH = (int) (z.getOffSetH() * maxValue);
        int offsetV = (int) (z.getOffSetV() * maxValue);
        int oldValueH = Math.min(maxValue - 100, offsetH);
        int oldValueV = Math.min(maxValue - 100, offsetV);
        hScroolBar.setValues(oldValueH, 100, 0, maxValue);
        vScroolBar.setValues(oldValueV, 100, 0, maxValue);
        CircosImagePanel.this.updateUI();
      }
      //////////////////////////////////////////////////////////////////////////
      // Redraw image if scroll bars were adjusted
      if (msg.equals("OFFSETH_CHANGED") || msg.equals("OFFSETV_CHANGED")) {
        CircosImagePanel.this.updateUI();
      }
      //////////////////////////////////////////////////////////////////////////
    }
  }
  //////////////////////////////////////////////////////////////////////////////
}
