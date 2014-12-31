package com.cjwatts.wally.analysis.feature;

public class ArmLength extends Feature {
	
	private static ArmLength instance;
	
	public static ArmLength getInstance() {
		return instance == null ? new ArmLength() : instance;
	}
	
}
