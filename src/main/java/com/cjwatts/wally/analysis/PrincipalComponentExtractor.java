package com.cjwatts.wally.analysis;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

import org.openimaj.feature.DoubleFV;
import org.openimaj.feature.FeatureExtractor;
import org.openimaj.image.FImage;
import org.openimaj.image.model.EigenImages;

public class PrincipalComponentExtractor implements
		FeatureExtractor<DoubleFV, FImage>, Serializable {
	private static final long serialVersionUID = 1L;

	private EigenImages eigen;

	public PrincipalComponentExtractor(int numEigenVectors) {
		eigen = new EigenImages(numEigenVectors);
	}

	@Override
	public DoubleFV extractFeature(FImage image) {
		return eigen.extractFeature(image);
	}

	public void train(List<FImage> training) {
		eigen.train(training);
	}

	private void writeObject(ObjectOutputStream oos) throws IOException {
		eigen.writeBinary(oos);
	}

	private void readObject(ObjectInputStream ois) throws IOException,
			ClassNotFoundException {
		eigen = new EigenImages(1);
		eigen.readBinary(ois);
	}

}
