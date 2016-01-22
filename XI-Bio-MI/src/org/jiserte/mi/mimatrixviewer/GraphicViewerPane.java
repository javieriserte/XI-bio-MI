package org.jiserte.mi.mimatrixviewer;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jiserte.mi.mimatrixviewer.circosview.CircosViewMainPane;
import org.jiserte.mi.mimatrixviewer.datastructures.CovariationData;
import org.jiserte.mi.mimatrixviewer.matrixview_new.MatrixViewMainPane;

public class GraphicViewerPane extends JTabbedPane implements Observer{

	private static final long serialVersionUID = -3653200229915752191L;
	private Controller controller;
	private List<MIViewingPane> registeredViewingPanes;

	
	
	public GraphicViewerPane(Controller controller) {
		super();
		this.setController(controller);
		this.registeredViewingPanes = new ArrayList<>();
		this.createGUI();
		
	}

	private void createGUI() {
		
		MatrixViewMainPane component = new MatrixViewMainPane();
		this.controller.registerModelObserver(this);
		
		this.registeredViewingPanes.add(component);

		this.addTab("Matrix Viewer", component);
		this.addTab("Circos Viewer", new CircosViewMainPane());
//		this.addTab("Bot�n #3", new JButton("bot�n 3"));
		
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
