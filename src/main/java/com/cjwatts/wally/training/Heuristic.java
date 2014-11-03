package com.cjwatts.wally.training;

import java.util.HashMap;
import java.util.Set;

import com.cjwatts.wally.analysis.Category;
import com.cjwatts.wally.analysis.Measurement;
import com.cjwatts.wally.persistence.HeuristicPersistenceHandler;

public abstract class Heuristic implements Trainable {

	protected final HeuristicPersistenceHandler h = new HeuristicPersistenceHandler(this);
	private float learningRate;
	private HashMap<String, Float> weightings = new HashMap<>();
	
	public Category estimate(Set<Measurement> measurements) {
		// TODO: Make work
		float accumulator = 0.0f;
		
		for (Measurement m : measurements) {
			accumulator += getWeighting(m.getName()) * m.getValue();
		}
		
		return Category.values()[(int) accumulator];
	}
	
	@Override
	public void train(TrainingData data) {
		for (TrainingPair p : data) {
			for (Measurement m : p.getMeasurements()) {
				// TODO: Make work
				float newWeighting = getWeighting(m.getName())
						+ getLearningRate()
						* p.getCategory().ordinal();
				
				setWeighting(m.getName(), newWeighting, false);
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

	public HashMap<String, Float> getWeightings() {
		return weightings;
	}
	
	public float getWeighting(String name) {
		return weightings.get(name);
	}

	public void setWeighting(String name, float value) {
		setWeighting(name, value, true);
	}
	
	public void setWeighting(String name, float value, boolean autosave) {
		weightings.put(name, value);
		if (autosave) h.save();
	}
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName().toLowerCase();
	}

}
