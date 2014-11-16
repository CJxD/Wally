package com.cjwatts.wally.detection.processing;

import org.openimaj.image.Image;
import org.openimaj.image.processing.background.BasicBackgroundSubtract;

public class BasicBackgroundRemover<I extends Image<?, I>> extends SubjectProcessor<I> {

	private BasicBackgroundSubtract<I> b;
	
	public BasicBackgroundRemover(I background) {
		b = new BasicBackgroundSubtract<I>(background);
	}
	
	@Override
	public void processImage(final I image) {
		b.processImage(image);
	}

}