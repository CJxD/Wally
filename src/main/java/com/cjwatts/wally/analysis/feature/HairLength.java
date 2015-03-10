package com.cjwatts.wally.analysis.feature;

import java.io.IOException;
import java.io.ObjectInputStream;

public class HairLength extends Feature {
	private static final long serialVersionUID = 3L;
	private static HairLength instance;
	
	public static HairLength getInstance() {
		return instance == null ? instance = new HairLength() : instance;
	}

	private synchronized void readObject(ObjectInputStream ois)
			throws IOException, ClassNotFoundException {
		instance = this;
	}
	
}
