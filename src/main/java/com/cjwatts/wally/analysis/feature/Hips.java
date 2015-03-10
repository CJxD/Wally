package com.cjwatts.wally.analysis.feature;

import java.io.IOException;
import java.io.ObjectInputStream;

public class Hips extends Feature {
	private static final long serialVersionUID = 3L;
	private static Hips instance;
	
	public static Hips getInstance() {
		return instance == null ? instance = new Hips() : instance;
	}

	private synchronized void readObject(ObjectInputStream ois)
			throws IOException, ClassNotFoundException {
		instance = this;
	}
	
}
