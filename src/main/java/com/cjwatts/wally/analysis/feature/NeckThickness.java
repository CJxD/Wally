package com.cjwatts.wally.analysis.feature;

import java.io.IOException;
import java.io.ObjectInputStream;

public class NeckThickness extends Feature {
	private static final long serialVersionUID = 3L;
	private static NeckThickness instance;
	
	public static NeckThickness getInstance() {
		return instance == null ? instance = new NeckThickness() : instance;
	}

	private synchronized void readObject(ObjectInputStream ois)
			throws IOException, ClassNotFoundException {
		instance = this;
	}
	
}
