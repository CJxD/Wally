package com.cjwatts.wally.training;

import Jama.Matrix;

public class PerceptronTrainer implements TrainingAlgorithm {
	private static final long serialVersionUID = 1L;

	protected double eta;
	protected long iterations;
	
	/**
	 * @param eta Learning rate
	 * @param iterations Number of iterations to perform
	 */
	public PerceptronTrainer(double eta, long iterations) {
		this.eta = eta;
		this.iterations = iterations;
	}
	
	@Override
	public Matrix findModel(Matrix X, Matrix y) {
		return findModel(X, y, Matrix.random(X.getColumnDimension(), 1));
	}
	
	@Override
	public Matrix findModel(Matrix X, Matrix y, Matrix w) {
		Matrix gradient;
		for (long i = 0; i < iterations; i++) {
			// Gradient descent 2Xt(Xw - y)
			gradient = X.transpose().times(2).times(X.times(w).minus(y));
			Matrix temp = gradient.times(eta);
			w = w.minus(temp);
		}
		
		return w;
	}

	@Override
	public double predict(Matrix X, Matrix w) {
		return X.times(w).get(0, 0);
	}

}
