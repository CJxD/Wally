package com.cjwatts.wally.analysis.feature;

import java.io.IOException;
import java.io.ObjectInputStream;

public class ArmThickness extends Feature {
	private static final long serialVersionUID = 3L;
	private static ArmThickness instance;
	
	public static ArmThickness getInstance() {
		return instance == null ? instance = new ArmThickness() : instance;
	}

	private synchronized void readObject(ObjectInputStream ois)
			throws IOException, ClassNotFoundException {
		instance = this;
	}
	
}
