package com.cjwatts.wally.analysis.feature;

public class Weight extends Feature {
	
	private static Weight instance;
	
	public static Weight getInstance() {
		return instance == null ? new Weight() : instance;
	}
	
}
