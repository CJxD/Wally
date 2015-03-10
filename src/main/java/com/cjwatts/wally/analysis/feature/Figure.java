package com.cjwatts.wally.analysis.feature;

import java.io.IOException;
import java.io.ObjectInputStream;

public class Figure extends Feature {
	private static final long serialVersionUID = 3L;
	private static Figure instance;
	
	public static Figure getInstance() {
		return instance == null ? instance = new Figure() : instance;
	}

	private synchronized void readObject(ObjectInputStream ois)
			throws IOException, ClassNotFoundException {
		instance = this;
	}
	
}
