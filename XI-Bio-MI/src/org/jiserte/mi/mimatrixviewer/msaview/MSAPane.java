package org.jiserte.mi.mimatrixviewer.msaview;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.font.FontRenderContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JSplitPane;
import javax.swing.plaf.basic.BasicSliderUI.ScrollListener;

import org.jiserte.mi.mimatrixviewer.msaview.color.ProteinColor;

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
  //////////////////////////////////////////////////////////////////////////////

  //////////////////////////////////////////////////////////////////////////////
  // Instance variables
  private MsaDataObservable msaData;
  private Observer msaObserver;
  private List<MsaHoverListener> listeners;
  //////////////////////////////////////////////////////////////////////////////

  //////////////////////////////////////////////////////////////////////////////
  // Components
  private JScrollBar hScrBar;
  private JScrollBar vScrBar;
  private MsaArea selection;
  private DescriptionImagePanel descPanel;
  private SequenceImagePanel seqPanel;
  private ProteinColor color;
  //////////////////////////////////////////////////////////////////////////////

  //////////////////////////////////////////////////////////////////////////////
  // Constructor
  public MSAPane() {
    super();
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
  public void setColor(ProteinColor color){
    this.color = color;
  }
  //////////////////////////////////////////////////////////////////////////////

  //////////////////////////////////////////////////////////////////////////////
  // Private methods
  
  private void notifyListeners(MsaHoverEvent e) {
    for (MsaHoverListener l : this.listeners) {
      l.msaHover(e);
    }
  }
  private void createGUI() {

    JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    splitPane.setResizeWeight(0);
    splitPane.setDividerLocation(0.3);
    splitPane.setDividerSize(10);

    this.seqPanel = new SequenceImagePanel();
    this.descPanel = new DescriptionImagePanel();
    this.setLayout(new BorderLayout());
    this.hScrBar = new JScrollBar(JScrollBar.HORIZONTAL, 0, 1, 0, 1);
    this.vScrBar = new JScrollBar(JScrollBar.VERTICAL, 0, 1, 0, 1);

    splitPane.setLeftComponent(this.descPanel);
    splitPane.setRightComponent(this.seqPanel);
    //
    this.hScrBar.addAdjustmentListener(new ScrollBarOffSetsListener());
    //
    this.vScrBar.addAdjustmentListener(new ScrollBarOffSetsListener());

    this.seqPanel.addComponentListener(new ComponentListener() {

      @Override
      public void componentShown(ComponentEvent arg0) {
      }

      @Override
      public void componentResized(ComponentEvent arg0) {
        adjustScrollBars();

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
        this.hScrBar.setValues(0,1,0, 1);
      } else {
        int newXval = Math.min(this.hScrBar.getValue(), nCols - nCharPerRow);
        newXval = Math.max(0, newXval);
        this.hScrBar.setValues(newXval, nCharPerRow,0, nCols);     
      }
      
      if (nRows <= nCharPerColumn) {
        this.vScrBar.setValues(0,1,0, 1);
      } else { 
        int newYval = Math.min(this.vScrBar.getValue(), nRows - nCharPerColumn);
        newYval = Math.max(0,newYval );
        this.vScrBar.setValues( newYval, nCharPerColumn, 0, nRows);
      }

    }
  }
  //////////////////////////////////////////////////////////////////////////////

  //////////////////////////////////////////////////////////////////////////////
  // Auxiliary classes
  private class SequenceImagePanel extends JPanel {

    private static final long serialVersionUID = 5805856155262708418L;

    @Override
    protected void paintComponent(Graphics g) {
      super.paintComponent(g);

      if (!msaData.getSequences().isEmpty()) {
        
        int width = this.getWidth();
        int height = this.getHeight();
        int nCharsPerRow = width / charWidth;

        int nCharsPerColumn = height / charHeight;
        Font font = new Font(fontName, 1, FontWidth);
        Graphics2D g2d = (Graphics2D) g;

        g2d.setColor(Color.white);
        g2d.fillRect(0, 0, width, height);

        g2d.setFont(font);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
            RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        // FontRenderContext frc = g2d.getFontRenderContext();
        int charCounterH = 0;
        int charCounterV = 1;
        for (int i = msaData.getOffsetX(); i < msaData.getOffsetX()
            + nCharsPerRow; i++) {
          for (int j = msaData.getOffsetY(); j < msaData.getOffsetY()
              + nCharsPerColumn; j++) {
            char cc = msaData.sequences.get(j).getSecond().charAt(i);
            Color c = color!=null? color.getColor(cc):Color.BLACK;
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

    private static final long serialVersionUID = 5805856155262708418L;

    @Override
    protected void paintComponent(Graphics g) {
      super.paintComponent(g);

      if (!msaData.getSequences().isEmpty()) {
        
        int width = this.getWidth();
        int height = this.getHeight();
        
        

        int nCharsPerColumn = height / charHeight;
        Font font = new Font(fontName, 1, FontWidth);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.white);
        g2d.fillRect(0, 0, width, height);
        g2d.setFont(font);
        g2d.setColor(Color.black);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
            RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        // FontRenderContext frc = g2d.getFontRenderContext();
        int charCounterV = 1;
          for (int j = msaData.getOffsetY(); j < msaData.getOffsetY()
              + nCharsPerColumn; j++) {
            String cd = msaData.getSequences().get(j).getFirst();
            g2d.drawString(cd, 0, charCounterV * charHeight);
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
        System.out.println("New OffSets X: " + hScrBar.getValue() + " Y: " + vScrBar.getValue());
        if (hScrBar.getValue() >= 0
            && hScrBar.getValue() < nCols
            && vScrBar.getValue() >= 0
            && vScrBar.getValue() < nRows) {
          
          msaData.setOffsetX(hScrBar.getValue());
          msaData.setOffsetY(vScrBar.getValue());
        }
      }
    }
  }
  
  private class MsaHover implements MouseMotionListener{

    @Override
    public void mouseDragged(MouseEvent e) {
      // TODO Auto-generated method stub
      
    }

    @Override
    public void mouseMoved(MouseEvent e) {
      
      int column =e.getX() / charWidth + msaData.getOffsetX();
      int row = e.getY() / charHeight + msaData.getOffsetY();
      MsaHoverEvent event = new MsaHoverEvent();
      event.column = column;
      event.row = row;
      event.description = msaData.getSequences().get(row).getFirst();
      event.c = event.description.charAt(column);
      
      notifyListeners(event);
      
    }
    
  }

  //////////////////////////////////////////////////////////////////////////////
}
