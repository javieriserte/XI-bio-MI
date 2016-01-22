package org.jiserte.mi.mimatrixviewer;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import org.jiserte.mi.mimatrixviewer.datastructures.CovariationData;

public class Model extends Observable {

	////////////////////////////////////////////////////////////////////////////
	// Instance Variables
	private List<CovariationData> data;
	private CovariationData currentData;
	private boolean dataChanged;
	////////////////////////////////////////////////////////////////////////////

	////////////////////////////////////////////////////////////////////////////
	// Contructor
	public Model() {
		
		this.setData(new ArrayList<CovariationData>());
		this.setCurrentData(null);
		
	}
	////////////////////////////////////////////////////////////////////////////
	
	
	////////////////////////////////////////////////////////////////////////////
	// Public interface
	public List<CovariationData> getData() {
		return data;
	}
	public void setData(List<CovariationData> data) {
		this.data = data;
		this.setChanged();
		this.setDataChanged(true);
		this.notifyObservers();
		this.clearChanged();
	}
	public CovariationData getCurrentData() {
		return currentData;
	}
	public void setCurrentData(CovariationData currentData) {
		this.currentData = currentData;
		this.setChanged();
		this.setDataChanged(false);
		this.notifyObservers();
		this.clearChanged();
	}
	public void addDataContainer(CovariationData data) {
		this.getData().add(data);
		this.setChanged();
		this.setDataChanged(true);
		this.notifyObservers();
		this.clearChanged();
	}
	public boolean hasDataChanged () {
	  return this.dataChanged;
	}
	////////////////////////////////////////////////////////////////////////////
	
	private void setDataChanged(boolean value) {
	  this.dataChanged = value;
	}
	
}
