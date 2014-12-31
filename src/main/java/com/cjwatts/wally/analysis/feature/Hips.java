package com.cjwatts.wally.analysis.feature;

public class Hips extends Feature {
	
	private static Hips instance;
	
	public static Hips getInstance() {
		return instance == null ? new Hips() : instance;
	}
	
}
