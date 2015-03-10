package com.cjwatts.wally.analysis.feature;

import java.io.IOException;
import java.io.ObjectInputStream;

public class FacialHairColour extends Feature {
	private static final long serialVersionUID = 3L;
	private static FacialHairColour instance;
	
	public static FacialHairColour getInstance() {
		return instance == null ? instance = new FacialHairColour() : instance;
	}

	private synchronized void readObject(ObjectInputStream ois)
			throws IOException, ClassNotFoundException {
		instance = this;
	}
	
}
