package com.cjwatts.wally.analysis.feature;

import java.io.IOException;
import java.io.ObjectInputStream;

public class LegThickness extends Feature {
	private static final long serialVersionUID = 3L;
	private static LegThickness instance;
	
	public static LegThickness getInstance() {
		return instance == null ? instance = new LegThickness() : instance;
	}

	private synchronized void readObject(ObjectInputStream ois)
			throws IOException, ClassNotFoundException {
		instance = this;
	}
	
}
