package com.cjwatts.wally.analysis;

import org.openimaj.image.MBFImage;

import com.cjwatts.wally.training.Heuristic;

public class Height extends Heuristic implements Analysable {

	@Override
	public Category analyse(MBFImage subject) {
		return estimate(null);
	}

}
