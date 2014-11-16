package com.cjwatts.wally.detection.processing;

import java.util.ArrayList;
import java.util.List;

import org.openimaj.image.Image;
import org.openimaj.image.processor.ImageProcessor;

public abstract class SubjectProcessor<I extends Image<?, I>> implements ImageProcessor<I> {

	@Override
	public abstract void processImage(final I image);
	
	/**
	 * Process the image without side-affecting the original
	 * @param image
	 * @return
	 */
	public I process(I image) {
		return image.process(this);
	}
	
	/**
	 * Process a list of images in-place
	 * @param images
	 * @return
	 */
	public void processAllInplace(List<I> images) {
		for (I image : images) {
			image.processInplace(this);
		}
	}
	
	/**
	 * Process a list of images without side-affecting the originals
	 * @param images
	 * @return
	 */
	public List<I> processAll(List<I> images) {
		List<I> newImages = new ArrayList<>();
		for (I image : images) {
			newImages.add(image.process(this));
		}
		return newImages;
	}

}