package com.cjwatts.wally.analysis.feature;

public class ShoulderShape extends Feature {
	
	private static ShoulderShape instance;
	
	public static ShoulderShape getInstance() {
		return instance == null ? new ShoulderShape() : instance;
	}
	
}
