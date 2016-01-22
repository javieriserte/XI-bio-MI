package org.jiserte.mi.mimatrixviewer.msaview;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JSplitPane;

import pair.Pair;

public class MSAPane extends JPanel {

  /**
   * 
   */
  //////////////////////////////////////////////////////////////////////////////
  // Class constants
  private static final long serialVersionUID = 1L;
  //////////////////////////////////////////////////////////////////////////////

  
  //////////////////////////////////////////////////////////////////////////////
  // Instance variables
  private int offsetX;
  private int offsetY;
  private List<Pair<String,String>> sequences;
  //////////////////////////////////////////////////////////////////////////////

  //////////////////////////////////////////////////////////////////////////////
  // Components
  private JScrollBar hScrBar;
  private JScrollBar vScrBar;
  private MsaArea selection;
  //////////////////////////////////////////////////////////////////////////////
  

  //////////////////////////////////////////////////////////////////////////////
  // Constructor
  public MSAPane() {
    super();
    this.createGUI();
  }  
  //////////////////////////////////////////////////////////////////////////////

  
  //////////////////////////////////////////////////////////////////////////////
  // Public interface
  public int getOffsetX() {
    return offsetX;
  }
  public void setOffsetX(int offsetX) {
    this.offsetX = offsetX;
  }
  public int getOffsetY() {
    return offsetY;
  }
  public void setOffsetY(int offsetY) {
    this.offsetY = offsetY;
  }
  public List<Pair<String,String>> getSequences() {
    return sequences;
  }
  public void setSequences(List<Pair<String,String>> sequences) {
    this.sequences = sequences;
  }
  //////////////////////////////////////////////////////////////////////////////

  //////////////////////////////////////////////////////////////////////////////
  // Private methods
  private void createGUI() {
    JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    splitPane.setResizeWeight(0);
    splitPane.setDividerLocation(0.3);
    splitPane.setDividerSize(10);
    
    SequenceImagePanel SeqImage = new SequenceImagePanel();
    DescriptionImagePanel DescImage = new DescriptionImagePanel();
    this.setLayout(new BorderLayout());
    this.hScrBar = new JScrollBar(JScrollBar.HORIZONTAL, 0, 1, 0, 1);
    this.vScrBar = new JScrollBar(JScrollBar.VERTICAL, 0, 1, 0, 1);

    splitPane.setLeftComponent(DescImage);
    splitPane.setRightComponent(SeqImage);
    
    this.add(splitPane, BorderLayout.CENTER);
    this.add(this.hScrBar, BorderLayout.SOUTH);
    this.add(this.vScrBar, BorderLayout.EAST);
  }
  //////////////////////////////////////////////////////////////////////////////
  
  //////////////////////////////////////////////////////////////////////////////
  // Auxiliary classes
  private class SequenceImagePanel extends JPanel{

    private static final long serialVersionUID = 5805856155262708418L;

    @Override
    protected void paintComponent(Graphics g) {
      super.paintComponent(g);
      
    }
    
  }
  private class DescriptionImagePanel extends JPanel{

    private static final long serialVersionUID = 5805856155262708418L;

    @Override
    protected void paintComponent(Graphics g) {
      super.paintComponent(g);
      
    }
    
  }

  //////////////////////////////////////////////////////////////////////////////
}

