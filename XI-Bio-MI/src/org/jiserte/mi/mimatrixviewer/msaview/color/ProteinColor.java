package org.jiserte.mi.mimatrixviewer.msaview.color;

import java.awt.Color;

public class ProteinColor {

  private static final Color colD = new Color(230,10,10); 
  private static final Color colE = new Color(230,10,10);
  private static final Color colC = new Color(230,230,0); 
  private static final Color colM = new Color(230,230,0);
  private static final Color colK = new Color(20,90,255); 
  private static final Color colR = new Color(20,90,255);
  private static final Color colS = new Color(250,150,0); 
  private static final Color colT = new Color(250,150,0);
  private static final Color colF = new Color(50,50,170); 
  private static final Color colY = new Color(50,50,170);
  private static final Color colN = new Color(0,220,220); 
  private static final Color colQ = new Color(0,220,220);
  private static final Color colG = new Color(235,235,235);
  private static final Color colL = new Color(15,130,15); 
  private static final Color colV = new Color(15,130,15); 
  private static final Color colI = new Color(15,130,15);
  private static final Color colA = new Color(200,200,200);
  private static final Color colW = new Color(180,90,180);
  private static final Color colH = new Color(130,130,210);
  private static final Color colP = new Color(220,150,130);
  private static final Color colX = new Color(0,0,0);
  private static final Color def = new Color(0  ,0  ,0  ); 

    public Color getColor(char c) {

      
      Color result = null;
      
      switch (Character.toUpperCase(c)) {
      case 'D': result = colD; break; 
      case 'E': result = colE; break;
      case 'C': result = colC; break; 
      case 'M': result = colM; break;
      case 'K': result = colK; break; 
      case 'R': result = colR; break;
      case 'S': result = colS; break; 
      case 'T': result = colT; break;
      case 'F': result = colF; break; 
      case 'Y': result = colY; break;
      case 'N': result = colN; break; 
      case 'Q': result = colQ; break;
      case 'G': result = colG; break;
      case 'L': result = colL; break; 
      case 'V': result = colV; break; 
      case 'I': result = colI; break;
      case 'A': result = colA; break;
      case 'W': result = colW; break;
      case 'H': result = colH; break;
      case 'P': result = colP; break;
      case 'X': result = colX; break;
      default : result = def; break;
      }
      return result;
    }

  }

