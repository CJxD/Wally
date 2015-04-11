package com.cjwatts.wally.detection.processing.background;

import org.openimaj.image.Image;

import com.cjwatts.wally.detection.processing.SubjectProcessor;

public abstract class BackgroundRemover<I extends Image<?, I>> extends SubjectProcessor<I> {

	public BackgroundRemover() {}
	
	/**
	 * Perform the chained preprocessor before applying this processor
	 * @param preprocessor
	 */
	public BackgroundRemover(SubjectProcessor<I> preprocessor) {
		super(preprocessor);
	}
	
}
