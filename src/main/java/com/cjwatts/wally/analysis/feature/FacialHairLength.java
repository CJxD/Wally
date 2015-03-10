package com.cjwatts.wally.analysis.feature;

import java.io.IOException;
import java.io.ObjectInputStream;

public class FacialHairLength extends Feature {
	private static final long serialVersionUID = 3L;
	private static FacialHairLength instance;
	
	public static FacialHairLength getInstance() {
		return instance == null ? instance = new FacialHairLength() : instance;
	}

	private synchronized void readObject(ObjectInputStream ois)
			throws IOException, ClassNotFoundException {
		instance = this;
	}
	
}
