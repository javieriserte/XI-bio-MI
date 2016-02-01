package org.jiserte.mi.mimatrixviewer.view.msaview;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;

import org.jiserte.mi.mimatrixviewer.view.colors.ProteinColor;

import pair.Pair;

public class MSAPane extends JPanel {

  /**
   * 
   */
  //////////////////////////////////////////////////////////////////////////////
  // Class constants
  private static final long serialVersionUID = 1L;
  private static final int charWidth = 15;
  private static final int charHeight = 15;
  private static final int FontWidth = 15;
  private static final String fontName = "Arial";
  private static final int HOVER_EVENT=1;
  private static final int SELECTION_EVENT=2;
  //////////////////////////////////////////////////////////////////////////////

  //////////////////////////////////////////////////////////////////////////////
  // Instance variables
  private MsaDataObservable msaData;
  private Observer msaObserver;
  private List<MsaHoverListener> hoverListeners;
  private List<MsaSelectionListener> selectionListeners;
//  private boolean isSelecting;
  private MsaArea selection;
  private Point selectionOrigin;
  //////////////////////////////////////////////////////////////////////////////

  //////////////////////////////////////////////////////////////////////////////
  // Components
  private JScrollBar hScrBar;
  private JScrollBar vScrBar;
  private DescriptionImagePanel descPanel;
  private SequenceImagePanel seqPanel;
  private ProteinColor color;
  //////////////////////////////////////////////////////////////////////////////

  //////////////////////////////////////////////////////////////////////////////
  // Constructor
  public MSAPane() {
    super();
    this.hoverListeners = new ArrayList<>();
    this.selectionListeners = new ArrayList<>();
    this.msaObserver = new Observer() {

      @Override
      public void update(Observable arg0, Object arg1) {
        int msg = (int) arg1;

        if (msg == MsaDataObservable.SEQUENCES_CHANGED) {
          // update ui and resize scrollbars
          adjustScrollBars();
          updateUI();
        }

        if (msg == MsaDataObservable.OFFSET_X_CHANGED
            || msg == MsaDataObservable.OFFSET_Y_CHANGED) {
          updateUI();
        }
      }
    };
    this.msaData = new MsaDataObservable();
    this.msaData.offsetX = 0;
    this.msaData.offsetY = 0;
    this.msaData.sequences = new ArrayList<>();
    this.msaData.addObserver(msaObserver);
    this.createGUI();
  }
  //////////////////////////////////////////////////////////////////////////////

  //////////////////////////////////////////////////////////////////////////////
  // Public interface
  public void setSequences(List<Pair<String, String>> sequences) {
    this.msaData.setSequences(sequences);
  }

  public void setOffSetX(int offset) {
    this.msaData.setOffsetX(offset);
  }

  public void setOffsetY(int offset) {
    this.msaData.setOffsetY(offset);
  }

  public void setColor(ProteinColor color) {
    this.color = color;
  }

  public void addMsaHoverListener(MsaHoverListener listener) {
    this.hoverListeners.add(listener);
  }

  public void addMsaSelectionListener(MsaSelectionListener listener) {
    this.selectionListeners.add(listener);
  }

  //////////////////////////////////////////////////////////////////////////////

  //////////////////////////////////////////////////////////////////////////////
  // Private methods

  private void notifyListeners(int type, Object event) {
    switch (type){
    case HOVER_EVENT:
      for (MsaHoverListener l : this.hoverListeners) {
        l.msaHover( (MsaHoverEvent) event);
      }
      break;
    case SELECTION_EVENT:
      for (MsaSelectionListener l : this.selectionListeners) {
        l.msaSelectionDone( (MsaSelectionEvent) event);
      }
      break;
    }
  }

