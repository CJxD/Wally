package com.cjwatts.wally.analysis;

import java.util.List;

import org.openimaj.feature.DoubleFV;
import org.openimaj.feature.FeatureExtractor;
import org.openimaj.image.FImage;
import org.openimaj.image.model.EigenImages;

public class PrincipleComponentExtractor<F extends FImage> implements FeatureExtractor<DoubleFV, F> {

	private EigenImages eigen;
	
	public PrincipleComponentExtractor(int numEigenVectors) {
		eigen = new EigenImages(numEigenVectors);
	}
	
	@Override
	public DoubleFV extractFeature(F image) {
		return eigen.extractFeature(image);
	}
	
	public void train(List<F> training) {
		eigen.train(training);
	}

}
