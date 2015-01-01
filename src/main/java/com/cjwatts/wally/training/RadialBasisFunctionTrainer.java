package com.cjwatts.wally.training;

import Jama.Matrix;

public class RadialBasisFunctionTrainer implements TrainingAlgorithm {
	private static final long serialVersionUID = 1L;
	
	private RBF rbf;
	
	public RadialBasisFunctionTrainer(RBF radialBasisFunction) {
		this.rbf = radialBasisFunction;
	}
	
	@Override
	public Matrix findModel(Matrix X, Matrix y) {
		
		// Apply RBF
		double[][] x = X.getArrayCopy(); // Make a clone, otherwise bad things happen
		for (int i = 0; i < X.getRowDimension(); i++) {
			for (int j = 0; j < X.getColumnDimension(); j++) {
				x[i][j] = rbf.eval(x[i][j]);
			}
		}

		return new LinearRegressionTrainer().findModel(new Matrix(x), y);
	}

	@Override
	public Matrix findModel(Matrix X, Matrix y, Matrix w) {
		return findModel(X, y);
	}
	
	@Override
	public double predict(Matrix X, Matrix w) {
		
		// Apply RBF
		double[][] x = X.getArrayCopy(); // Make a clone, otherwise bad things happen
		for (int i = 0; i < X.getColumnDimension(); i++) {
			x[1][i] = rbf.eval(x[1][i]);
		}
		
		return new Matrix(x).times(w).get(0, 0);
	}

}
