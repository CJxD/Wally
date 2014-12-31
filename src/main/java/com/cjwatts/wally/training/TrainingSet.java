package com.cjwatts.wally.training;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class TrainingSet extends HashMap<Heuristic, TrainingData> implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * Add new training data for the heuristic.
	 * Appends to training data if key already exists.
	 * 
	 * @param h Heuristic to train
	 * @param d Training data for this heuristic only
	 */
	public void add(Heuristic h, TrainingData d) {
		if (get(h) == null) put(h, d);
		
		for (TrainingPair p : d) {
			add(h, p);
		}
	}
	
	/**
	 * Add new training data for the heuristic.
	 * Appends to training data if key already exists,
	 * otherwise, creates a new {@link TrainingData} entry.
	 * 
	 * @param h Heuristic to train
	 * @param p Training pair for this heuristic
	 */
	public void add(Heuristic h, TrainingPair p) {
		TrainingData current = get(h);
		
		if (current == null) {
			// Create new TrainingData
			TrainingData d = new TrainingData();
			d.put(p);
			put(h, d);
		} else {
			// Append to existing
			current.put(p);
		}
	}
	
	/**
	 * Add new training data for all heuristics.
	 * 
	 * @param s Training set to append
	 */
	public void addAll(TrainingSet s) {
		for (Map.Entry<Heuristic, TrainingData> e : s.entrySet()) {
			add(e.getKey(), e.getValue());
		}
	}
	
	/**
	 * Remove a training pair from the heuristic.
	 * 
	 * @param h Heuristic to train
	 * @param p Training pair for this heuristic
	 * @return true if pair was removed
	 */
	public boolean remove(Heuristic h, TrainingPair p) {
		TrainingData current = get(h);
		
		if (current != null) {
			current.remove(p);
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Train all heuristics with the gathered training data
	 */
	public void trainAll() {
		for (Map.Entry<Heuristic, TrainingData> e : entrySet()) {
			e.getKey().train(e.getValue());
		}
	}
	
}
