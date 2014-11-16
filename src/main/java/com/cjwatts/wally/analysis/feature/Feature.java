package com.cjwatts.wally.analysis.feature;

import org.openimaj.feature.DoubleFV;
import org.openimaj.image.FImage;

import com.cjwatts.wally.analysis.Analysable;
import com.cjwatts.wally.analysis.Category;
import com.cjwatts.wally.analysis.PrincipleComponentExtractor;
import com.cjwatts.wally.training.Heuristic;

public abstract class Feature extends Heuristic implements Analysable {

	@Override
	public Category analyse(DoubleFV components) {
		return estimate(components);
	}

	@Override
	public Category analyse(FImage subject) {
		PrincipleComponentExtractor<FImage> pce = new PrincipleComponentExtractor<>(100);
		
		return analyse(pce.extractFeature(subject));
	}

}
