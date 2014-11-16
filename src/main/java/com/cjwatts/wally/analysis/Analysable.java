package com.cjwatts.wally.analysis;

import org.openimaj.feature.DoubleFV;
import org.openimaj.image.FImage;

public interface Analysable {
	public Category analyse(DoubleFV components);
	public Category analyse(FImage subject);
}
