package com.cjwatts.wally.analysis.feature;

public class Chest extends Feature {
	
	private static Chest instance;
	
	public static Chest getInstance() {
		return instance == null ? new Chest() : instance;
	}
	
}
