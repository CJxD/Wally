package com.cjwatts.wally.detection;

import java.util.List;

import org.openimaj.image.MBFImage;

public abstract class FeatureExtractor {
	protected abstract List<MBFImage> extractAllPreprocessed(MBFImage scene,
			MBFImage preprocessed);

	/**
	 * Extract all feature matches from the scene
	 * 
	 * @param scene
	 * @return List of all matches as separate images
	 */
	public List<MBFImage> extractAll(MBFImage scene) {
		return extractAll(scene, null);
	}

	/**
	 * Extract all feature matches from the scene, using preprocessed images for
	 * initial detection
	 * 
	 * @param scene
	 * @param p
	 *            Preprocessor
	 * @return List of all matches as separate images
	 */
	public List<MBFImage> extractAll(MBFImage scene, DetectionPreprocessor p) {
		if (p != null)
			return extractAllPreprocessed(scene, p.preprocess(scene));
		else
			return extractAllPreprocessed(scene, scene);
	}
}
