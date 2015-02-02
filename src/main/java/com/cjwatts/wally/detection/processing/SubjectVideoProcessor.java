package com.cjwatts.wally.detection.processing;

import org.openimaj.image.Image;
import org.openimaj.video.processor.VideoProcessor;

public class SubjectVideoProcessor<I extends Image<?, I>> extends VideoProcessor<I> {
	
	private SubjectProcessor<I> processor;
	
	public SubjectVideoProcessor(SubjectProcessor<I> processor) {
		this.processor = processor;
	}

	@Override
	public I processFrame(I frame) {
		return processor.process(frame);
	}
	
}
