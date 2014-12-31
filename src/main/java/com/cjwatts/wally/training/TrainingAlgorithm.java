package com.cjwatts.wally.training;

import java.io.Serializable;

import Jama.Matrix;

public interface TrainingAlgorithm extends Serializable {

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
	
}
