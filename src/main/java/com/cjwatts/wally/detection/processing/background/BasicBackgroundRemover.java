package com.cjwatts.wally.detection.processing.background;

import org.openimaj.image.Image;
import org.openimaj.image.processing.background.BasicBackgroundSubtract;

import com.cjwatts.wally.detection.processing.SubjectProcessor;

public class BasicBackgroundRemover<I extends Image<?, I>> extends BackgroundRemover<I> {

	private BasicBackgroundSubtract<I> b;
	
	public BasicBackgroundRemover(I background) {
		b = new BasicBackgroundSubtract<I>(background);
	}
	
	public BasicBackgroundRemover(I background, SubjectProcessor<I> preprocessor) {
		super(preprocessor);
		b = new BasicBackgroundSubtract<I>(background);
	}
	
	@Override
	protected void processSubject(final I image) {
		b.processImage(image);
	}

}