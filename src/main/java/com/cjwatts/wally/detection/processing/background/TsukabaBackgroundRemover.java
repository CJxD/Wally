package com.cjwatts.wally.detection.processing.background;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.openimaj.citation.annotation.Reference;
import org.openimaj.citation.annotation.ReferenceType;
import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.FImage;
import org.openimaj.image.Image;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.colour.ColourSpace;
import org.openimaj.image.connectedcomponent.GreyscaleConnectedComponentLabeler;
import org.openimaj.image.pixel.ConnectedComponent;

import com.cjwatts.wally.detection.processing.SubjectProcessor;

@Reference(
		type = ReferenceType.Inproceedings,
		author = { 
				"Kim, Hansung",
				"Sakamoto, Ryuuki",
				"Kitahara, Itaru",
				"Toriyama, Tomoji",
				"Kogure, Kiyoshi"},
		title = "Robust silhouette extraction technique using background subtraction",
		year = "2007",
		booktitle = "10th Meeting on Image Recognition and Understand, MIRU"
)
public class TsukabaBackgroundRemover extends BackgroundRemover<MBFImage> {

	public static float[] lumThresholds = {0.06f, 0.1f, 0.15f};
	public static float hueThreshold = 0.5f;
	
	/**
	 * GGF-distribution background model
	 */
	protected class BackgroundModel {
		float[][] luminance;
		float[][] colour;
		
		public BackgroundModel(MBFImage background) {
			background = ColourSpace.convert(background, ColourSpace.HSI);
			luminance = background.getBand(2).pixels;
			colour = background.getBand(0).pixels;
		}
		
		public int getHeight() {
			return luminance.length;
		}
		
		public int getWidth() {
			return luminance[0].length;
		}
	}
	
	protected enum Classification {
		RELIABLE_BACKGROUND(0.0f), SUSPICIOUS_BACKGROUND(0.0f),
		SUSPICIOUS_FOREGROUND(0.8f), RELIABLE_FOREGROUND(1.0f);
		
		private float pixelValue;
		
		Classification(float pixelValue) {
			this.pixelValue = pixelValue;
		}
		
		public float getPixelValue() {
			return pixelValue;
		}
	}
	
	protected BackgroundModel model;
	
	public TsukabaBackgroundRemover(MBFImage background) {
		model = new BackgroundModel(background);
	}
	
	public TsukabaBackgroundRemover(MBFImage background, SubjectProcessor<MBFImage> preprocessor) {
		super(preprocessor);
		model = new BackgroundModel(background);
	}
	
	@Override
	protected void processSubject(final MBFImage image) {
		if (image.getHeight() != model.getHeight()
				|| image.getWidth() != model.getWidth())
			throw new IllegalArgumentException("Image must have the same dimensions as the background");
		
		FImage mask = new FImage(image.getWidth(), image.getHeight());
		
		float[][] imageLum = image.getBand(2).pixels;
		float[][] imageHue = image.getBand(0).pixels;
		
		for (int i = 0; i < image.getHeight(); i++) {
			for (int j = 0; j < image.getWidth(); j++) {
				// Difference of luminance
				float diff = Math.abs(imageLum[i][j] - model.luminance[i][j]);
				
				/*
				 * --Classification--
				 * Classify difference into one of 4 classes
				 */
				if (diff < lumThresholds[0]) {
					mask.pixels[i][j] = Classification.RELIABLE_BACKGROUND.getPixelValue();
				}
				else if (diff < lumThresholds[1]) {
					mask.pixels[i][j] = Classification.SUSPICIOUS_BACKGROUND.getPixelValue();
				}
				else if (diff < lumThresholds[2]) {
					/*
					 * --Shadow elimination--
					 * Shadows do not change hue - declassify areas that don't
					 */
					diff = Math.abs(imageHue[i][j] - model.colour[i][j]);
					if (diff < lumThresholds[0])
						mask.pixels[i][j] = Classification.SUSPICIOUS_BACKGROUND.getPixelValue();
					else
						mask.pixels[i][j] = Classification.SUSPICIOUS_FOREGROUND.getPixelValue();
				}
				else {
					mask.pixels[i][j] = Classification.RELIABLE_FOREGROUND.getPixelValue();
				}
			}
		}
		
		DisplayUtilities.display(mask);
		try {
			ImageUtilities.write(mask, new java.io.File("../mask.jpg"));
		} catch (IOException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
		
		/*
		 * --Labelling--
		 * Find all large connected components
		 */
		/*GreyscaleConnectedComponentLabeler labeller = new GreyscaleConnectedComponentLabeler();
    	List<ConnectedComponent> components = labeller.findComponents(mask);
    	
    	// Order the components by size
    	Collections.sort(components, new Comparator<ConnectedComponent>() {
			@Override
			public int compare(ConnectedComponent arg0, ConnectedComponent arg1) {
				return arg1.calculateArea() - arg0.calculateArea();
			}
    	});*/
    	
    	//mask = components.get(0).toFImage();
		
		//extractSilhouettes(mask);
		//finalise(mask);
    	
		image.multiplyInplace(mask);
	}
	
}