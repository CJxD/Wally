package com.cjwatts.wally.training;

import Jama.Matrix;

public class LinearRegressionTrainer implements TrainingAlgorithm {
	private static final long serialVersionUID = 1L;

	@Override
	public Matrix findModel(Matrix X, Matrix y) {
		// w = (X'X)^-1 * X'y
		Matrix Xt = X.transpose();
		
		return Xt.times(X)
				.inverse()
				.times(Xt)
				.times(y);
	}

	@Override
	public Matrix findModel(Matrix X, Matrix y, Matrix w) {
		return findModel(X, y);
	}

	@Override
	public double predict(Matrix X, Matrix w) {
		return X.times(w).get(0, 0);
	}
	
}
