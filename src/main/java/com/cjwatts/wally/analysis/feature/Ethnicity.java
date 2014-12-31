package com.cjwatts.wally.analysis.feature;

public class Ethnicity extends Feature {
	
	private static Ethnicity instance;
	
	public static Ethnicity getInstance() {
		return instance == null ? new Ethnicity() : instance;
	}
	
}
