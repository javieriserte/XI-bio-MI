package org.jiserte.mi.mimatrixviewer;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jiserte.mi.mimatrixviewer.datastructures.CovariationData;

import pair.Pair;

public class GraphicViewerPane extends JTabbedPane implements Observer{

	private static final long serialVersionUID = -3653200229915752191L;
	private Controller controller;
	private List<MIViewingPane> registeredViewingPanes;

	
	
	public GraphicViewerPane(Controller controller, List<Pair<String, MIViewingPane>> tabs) {
		super();
		this.setController(controller);
		this.registeredViewingPanes = new ArrayList<>();
		this.createGUI(tabs);
		
	}

	private void createGUI(List<Pair<String, MIViewingPane>> tabs) {
		
//		MatrixViewMainPane component = new MatrixViewMainPane();
		this.controller.registerModelObserver(this);
		
		

		for (Pair<String, MIViewingPane> tab : tabs) {
	    this.addTab(tab.getFirst(), tab.getSecond());
	    this.registeredViewingPanes.add((MIViewingPane)tab.getSecond());
		}
//		this.addTab("Matrix Viewer", component);
//		this.addTab("Circos Viewer", new CircosViewMainPane());
//		this.addTab("MSA", new MsaViewMainPane());
		
		
		this.addChangeListener(new ChangeListener() {
      
      @Override
      public void stateChanged(ChangeEvent e) {
        GraphicViewerPane p = (GraphicViewerPane) e.getSource();
        ((MIViewingPane) p.getSelectedComponent()).forceDrawing();
      }
    });
		
	}



	public Controller getController() {
		return controller;
	}



	public void setController(Controller controller) {
		this.controller = controller;
	}

	@Override
	public void update(Observable o, Object arg) {
		CovariationData data = ((Model)o).getCurrentData();
		
		
		if (data!=null) {
	    int componentsCount = GraphicViewerPane.this.getComponentCount();
	    for (int i =0 ; i< componentsCount ; i++) {
	      ((MIViewingPane)GraphicViewerPane.this.getComponentAt(i)).setData(data);
	    }

			MIViewingPane selectedComponent = (MIViewingPane)this.getSelectedComponent();
			selectedComponent.forceDrawing();
			
		} 
		
	}
	
	

	
	
}
