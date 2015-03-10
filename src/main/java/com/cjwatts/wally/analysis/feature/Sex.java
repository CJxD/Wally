package com.cjwatts.wally.analysis.feature;

import java.io.IOException;
import java.io.ObjectInputStream;

public class Sex extends Feature {
	private static final long serialVersionUID = 3L;
	private static Sex instance;
	
	public static Sex getInstance() {
		return instance == null ? instance = new Sex() : instance;
	}

	private synchronized void readObject(ObjectInputStream ois)
			throws IOException, ClassNotFoundException {
		instance = this;
	}
	
}
