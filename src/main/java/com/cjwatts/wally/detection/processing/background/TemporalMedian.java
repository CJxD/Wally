package com.cjwatts.wally.detection.processing.background;

import java.awt.Color;
import java.util.Random;

import org.openimaj.image.MBFImage;
import org.openimaj.video.Video;

public class TemporalMedian extends BackgroundDetector<MBFImage> {

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
			video.reset();
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
		int[] medians = new int[height*width];
		
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				medians[j + i * width] = median(pixels[i][j]);
			}
		}
		
		// Create new median image
		return new MBFImage(medians, width, height);
		
	}
 
	private int median(int[] pixels) {
		Random rand = new Random();
		int length = pixels.length;
		int mid = length / 2;
		
		int left = 0;
		int right = length - 1;
		
		while (right >= left) {
			int pivotIndex = partition(pixels, left, right, rand.nextInt(right - left + 1) + left);
			if (pivotIndex == mid) {
				return pixels[pivotIndex];
			} else if (pivotIndex < mid) {
				left = pivotIndex + 1;
			} else {
				right = pivotIndex - 1;
			}
		}
		
		return pixels[0];
	}
	
	private int partition(int[] arr, int left, int right, int pivot) {
		int pivotVal = arr[pivot];
		
		swap(arr, pivot, right);
		
		int storeIndex = left;
		
		for (int i = left; i < right; i++) {
			if (arr[i] < pivotVal) {
				swap(arr, i, storeIndex);
				storeIndex++;
			}
		}
		
		swap(arr, right, storeIndex);
		return storeIndex;
	}
 
	private void swap(int[] arr, int i, int j) {
		if (i != j) {
			int temp = arr[i];
			arr[i] = arr[j];
			arr[j] = temp;
		}
	}
}
