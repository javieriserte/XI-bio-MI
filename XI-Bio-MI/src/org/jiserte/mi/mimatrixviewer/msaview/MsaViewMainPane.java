package org.jiserte.mi.mimatrixviewer.msaview;

import java.awt.BorderLayout;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jiserte.mi.mimatrixviewer.MIViewingPane;
import org.jiserte.mi.mimatrixviewer.datastructures.CovariationData;
import org.jiserte.mi.mimatrixviewer.msaview.color.ProteinColor;

public class MsaViewMainPane extends MIViewingPane {

  /**
   * 
   */
  private static final long serialVersionUID = -1082765061896776669L;
  //////////////////////////////////////////////////////////////////////////////
  // Instance variables
  private MSAPane msapane;
  //////////////////////////////////////////////////////////////////////////////

  public MsaViewMainPane() {
    super();
    this.createGUI();
  }
  
  public void createGUI() {
    this.setLayout(new BorderLayout());
    
    this.msapane = new MSAPane();
    this.msapane.setColor(new ProteinColor());
    
    this.add(this.msapane, BorderLayout.CENTER);
    
    JLabel label = new JLabel("HELLO");
    
    this.add(label,BorderLayout.SOUTH);
    
    this.msapane.addMsaHoverListener(new MsaHoverListener() {
      
      @Override
      public void msaHover(MsaHoverEvent e) {
        label.setText("Hovering: " + e.column +", "+e.row + " : " + e.c);
        
      }
    });
    
    
  }
  
  @Override
  public void setData(CovariationData data) {
    Observer o = new Observer() {
      
      @Override
      public void update(Observable arg0, Object arg1) {
        if (((int) arg1) == CovariationData.MSA_CHANGED) {
          CovariationData changedData = (CovariationData)arg0;
          if (changedData.hasMsa()) {
            msapane.setSequences(data.getMsa());
          }
        }
      }
    };
    data.addObserver(o);
    if (data.hasMsa()) {
      this.msapane.setSequences(data.getMsa());
    }
    
  }

  @Override
  public void forceDrawing() {
    this.msapane.updateUI();
    
  }
  
}
