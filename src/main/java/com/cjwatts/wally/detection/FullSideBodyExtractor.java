package com.cjwatts.wally.detection;

import java.util.ArrayList;
import java.util.List;

import org.openimaj.image.FImage;
import org.openimaj.image.MBFImage;
import org.openimaj.image.objectdetection.haar.Detector;
import org.openimaj.image.processing.face.detection.HaarCascadeDetector.BuiltInCascade;
import org.openimaj.math.geometry.shape.Rectangle;

public class FullSideBodyExtractor extends FeatureExtractor {

	@Override
	protected List<MBFImage> extractAllPreprocessed(MBFImage scene,
			MBFImage preprocessed) {
		FImage flat = preprocessed.flatten();
		List<MBFImage> subjects = new ArrayList<>();

		Detector detector = BuiltInCascade.fullbody.load().getDetector();
		List<Rectangle> detected = detector.detect(flat);

		for (Rectangle r : detected) {
			subjects.add(scene.extractROI(r));
		}

		return subjects;
	}

}
