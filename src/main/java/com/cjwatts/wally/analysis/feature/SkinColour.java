package com.cjwatts.wally.analysis.feature;

public class SkinColour extends Feature {
	
	private static SkinColour instance;
	
	public static SkinColour getInstance() {
		return instance == null ? new SkinColour() : instance;
	}
	
}
