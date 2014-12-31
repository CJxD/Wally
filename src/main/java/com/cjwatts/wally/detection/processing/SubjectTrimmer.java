package com.cjwatts.wally.detection.processing;

import org.openimaj.image.FImage;
import org.openimaj.image.MBFImage;
import org.openimaj.math.geometry.shape.Rectangle;

public class SubjectTrimmer extends SubjectProcessor<MBFImage> {

	public SubjectTrimmer(SubjectProcessor<MBFImage> preprocessor) {
		super(preprocessor);
	}

	@Override
	protected void processSubject(final MBFImage image) {
		FImage bw = image.flattenMax();
		
		Rectangle bounds = new Rectangle();
		
		for (int y = 0; y < image.getHeight(); y++) {
			for (int x = 0; x < image.getWidth(); x++) {
				if (bw.getPixel(x, y) > 0.5f) {
					/* WHITE */
					
					// Deal with left-most and right-most boundaries
					if (bounds.x == 0 || x < bounds.x) {
						bounds.x = x;
					} else if (x - bounds.x > bounds.width) {
						bounds.width = x - bounds.x;
					}
					
					// Deal with upper-most and lower-most boundaries
					if (bounds.y == 0) {
						bounds.y = y;
					} else if (y - bounds.y > bounds.height) {
						bounds.height = y - bounds.y;
					}
					
				}
			}
		}
		
		// Crop
		image.internalAssign(image.extractROI(bounds));
	}

}