  private void createGUI() {

    JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    splitPane.setResizeWeight(0);
    splitPane.setDividerLocation(200);
    splitPane.setDividerSize(15);
    

    this.seqPanel = new SequenceImagePanel();
    this.descPanel = new DescriptionImagePanel();
    this.setLayout(new BorderLayout());
    this.hScrBar = new JScrollBar(JScrollBar.HORIZONTAL, 0, 1, 0, 1);
    this.vScrBar = new JScrollBar(JScrollBar.VERTICAL, 0, 1, 0, 1);
    this.descPanel.setPreferredSize(new Dimension(200, 0));

    splitPane.setLeftComponent(this.descPanel);
    splitPane.setRightComponent(this.seqPanel);

    MsaHover msaHover = new MsaHover();
    this.seqPanel.addMouseMotionListener(msaHover);
    this.seqPanel.addMouseListener(msaHover);

    //
    this.hScrBar.addAdjustmentListener(new ScrollBarOffSetsListener());
    //
    this.vScrBar.addAdjustmentListener(new ScrollBarOffSetsListener());

    this.seqPanel.addComponentListener(new ComponentListener() {
      @Override
      public void componentResized(ComponentEvent arg0) {
        adjustScrollBars();
      }

      @Override
      public void componentShown(ComponentEvent arg0) {
      }

      @Override
      public void componentMoved(ComponentEvent arg0) {
      }

      @Override
      public void componentHidden(ComponentEvent arg0) {
      }
    });

    this.add(splitPane, BorderLayout.CENTER);
    this.add(this.hScrBar, BorderLayout.SOUTH);
    this.add(this.vScrBar, BorderLayout.EAST);
  }

  private void adjustScrollBars() {
    if (this.seqPanel != null) {
      int width = this.seqPanel.getWidth();
      int height = this.seqPanel.getHeight();
      int nCharPerRow = width / MSAPane.charWidth;
      int nCharPerColumn = height / MSAPane.charHeight;
      int nRows = this.msaData.getSequences().size();
      int nCols = nRows != 0
          ? this.msaData.getSequences().get(0).getSecond().length() : 0;

      if (nCols <= nCharPerRow) {
        this.hScrBar.setValues(0, 1, 0, 1);
      } else {
        int newXval = Math.min(this.hScrBar.getValue(), nCols - nCharPerRow);
        newXval = Math.max(0, newXval);
        this.hScrBar.setValues(newXval, nCharPerRow, 0, nCols);
      }

      if (nRows <= nCharPerColumn) {
        this.vScrBar.setValues(0, 1, 0, 1);
      } else {
        int newYval = Math.min(this.vScrBar.getValue(), nRows - nCharPerColumn);
        newYval = Math.max(0, newYval);
        this.vScrBar.setValues(newYval, nCharPerColumn, 0, nRows);
      }

    }
  }
  //////////////////////////////////////////////////////////////////////////////

  //////////////////////////////////////////////////////////////////////////////
  // Auxiliary classes
  private class SequenceImagePanel extends JPanel {
    int osTop;
    int osBot;
    int osLeft;
    int osRight;
    int width;
    int height;
    int nCharsPerRow;
    int nCharsPerColumn;

    Color selColorBr = new Color(140, 255, 205);
    Color selColorBg = new Color(215, 255, 225);

    int charCounterH;
    int charCounterV;

    private static final long serialVersionUID = 5805856155262708418L;

    @Override
    protected void paintComponent(Graphics g) {
      super.paintComponent(g);

      if (!msaData.getSequences().isEmpty()) {
        width = this.getWidth();
        height = this.getHeight();
        nCharsPerRow = width / charWidth;

        nCharsPerColumn = height / charHeight;
        Font font = new Font(fontName, 1, FontWidth);
        Graphics2D g2d = (Graphics2D) g;

        ////////////////////////////////////////////////////////////////////////
        // Draw background
        g2d.setColor(Color.white);
        g2d.fillRect(0, 0, width, height);
        // Draw Background for selection

        if (selection != null) {
          osTop = selection.getTop() - msaData.getOffsetY();
          osBot = selection.getBottom() - msaData.getOffsetY();
          osLeft = selection.getLeft() - msaData.getOffsetX();
          osRight = selection.getRight() - msaData.getOffsetX();

          osTop = Math.max(0, osTop);
          osBot = Math.min(nCharsPerColumn, osBot);
          osLeft = Math.max(0, osLeft);
          osRight = Math.min(nCharsPerRow, osRight);

          g2d.setColor(selColorBg);
          g2d.fillRect(osLeft * charWidth, osTop * charHeight,
              (osRight - osLeft + 1) * charWidth,
              (osBot - osTop + 1) * charHeight);
          g2d.setColor(selColorBr);
          g2d.drawRect(osLeft * charWidth, osTop * charHeight,
              (osRight - osLeft + 1) * charWidth,
              (osBot - osTop + 1) * charHeight);

        }
        ////////////////////////////////////////////////////////////////////////

        ////////////////////////////////////////////////////////////////////////
        // Draw border
        g2d.setColor(Color.gray);
        g2d.drawLine(0, 0, width , 0);
        g2d.drawLine(0, 0,  0, height);
        ////////////////////////////////////////////////////////////////////////
        
        g2d.setFont(font);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
            RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        // FontRenderContext frc = g2d.getFontRenderContext();
        charCounterH = 0;
        charCounterV = 1;
        for (int i = msaData.getOffsetX(); i < msaData.getOffsetX()
            + nCharsPerRow; i++) {
          for (int j = msaData.getOffsetY(); j < msaData.getOffsetY()
              + nCharsPerColumn; j++) {
            char cc = msaData.sequences.get(j).getSecond().charAt(i);
            Color c = color != null ? color.getColor(cc) : Color.BLACK;
            g2d.setColor(c);
            g2d.drawString(String.valueOf(cc), charCounterH * charWidth,
                charCounterV * charHeight);
            charCounterV++;
          }
          charCounterH++;
          charCounterV = 1;
        }
      }

    }

  }

