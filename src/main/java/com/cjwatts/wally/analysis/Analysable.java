package com.cjwatts.wally.analysis;

import org.openimaj.image.MBFImage;

public interface Analysable {
	public Category analyse(MBFImage subject);
}
