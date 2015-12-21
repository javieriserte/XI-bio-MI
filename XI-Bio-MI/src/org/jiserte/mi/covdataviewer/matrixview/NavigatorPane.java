package org.jiserte.mi.covdataviewer.matrixview;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jiserte.mi.mimatrixviewer.MIMatrixViewer;
import org.jiserte.mi.mimatrixviewer.matrixview.MatrixViewMainPane;
import org.jiserte.mi.mimatrixviewer.matrixview_new.colors.ColorMapper;

/**
 * Component that shows a zoom-in portion of a MI matrix.
 * 
 * @author Javier Iserte
 *
 */
public class NavigatorPane extends JScrollPane {

	////////////////////////////////
	// Class Constants
	private static final long serialVersionUID = -5691536429438522274L;
	private static final int CELL_SIZE = 10;
	private static final int CELL_SEP = 1;
	private static final int HEADER_SIZE = 25;
	private static final int FONT_SIZE = 12;
	private static final String FONT_NAME = "Verdana";
	////////////////////////////////
	
	////////////////////////////////
	// Components
	private JPanel imagePanel;
	////////////////////////////////

	////////////////////////////////
	// Instance Variables
	@SuppressWarnings("unused")
	private ColorMapper coloringStrategy;
	private BufferedImage image;
	private double[][] subMatrix;
	private char[] aaSeqHor;
	private char[] aaSeqVer;
	private MatrixViewMainPane viewer;
	////////////////////////////////
	
	////////////////////////////////
	// Constructor
	public NavigatorPane(MatrixViewMainPane matrixViewMainPane) {
		super();
		this.setViewer(matrixViewMainPane);
		this.setImagePanel(new ZoomImagePanel());
		this.getImagePanel().setOpaque(true);
		this.setViewportView(this.getImagePanel());
	}
	////////////////////////////////
	
	////////////////////////////////
	// Public Interface
	/**
	 * This methods erases the current image setting it to null.
	 * This should be used to indicate that a new image must be generated.
	 */
	public void resetImage() {
		this.setImage(null);
	}
	/**
	 * Precalculates the size of an image if it is draw used current settings
	 * and changes the size of the viewport to accomodate it. 
	 */
	public void acomodateSize() {
		int hSize = this.getSubMatrix()[0].length * (NavigatorPane.CELL_SEP+NavigatorPane.CELL_SIZE)+NavigatorPane.CELL_SEP + NavigatorPane.HEADER_SIZE;
		int vSize = this.getSubMatrix().length* (NavigatorPane.CELL_SEP+NavigatorPane.CELL_SIZE)+NavigatorPane.CELL_SEP + NavigatorPane.HEADER_SIZE;
		
		Dimension size = new Dimension(hSize,vSize);
		
		this.getImagePanel().setPreferredSize(size);
		this.getImagePanel().setSize(size);
	}
	/**
	 * Use this method to tell this component that draw a matrix with 
	 * given data.
	 * @param subMatrix
	 */
	public void renderImage(double[][] subMatrix, char[] aaSeqHor, char[] aaSeqVer) {
		this.setSubMatrix(subMatrix);
		this.setAaSeqHor(aaSeqHor);
		this.setAaSeqVer(aaSeqVer);
		this.resetImage();
		this.acomodateSize();
		this.getImagePanel().updateUI();
	}
	////////////////////////////////

	////////////////////////////////
	// Getters and setters
	public ColorMapper getColoringStrategy() {
//		return (ColorMapper) this.getViewer().getColoringPane().getZoomMatrixColoringModel().getSelectedItem();
		return coloringStrategy;
	}

	public void setColoringStrategy(ColorMapper coloringStrategy) {
		this.coloringStrategy = coloringStrategy;
	}

	public BufferedImage getImage() {
		return image;
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}
	protected JPanel getImagePanel() {
		return imagePanel;
	}

	protected void setImagePanel(JPanel imagePanel) {
		this.imagePanel = imagePanel;
	}
	public double[][] getSubMatrix() {
		return subMatrix;
	}

	public void setSubMatrix(double[][] subMatrix) {
		this.subMatrix = subMatrix;
	}
	protected char[] getAaSeqHor() {
		return aaSeqHor;
	}

	protected void setAaSeqHor(char[] aaSeqHor) {
		this.aaSeqHor = aaSeqHor;
	}

	protected char[] getAaSeqVer() {
		return aaSeqVer;
	}

	protected void setAaSeqVer(char[] aaSeqVer) {
		this.aaSeqVer = aaSeqVer;
	}
	///////////////////////////////

	protected MatrixViewMainPane getViewer() {
		return viewer;
	}

	protected void setViewer(MatrixViewMainPane matrixViewMainPane) {
		this.viewer = matrixViewMainPane;
	}

