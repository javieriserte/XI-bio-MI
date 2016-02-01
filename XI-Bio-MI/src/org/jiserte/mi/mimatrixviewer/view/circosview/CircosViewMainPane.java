package org.jiserte.mi.mimatrixviewer.view.circosview;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jiserte.mi.mimatrixviewer.MIViewingPane;
import org.jiserte.mi.mimatrixviewer.datastructures.CovariationData;

public class CircosViewMainPane extends MIViewingPane {

  //////////////////////////////////////////////////////////////////////////////
  // instance variables
  private CircosImagePanel imagePanel;
  private JLabel selectionLabel;
  //////////////////////////////////////////////////////////////////////////////

  /**
   * 
   */
  private static final long serialVersionUID = 6043293511595183993L;

  public CircosViewMainPane() {
    super();
    this.createGUI();
  }

  public void createGUI() {

    ////////////////////////////////////////////////////////////////////////////
    // Set the layout
    this.setOpaque(true);
    GridBagLayout layout = new GridBagLayout();
    this.setLayout(layout);

    layout.columnWeights = new double[] { 1 };
    layout.columnWidths = new int[] { 0 };
    layout.rowWeights = new double[] { 1, 0, 0 };
    layout.rowHeights = new int[] { 0, 50, 50 };

    GridBagConstraints c = new GridBagConstraints();

    ////////////////////////////////////////////////////////////////////////////
    // Panel for circos image
    this.imagePanel = new CircosImagePanel();
    this.imagePanel.addCircosLinkHoverListener(new CircosLinkHoverListener() {
      @Override
      public void circosHover(CircosLinkHoverEvent e) {
        selectionLabel.setText("Selected: " + e.getTriplet().getNominalX() + "/"
            + e.getTriplet().getNominalY() + ": " + e.getTriplet().getValue());
      }
    });
    c.gridheight = 1;
    c.gridwidth = 1;
    c.fill = GridBagConstraints.BOTH;
    c.anchor = GridBagConstraints.CENTER;
    c.gridx = 0;
    c.gridy = 0;
    this.add(this.imagePanel, c);
    ////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////
    // Controls panel
    JPanel controls1 = new JPanel();
    JPanel controls2 = new JPanel();
    controls1.setLayout(new FlowLayout());
    JSlider slider = new JSlider(JSlider.HORIZONTAL, 100, 400, 100);
    JSlider links = new JSlider(JSlider.HORIZONTAL, 1, 1000, 1);
    ChangeListener l = new ZoomChangeListener();
    slider.addChangeListener(l);
    controls1.add(new JLabel("Zoom:"));
    controls1.add(slider);
    links.addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e) {
        imagePanel.setMaxLinksToShow(((JSlider) e.getSource()).getValue());
      }
    });
    controls1.add(new JLabel("links:"));
    controls1.add(links);
    this.selectionLabel = new JLabel("Selected None");
    controls2.add(this.selectionLabel);
    controls2.add(new JButton("Edit Track Colors"));
    controls2.add(new JButton("Protein Band and Circos Links Colors"));
    c.gridy = 1;
    this.add(controls1, c);
    c.gridy = 2;
    this.add(controls2, c);
    ////////////////////////////////////////////////////////////////////////////

  }

  @Override
  public void setData(CovariationData data) {
    this.imagePanel.setData(data);
  }

  @Override
  public void forceDrawing() {

    this.imagePanel.updateUI();

  }

  //////////////////////////////////////////////////////////////////////////////
  // Auxiliary classes
  public class ZoomChangeListener implements ChangeListener {
    @Override
    public void stateChanged(ChangeEvent e) {
      JSlider comp = (JSlider) e.getSource();
      CircosViewMainPane.this.imagePanel
          .setZoomFactor(((double) comp.getValue() / 100d));
    }
  }
  //////////////////////////////////////////////////////////////////////////////
}
