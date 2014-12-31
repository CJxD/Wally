package com.cjwatts.wally.analysis.feature;

public class LegThickness extends Feature {
	
	private static LegThickness instance;
	
	public static LegThickness getInstance() {
		return instance == null ? new LegThickness() : instance;
	}
	
}
