package com.cjwatts.wally.training;

import Jama.Matrix;

public class LinearRegressionTrainer implements TrainingAlgorithm {
	private static final long serialVersionUID = 1L;

	@Override
	public LinearRegressionTrainer clone() {
		return new LinearRegressionTrainer();
	}
	
	/**
	 * @param X The data matrix
	 * @return X with an extra 1 at the beginning of each row
	 */
	protected Matrix addBias(Matrix X) {
		double[][] temp = X.getArrayCopy();
		int cols = X.getColumnDimension();
		
		for (int i = 0; i < temp.length; i++) {
			double[] newRow = new double[cols + 1];
			System.arraycopy(temp[i], 0, newRow, 1, cols);
			newRow[0] = 1;
			
			temp[i] = newRow;
		}
		
		return new Matrix(temp);
	}
	
	@Override
	public Matrix findModel(Matrix X, Matrix y) {
		// Add bias term
		X = addBias(X);
		
		// w = (X'X + aI)^-1 * X'y
		Matrix Xt = X.transpose();
		Matrix reg = Matrix.identity(
				X.getColumnDimension(),
				X.getColumnDimension())
				.times(0.0005);
		
		return Xt.times(X)
				.plus(reg)
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
		X = addBias(X);
		return X.times(w).get(0, 0);
	}
	
}
