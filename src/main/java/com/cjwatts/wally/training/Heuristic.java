package com.cjwatts.wally.training;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import Jama.Matrix;
import org.openimaj.feature.DoubleFV;

import com.cjwatts.wally.analysis.Category;
import com.cjwatts.wally.persistence.HeuristicPersistenceHandler;
import com.google.common.primitives.Doubles;

public abstract class Heuristic implements Trainable, Serializable {
	private static final long serialVersionUID = 2L;
	
	private transient HeuristicPersistenceHandler h = new HeuristicPersistenceHandler(this);
	private transient Map<Integer, Double> weightings = new TreeMap<>();
	private TrainingAlgorithm algorithm;
	
	private boolean loaded = false;
	
	public Heuristic() {
		this(new PerceptronTrainer(0.00001, 1000));
	}
	
	public Heuristic(TrainingAlgorithm algorithm) {
		if (load()) {
			loaded = true;
		}
		
		this.setAlgorithm(algorithm);
	}
	
	public synchronized Category estimate(DoubleFV measurements) {
		if (weightings.size() == 0) throw new IllegalStateException("Heuristic has not been trained.");

		float accumulator = 0.0f;
		
		for (int i = 0; i < measurements.length(); i++) {
			accumulator += getWeighting(i) * measurements.get(i);
		}
		
		// Convert to integer and check range
		int category = Math.round(accumulator);
		category = Math.min(category, Category.values().length - 1);
		category = Math.max(category, 0);
		
		return Category.values()[category];
	}
	
	@Override
	public void train(TrainingData data) {
//algorithm = new LinearRegressionTrainer();
		// Organise X matrix and y vector
		double[][] trainingVectors = new double[data.size()][weightings.size()];
		double[][] target = new double[data.size()][1];
		int i = 0;
		for (TrainingPair p : data) {
			trainingVectors[i] = p.getMeasurements().values;
			target[i][0] = p.getCategory().ordinal();
			i++;
		}
		
		Matrix X = new Matrix(trainingVectors);
		Matrix y = new Matrix(target);
		
		// Find model
		Matrix model;
		if (weightings.size() > 0) {
			// Organise weights vector
			double[][] weights = new double[][] {Doubles.toArray(weightings.values())};
			Matrix w = new Matrix(weights).transpose();
			model = algorithm.findModel(X, y, w);
		} else {
			model = algorithm.findModel(X, y);
		}
		
		// Set weights
		for (i = 0; i < model.getRowDimension(); i++) {
			setWeighting(i, model.get(i, 0));
		}
		
		save();
	}

	public synchronized TrainingAlgorithm getAlgorithm() {
		return algorithm;
	}

	public synchronized void setAlgorithm(TrainingAlgorithm algorithm) {
		setAlgorithm(algorithm, true);
	}
	
	public synchronized void setAlgorithm(TrainingAlgorithm algorithm, boolean autosave) {
		this.algorithm = algorithm;
		if (autosave) save();
	}

	public synchronized Map<Integer, Double> getWeightings() {
		return weightings;
	}
	
	public synchronized Double getWeighting(Integer component) {
		return weightings.get(component);
	}

	public synchronized void setWeighting(Integer component, Double value) {
		setWeighting(component, value, true);
	}
	
	public synchronized void setWeighting(Integer component, Double value, boolean autosave) {
		weightings.put(component, value);
		if (autosave) save();
	}
	
	public synchronized void clearAll() {
		weightings = new HashMap<>();
	}

	/**
	 * Load instance from storage
	 */
	public synchronized boolean load() {
		boolean result = h.load();
		loaded = result;
		return result;
	}
	
	/**
	 * Save this instance
	 */
	public synchronized void save() {
		h.save();
		loaded = true;
	}
	
	/**
	 * @return True if heuristic persists on the disk
	 */
	public synchronized boolean isLoaded() {
		return loaded;
	}
	
	private void writeObject(ObjectOutputStream oos) throws IOException {
		save();
	}

	private void readObject(ObjectInputStream ois) throws IOException,
			ClassNotFoundException {
		h = new HeuristicPersistenceHandler(this);
		weightings = new HashMap<>();
		algorithm = new PerceptronTrainer(0.00001, 1000);
		load();
	}
	
	@Override
	public int hashCode() {
		return getClass().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		return true;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName().toLowerCase();
	}

}
