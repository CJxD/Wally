package com.cjwatts.wally.detection.processing.background;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map.Entry;

import org.openimaj.image.MBFImage;
import org.openimaj.video.Video;

public class TemporalMode extends BackgroundDetector<MBFImage> {

	// Maximum allowable video length in seconds
	public static final int maxLength = 60;
	
	@Override
	public MBFImage detectBackground(Video<MBFImage> video) {
		int height = video.getHeight();
		int width = video.getWidth();
		long count = video.countFrames();
		int frames;
		
		if (count == -1) {
			// If the frame count failed, manually count the frames
			count = 0;
			while (video.hasNextFrame()) {
				video.getNextFrame();
				count++;
			}
			// Jump back to the beginning
			video.reset();
		}
		
		if (count < 2) {
			throw new IllegalArgumentException("Video must have at least 2 frames.");
		} else if (count / video.getFPS() > maxLength
					|| count > Integer.MAX_VALUE) {
			throw new IllegalArgumentException("Video is too long for background detection.");
		} else {
			frames = (int) count;
		}
		
		/*
		 * Create pixel lists for each frame
		 */
		int[][][] pixels = new int[height][width][frames];
		
		for (int k = 0; k < frames - 1; k++) {
			MBFImage frame = (MBFImage) video.getNextFrame();
			
			for (int i = 0; i < height; i++) {
				for (int j = 0; j < width; j++) {
					// Convert pixel float array to RGB int
					Float[] p = frame.getPixel(j, i);
					pixels[i][j][k] = new Color(p[0], p[1], p[2]).getRGB();
				}
			}
		}
		
		/*
		 * Find median, lower bound and upper bound of each pixel
		 */
		int[] modes = new int[height*width];
		
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				modes[j + i * width] = mode(pixels[i][j]);
			}
		}
		
		// Create new median image
		return new MBFImage(modes, width, height);
		
	}
 
	private int mode(int[] pixels) {
		HashMap<Integer, Integer> histogram = new HashMap<>(256);
		
		for (int p : pixels) {
			// Increment pixel count for this pixel colour
			Integer current = histogram.get(p);
			histogram.put(p, current == null ? 1 : current + 1);
		}
		
		// Find most common colour
		int max = 0;
		int value = 0;
		for (Entry<Integer, Integer> freq : histogram.entrySet()) {
			int f = freq.getValue();
			if (f > max) {
				max = f;
				value = freq.getKey();
			}
		}
		
		return value;
	}
	
}
