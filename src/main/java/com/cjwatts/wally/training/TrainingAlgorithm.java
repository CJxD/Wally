package com.cjwatts.wally.training;

import java.io.Serializable;

import Jama.Matrix;

public interface TrainingAlgorithm extends Serializable, Cloneable {

	/**
	 * @return An algorithm with the same settings as the current
	 */
	public TrainingAlgorithm clone();
	
	/**
	 * Perform regression to generate a model of
	 * weights for each variable.
	 * 
	 * @param X The input data in rows for each input vector
	 * 			and columns for each feature
	 * @param y The expected output for each input vector
	 * 
	 * @return Vector of weights
	 */
	public Matrix findModel(Matrix X, Matrix y);
	
	/**
	 * Perform regression to generate a model of
	 * weights for each variable.
	 * 
	 * @param X The input data in rows for each input vector
	 * 			and columns for each feature
	 * @param y The expected output for each input vector
	 * @param w The previously known weights
	 * 
	 * @return Vector of weights
	 */
	public Matrix findModel(Matrix X, Matrix y, Matrix w);
	
	/**
	 * Make a prediction from an observation
	 * 
	 * @param X A single row of inputs
	 * @param w The model
	 * 
	 * @return The projected result
	 */
	public double predict(Matrix x, Matrix w);
	
}
