package com.cjwatts.wally.analysis.feature;

import java.io.IOException;
import java.io.ObjectInputStream;

public class Ethnicity extends Feature {
	private static final long serialVersionUID = 3L;
	private static Ethnicity instance;
	
	public static Ethnicity getInstance() {
		return instance == null ? instance = new Ethnicity() : instance;
	}

	private synchronized void readObject(ObjectInputStream ois)
			throws IOException, ClassNotFoundException {
		instance = this;
	}
	
}
