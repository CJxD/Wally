package com.cjwatts.wally.training;

import Jama.Matrix;

public class RadialBasisFunctionTrainer implements TrainingAlgorithm {
	private static final long serialVersionUID = 1L;

	interface RBF {
		public double eval(double x, double sigma);
	}
	
	public static RBF GAUSSIAN_RBF = new RBF() {
		
		@Override
		public double eval(double a, double sigma) {
			return Math.exp(a / (sigma * sigma));
		}
		
	};
	
	@Override
	public Matrix findModel(Matrix X, Matrix y) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Matrix findModel(Matrix X, Matrix y, Matrix w) {
		return findModel(X, y);
	}

}
