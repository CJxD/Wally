package com.cjwatts.wally.analysis.feature;

import java.io.IOException;
import java.io.ObjectInputStream;

public class Weight extends Feature {
	private static final long serialVersionUID = 3L;
	private static Weight instance;
	
	public static Weight getInstance() {
		return instance == null ? instance = new Weight() : instance;
	}
	
	private synchronized void readObject(ObjectInputStream ois)
			throws IOException, ClassNotFoundException {
		instance = this;
	}
	
}
