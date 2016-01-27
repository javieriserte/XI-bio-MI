package org.jiserte.mi.mimatrixviewer.msaview;

import javax.swing.JComponent;

public class MsaSelectionEvent {
  private MsaArea selection;
  private JComponent sender;
  
  public MsaSelectionEvent() {
    super();
    this.selection = null;
    this.sender = null;
  }
  /**
   * @return the selection
   */
  public MsaArea getSelection() {
    return selection;
  }
  /**
   * @param selection the selection to set
   */
  public void setSelection(MsaArea selection) {
    this.selection = selection;
  }
  /**
   * @return the sender
   */
  public JComponent getSender() {
    return sender;
  }
  /**
   * @param sender the sender to set
   */
  public void setSender(JComponent sender) {
    this.sender = sender;
  }
}
