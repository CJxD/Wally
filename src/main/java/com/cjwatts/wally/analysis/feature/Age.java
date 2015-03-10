package com.cjwatts.wally.analysis.feature;

import java.io.IOException;
import java.io.ObjectInputStream;

public class Age extends Feature {
	private static final long serialVersionUID = 3L;
	private static Age instance;

	public static Age getInstance() {
		return instance == null ? instance = new Age() : instance;
	}

	private synchronized void readObject(ObjectInputStream ois)
			throws IOException, ClassNotFoundException {
		instance = this;
	}

}
