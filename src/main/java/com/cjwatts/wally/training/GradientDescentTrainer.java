package com.cjwatts.wally.training;

import Jama.Matrix;

public class GradientDescentTrainer implements TrainingAlgorithm {
	private static final long serialVersionUID = 1L;

	protected double eta;
	protected long iterations;
	
	/**
	 * @param eta Learning rate
	 * @param iterations Number of iterations to perform
	 */
	public GradientDescentTrainer(double eta, long iterations) {
		this.eta = eta;
		this.iterations = iterations;
	}
	
	@Override
	public GradientDescentTrainer clone() {
		return new GradientDescentTrainer(eta, iterations);
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
		return findModel(X, y, Matrix.random(X.getColumnDimension() + 1, 1));
	}
	
	@Override
	public Matrix findModel(Matrix X, Matrix y, Matrix w) {
		if (w.getRowDimension() < X.getColumnDimension())
			return findModel(X, y);
		
		X = addBias(X);
		
		if (w.getRowDimension() < X.getColumnDimension()) {
			Matrix temp = Matrix.identity(X.getColumnDimension() + 1, 1);
			temp.setMatrix(1, temp.getRowDimension() - 1, 0, 0, w);
			w = temp;
		}
		
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
		X = addBias(X);
		return X.times(w).get(0, 0);
	}

}
