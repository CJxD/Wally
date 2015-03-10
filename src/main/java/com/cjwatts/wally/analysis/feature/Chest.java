package com.cjwatts.wally.analysis.feature;

import java.io.IOException;
import java.io.ObjectInputStream;

public class Chest extends Feature {
	private static final long serialVersionUID = 3L;
	private static Chest instance;
	
	public static Chest getInstance() {
		return instance == null ? instance = new Chest() : instance;
	}

	private synchronized void readObject(ObjectInputStream ois)
			throws IOException, ClassNotFoundException {
		instance = this;
	}
	
}
