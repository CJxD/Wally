package com.cjwatts.wally.analysis.feature;

public class NeckThickness extends Feature {
	
	private static NeckThickness instance;
	
	public static NeckThickness getInstance() {
		return instance == null ? new NeckThickness() : instance;
	}
	
}
