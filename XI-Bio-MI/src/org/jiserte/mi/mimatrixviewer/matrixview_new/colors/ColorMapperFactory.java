package org.jiserte.mi.mimatrixviewer.matrixview_new.colors;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import datatypes.range.Range;

import org.jiserte.mi.mimatrixviewer.MI_Matrix;

/**
 * This class provies many built-in ColorMappers for covariation data.
 * @author javier
 *
 */
public class ColorMapperFactory {

  /**
   * A color mapper for covariation data intended to be used in the main
   * matrix visual representation. Blue to black gradient from minimum value to
   * a given cutoff value. Black to red from the cutoff value to the maximum 
   * value. Green for undefined values.
   *   
   * @param matrix is the data matrix, required to calculate min and max values.
   * @param cutoff is a value that defines the limit between the red and blue 
   * gradients. Typically 6.5 for MI.
   * @return
   */
	public static ColorMapper getBlueRedForMatrix(MI_Matrix matrix, 
	    double cutoff) {

	  ////////////////////////////////////////////////////////////////////////////
	  // Get min and max values
		double min = matrix.getMinZscore();
		double max = matrix.getMaxZscore();
    ////////////////////////////////////////////////////////////////////////////
		
		////////////////////////////////////////////////////////////////////////////
		// Array to store the coloring strategies for this color mapper
		List<ColoringStrategy> coloringMethods = new ArrayList<>();
    ////////////////////////////////////////////////////////////////////////////
		
		////////////////////////////////////////////////////////////////////////////
		// If cutoff is lower then min or cut off is greater than max, the only one
		// gradient is used
		if (min<cutoff) {
			Range<Double> range = new Range<Double>(min, cutoff, true, false);
			coloringMethods.add(new GradientColoringStrategy(range, Color.blue,
			    Color.black,false));
		}
		if (max >= cutoff) {
			Range<Double> range = new Range<Double>(cutoff, max, true, true);
			coloringMethods.add(new GradientColoringStrategy(range,  Color.black,
			    Color.red,false));
		}
    ////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////
    // Color strategy for undefined values
		coloringMethods.add(new SolidColoringStrategy(new Range<Double>(
		    MI_Matrix.UNDEFINED, MI_Matrix.UNDEFINED, true, true), Color.green));
    ////////////////////////////////////////////////////////////////////////////

		////////////////////////////////////////////////////////////////////////////
		// Creates and return the mapper
		ColorMapper mapper = new ColorMapper(coloringMethods);
		return mapper;
    ////////////////////////////////////////////////////////////////////////////
		
	}
  /**
   * A color mapper for covariation data intended to be used in the main
   * matrix visual representation. Is a single gradient from two given colors.
   * Green is used for undefined values.
   *   
   * @param matrix is the data matrix, required to calculate min and max values.
   * @lower is the color for the lowest value.
   * @higher is the color for the highest value.
   * @return
   */
  public static ColorMapper getContinuousForMatrix(MI_Matrix matrix,
      Color lower, Color higher, boolean useHSBGradient) {
    
    ////////////////////////////////////////////////////////////////////////////
    // Get min and max values
    double min = matrix.getMinZscore();
    double max = matrix.getMaxZscore();
    ////////////////////////////////////////////////////////////////////////////
    
    ////////////////////////////////////////////////////////////////////////////
    // Array to store the coloring strategies for this color mapper
    List<ColoringStrategy> coloringMethods = new ArrayList<>();
    ////////////////////////////////////////////////////////////////////////////
    
    ////////////////////////////////////////////////////////////////////////////
    // Color strategy for the main gradient 
    Range<Double> range = new Range<Double>(min, max, true, true);
    coloringMethods.add(new GradientColoringStrategy(range, lower, higher, 
        useHSBGradient));
    ////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////
    // Color strategy for undefined values
    coloringMethods.add(new SolidColoringStrategy(new Range<Double>(
        MI_Matrix.UNDEFINED, MI_Matrix.UNDEFINED, true, true), Color.green));
    ////////////////////////////////////////////////////////////////////////////
    
    ////////////////////////////////////////////////////////////////////////////
    // Creates and returns the color mapper
    ColorMapper mapper = new ColorMapper(coloringMethods);
    return mapper;
    ////////////////////////////////////////////////////////////////////////////
    
  }