  private class DescriptionImagePanel extends JPanel {
    Font font = new Font(fontName, 1, FontWidth);
    private static final long serialVersionUID = 5805856155262708418L;

    @Override
    protected void paintComponent(Graphics g) {
      super.paintComponent(g);

      if (!msaData.getSequences().isEmpty()) {

        int width = this.getWidth();
        int height = this.getHeight();

        
        
        int nCharsPerColumn = height / charHeight;
       
        Graphics2D g2d = (Graphics2D) g;
        

        
        g2d.setColor(Color.white);
        g2d.fillRect(0, 0, width, height);

        g2d.setColor(Color.gray);
        g2d.drawLine(0, 0, width , 0);
        g2d.drawLine(width-1, 0,  width-1, height);

        g2d.setFont(font);
        g2d.setColor(Color.black);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
            RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        // FontRenderContext frc = g2d.getFontRenderContext();
        int charCounterV = 1;
        for (int j = msaData.getOffsetY(); j < msaData.getOffsetY()
            + nCharsPerColumn; j++) {
          g2d.drawString(msaData.getSequences().get(j).getFirst(), 0, charCounterV * charHeight);
          charCounterV++;
        }
        charCounterV = 1;
      }

    }

  }

  private class MsaDataObservable extends Observable {
    private static final int OFFSET_X_CHANGED = 0;
    private static final int OFFSET_Y_CHANGED = 1;
    private static final int SEQUENCES_CHANGED = 2;

    private int offsetX;
    private int offsetY;
    private List<Pair<String, String>> sequences;

    public int getOffsetX() {
      return offsetX;
    }

    public void setOffsetX(int offsetX) {
      this.offsetX = offsetX;
      this.setChanged();
      this.notifyObservers(MsaDataObservable.OFFSET_X_CHANGED);
    }

    public int getOffsetY() {
      return offsetY;
    }

    public void setOffsetY(int offsetY) {
      this.offsetY = offsetY;
      this.setChanged();
      this.notifyObservers(MsaDataObservable.OFFSET_Y_CHANGED);
    }

    public List<Pair<String, String>> getSequences() {
      return sequences;
    }

    public void setSequences(List<Pair<String, String>> sequences) {
      this.sequences = sequences;
      this.setChanged();
      this.notifyObservers(MsaDataObservable.SEQUENCES_CHANGED);

    }
  }

  private class ScrollBarOffSetsListener implements AdjustmentListener {
    @Override
    public void adjustmentValueChanged(AdjustmentEvent arg0) {

      if (!msaData.getSequences().isEmpty()) {
        int nCols = msaData.sequences.get(0).getSecond().length();
        int nRows = msaData.sequences.size();
        if (hScrBar.getValue() >= 0 && hScrBar.getValue() < nCols
            && vScrBar.getValue() >= 0 && vScrBar.getValue() < nRows) {

          msaData.setOffsetX(hScrBar.getValue());
          msaData.setOffsetY(vScrBar.getValue());
        }
      }
    }
  }

  private class MsaHover implements MouseMotionListener, MouseListener {

