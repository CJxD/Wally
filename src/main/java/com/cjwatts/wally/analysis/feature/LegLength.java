package com.cjwatts.wally.analysis.feature;

import java.io.IOException;
import java.io.ObjectInputStream;

public class LegLength extends Feature {
	private static final long serialVersionUID = 3L;
	private static LegLength instance;
	
	public static LegLength getInstance() {
		return instance == null ? instance = new LegLength() : instance;
	}

	private synchronized void readObject(ObjectInputStream ois)
			throws IOException, ClassNotFoundException {
		instance = this;
	}
	
}
