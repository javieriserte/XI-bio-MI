package org.jiserte.mi.mimatrixviewer.view.colors;

import java.awt.Color;
import java.util.Arrays;

import datatypes.range.Range;

public class GradientColoringStrategy implements ColoringStrategy {

  //////////////////////////////////////////////////////////////////////////////
  // Instance Variables
	private Range<Double> range;
  private boolean useHSBGradient;
	private Color initial;
	private Color end;
  //////////////////////////////////////////////////////////////////////////////
	
	public GradientColoringStrategy(Range<Double> range, Color initial,
			Color end, boolean useHSBGradient ) {
		super();
		this.setUseHSBGradient(useHSBGradient);
		this.setRange(range);
		this.setInitial(initial);
		this.setEnd(end);
	}

  @Override
	public Color getColor(double value) {
		
		double f = (value - getRange().getLowerBound()) / (getRange().getUpperBound()-getRange().getLowerBound());
		
    return GradientColoringStrategy.getColorByFraction(this.getInitial(), this.getEnd(), f, this.getUseHSBGradient());
		
	}

  /**
	 * This class method calculates the Color in a gradient between a lower bound 
	 * color and an upper bound color, for a given fraction between the first 
	 * color and the second color:
	 * 
	 * <blockquote>
	 * newColor = lowerColor * (1-f) + upperColor * f
	 * </blockquote>
	 * The transformation is linear and can be done in RBG of HSB color spaces.
	 * The color returned is always and RBG color.
	 * @param lower
	 * @param upper
	 * @param f
	 * @return
	 */
	public static Color getColorByFraction(Color lower, Color upper, double f, 
	   boolean useHSBcolorSpace) {
	   ///////////////////////////////////////////////////////////////////////////
	   // Gradient in HSB Color Space
	   if (useHSBcolorSpace) {
	     /////////////////////////////////////////////////////////////////////////
	     // Maps color from RGB color space to HSB color space
	     float hsbComps[] = new float[3];
       Float[][] hsbColors = Arrays.stream(new Color[]{lower,upper})
           .map(p ->  Color.RGBtoHSB(p.getRed(), p.getGreen(), p.getBlue(),
               null))
           .map(p -> new Float[]{p[0],p[1],p[2]})
           .toArray(Float[][]::new);
       // Calculates the gradient in HSV Color Space
       for (int i=0 ; i< hsbComps.length; i++) {
         hsbComps[i] = (float) (hsbColors[0][i] * (1-f) + f * hsbColors[1][i]);
       }
       // Create and return a new color with with the HSV values
       return Color.getHSBColor(hsbComps[0], hsbComps[1], hsbComps[2]);
       /////////////////////////////////////////////////////////////////////////
	   }
     ///////////////////////////////////////////////////////////////////////////
     // Gradient in RGB Color Space 
	   else {
	     /////////////////////////////////////////////////////////////////////////
	     // Collect colors component values
	     Integer[][] colors = Arrays.stream(new Color[]{lower,upper})
	         .map(p -> new Integer[]{p.getRed(), p.getGreen(), p.getBlue()})
	         .toArray(Integer[][]::new);
	     // Calculates gradient
	     int[] rgbComps = new int[3];
       for (int i=0 ; i< rgbComps.length; i++) {
         rgbComps[i] = (int) (colors[0][i] * (1-f) + f * colors[1][i]);
       }
       //
       // Create and return a new color with with the RGB values
       return new Color (rgbComps[0], rgbComps[1], rgbComps[2]);
       /////////////////////////////////////////////////////////////////////////
	   }
     ///////////////////////////////////////////////////////////////////////////
	  
	}
	
	@Override
	public boolean isValidValue(double value) {
		
		return this.getRange().inRange(value);
		
	}
	
	public boolean getUseHSBGradient() {
	    return this.useHSBGradient;
	  }

  private void setUseHSBGradient(boolean useHSBGradient) {
    this.useHSBGradient = useHSBGradient;    
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
