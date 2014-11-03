package com.cjwatts.wally.detection;

import org.openimaj.image.MBFImage;

public abstract class DetectionPreprocessor {

	private DetectionPreprocessor prepreprocessor;

	public DetectionPreprocessor() {
	}

	public DetectionPreprocessor(DetectionPreprocessor prepreprocessor) {
		this.prepreprocessor = prepreprocessor;
	}

	/**
	 * Perform the processing action
	 * 
	 * @param scene
	 * @return Processed scene
	 */
	protected abstract MBFImage process(MBFImage scene);

	/**
	 * Perform all chained preprocessing actions ready for feature extraction
	 * 
	 * @param scene
	 * @return Processed scene
	 */
	public MBFImage preprocess(MBFImage scene) {
		if (prepreprocessor != null)
			return prepreprocessor.preprocess(scene);
		else
			return process(scene);
	}

}
