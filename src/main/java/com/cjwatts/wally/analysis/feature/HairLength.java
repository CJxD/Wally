package com.cjwatts.wally.analysis.feature;

public class HairLength extends Feature {
	
	private static HairLength instance;
	
	public static HairLength getInstance() {
		return instance == null ? new HairLength() : instance;
	}
	
}
