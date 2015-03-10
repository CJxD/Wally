package com.cjwatts.wally.analysis.feature;

import java.io.IOException;
import java.io.ObjectInputStream;

public class ArmLength extends Feature {
	private static final long serialVersionUID = 3L;
	private static ArmLength instance;
	
	public static ArmLength getInstance() {
		return instance == null ? instance = new ArmLength() : instance;
	}

	private synchronized void readObject(ObjectInputStream ois)
			throws IOException, ClassNotFoundException {
		instance = this;
	}
	
}
