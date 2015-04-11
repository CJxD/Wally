package com.cjwatts.wally.training;

import java.util.Random;

import Jama.Matrix;

public class RandomTrainer implements TrainingAlgorithm {
	private static final long serialVersionUID = 2L;

	private transient Random random = new Random();
	
	@Override
	public RandomTrainer clone() {
		return new RandomTrainer();
	}
	
	@Override
	public Matrix findModel(Matrix X, Matrix y, Matrix w) {
		return findModel(X, y);
	}
	
	@Override
	public Matrix findModel(Matrix X, Matrix y) {
		Matrix w = new Matrix(X.getColumnDimension(), 1);
		
		// Find max of y and store it in "weighting matrix"
		double vals[] = y.getColumnPackedCopy();
		double max = 0;
		
		for (double d : vals) {
			if (d > max) max = d;
		}
		
		w.set(0, 0, max);
		
		return w;
	}
	
	@Override
	public double predict(Matrix X, Matrix w) {
		return random.nextInt((int) w.get(0, 0));
	}

}
