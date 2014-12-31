package com.cjwatts.wally.analysis;

import org.openimaj.feature.DoubleFV;

public interface Analysable {
	public Category analyse(DoubleFV components);
}
