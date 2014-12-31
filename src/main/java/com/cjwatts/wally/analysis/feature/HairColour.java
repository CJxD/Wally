package com.cjwatts.wally.analysis.feature;

public class HairColour extends Feature {
	
	private static HairColour instance;
	
	public static HairColour getInstance() {
		return instance == null ? new HairColour() : instance;
	}
	
}
