package com.cjwatts.wally.analysis.feature;

import java.io.IOException;
import java.io.ObjectInputStream;

public class HairColour extends Feature {
	private static final long serialVersionUID = 3L;
	private static HairColour instance;
	
	public static HairColour getInstance() {
		return instance == null ? instance = new HairColour() : instance;
	}

	private synchronized void readObject(ObjectInputStream ois)
			throws IOException, ClassNotFoundException {
		instance = this;
	}
	
}