    @Override
    public void mouseDragged(MouseEvent e) {
      if (SwingUtilities.isLeftMouseButton(e)) {

        ////////////////////////////////////////////////////////////////////////
        // pan MSA viewport
        int w = seqPanel.getWidth();
        int h = seqPanel.getHeight();

        double[] limits = new double[] { 0.1, 0.9 };
        double[] sizes = new double[] { w, h };
        int[] coord = new int[] { e.getX(), e.getY() };

        boolean chgH = coord[0] < limits[0] * sizes[0]
            || coord[0] > limits[1] * sizes[0];
        boolean chgV = coord[1] < limits[0] * sizes[1]
            || coord[1] > limits[1] * sizes[1];

        boolean[] chg = new boolean[] { chgH, chgV };
        JScrollBar[] bars = new JScrollBar[] { hScrBar, vScrBar };

        for (int i = 0; i < chg.length; i++) {
          JScrollBar bar = bars[i];
          if (chg[i]) {
            boolean chgLowerBound = coord[i] < limits[0] * sizes[i];
            int newValue = Math.max(0,
                Math.min(bar.getValue() + (chgLowerBound ? -1 : +1),
                    bar.getMaximum() - bar.getVisibleAmount()));
            bar.setValues(newValue, bar.getVisibleAmount(), bar.getMinimum(),
                bar.getMaximum());
          }
        }
        ////////////////////////////////////////////////////////////////////////

        ////////////////////////////////////////////////////////////////////////
        // Selection
        Point point = getResiduesPoint(e.getX(), e.getY());

        int[] topbot = point.y < selectionOrigin.y
            ? new int[] { point.y, selectionOrigin.y }
            : new int[] { selectionOrigin.y, point.y };

        int[] leftright = point.x < selectionOrigin.x
            ? new int[] { point.x, selectionOrigin.x }
            : new int[] { selectionOrigin.x, point.x };

        selection = new MsaArea();
        selection.setSelectionMode(MsaArea.AREA_SELECTION_MODE);
        selection.setTop(topbot[0]);
        selection.setBottom(topbot[1]);
        selection.setLeft(leftright[0]);
        selection.setRight(leftright[1]);

        seqPanel.updateUI();

        ////////////////////////////////////////////////////////////////////////

      }
    }

    private Point getResiduesPoint(int x, int y) {

      int msaWidth = msaData.getSequences().get(0).getSecond().length();
      int msaHeight = msaData.getSequences().size();

      int column = Math.min(msaWidth - 1, x / charWidth + msaData.getOffsetX());

      int row = Math.min(msaHeight - 1, y / charHeight + msaData.getOffsetY());
      return new Point(column, row);
    }

    @Override
    public void mouseMoved(MouseEvent e) {

      Point point = getResiduesPoint(e.getX(), e.getY());

      MsaHoverEvent event = new MsaHoverEvent();
      event.column = point.x;
      event.row = point.y;
      event.description = msaData.getSequences().get(point.y).getFirst();
      event.c = msaData.getSequences().get(point.y).getSecond().charAt(point.x);

      notifyListeners(HOVER_EVENT ,event);

    }

    @Override
    public void mouseClicked(MouseEvent e) {
      if (SwingUtilities.isLeftMouseButton(e) ) {
        selection = null;
        seqPanel.updateUI();
        MsaSelectionEvent ev = new MsaSelectionEvent();
        ev.setSender(MSAPane.this);
        ev.setSelection(selection);
        notifyListeners(SELECTION_EVENT, ev);        
      }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
      // TODO Auto-generated method stub

    }

    @Override
    public void mouseExited(MouseEvent e) {
      // TODO Auto-generated method stub

    }

    @Override
    public void mousePressed(MouseEvent e) {
      if (SwingUtilities.isLeftMouseButton(e)) {
        selectionOrigin = this.getResiduesPoint(e.getX(), e.getY());
//        isSelecting = true;
      }

    }

    @Override
    public void mouseReleased(MouseEvent e) {
//      isSelecting = false;
      MsaSelectionEvent ev = new MsaSelectionEvent();
      ev.setSender(MSAPane.this);
      ev.setSelection(selection);
      notifyListeners(SELECTION_EVENT, ev);
    }

  }

  //////////////////////////////////////////////////////////////////////////////
}
