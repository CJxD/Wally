package com.cjwatts.wally.analysis.feature;

public class Sex extends Feature {
	
	private static Sex instance;
	
	public static Sex getInstance() {
		return instance == null ? new Sex() : instance;
	}
	
}
