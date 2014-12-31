package com.cjwatts.wally.analysis.feature;

public class Proportions extends Feature {
	
	private static Proportions instance;
	
	public static Proportions getInstance() {
		return instance == null ? new Proportions() : instance;
	}
	
}
