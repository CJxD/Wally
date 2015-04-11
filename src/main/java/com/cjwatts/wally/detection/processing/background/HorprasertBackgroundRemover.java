package com.cjwatts.wally.detection.processing.background;

import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.FImage;
import org.openimaj.image.MBFImage;

import com.cjwatts.wally.detection.processing.SubjectProcessor;

public class HorprasertBackgroundRemover extends BackgroundRemover<MBFImage> {
	
	protected class BackgroundModel {
		float[][] r;
		float[][] g;
		float[][] b;
		
		float[][] rsigma;
		float[][] gsigma;
		float[][] bsigma;
		
		// Premultiplied alpha coefficients for speed
		private float[][] ralpha;
		private float[][] galpha;
		private float[][] balpha;
		
		public BackgroundModel(MBFImage background) {
			DisplayUtilities.display(background);
			r = background.getBand(0).pixels;
			g = background.getBand(1).pixels;
			b = background.getBand(2).pixels;
			
			rsigma = new float[r.length][r[0].length];
			gsigma = new float[g.length][g[0].length];
			bsigma = new float[b.length][b[0].length];
			
			ralpha = new float[r.length][r[0].length];
			galpha = new float[r.length][r[0].length];
			balpha = new float[r.length][r[0].length];
			
			// Work out alpha coefficients
			for (int i = 0; i < r.length; i++) {
				for (int j = 0; j < r[0].length; j++) {
					// Sigma = 1 for all channels when only one background image is given
					rsigma[i][j] = 1;
					gsigma[i][j] = 1;
					bsigma[i][j] = 1;
					
					float a = 
							(float) (Math.pow(r[i][j] / rsigma[i][j], 2) +
									Math.pow(g[i][j] / gsigma[i][j], 2) +
									Math.pow(b[i][j] / bsigma[i][j], 2));
					// Prevent division by zero
					a = a == 0 ? 1 : a;
					
					ralpha[i][j] = r[i][j] / (a * rsigma[i][j]);
					galpha[i][j] = g[i][j] / (a * gsigma[i][j]);
					balpha[i][j] = b[i][j] / (a * bsigma[i][j]);
				}
			}
		}
		
		public float bd(int x, int y, float r, float g, float b) {
			return ralpha[y][x]*r + galpha[y][x]*g + balpha[y][x]*b;
		}
		
		public float cd(int x, int y, float r, float g, float b) {
			float bd = bd(x, y, r, g, b);
			return (float) Math.sqrt(Math.pow(
					(r - bd * model.r[y][x]) / model.rsigma[y][x]
							+ (g - bd * model.g[y][x]) / model.gsigma[y][x]
							+ (b - bd * model.b[y][x]) / model.bsigma[y][x]
							, 2));
		}
		
		public int getHeight() {
			return r.length;
		}
		
		public int getWidth() {
			return r[0].length;
		}
	}
	
	protected enum Classification {
		BACKGROUND(0.0f), HIGHLIGHT(0.8f),
		SHADOW(0.2f), FOREGROUND(1.0f);
		
		private float pixelValue;
		
		Classification(float pixelValue) {
			this.pixelValue = pixelValue;
		}
		
		public float getPixelValue() {
			return pixelValue;
		}
	}
	
	//public static float tcd = 4f, tbdmin = -1.3f, tbdlow = -0.3f, tbdhigh = 10f;
	public static float tcd = 2f, tbdmin = -0.5f, tbdlow = -0.1f, tbdhigh = 5f;
	
	protected BackgroundModel model;
	protected float[][][] diffCache;
	protected float[] rmsCache;
	
	public HorprasertBackgroundRemover(MBFImage background) {
		model = new BackgroundModel(background);
	}
	
	public HorprasertBackgroundRemover(MBFImage background, SubjectProcessor<MBFImage> preprocessor) {
		super(preprocessor);
		model = new BackgroundModel(background);
	}
	
	@Override
	protected void processSubject(final MBFImage image) {
		if (image.getHeight() != model.getHeight()
				|| image.getWidth() != model.getWidth())
			throw new IllegalArgumentException("Image must have the same dimensions as the background");
		
		// Cache BD and CD values
		createCache(image);
		
		DisplayUtilities.display(image, "Horprasert Input");
		
		FImage mask = new FImage(image.getWidth(), image.getHeight());

		for (int i = 0; i < image.getHeight(); i++) {
			for (int j = 0; j < image.getWidth(); j++) {
				
				// Normalised brightness distortion
				float nbd = nbd(j, i);
				
				// Normalised chrominance distortion
				float ncd = ncd(j, i);
				
				/*
				 * --Classification--
				 * Classify difference into one of 4 classes
				 */
				if (ncd > tcd || nbd < tbdmin) {
					mask.pixels[i][j] = Classification.FOREGROUND.getPixelValue();
				}
				else if (tbdlow < nbd && nbd < tbdhigh) {
					mask.pixels[i][j] = Classification.BACKGROUND.getPixelValue();
				}
				else if (nbd < 0.0f) {
					mask.pixels[i][j] = Classification.SHADOW.getPixelValue();
				}
				else {
					mask.pixels[i][j] = Classification.HIGHLIGHT.getPixelValue();
				}
			}
		}

		image.multiplyInplace(mask);
		
		DisplayUtilities.display(mask, "Horprasert Mask");
		DisplayUtilities.display(image, "Horprasert Output");
		
		clearCache();
	}
	
	/**
	 * Make a cache of normalised BD and CD values
	 * @param image
	 */
	protected void createCache(MBFImage image) {
		/*
		 * 0 => BD
		 * 1 => CD
		 */
		diffCache = new float[image.getHeight()][image.getWidth()][2];
		
		float sumBD = 0, sumCD = 0;
		
		for (int i = 0; i < image.getHeight(); i++) {
			for (int j = 0; j < image.getWidth(); j++) {
				float r = image.getBand(0).getPixel(j, i);
				float g = image.getBand(1).getPixel(j, i);
				float b = image.getBand(2).getPixel(j, i);
				
				// Brightness distortion
				diffCache[i][j][0] = model.bd(j, i, r, g, b);
				sumBD += (diffCache[i][j][0] - 1)*(diffCache[i][j][0] - 1);
				
				// Chrominance distortion
				diffCache[i][j][1] = model.cd(j, i, r, g, b);
				sumCD += diffCache[i][j][1]*diffCache[i][j][1];
			}
		}
		
		/*
		 * 0 => RMS(BD)
		 * 1 => RMS(CD)
		 */
		rmsCache = new float[2];
		
		int n = image.getHeight() * image.getWidth();
		
		// RMS(BD)
		rmsCache[0] = (float) Math.sqrt(sumBD / n);
		
		// RMS(CD)
		rmsCache[1] = (float) Math.sqrt(sumCD / n);
	}
	
	/**
	 * Remove all values from the cache
	 */
	protected void clearCache() {
		diffCache = null;
		rmsCache = null;
	}
	
	protected float nbd(int x, int y) {
		/*
		 * 0 => BD
		 * 1 => CD
		 */
		return (diffCache[y][x][0] - 1) / rmsCache[0];
	}
	
	protected float ncd(int x, int y) {
		/*
		 * 0 => BD
		 * 1 => CD
		 */
		return diffCache[y][x][1] / rmsCache[1];
	}
}
