package com.cjwatts.wally.analysis.feature;

import java.io.IOException;
import java.io.ObjectInputStream;

public class MuscleBuild extends Feature {
	private static final long serialVersionUID = 3L;
	private static MuscleBuild instance;
	
	public static MuscleBuild getInstance() {
		return instance == null ? instance = new MuscleBuild() : instance;
	}
	
	private synchronized void readObject(ObjectInputStream ois)
			throws IOException, ClassNotFoundException {
		instance = this;
	}
	
}
