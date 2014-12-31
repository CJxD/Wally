package com.cjwatts.wally.analysis.feature;

public class MuscleBuild extends Feature {
	
	private static MuscleBuild instance;
	
	public static MuscleBuild getInstance() {
		return instance == null ? new MuscleBuild() : instance;
	}
	
}
