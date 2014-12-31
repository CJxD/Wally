package com.cjwatts.wally.detection.processing;

import org.openimaj.image.MBFImage;
import org.openimaj.image.processing.resize.ResizeProcessor;

public class InputLimiter extends SubjectProcessor<MBFImage> {

	public int maxsize = 800;
	
	public InputLimiter() {
		
	}
	
	public InputLimiter(int maxsize) {
		this.maxsize = maxsize;
	}
	
	public InputLimiter(SubjectProcessor<MBFImage> preprocessor) {
		super(preprocessor);
	}
	
	public InputLimiter(SubjectProcessor<MBFImage> preprocessor, int maxsize) {
		super(preprocessor);
		this.maxsize = maxsize;
	}
	
	@Override
	protected void processSubject(final MBFImage image) {
		if (image.getHeight() > maxsize || image.getWidth() > maxsize)
			image.processInplace(new ResizeProcessor(maxsize));
	}

}
