package com.cjwatts.wally.analysis.feature;

public class FacialHairLength extends Feature {
	
	private static FacialHairLength instance;
	
	public static FacialHairLength getInstance() {
		return instance == null ? new FacialHairLength() : instance;
	}
	
}
