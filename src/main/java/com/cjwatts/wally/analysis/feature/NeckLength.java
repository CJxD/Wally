package com.cjwatts.wally.analysis.feature;

public class NeckLength extends Feature {
	
	private static NeckLength instance;
	
	public static NeckLength getInstance() {
		return instance == null ? new NeckLength() : instance;
	}
	
}
