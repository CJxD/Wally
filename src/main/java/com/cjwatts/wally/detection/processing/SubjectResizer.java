package com.cjwatts.wally.detection.processing;

import org.openimaj.feature.local.SpatialLocation;
import org.openimaj.image.MBFImage;
import org.openimaj.image.processing.resize.ResizeProcessor;

public class SubjectResizer extends SubjectProcessor<MBFImage> {

	public static final int defaultWidth = 260;
	public static final int defaultHeight = 640;
	
	private int targetWidth, targetHeight;
	private float targetAspect;
	
	public SubjectResizer() {
		this(null);
	}
	
	public SubjectResizer(int width, int height) {
		this(width, height, null);
	}
	
	public SubjectResizer(SubjectProcessor<MBFImage> preprocessor) {
		super(preprocessor);
		this.targetWidth = defaultWidth;
		this.targetHeight = defaultHeight;
		this.targetAspect = defaultWidth / defaultHeight;
	}
	
	public SubjectResizer(int width, int height, SubjectProcessor<MBFImage> preprocessor) {
		super(preprocessor);
		this.targetWidth = width;
		this.targetHeight = height;
		this.targetAspect = width / height;
	}

	@Override
	protected void processSubject(final MBFImage image) {
		
		image.trim();
		
		/*
		 * Create blank canvas to place final image on
		 */
		MBFImage canvas = new MBFImage(targetWidth, targetHeight);
		
		/*
		 * Resize image with respect to the dimension with the largest difference
		 * e.g. with canvas = 200x300, image = 250x500 reduces to 150x300
		 */
		int widthDiff = image.getWidth() - targetWidth;
		int heightDiff = image.getHeight() - targetHeight;
		float ratio;
		
		if (widthDiff > heightDiff * targetAspect) {
			ratio = (float) targetWidth / image.getWidth();
		} else {
			ratio = (float) targetHeight / image.getHeight();
		}
		
		image.processInplace(new ResizeProcessor(ratio));
		
		/*
		 * Move the image to the centre of the canvas
		 */
		SpatialLocation cropPosition = new SpatialLocation();
		cropPosition.x = (targetWidth - image.getWidth()) / 2;
		cropPosition.y = (targetHeight - image.getHeight()) / 2;
		
		/*
		 * Apply scaled image to canvas
		 */
		canvas.drawImage(image, cropPosition);
		image.internalAssign(canvas);
		
	}

	public int getWidth() {
		return targetWidth;
	}

	public void setWidth(int width) {
		this.targetWidth = width;
	}

	public int getHeight() {
		return targetHeight;
	}

	public void setHeight(int height) {
		this.targetHeight = height;
	}

}
