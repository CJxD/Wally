package com.cjwatts.wally.training;

import java.util.Arrays;

import Jama.Matrix;

public class MedianTrainer implements TrainingAlgorithm {
	private static final long serialVersionUID = 2L;
	
	@Override
	public MedianTrainer clone() {
		return new MedianTrainer();
	}
	
	@Override
	public Matrix findModel(Matrix X, Matrix y, Matrix w) {
		return findModel(X, y);
	}
	
	@Override
	public Matrix findModel(Matrix X, Matrix y) {
		Matrix w = new Matrix(X.getColumnDimension(), 1);
		
		// Find median of y and store it in "weighting matrix"
		double vals[] = y.getColumnPackedCopy();
		Arrays.sort(vals);
		
		int l = vals.length;
		double median;
		if (l % 2 == 0)
		    median = (vals[l / 2] + vals[l / 2 - 1]) / 2;
		else
		    median = vals[l / 2];
		
		w.set(0, 0, median);
		
		return w;
	}
	
	@Override
	public double predict(Matrix X, Matrix w) {
		return w.get(0, 0);
	}

}
