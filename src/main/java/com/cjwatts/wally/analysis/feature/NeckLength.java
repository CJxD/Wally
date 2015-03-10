package com.cjwatts.wally.analysis.feature;

import java.io.IOException;
import java.io.ObjectInputStream;

public class NeckLength extends Feature {
	private static final long serialVersionUID = 3L;
	private static NeckLength instance;
	
	public static NeckLength getInstance() {
		return instance == null ? instance = new NeckLength() : instance;
	}

	private synchronized void readObject(ObjectInputStream ois)
			throws IOException, ClassNotFoundException {
		instance = this;
	}
	
}
