package com.cjwatts.wally.analysis.feature;

public class Height extends Feature {
	
	private static Height instance;
	
	public static Height getInstance() {
		return instance == null ? new Height() : instance;
	}
	
}
