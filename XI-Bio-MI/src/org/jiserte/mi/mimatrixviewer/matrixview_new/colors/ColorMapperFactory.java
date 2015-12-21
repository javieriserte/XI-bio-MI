package org.jiserte.mi.mimatrixviewer.matrixview_new.colors;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import datatypes.range.Range;

import org.jiserte.mi.mimatrixviewer.MI_Matrix;

public class ColorMapperFactory {

	public static ColorMapper getBlueRedForMatrix(MI_Matrix matrix, double cutoff) {
		
		double min = matrix.getMinZscore();
		
		double max = matrix.getMaxZscore();
		
		List<ColoringStrategy> coloringMethods = new ArrayList<>();
		
		if (min<cutoff) {
			
			Range<Double> range = new Range<Double>(min, cutoff, true, false);
			coloringMethods.add(new GradientColoringStrategy(range, Color.blue, Color.black));
			
		}
		
		if (max >= cutoff) {
			
			Range<Double> range = new Range<Double>(cutoff, max, true, true);
			coloringMethods.add(new GradientColoringStrategy(range,  Color.black, Color.red));
			
		}
		
		coloringMethods.add(new SolidColoringStrategy(new Range<Double>(MI_Matrix.UNDEFINED, MI_Matrix.UNDEFINED, true, true), Color.green));
		
		ColorMapper mapper = new ColorMapper(coloringMethods);
		
		return mapper;
		
	}
	
public static ColorMapper getContinuousForMatrix(MI_Matrix matrix, Color lower, Color higher) {
    
    double min = matrix.getMinZscore();
    
    double max = matrix.getMaxZscore();
    
    List<ColoringStrategy> coloringMethods = new ArrayList<>();
    
    Range<Double> range = new Range<Double>(min, max, true, true);
    coloringMethods.add(new GradientColoringStrategy(range, lower, higher));
    
    coloringMethods.add(new SolidColoringStrategy(new Range<Double>(MI_Matrix.UNDEFINED, MI_Matrix.UNDEFINED, true, true), Color.green));
    
    ColorMapper mapper = new ColorMapper(coloringMethods);
    
    return mapper;
    
  }

public static ColorMapper getPercentilForMatrix(MI_Matrix matrix, Color lower, Color higher, int levels) {
  
  double[] sorted = Arrays.copyOf( matrix.getZscore(), matrix.getZscore().length);
  
  Arrays.sort(sorted);
  
  int li = 0;
  while(li< sorted.length && sorted[li] == MI_Matrix.UNDEFINED) {
    li++;
  }

  List<ColoringStrategy> coloringMethods = new ArrayList<>();

  boolean first= true;
  
  int low = li;

  for (int i = 1 ; i<= levels ; i++) {
    
    int currentIndex =  (int) (li + i * (sorted.length -1  - li) / levels);
    
    Range<Double> range = new Range<Double>(sorted[low], sorted[currentIndex], first, true);
    
    double f = (double) (i-1) / (double) (levels-1);
    
    Color col = GradientColoringStrategy.getColorByFraction(lower, higher, f );
    
    coloringMethods.add(new SolidColoringStrategy(range, col));
    first = false;
    low = currentIndex;
    
  }
  
  
  
  
  
  coloringMethods.add(new SolidColoringStrategy(new Range<Double>(MI_Matrix.UNDEFINED, MI_Matrix.UNDEFINED, true, true), Color.green));
  
  ColorMapper mapper = new ColorMapper(coloringMethods);
  
  return mapper;
  
}

	
	public static ColorMapper getBlackWhiteForZoom(MI_Matrix matrix, double cutoff) {
		
		double min = matrix.getMinZscore();
		
		double max = matrix.getMaxZscore();
		
		List<ColoringStrategy> coloringMethods = new ArrayList<>();
		
		if (min<cutoff) {
			
			Range<Double> range = new Range<Double>(min, cutoff, true, false);
			coloringMethods.add(new SolidColoringStrategy(range, Color.white));
			
		}
		
		if (max >= cutoff) {
			
			Range<Double> range = new Range<Double>(cutoff, max, true, true);
			coloringMethods.add(new SolidColoringStrategy(range, Color.black));
			
		}
		
		coloringMethods.add(new SolidColoringStrategy(new Range<Double>(MI_Matrix.UNDEFINED, MI_Matrix.UNDEFINED, true, true), Color.green));
		
		ColorMapper mapper = new ColorMapper(coloringMethods);
		
		return mapper;
		
	}
	
	
}
