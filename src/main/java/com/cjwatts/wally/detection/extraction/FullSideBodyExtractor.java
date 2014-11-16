package com.cjwatts.wally.detection.extraction;

import java.util.List;

import org.openimaj.image.MBFImage;

public class FullSideBodyExtractor extends SubjectExtractor<MBFImage> {

	@Override
	public List<MBFImage> extractAll(MBFImage scene,
			MBFImage data) {
		return new FullFrontBodyExtractor().extractAll(scene, data);
	}

}
