package com.cjwatts.wally.training;

import java.util.HashMap;

import org.openimaj.feature.DoubleFV;

import com.cjwatts.wally.analysis.Category;
import com.cjwatts.wally.persistence.HeuristicPersistenceHandler;

public abstract class Heuristic implements Trainable {

	protected final HeuristicPersistenceHandler h = new HeuristicPersistenceHandler(this);
	private float learningRate;
	private HashMap<Integer, Float> weightings = new HashMap<>();
	
	public Category estimate(DoubleFV measurements) {
		// TODO: Make work
		float accumulator = 0.0f;
		
		for (int i = 0; i < measurements.length(); i++) {
			accumulator += getWeighting(i) * measurements.values[i];
		}
		
		return Category.values()[(int) accumulator];
	}
	
	@Override
	public void train(TrainingData data) {
		for (TrainingPair p : data) {
			for (int i = 0; i < p.getMeasurements().length(); i++) {
				// TODO: Make work
				float newWeighting = getWeighting(i)
						+ getLearningRate()
						* p.getCategory().ordinal();
				
				setWeighting(i, newWeighting, false);
			}
		}
		
		h.save();
	}

	public float getLearningRate() {
		return learningRate;
	}

	public void setLearningRate(float learningRate) {
		setLearningRate(learningRate, true);
	}
	
	public void setLearningRate(float learningRate, boolean autosave) {
		this.learningRate = learningRate;
		if (autosave) h.save();
	}

	public HashMap<Integer, Float> getWeightings() {
		return weightings;
	}
	
	public float getWeighting(Integer component) {
		return weightings.get(component);
	}

	public void setWeighting(Integer component, float value) {
		setWeighting(component, value, true);
	}
	
	public void setWeighting(Integer component, float value, boolean autosave) {
		weightings.put(component, value);
		if (autosave) h.save();
	}
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName().toLowerCase();
	}

}
