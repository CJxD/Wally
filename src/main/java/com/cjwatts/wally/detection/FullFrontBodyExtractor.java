package com.cjwatts.wally.detection;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openimaj.image.FImage;
import org.openimaj.image.MBFImage;
import org.openimaj.image.objectdetection.haar.Detector;
import org.openimaj.image.processing.face.detection.HaarCascadeDetector.BuiltInCascade;
import org.openimaj.math.geometry.shape.Rectangle;

public class FullFrontBodyExtractor extends FeatureExtractor {

	@Override
	protected List<MBFImage> extractAllPreprocessed(MBFImage scene,
			MBFImage preprocessed) {
		FImage flat = preprocessed.flatten();
		List<MBFImage> subjects = new ArrayList<>();

		Detector detector = BuiltInCascade.fullbody.load().getDetector();
		List<Rectangle> detected = detector.detect(flat);

		/*
		 * Vote for detected entities
		 * 3 votes or more required
		 */
		
		// Holds a list of all groups of overlapping rectangles
		List<List<Rectangle>> votes = new ArrayList<>();
		Set<Rectangle> seen = new HashSet<>();
		
		for (Rectangle r : detected) {
			
			if (!seen.contains(r)
					&& r.calculateArea() > 10000) {

				// Add to the list of overlapping rectangles
				List<Rectangle> l = new ArrayList<>();
				l.add(r);
				seen.add(r);
				
				for (Rectangle other : detected) {
					if (!seen.contains(other)
							&& other.isOverlapping(r)
							&& other.calculateArea() > 10000) {
						l.add(other);
						seen.add(other);
					}
				}
				
				votes.add(l);
				
			}
		}
		
		/*
		 * Return ROIs where 3 or more rectangles overlap
		 */
		for (List<Rectangle> candidates : votes) {
			int size = candidates.size();
			
			if (size >= 3) {
				// Compute the average of the rectangles
				Rectangle average = new Rectangle();

				for (Rectangle c : candidates) {
					average.x += c.x;
					average.y += c.y;
					average.height += c.height;
					average.width += c.width;
				}
				average.x /= size;
				average.y /= size;
				average.height /= size;
				average.width /= size;
				
				subjects.add(scene.extractROI(average));
			}
		}

		return subjects;
	}

}
