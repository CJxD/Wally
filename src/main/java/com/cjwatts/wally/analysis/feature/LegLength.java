package com.cjwatts.wally.analysis.feature;

public class LegLength extends Feature {
	
	private static LegLength instance;
	
	public static LegLength getInstance() {
		return instance == null ? new LegLength() : instance;
	}
	
}
