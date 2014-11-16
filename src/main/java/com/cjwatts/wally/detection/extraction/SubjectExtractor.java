package com.cjwatts.wally.detection.extraction;

import java.util.ArrayList;
import java.util.List;

import org.openimaj.image.Image;

public abstract class SubjectExtractor<I extends Image<Float[], I>> {
	/**
	 * Extract all feature matches from the scene
	 * 
	 * @param scene Image to segment
	 * @param data Image to use for detection
	 * @return List of all matches as separate images
	 */
	public abstract List<I> extractAll(I scene, I data);

	/**
	 * Extract all feature matches from the scene
	 * 
	 * @param scene Image to segment
	 * @return List of all matches as separate images
	 */
	public List<I> extractAll(I scene) {
		return extractAll(scene, scene);
	}
	
	/**
	 * Extract all feature matches from a list of scenes
	 * 
	 * @param scene Images to segment
	 * @return List of all matches as separate images
	 */
	public List<I> extractAll(List<I> scenes) {
		return extractAll(scenes, scenes);
	}
	
	/**
	 * Extract all feature matches from a list of scenes
	 * 
	 * @param scene Images to segment
	 * @param data Images to use for detection
	 * @return List of all matches as separate images
	 */
	public List<I> extractAll(List<I> scenes, List<I> data) {
		List<I> subjects = new ArrayList<>();
		for (int i = 0; i < scenes.size(); i++) {
			subjects.addAll(extractAll(scenes.get(i), data.get(i)));
		}
		return subjects;
	}
}
