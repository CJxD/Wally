package com.cjwatts.wally.analysis.feature;

public class FacialHairColour extends Feature {
	
	private static FacialHairColour instance;
	
	public static FacialHairColour getInstance() {
		return instance == null ? new FacialHairColour() : instance;
	}
	
}
