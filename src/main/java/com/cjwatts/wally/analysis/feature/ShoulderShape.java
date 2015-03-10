package com.cjwatts.wally.analysis.feature;

import java.io.IOException;
import java.io.ObjectInputStream;

public class ShoulderShape extends Feature {
	private static final long serialVersionUID = 3L;
	private static ShoulderShape instance;
	
	public static ShoulderShape getInstance() {
		return instance == null ? instance = new ShoulderShape() : instance;
	}

	private synchronized void readObject(ObjectInputStream ois)
			throws IOException, ClassNotFoundException {
		instance = this;
	}
	
}
