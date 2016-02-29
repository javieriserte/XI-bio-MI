package org.jiserte.mi.mimatrixviewer.view.positives;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;

import org.jiserte.mi.mimatrixviewer.MIViewingPane;
import org.jiserte.mi.mimatrixviewer.datastructures.CovariationData;

public class PositiveDataMainViewingPane extends MIViewingPane {

  // ///////////////////////////////////////////////////////////////////////////
  // Class constants
  private static final long serialVersionUID = 1930039293513077159L;

  // ///////////////////////////////////////////////////////////////////////////

  // ///////////////////////////////////////////////////////////////////////////
  // Instance Variables

  // ///////////////////////////////////////////////////////////////////////////

  // ///////////////////////////////////////////////////////////////////////////
  // Components
  private RocPanel rocImagePanel;
  private PpvPanel ppvImagePanel;
  // ///////////////////////////////////////////////////////////////////////////

  // ///////////////////////////////////////////////////////////////////////////
  // Constructors
  public PositiveDataMainViewingPane() {
    super();
    this.createGUI();
  }

  // /
  // ///////////////////////////////////////////////////////////////////////////

  // ///////////////////////////////////////////////////////////////////////////
  // Public Interface
  @Override
  public void setData(CovariationData data) {
    this.rocImagePanel.setData(data);
    this.rocImagePanel.updateUI();
    
    this.ppvImagePanel.setData(data);
    this.ppvImagePanel.updateUI();

  }

  @Override
  public void forceDrawing() {
    this.rocImagePanel.updateUI();
    this.ppvImagePanel.updateUI();


  }

  // ///////////////////////////////////////////////////////////////////////////

  // ///////////////////////////////////////////////////////////////////////////
  // Private Methods
  private void createGUI() {

    // /////////////////////////////////////////////////////////////////////////
    // Set layout
    GridBagLayout layout = new GridBagLayout();
    layout.columnWeights = new double[] { 1d };
    layout.columnWidths = new int[] { 100 };
    layout.rowHeights = new int[] {200,100};
    layout.rowWeights = new double[] {0.666,0.333};
    GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.CENTER;
    c.fill = GridBagConstraints.BOTH;
    c.gridx = 0;
    this.setLayout(layout);
    // Add Roc Panel & PPV vs Score Panel
    c.gridy = 0;
    this.rocImagePanel = new RocPanel();
    this.add(this.rocImagePanel, c);
    c.gridy=1;
    this.ppvImagePanel = new PpvPanel();
    this.add(this.ppvImagePanel, c);
    
  }
  // ///////////////////////////////////////////////////////////////////////////

}
