package com.cjwatts.wally.analysis.feature;

public class Figure extends Feature {
	
	private static Figure instance;
	
	public static Figure getInstance() {
		return instance == null ? new Figure() : instance;
	}
	
}
