package com.cjwatts.wally.detection.processing;

import org.openimaj.image.MBFImage;

public class SubjectNormaliser extends SubjectProcessor<MBFImage> {
	
	public SubjectNormaliser() {
		
	}
	
	public SubjectNormaliser(SubjectProcessor<MBFImage> preprocessor) {
		super(preprocessor);
	}
	
	@Override
	protected void processSubject(final MBFImage image) {
		image.normalise();
	}

}
