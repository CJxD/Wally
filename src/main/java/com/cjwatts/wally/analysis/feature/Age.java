package com.cjwatts.wally.analysis.feature;

public class Age extends Feature {
	
	private static Age instance;
	
	public static Age getInstance() {
		return instance == null ? new Age() : instance;
	}
	
}
