package com.cjwatts.wally.detection.processing.background;

import org.openimaj.image.Image;
import org.openimaj.video.Video;

public abstract class BackgroundDetector<I extends Image<?, I>> {
	public abstract I detectBackground(Video<I> video);
}
