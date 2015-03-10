package com.cjwatts.wally.analysis.feature;

import java.io.IOException;
import java.io.ObjectInputStream;

public class Height extends Feature {
	private static final long serialVersionUID = 3L;
	private static Height instance;
	
	public static Height getInstance() {
		return instance == null ? instance = new Height() : instance;
	}

	private synchronized void readObject(ObjectInputStream ois)
			throws IOException, ClassNotFoundException {
		instance = this;
	}
	
}
