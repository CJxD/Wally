package com.cjwatts.wally.analysis.feature;

import java.io.IOException;
import java.io.ObjectInputStream;

public class LegShape extends Feature {
	private static final long serialVersionUID = 3L;
	private static LegShape instance;
	
	public static LegShape getInstance() {
		return instance == null ? instance = new LegShape() : instance;
	}

	private synchronized void readObject(ObjectInputStream ois)
			throws IOException, ClassNotFoundException {
		instance = this;
	}
	
}
