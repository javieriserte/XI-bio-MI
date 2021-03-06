package org.jiserte.mi.mimatrixviewer.view.matrixview;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.MemoryImageSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jiserte.mi.mimatrixviewer.datastructures.CovariationData;
import org.jiserte.mi.mimatrixviewer.view.colors.ColorMapper;

public class MIMatrixPane extends JScrollPane {

  //////////////////////////////////////////////////////////////////////////////
  // Class Constants
  private static final long serialVersionUID = 1L;
  private static final int ZOOM_SIZE = 75;
  //////////////////////////////////////////////////////////////////////////////

  //////////////////////////////////////////////////////////////////////////////
  //  Instance Variables
  private CovariationData data = null;
  // Mi data values
  @SuppressWarnings("unused")
  private ColorMapper color = null;
  // The way that each pair of residues is colored on map.
  private int numberOfProteins = 1;
  // How many proteins correspond to the map
  private int[] proteinLengths = null;
  // The length of each protein
  private String[] names = null;
  // The name of each protein
  private BufferedImage image = null;;
  // The generated map image
  private ImagePanel imagePane = null;
  // Component to store the image
  private MatrixViewMainPane viewer;
  // Parent MI Matrix Viewer
  private char[] aminoAcids;
  // Amino acid from whole MI Data
  private Rectangle area;
  private boolean showArea;
  private Rectangle selectedRegion;
  private boolean hasSelectedRegion;
  private List<MatrixAreaChangedListener> areaChangedListners;
  //////////////////////////////////////////////////////////////////////////////
  
  //////////////////////////////////////////////////////////////////////////////
  // Constructor
  public MIMatrixPane(MatrixViewMainPane viewer) {
    super();
    this.areaChangedListners = new ArrayList<>();
    ImagePanel imagePanel = new ImagePanel();
    GetValuesFromPointMouseListener l = new GetValuesFromPointMouseListener();
    imagePanel.addMouseListener(l);
    imagePanel.addMouseMotionListener(l);
    this.setImagePane(imagePanel);
    this.setViewportView(this.getImagePane());
    this.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
    this.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    this.getImagePane().setOpaque(true);
    this.getImagePane().setDoubleBuffered(true);
    this.setOpaque(true);
    this.setViewer(viewer);
    int[] pixels = new int[16 * 16];
    Image image = Toolkit.getDefaultToolkit().createImage(
        new MemoryImageSource(16, 16, pixels, 0, 16));
    Cursor transparentCursor = Toolkit.getDefaultToolkit().createCustomCursor(
        image, new Point(0, 0), "invisibleCursor");
    this.getImagePane().setCursor(transparentCursor);
    this.showArea = false;
  }
  //////////////////////////////////////////////////////////////////////////////

  //////////////////////////////////////////////////////////////////////////////
  // Public Interface
  public void addMatrixChangedListener(MatrixAreaChangedListener listener) {
    this.areaChangedListners.add(listener);
  }
  public void resetImage() {
    this.setImage(null);
    this.repaint();
  }
  public void accomodateSize() {
    boolean showBands = this.getProteinLengths().length >0;
    int nprot = this.getProteinLengths().length;
    int size = this.data.getMatrixSize()+ nprot + (showBands?30:0);
    Dimension matrixSize = new Dimension(size, size);
    this.getImagePane().setSize(matrixSize);
    this.getImagePane().setPreferredSize(matrixSize);
  }
  public double[] getValuesFromPoint(int x, int y) {
    return null;
  }
  //////////////////////////////////////////////////////////////////////////////
  
  
  //////////////////////////////////////////////////////////////////////////////
  // Private and protected 
  // methods
  private void notifyAreaMatrixChangedListeners(MatrixAreaChangedEvent e) {
    for (MatrixAreaChangedListener listener : this.areaChangedListners) {
      listener.matrixAreaChanged(e);
    }
  }
  /**
   * Creates the MI image map.
   * This could be implemented as an strategy in order to draw the map in different ways.
   * @return
   */
  private BufferedImage createMapImage() {

    if (this.getMatrix() == null || this.getColor() == null) {
      return null;
    }

    boolean showNames = (this.getNames()!=null 
        && this.getNames().length == this.numberOfProteins);
    boolean showBands = this.getProteinLengths().length >0;
    int namesLabelWidth = showBands?30:0;

    BufferedImage image = new BufferedImage(namesLabelWidth + 
        data.getMatrixSize() + this.numberOfProteins, namesLabelWidth + 
        data.getMatrixSize() + this.numberOfProteins, 
        BufferedImage.TYPE_INT_RGB);
    Graphics2D graphics = (Graphics2D) image.getGraphics();
    graphics.setColor(Color.white);
    graphics.fillRect(0, 0, image.getWidth(),image.getHeight());

    ////////////////////////////////////////////////////////////////////////////
    // Draw Mi Points
    for (int i=0; i< this.data.getMatrixSize();i++) {
      for (int j=0; j< this.data.getMatrixSize();j++) {
        int px = this.translateCoordinate(i+1);
        int py = this.translateCoordinate(j+1);
        image.setRGB(namesLabelWidth+px, namesLabelWidth+py, 
            this.getColor().getColor(data.getMatrix().getValue(i+1, j+1))
            .getRGB());
      }
    }
    ////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////
    // Draw Grid for proteins
    int protAcum = 0;
    for (int i = 0; i<this.getNumberOfProteins();i++) {
      protAcum += this.getProteinLengths()[i];
      image.getGraphics().setColor(Color.white);
      image.getGraphics().drawLine( namesLabelWidth +protAcum+i, namesLabelWidth + 0, namesLabelWidth+ protAcum+i, namesLabelWidth+ data.getMatrixSize()+this.getNumberOfProteins()-2);
      image.getGraphics().drawLine( namesLabelWidth+ 0, namesLabelWidth+ protAcum+i, namesLabelWidth+ data.getMatrixSize()+this.getNumberOfProteins()-2,namesLabelWidth+protAcum+i );
    }
    ////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////
    // Draw Text
    graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);

    graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

    Color[] colors = new Color[]{new Color(255, 0, 0, 150), new Color(0 , 255, 0, 150)}; 

    int i =0;

    int region_counter = 0;

    int markerRegionWidth = namesLabelWidth;

    int offset = namesLabelWidth;

    for (int len : this.getProteinLengths()) {

      graphics.setColor(colors[i]);

      graphics.fillRect(offset + region_counter , 0 , len, markerRegionWidth);	

      graphics.fillRect(0 ,offset+region_counter , markerRegionWidth , len);

      offset = offset + len;

      region_counter = region_counter+1;

      i=1-i;

    }
    //		
    if (showNames) {

      offset = namesLabelWidth;

      region_counter = 0;

      graphics.setFont(new Font("Arial", 1, 25));

      graphics.setColor(new Color(50,50,170));

      for (int len : this.getProteinLengths()) {

        Rectangle2D textBounds = graphics.getFont().createGlyphVector(((Graphics2D) image.getGraphics()).getFontRenderContext(), this.getNames()[region_counter].trim()).getVisualBounds();

        int textAdv = (int)(len - textBounds.getWidth() ) / 2; 

        int textHeight = (int) (markerRegionWidth + textBounds.getHeight())/2;

        graphics.drawString( this.getNames()[region_counter].trim(), (int) (offset + region_counter + textAdv - textBounds.getMinX()/2), textHeight );

        AffineTransform transformOrig = ((Graphics2D) image.getGraphics()).getTransform();

        graphics.setColor(new Color(50,50,170));

        graphics.rotate(Math.PI/2,markerRegionWidth/2,markerRegionWidth/2);

        graphics.drawString( this.getNames()[region_counter].trim(), (int) (offset + region_counter + textAdv - textBounds.getMinX()/2), textHeight );

        graphics.setTransform(transformOrig);

        offset = offset + len;

        region_counter = region_counter + 1;

      }

    }
    ////////////////////////////////////////////////////////////////////////////
    this.getImagePane().setSize(image.getWidth(), image.getHeight());
    //System.err.println(image.getWidth() + " x " +  image.getHeight());
    this.getImagePane().updateUI();
    return image;
  }

  /**
   * Re-map position to draw, according to the protein the position belongs.
   * 
   * @param pos from 1 to size of matrix
   * @return
   */
  private int translateCoordinate(int pos) {

    int prot = 0;
    int acumSize = 0;
    do {
      acumSize += this.proteinLengths[prot];
      prot++;
    } while (pos > acumSize);

    return pos+prot-2;
  }

  /////////////////////////////////////
  // Getters and Setters
  public CovariationData getMatrix() {
    return data;
  }

  public void setData(CovariationData data) {
    this.data = data;
    if (data.getNumberOfProteins() == 0) {
      this.proteinLengths =  new int[]{this.data.getMatrixSize()};
    } else {
      this.proteinLengths= new int[data.getNumberOfProteins()];
        for (int i = 1 ; i<= data.getNumberOfProteins(); i++) {
          this.proteinLengths[i] = data.getProteinLength(i);
        }
    }
    char[] aa = new char[this.data.getMatrixSize()];

    for (int i =0 ;i<this.data.getMatrixSize();i++) {
      //aa[i] = this.getMatrix().getValue(i+1, i+2).getAa1();
      aa[i] = this.data.getReferenceSequence()[i];

    }
    //aa[this.data.getData().getSize()-1] = this.data.getData().getReferenceSequenceCharAt(this.data.getData().getSize());
    this.setAminoAcids(aa);
    this.accomodateSize();
  }

  public ColorMapper getColor() {
    return (ColorMapper) this.getViewer().getColoringPane().getMatrixColoringModel().getSelectedItem();
    //		return color;
  }

  public void setColor(ColorMapper color) {
    this.color = color;
  }

  public int getNumberOfProteins() {
    return numberOfProteins;
  }

  public void setNumberOfProteins(int numberOfProteins) {
    this.numberOfProteins = numberOfProteins;
  }

  public int[] getProteinLengths() {
    return proteinLengths;
  }

  public void setProteinLengths(int[] proteinLengths) {
    this.setNumberOfProteins(proteinLengths.length);
    this.proteinLengths = proteinLengths;
  }

  protected BufferedImage getImage() {
    return image;
  }

  protected void setImage(BufferedImage image) {
    this.image = image;
  }

  protected ImagePanel getImagePane() {
    return imagePane;
  }

  protected void setImagePane(ImagePanel imagePane) {
    this.imagePane = imagePane;
  }
  public String[] getNames() {
    return names;
  }

  public void setNames(String[] names) {
    this.names = names;
  }

  public MatrixViewMainPane getViewer() {
    return viewer; 

  }

  public void setViewer(MatrixViewMainPane viewer2) {
    this.viewer = viewer2;
  }
  public char[] getAminoAcids() {
    return aminoAcids;
  }

  public void setAminoAcids(char[] aminoAcids) {
    this.aminoAcids = aminoAcids;
  }
  ////////////////////////////////////////////////////////////////////////////
  // Auxiliary Classes
  private class ImagePanel extends JPanel {

    private static final long serialVersionUID = -4046360075583779295L;

    @Override
    public void paintComponent(Graphics g) {
      super.paintComponent(g);
      if (MIMatrixPane.this.data!=null) {
        if (MIMatrixPane.this.getImage()==null) {
          MIMatrixPane.this.setImage(MIMatrixPane.this.createMapImage());
        }
        g.drawImage(MIMatrixPane.this.getImage(), 0, 0, null);

        if (MIMatrixPane.this.hasSelectedRegion) {
          ((Graphics2D)g).setColor(Color.blue);
          ((Graphics2D)g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.3f));
          ((Graphics2D)g).fill(MIMatrixPane.this.selectedRegion);
          ((Graphics2D)g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1f));
          ((Graphics2D)g).draw(MIMatrixPane.this.selectedRegion);
        }

        if (MIMatrixPane.this.showArea) {
          ((Graphics2D)g).setColor(Color.green);
          ((Graphics2D)g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1f));
          ((Graphics2D)g).draw(MIMatrixPane.this.area);
        }
      }

    }
  }

  private class GetValuesFromPointMouseListener implements MouseListener, MouseMotionListener {

    @Override
    public void mousePressed(MouseEvent e) {
      if (e.getButton() == MouseEvent.BUTTON1) {
        int mpx = e.getX();
        int mpy = e.getY();
        if (MIMatrixPane.this.getMatrix()!=null) {
          this.updateSelectedRegion(mpx,mpy);
          MatrixAreaChangedEvent e2 = this.getAreaChangedEvent(mpx, mpy);
          e2.setAction(MatrixAreaChangedEvent.SELECTED_AREA);
          MIMatrixPane.this.notifyAreaMatrixChangedListeners(e2);
        } 
      }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
      int mpx = e.getX();
      int mpy = e.getY();
      if (MIMatrixPane.this.getMatrix()!=null) {
        drawZoomInCursor(mpx,mpy);
        MatrixAreaChangedEvent e2 = this.getAreaChangedEvent(mpx, mpy);
        e2.setAction(MatrixAreaChangedEvent.HOVER_AREA);
        MIMatrixPane.this.notifyAreaMatrixChangedListeners(e2);
      }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
      int mpx = e.getX();
      int mpy = e.getY();
      if (MIMatrixPane.this.getMatrix()!=null) {
        drawZoomInCursor(mpx,mpy);
        MatrixAreaChangedEvent e2 = this.getAreaChangedEvent(mpx, mpy);
        e2.setAction(MatrixAreaChangedEvent.HOVER_AREA);
        MIMatrixPane.this.notifyAreaMatrixChangedListeners(e2);
      }
    }

    @Override
    public void mouseClicked(MouseEvent e) {}
    @Override
    public void mouseReleased(MouseEvent e) {}
    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}

    private MatrixAreaChangedEvent getAreaChangedEvent(int mpx,int mpy) {
      Rectangle rect = new Rectangle();
      int px = mpx - 30;
      int py = mpy - 30;
      px = Math.min(MIMatrixPane.this.data.getMatrixSize() - MIMatrixPane.ZOOM_SIZE, px);
      py = Math.min(MIMatrixPane.this.data.getMatrixSize() - MIMatrixPane.ZOOM_SIZE, py);
      px = Math.max(0, px);
      py = Math.max(0, py);
      int w = Math.min(MIMatrixPane.ZOOM_SIZE, MIMatrixPane.this.data.getMatrixSize() - px);
      int h = Math.min(MIMatrixPane.ZOOM_SIZE, MIMatrixPane.this.data.getMatrixSize() - py);
      rect.setBounds(px, py, w, h);

      double[][] values = new double[w][h];
      char[] hChars = Arrays.copyOfRange(MIMatrixPane.this.getAminoAcids(),px,px+w);
      char[] vChars = Arrays.copyOfRange(MIMatrixPane.this.getAminoAcids(),py,py+h);
      for (int i = 0; i< w;i++) {
        for (int j =0 ; j<h;j++) {
          double value = MIMatrixPane.this.data.getMatrix().getValue(1+px+i, 1 + py + j);
          values[i][j] = value;
        }
      }
      MatrixAreaChangedEvent e = new MatrixAreaChangedEvent();
      e.setRect(rect);
      e.setValues(values);
      e.sethChars(hChars);
      e.setvChars(vChars);

      return e;
    }

    private void updateSelectedRegion(int mpx,int mpy) {
      int px = mpx - 30;
      int py = mpy - 30;
      px = Math.min(MIMatrixPane.this.data.getMatrixSize() - MIMatrixPane.ZOOM_SIZE, px);
      py = Math.min(MIMatrixPane.this.data.getMatrixSize()- MIMatrixPane.ZOOM_SIZE, py);
      px = Math.max(0, px);
      py = Math.max(0, py);
      int w = Math.min(MIMatrixPane.ZOOM_SIZE, MIMatrixPane.this.data.getMatrixSize() - px);
      int h = Math.min(MIMatrixPane.ZOOM_SIZE, MIMatrixPane.this.data.getMatrixSize() - py);
//      rect.setBounds(px, py, w, h);

      Rectangle areaRect = new Rectangle();
      areaRect.setBounds(px+30, py+30, w, h);
      MIMatrixPane.this.selectedRegion = areaRect;
      MIMatrixPane.this.hasSelectedRegion = true;
      MIMatrixPane.this.updateUI();
    }

    private void drawZoomInCursor(int mpx,int mpy) {
//      Rectangle rect = new Rectangle();
      int px = mpx - 30;
      int py = mpy - 30;
      px = Math.min(MIMatrixPane.this.data.getMatrixSize() - MIMatrixPane.ZOOM_SIZE, px);
      py = Math.min(MIMatrixPane.this.data.getMatrixSize() - MIMatrixPane.ZOOM_SIZE, py);
      px = Math.max(0, px);
      py = Math.max(0, py);
      int w = Math.min(MIMatrixPane.ZOOM_SIZE, MIMatrixPane.this.data.getMatrixSize() - px);
      int h = Math.min(MIMatrixPane.ZOOM_SIZE, MIMatrixPane.this.data.getMatrixSize()- py);
//      rect.setBounds(px, py, w, h);

      Rectangle areaRect = new Rectangle();
      areaRect.setBounds(px+30, py+30, w, h);

      MIMatrixPane.this.area = areaRect;
      MIMatrixPane.this.showArea = true;
      MIMatrixPane.this.updateUI();
    }

  }



}