	///////////////////////////////
	// Protected and private methods
	protected void createImage() {
		if (this.getSubMatrix()==null) {
			return;
		}
		
		int hSize = this.getSubMatrix()[0].length * (NavigatorPane.CELL_SIZE + NavigatorPane.CELL_SEP) + NavigatorPane.CELL_SEP + NavigatorPane.HEADER_SIZE;
		int vSize = this.getSubMatrix().length * (NavigatorPane.CELL_SIZE + NavigatorPane.CELL_SEP) + NavigatorPane.CELL_SEP + NavigatorPane.HEADER_SIZE;
		this.setImage(new BufferedImage(hSize, vSize, BufferedImage.TYPE_INT_RGB));
		((Graphics2D)this.getImage().getGraphics()).setColor(Color.white);
		((Graphics2D)this.getImage().getGraphics()).fillRect(0, 0, hSize, vSize);
		////////////////////////////
		// Draw cells
		BufferedImage cells = new BufferedImage(hSize - NavigatorPane.HEADER_SIZE, hSize - NavigatorPane.HEADER_SIZE, BufferedImage.TYPE_INT_RGB);
		Graphics2D cellsGraphics = (Graphics2D) cells.getGraphics();
		cellsGraphics.setColor(Color.gray);
		cellsGraphics.fillRect(0, 0, cells.getWidth(), cells.getHeight());
		for (int i = 0; i< this.getSubMatrix()[0].length; i++ ) {
			for (int j = 0; j< this.getSubMatrix().length; j++ ) {
				Color color = this.getColoringStrategy().getColor(this.subMatrix[i][j]);
				cellsGraphics.setColor(color);
				cellsGraphics.fillRect(
						i*(NavigatorPane.CELL_SIZE+NavigatorPane.CELL_SEP) + NavigatorPane.CELL_SEP, 
						j*(NavigatorPane.CELL_SIZE+NavigatorPane.CELL_SEP) + NavigatorPane.CELL_SEP,
						NavigatorPane.CELL_SIZE, NavigatorPane.CELL_SIZE);
				
			}
		}
		((Graphics2D)this.getImage().getGraphics()).drawImage(cells, NavigatorPane.HEADER_SIZE, NavigatorPane.HEADER_SIZE, null);
		////////////////////////////
		
		////////////////////////////
		// Draw Grid
		// No nothing really.
		// Grid is background not recolored
		////////////////////////////
		
		////////////////////////////
		// Draw Headers
		BufferedImage horHeader = new BufferedImage(cells.getWidth(), NavigatorPane.HEADER_SIZE, BufferedImage.TYPE_INT_RGB);
		BufferedImage verHeader = new BufferedImage(NavigatorPane.HEADER_SIZE, cells.getHeight(),BufferedImage.TYPE_INT_RGB);
		Graphics2D hhGraphics = (Graphics2D) horHeader.getGraphics();
		Graphics2D vhGraphics = (Graphics2D) verHeader.getGraphics();
		hhGraphics.setColor(Color.white);
		hhGraphics.fillRect(0, 0, horHeader.getWidth(), horHeader.getHeight());
		vhGraphics.setColor(Color.white);
		vhGraphics.fillRect(0, 0, verHeader.getWidth(), verHeader.getHeight());
		
		hhGraphics.setFont(new Font(NavigatorPane.FONT_NAME, 0, NavigatorPane.FONT_SIZE));
		hhGraphics.setColor(Color.black);
		vhGraphics.setFont(new Font(NavigatorPane.FONT_NAME, 0, NavigatorPane.FONT_SIZE));
		vhGraphics.setColor(Color.black);
		
		for (int i = 0; i < this.getSubMatrix()[0].length; i++) {
			
			String currentChar = String.valueOf(this.getAaSeqHor()[i]);
			Rectangle2D textBounds = hhGraphics.getFont().createGlyphVector(hhGraphics.getFontRenderContext(),currentChar).getVisualBounds();
			long posX = Math.round ((i * (NavigatorPane.CELL_SIZE + NavigatorPane.CELL_SEP) + NavigatorPane.CELL_SEP + (NavigatorPane.CELL_SIZE - textBounds.getWidth()) / 2 - textBounds.getMinX()));
			long posY = Math.round(NavigatorPane.HEADER_SIZE - textBounds.getMaxY() - NavigatorPane.HEADER_SIZE / 2 + textBounds.getHeight()/2);
            hhGraphics.drawString( currentChar, (int) posX, (int) (posY));
			
		}
		
		for (int i = 0; i < this.getSubMatrix().length; i++) {
			
			String currentChar = String.valueOf(this.getAaSeqVer()[i]);
			Rectangle2D textBounds = vhGraphics.getFont().createGlyphVector(vhGraphics.getFontRenderContext(),currentChar).getVisualBounds();
			long posX = Math.round((NavigatorPane.HEADER_SIZE - textBounds.getWidth() ) / 2 - textBounds.getMinX());
			long posY = Math.round(i * (NavigatorPane.CELL_SIZE+NavigatorPane.CELL_SEP)  - textBounds.getMaxY() + NavigatorPane.CELL_SIZE / 2 + textBounds.getHeight()/2);
			vhGraphics.drawString( currentChar, (int)posX, (int) posY);
			
		}
		this.getImage().getGraphics().drawImage(horHeader, NavigatorPane.HEADER_SIZE, 0, null);
		this.getImage().getGraphics().drawImage(verHeader, 0, NavigatorPane.HEADER_SIZE, null);
		////////////////////////////
	}
	///////////////////////////////
	/////////////////////////////////
	// Auxiliary Classes
	private class ZoomImagePanel extends JPanel {
		/**
		 * 
		 */
		private static final long serialVersionUID = -7709385439488238368L;

		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			if (NavigatorPane.this.getImage()==null) {
				NavigatorPane.this.createImage();
			}
			Graphics2D graphics2d = (Graphics2D)g;
			graphics2d.drawImage(NavigatorPane.this.getImage(), 0, 0,null);
			graphics2d.setColor(Color.red);
		}
	}
	/////////////////////////////////
}
