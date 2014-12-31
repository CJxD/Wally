package com.cjwatts.wally.analysis.feature;

public class ArmThickness extends Feature {
	
	private static ArmThickness instance;
	
	public static ArmThickness getInstance() {
		return instance == null ? new ArmThickness() : instance;
	}
	
}
