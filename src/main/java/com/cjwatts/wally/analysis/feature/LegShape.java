package com.cjwatts.wally.analysis.feature;

public class LegShape extends Feature {
	
	private static LegShape instance;
	
	public static LegShape getInstance() {
		return instance == null ? new LegShape() : instance;
	}
	
}
