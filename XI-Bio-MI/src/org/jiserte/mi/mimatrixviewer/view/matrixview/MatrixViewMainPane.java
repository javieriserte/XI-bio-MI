package org.jiserte.mi.mimatrixviewer.view.matrixview;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Rectangle;

import javax.swing.JSplitPane;

import org.jiserte.mi.mimatrixviewer.MIViewingPane;
import org.jiserte.mi.mimatrixviewer.datastructures.CovariationData;
import org.jiserte.mi.mimatrixviewer.view.colors.ColorMapperFactory;

public class MatrixViewMainPane extends MIViewingPane{


	//////////////////////////////////////////////////////////////////////////////
	// Class constants
	private static final long serialVersionUID = -1703838873771510760L;
	//////////////////////////////////////////////////////////////////////////////
	
	//////////////////////////////////////////////////////////////////////////////
	// Instance variables
	private CovariationData data;
	//////////////////////////////////////////////////////////////////////////////
	
	//////////////////////////////////////////////////////////////////////////////
	// Components
	private MIMatrixPane            matrixPane;
	private ZoomPanel                zoomPanel;
	private ColoringSelectionPane coloringPane;
	//////////////////////////////////////////////////////////////////////////////


	public MatrixViewMainPane() {
		super();
		this.setMatrixPane(new MIMatrixPane(this));
		this.setZoomPanel(new ZoomPanel(this));
		this.setColoringPane(new ColoringSelectionPane(this));

		this.setLayout(new BorderLayout());
		
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		
		splitPane.add(this.getZoomPanel());
		
		splitPane.add(this.getMatrixPane());
		
		this.getMatrixPane().addMatrixChangedListener(new MatrixAreaChangedListener() {
			
			@Override
			public void matrixAreaChanged(MatrixAreaChangedEvent e) {
			  if (e.getAction() == MatrixAreaChangedEvent.SELECTED_AREA) {
  				MatrixViewMainPane.this.zoomArea(
  						e.getRect(),
  						e.getValues(),
  						e.gethChars(),
  						e.getvChars());
  			}
			}
		});

		this.add(splitPane, BorderLayout.CENTER);
		this.add(this.getColoringPane(),BorderLayout.SOUTH);
		
		splitPane.setDividerLocation(200);

		
	}

	/**
	 * @return the matrixPane
	 */
	public MIMatrixPane getMatrixPane() {
		return matrixPane;
	}

	/**
	 * @param matrixPane the matrixPane to set
	 */
	protected void setMatrixPane(MIMatrixPane matrixPane) {
		this.matrixPane = matrixPane;
	}

	/**
	 * @return the zoomPanel
	 */
	protected ZoomPanel getZoomPanel() {
		return zoomPanel;
	}

	/**
	 * @param zoomPanel the zoomPanel to set
	 */
	protected void setZoomPanel(ZoomPanel zoomPanel) {
		this.zoomPanel = zoomPanel;
	}

	/**
	 * @return the coloringPane
	 */
	protected ColoringSelectionPane getColoringPane() {
		return coloringPane;
	}

	/**
	 * @param coloringPane the coloringPane to set
	 */
	protected void setColoringPane(ColoringSelectionPane coloringPane) {
		this.coloringPane = coloringPane;
	}
	

	public void zoomArea(Rectangle rect, double[][] values, char[] hChars, char[] vChars) {
		this.getZoomPanel().renderImage(values, hChars, vChars);
		
	}

	public void setProteinLengths(int[] protLengths) {
		
	}

	@Override
	public void setData(CovariationData data) {
		this.data = data;
		
		this.getMatrixPane().setData(data);
		
		this.getColoringPane().getMatrixColoringModel().removeAllElements();
    this.getColoringPane().addMatrixColoringStrategy(
        ColorMapperFactory.getPercentilForMatrix(this.data.getMatrix(), 
            Color.yellow.darker().darker(), Color.red, 5,true));
    this.getColoringPane().addMatrixColoringStrategy(
        ColorMapperFactory.getContinuousForMatrix(this.data.getMatrix(), 
            Color.BLACK, Color.red,false));
    this.getColoringPane().addMatrixColoringStrategy(
        ColorMapperFactory.getBlueRedForMatrix(this.data.getMatrix(), 6.5d));
//		
		this.getColoringPane().getZoomMatrixColoringModel().removeAllElements();
		this.getColoringPane().addZoomMatrixColoringStrategy(
		    ColorMapperFactory.getBlackWhiteForZoom(this.data.getMatrix(), 6.5d));
//		this.getColoringPane().addZoomMatrixColoringStrategy(ZoomMatrixColoringStrategyFactory.BlackAndWhiteWithCutoff10());
		
	}

	@Override
	public void forceDrawing() {
		
		this.getMatrixPane().resetImage();
		
	}

}
