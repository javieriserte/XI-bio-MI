package org.jiserte.mi.mimatrixviewer.matrixview_new.colors;

import java.awt.Color;

import datatypes.range.Range;

public class GradientColoringStrategy implements ColoringStrategy {

	private Range<Double> range;

	private Color initial;
	private Color end;
	
	public GradientColoringStrategy(Range<Double> range, Color initial,
			Color end) {
		super();
		this.setRange(range);
		this.setInitial(initial);
		this.setEnd(end);
	}

	@Override
	public Color getColor(double value) {
		
		double f = (value - getRange().getLowerBound()) / (getRange().getUpperBound()-getRange().getLowerBound());
		
    return GradientColoringStrategy.getColorByFraction(this.getInitial(), this.getEnd(), f);
		
	}
	
	public static Color getColorByFraction(Color lower, Color upper, double f) {
	  
	    int r = (int) (lower.getRed() * (1-f) + f * (upper.getRed()));
	    int g = (int) (lower.getGreen() * (1-f) + f * (upper.getGreen()));
	    int b = (int) (lower.getBlue() * (1-f) + f * (upper.getBlue()));
	    //System.out.println(r + "," +g + "," + b);
	    Color color;
	    try {
	      color = new Color(r, g, b);
	    } catch (Exception e) {
	      color = Color.black;  
	    }
	    return color;
	  
	}

	@Override
	public boolean isValidValue(double value) {
		
		return this.getRange().inRange(value);
		
	}

	private Color getInitial() {
		return initial;
	}

	private void setInitial(Color initial) {
		this.initial = initial;
	}

	private Color getEnd() {
		return end;
	}

	private void setEnd(Color end) {
		this.end = end;
	}

	private Range<Double> getRange() {
		return range;
	}

	
	private void setRange(Range<Double> range) {
		this.range = range;
	}

  @Override
  public double min() {
    return this.range.getLowerBound();
  }

  @Override
  public double max() {
    // TODO Auto-generated method stub
    return this.range.getUpperBound();
  }

}
