package org.jiserte.mi.misticmod.onepixelmap;

import io.onelinelister.LineParser;

import java.util.List;

import org.jiserte.mi.misticmod.datastructures.MI_PositionWithProtein;

////////////////////////////////////////////////////////////////////////////
// Auxiliary classes
class MI_PosLinePaser implements LineParser<MI_PositionWithProtein>{

	List<Integer> lengths;
	
	protected void setLengths(List<Integer> lengths) {
		
		this.lengths = lengths;
		
	}

	@Override
	public MI_PositionWithProtein parse(String line) {

		MI_PositionWithProtein pos = MI_PositionWithProtein.valueOf(line);
		
		pos.assignProteinNumber(lengths);
		
		return pos;
		
	}
	
}
////////////////////////////////////////////////////////////////////////////