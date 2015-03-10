package com.cjwatts.wally.analysis.feature;

import java.io.IOException;
import java.io.ObjectInputStream;

public class SkinColour extends Feature {
	private static final long serialVersionUID = 3L;
	private static SkinColour instance;
	
	public static SkinColour getInstance() {
		return instance == null ? instance = new SkinColour() : instance;
	}

	private synchronized void readObject(ObjectInputStream ois)
			throws IOException, ClassNotFoundException {
		instance = this;
	}
	
}
