package com.cjwatts.wally.detection;

import org.openimaj.image.MBFImage;
import org.openimaj.image.processing.background.BasicBackgroundSubtract;

public class BackgroundRemover extends DetectionPreprocessor {

	private MBFImage background;

	public BackgroundRemover(MBFImage background) {
		this(background, null);
	}

	public BackgroundRemover(MBFImage background,
			DetectionPreprocessor prepreprocessor) {
		super(prepreprocessor);
		this.background = background;
	}

	/**
	 * @param scene
	 * @return Scene with background pixel values subtracted
	 */
	@Override
	protected MBFImage process(MBFImage scene) {
		return scene.process(new BasicBackgroundSubtract<MBFImage>(background));
	}

	public MBFImage getBackground() {
		return background;
	}

	public void setBackground(MBFImage background) {
		this.background = background;
	}

}
