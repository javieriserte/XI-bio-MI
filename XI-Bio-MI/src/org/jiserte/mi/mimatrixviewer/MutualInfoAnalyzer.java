package org.jiserte.mi.mimatrixviewer;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import org.jiserte.mi.mimatrixviewer.view.circosview.CircosViewMainPane;
import org.jiserte.mi.mimatrixviewer.view.matrixview.MatrixViewMainPane;
import org.jiserte.mi.mimatrixviewer.view.msaview.MsaArea;
import org.jiserte.mi.mimatrixviewer.view.msaview.MsaSelectionEvent;
import org.jiserte.mi.mimatrixviewer.view.msaview.MsaSelectionListener;
import org.jiserte.mi.mimatrixviewer.view.msaview.MsaViewMainPane;
import org.jiserte.mi.mimatrixviewer.view.positives.PositiveDataMainViewingPane;

import pair.Pair;

public class MutualInfoAnalyzer extends JFrame {

	private static final long serialVersionUID = -4941234241763767073L;
	
	private GeneralDataPane generalDataPane;
	private GraphicViewerPane graphicViewerPane;
	private Model model;
	private Controller controller;
	
	
	public static void main(String[] args) {
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				
				Model model = new Model();
				
				MutualInfoAnalyzer inst = new MutualInfoAnalyzer(model, new Controller(model));
					// creates the main instance
				
				
				inst.setVisible(true);
				inst.setPreferredSize(new Dimension(1024,768));
				inst.setSize(new Dimension(1024,768));
				inst.setLocationRelativeTo(null);
				inst.setTitle("Mutual Info Analyzer");
				
				inst.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
				
				inst.pack();
				
				
			}
		});

	}
	
	
	////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTOR
	public MutualInfoAnalyzer(Model model, Controller controller) {
		
		super();
		this.setModel(model);		
		this.setController(controller);
		this.createGUI();
		
	}


	private void createGUI() {
		
		try {

			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	        	// Set System L&F
			this.createPanes();
				// Brings the main pane to screen

		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}


	private void createPanes() {
		
		GridBagLayout layout = new GridBagLayout();
		layout.columnWeights = new double[]{0,1};
		layout.columnWidths = new int[]{300,0};
		layout.rowHeights = new int[]{0};
		layout.rowWeights = new double[]{1};
		
		this.setLayout(layout);
		
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridheight = 1;
		constraints.gridwidth = 1;
		constraints.gridy = 0;
		constraints.gridx = 0;
		
		this.generalDataPane = new GeneralDataPane(this.getController());
		
	  this.add(this.generalDataPane,constraints);
		
//		this.setMinimumSize(new Dimension(512 ,300 ));
		this.setPreferredSize(new Dimension(1024 ,768 ));

		constraints.gridx = 1;
		
		List<Pair<String, MIViewingPane>> tabs = new ArrayList<>();
		tabs.add(new Pair<>("Matrix Viewer", new MatrixViewMainPane()));
		tabs.add(new Pair<>("Circos Viewer", new CircosViewMainPane()));
		tabs.add(new Pair<>("Positives", new PositiveDataMainViewingPane()));
		MsaViewMainPane msaViewer = new MsaViewMainPane();
		msaViewer.addMsaSelectionListener(new MsaSelectionListener() {
      
      @Override
      public void msaSelectionDone(MsaSelectionEvent event) {
        MsaArea selection = event.getSelection();
        if (event.getSelection() != null) {
          String msg = "Sel["+selection.getLeft()+" "+ selection.getTop() + " " + selection.getRight() + " " + selection.getBottom()+"]";
          MutualInfoAnalyzer.this.generalDataPane.setInfo(msg);
        } else {
          MutualInfoAnalyzer.this.generalDataPane.setInfo("Nothing Selected");
        }
      }
    });
		
    tabs.add(new Pair<>("MSA viewer", msaViewer));
		
		this.graphicViewerPane = new GraphicViewerPane(this.getController(), tabs);
		
		this.add(this.graphicViewerPane,constraints);
		
	}
	
	public Model getModel() {
		return this.model;
	}


	public void setModel(Model model) {
		this.model= model;
	}


	public Controller getController() {
		return controller;
	}


	public void setController(Controller controller) {
		this.controller = controller;
	}
	
}
