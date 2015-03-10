package com.cjwatts.wally.analysis.feature;

import java.io.IOException;
import java.io.ObjectInputStream;

public class Proportions extends Feature {
	private static final long serialVersionUID = 3L;
	private static Proportions instance;
	
	public static Proportions getInstance() {
		return instance == null ? instance = new Proportions() : instance;
	}

	private synchronized void readObject(ObjectInputStream ois)
			throws IOException, ClassNotFoundException {
		instance = this;
	}
	
}