  /**
   * Color mapper for percentiles of data.
   * Values are sorted and divided into n levels. Each level is represented by
   * a color. The color is calculated from a gradient between two colors 
   * assigned to the min and the max values. Green is used to the undefined 
   * values.
   *  
   * @param matrix
   * @param lower
   * @param higher
   * @param levels
   * @return
   */
  public static ColorMapper getPercentilForMatrix(MI_Matrix matrix, Color lower,
      Color higher, int levels, boolean useHSBGradient) {
  
    ////////////////////////////////////////////////////////////////////////////
    // Copy the Z-Score data from the MI Matrix to sort the values. 
    double[] sorted = Arrays.copyOf( matrix.getZscore(), 
        matrix.getZscore().length);
    Arrays.sort(sorted);
    ////////////////////////////////////////////////////////////////////////////
  
    ////////////////////////////////////////////////////////////////////////////
    // From the sorted values, get the index of the lower defined value. 
    int li = 0;
    while(li< sorted.length && sorted[li] == MI_Matrix.UNDEFINED) {
      li++;
    }
    ////////////////////////////////////////////////////////////////////////////
  
    ////////////////////////////////////////////////////////////////////////////
    // Array to store the coloring strategies for this color mapper
    List<ColoringStrategy> coloringMethods = new ArrayList<>();
    ////////////////////////////////////////////////////////////////////////////
    
    ////////////////////////////////////////////////////////////////////////////
    // Add a ColorStrategy for each percentile level.
    // All ColorStrategy are gradients with and open/closed range. Except the
    // first one, that is closed/closed:
    // [range 1]-(range 2]-(range 3]-(range 4].
    boolean first= true;
    // li is the index of the lowest defined value.
    int lowerIndex = li;
    // Apply to each percentile level. 
    for (int i = 1 ; i<= levels ; i++) {
      // Get an index for the upper limit of the percentile level.
      int upperIndex =  (int) (li + i * (sorted.length -1  - li) / levels);
      // Calculates the advance in the gradient for the current percentile level.
      double f = (double) (i-1) / (double) (levels-1);
      // Creates a SolidColoringStrategy for the current percentile. 
      Range<Double> range = new Range<Double>(sorted[lowerIndex],
          sorted[upperIndex], first, true);
      Color col = GradientColoringStrategy.getColorByFraction(lower, higher, f,
          useHSBGradient);
      // Add the current color strategy to the mapper
      coloringMethods.add(new SolidColoringStrategy(range, col));
      // Update first. From the second percentile must be false to create 
      // open/closed ranges for the color strategies.
      first = false;
      // update the lower index to value of the upper index
      lowerIndex = upperIndex ;
    }
    
    ////////////////////////////////////////////////////////////////////////////
    // Add a color strategy for undefined values 
    coloringMethods.add(new SolidColoringStrategy(new Range<Double>(
        MI_Matrix.UNDEFINED, MI_Matrix.UNDEFINED, true, true), Color.green));
    ////////////////////////////////////////////////////////////////////////////
    
    ////////////////////////////////////////////////////////////////////////////
    // Creates and returns the mapper
    ColorMapper mapper = new ColorMapper(coloringMethods);
    return mapper;
    //////////////////////////////////////////////////////////////////////////////
  
  }

  /**
   * A Black and White Color mapper
   * Color below a cutoff are white, color above are black.
   * Undefined values are green.
   * 
   * @param matrix
   * @param cutoff
   * @return
   */
	public static ColorMapper getBlackWhiteForZoom(MI_Matrix matrix, 
	    double cutoff) {
		
	  ////////////////////////////////////////////////////////////////////////////
	  // Get the minimum and the maximum values.
		double min = matrix.getMinZscore();
		double max = matrix.getMaxZscore();
    ////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////
    // Array to store the color strategies for this mapper
		List<ColoringStrategy> coloringMethods = new ArrayList<>();
    ////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////
		// If cutoff value is lower than minimum value or cutoff value is higher
		// than maximum value only one color strategy is used.
		if (min<cutoff) {
			Range<Double> range = new Range<Double>(min, cutoff, true, false);
			coloringMethods.add(new SolidColoringStrategy(range, Color.white));
		}
		if (max >= cutoff) {
			Range<Double> range = new Range<Double>(cutoff, max, true, true);
			coloringMethods.add(new SolidColoringStrategy(range, Color.black));
		}
    ////////////////////////////////////////////////////////////////////////////
		
		////////////////////////////////////////////////////////////////////////////
		// Color strategy for undefined values
		coloringMethods.add(new SolidColoringStrategy(new Range<Double>(
		    MI_Matrix.UNDEFINED, MI_Matrix.UNDEFINED, true, true), Color.green));
    ////////////////////////////////////////////////////////////////////////////
		
		////////////////////////////////////////////////////////////////////////////
		// Creates and returns the color mapper
		ColorMapper mapper = new ColorMapper(coloringMethods);
		return mapper;
    ////////////////////////////////////////////////////////////////////////////
		
	}
	
	
}
