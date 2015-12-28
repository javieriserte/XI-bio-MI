package org.jiserte.mi.mimatrixviewer.matrixview_new;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import java.awt.Graphics2D;

import org.jiserte.mi.mimatrixviewer.matrixview_new.colors.ColorMapper;
import org.jiserte.mi.mimatrixviewer.matrixview_new.colors.ColoringStrategy;

public class ColorMapperCellRenderer extends JPanel implements
    ListCellRenderer<ColorMapper> {
  
  //////////////////////////////////////////////////////////////////////////////
  // 
  private ColorMapper value;
  //private JList<? extends ColorMapper> list;
  //private int index;
  private boolean isSelected;
  //private boolean cellHasFocus;
  //////////////////////////////////////////////////////////////////////////////
  /**
   * 
   */
  private static final long serialVersionUID = 3983560687693190009L;

  @Override
  public Component getListCellRendererComponent(
      JList<? extends ColorMapper> list, ColorMapper value, int index,
      boolean isSelected, boolean cellHasFocus) {
      this.value = value;
      //this.list = list;
      //this.index = index;
      this.isSelected = isSelected;
      //this.cellHasFocus = cellHasFocus;
      this.setPreferredSize(new Dimension(100,25));
      this.setMinimumSize(new Dimension(100,25));
      this.setSize(new Dimension(100,25));
    return this;
  }
  
  public void paint(Graphics g) {
    int divisions=5;
    int posX=1;
    int divWidth = (this.getWidth() -1 ) / divisions;
    int top = 5;
    int height = this.getHeight() - 2 * top;
    if (this.value != null) {
      int totalDivisions = divisions * this.value.getColors().size();
      int totalDivisionsCounter = 0;
      for (ColoringStrategy col : this.value.getColors()) {
        for (int d = 0; d < divisions ; d++) {
          double min = col.min();
          double max = col.max();
          if (min == Double.NEGATIVE_INFINITY) { min =  Double.MIN_VALUE;}
          if (max == Double.POSITIVE_INFINITY) { min =  Double.MAX_VALUE;}
          double val = min + (max- min) * d / (divisions-1);

          ((Graphics2D) g).setColor(col.getColor(val));
          posX = totalDivisionsCounter * (this.getWidth() -1) / totalDivisions;
          Rectangle rect = new Rectangle(posX, top, divWidth, height);
          ((Graphics2D) g).fill(rect);
          totalDivisionsCounter++;
        }
      }
      ((Graphics2D) g).setStroke(new BasicStroke(2));
      ((Graphics2D) g).setColor(this.isSelected?Color.black:Color.gray);
      ((Graphics2D) g).draw(new Rectangle(1,top-1,this.getWidth()-3,height));
      
    }
  }
  
  

}
