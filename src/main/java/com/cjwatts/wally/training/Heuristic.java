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

	private transient HeuristicPersistenceHandler h = new HeuristicPersistenceHandler(
			this);
	private transient Map<Integer, Double> weightings = new TreeMap<>();
	private TrainingAlgorithm algorithm;

	private boolean loaded = false;

	public Heuristic() {
		if (load()) {
			loaded = true;
		}
	}

	public Heuristic(TrainingAlgorithm algorithm) {
		this.algorithm = algorithm;
	}

	public Category estimate(DoubleFV observations) {
		synchronized (this) {
			if (!loaded)
				h.load();
			if (algorithm == null)
				throw new IllegalStateException(
						"No algorithm has been selected.");
			if (weightings.size() == 0)
				throw new IllegalStateException(
						"Heuristic has not been trained.");
			// Allow +1 margin in case a bias is being used (which is for all
			// the algorithms I've written so far)
			if (weightings.size() - observations.length() > 1)
				throw new IllegalArgumentException(
						"There is a mismatch between the number of observations ("
								+ observations.length()
								+ ") and the number of weightings ("
								+ weightings.size() + ").");
		}

		// Project the measurements onto the model
		Matrix observationSet = new Matrix(observations.values, 1);
		double[] weights;
		synchronized (this) {
			weights = Doubles.toArray(weightings.values());
		}
		Matrix model = new Matrix(weights, 1).transpose();

		double result = algorithm.predict(observationSet, model);

		// Convert to integer and check range
		int category = (int) Math.round(result);
		category = Math.min(category, Category.values().length - 1);
		category = Math.max(category, 1);

		return Category.values()[category];
	}

	@Override
	public void train(TrainingData data) {
		synchronized (this) {
			if (algorithm == null)
				throw new IllegalStateException(
						"No algorithm has been selected.");
		}

		// Organise X matrix and y vector
		double[][] trainingVectors = new double[data.size()][0];
		double[][] target = new double[data.size()][1];
		int i = 0;
		for (TrainingPair p : data) {
			trainingVectors[i] = p.getObservations().values;
			target[i][0] = p.getCategory().ordinal();
			i++;
		}

		Matrix X = new Matrix(trainingVectors);
		Matrix y = new Matrix(target);

		// Find model
		Matrix model;
		// Allow +1 margin for bias
		if (weightings.size() - X.getColumnDimension() <= 1) {
			// Organise weights vector
			double[][] weights;
			synchronized (weightings) {
				weights = new double[][] { Doubles.toArray(weightings.values()) };
			}
			Matrix w = new Matrix(weights).transpose();
			synchronized (algorithm) {
				model = algorithm.findModel(X, y, w);
			}
		} else {
			synchronized (algorithm) {
				model = algorithm.findModel(X, y);
			}
		}

		// Set weights
		synchronized (weightings) {
			weightings.clear();
			for (i = 0; i < model.getRowDimension(); i++) {
				weightings.put(i, model.get(i, 0));
			}
		}

		save();
	}

	public synchronized TrainingAlgorithm getAlgorithm() {
		return algorithm;
	}

	public synchronized void setAlgorithm(TrainingAlgorithm algorithm) {
		setAlgorithm(algorithm, true);
	}

	public synchronized void setAlgorithm(TrainingAlgorithm algorithm,
			boolean autosave) {
		this.algorithm = algorithm;
		clearAll();
		if (autosave)
			save();
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

	public synchronized void setWeighting(Integer component, Double value,
			boolean autosave) {
		weightings.put(component, value);
		if (autosave)
			save();
	}

	public synchronized void clearAll() {
		weightings.clear();
	}

	/**
	 * Load instance from storage
	 */
	public synchronized boolean load() {
		return loaded = h.load();
	}

	/**
	 * Save this instance
	 */
	public synchronized boolean save() {
		// If not loaded and save failed, loaded is still false
		// If save successful, loaded is true
		boolean saved = h.save();
		loaded = loaded | saved;
		return saved;
	}

	/**
	 * @return True if heuristic persists on the disk
	 */
	public synchronized boolean isLoaded() {
		return loaded;
	}

	private synchronized void writeObject(ObjectOutputStream oos)
			throws IOException {
		save();
	}

	private synchronized void readObject(ObjectInputStream ois)
			throws IOException, ClassNotFoundException {
		h = new HeuristicPersistenceHandler(this);
		weightings = new HashMap<>();
		algorithm = null;
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
