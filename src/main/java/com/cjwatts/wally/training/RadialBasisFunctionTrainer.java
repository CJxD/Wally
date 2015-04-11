package com.cjwatts.wally.training;

import org.openimaj.ml.clustering.kmeans.DoubleKMeans;

import Jama.Matrix;

public class RadialBasisFunctionTrainer implements TrainingAlgorithm {
	private static final long serialVersionUID = 2L;
	
	/**
	 * Factory method to produce a gaussian-based RBF
	 * @param sigma Scaling factor for the bases
	 */
	public static RadialBasisFunctionTrainer createGaussianRBF(final double sigma) {
		return new RadialBasisFunctionTrainer(new MappingFunction() {
			private static final long serialVersionUID = 1L;

			@Override
			public double eval(double x) {
				return Math.exp(x / (sigma * sigma));
			}
			
		});
	}
	
	protected final MappingFunction mapping;
	protected double[][] centroids = null;
	
	@Override
	public RadialBasisFunctionTrainer clone() {
		RadialBasisFunctionTrainer t = new RadialBasisFunctionTrainer(mapping);
		t.centroids = centroids;
		return t;
	}
	
	/**
	 * @param mappingFunction The non-linear mapping function to use 
	 */
	public RadialBasisFunctionTrainer(MappingFunction mappingFunction) {
		this.mapping = mappingFunction;
	}
	
	protected double[][] kmeans(Matrix X) {
		int k = X.getColumnDimension();
		if (k > X.getRowDimension())
			throw new IllegalArgumentException("Not enough row samples (" + X.getRowDimension() + ") to generate " + k + " unique bases.");
		
		DoubleKMeans kmeans = DoubleKMeans.createKDTreeEnsemble(k);
		double[][] x = X.getArray();
		return kmeans.cluster(x).centroids;
	}
	
	protected Matrix transform(Matrix X) {
		int rows = X.getRowDimension();
		int cols = X.getColumnDimension();
		Matrix C = new Matrix(centroids);
		
		// Replace each observation in X with the weighted distances from each centroid
		Matrix row, centroid, output = new Matrix(rows, cols);
		
		for (int i = 0; i < rows; i++) {
			// Extract the current row as a new matrix
			row = X.getMatrix(i, i, 0, cols - 1);
			
			for (int j = 0; j < cols; j++) {
				// Get centroid j
				centroid = C.getMatrix(j, j, 0, cols - 1);
				
				// Apply Mapping Function to distance from centroid
				output.set(i, j, mapping.eval(row.minus(centroid).norm2()));
			}
		}
		
		return output;
	}
	
	@Override
	public Matrix findModel(Matrix X, Matrix y, Matrix w) {
		return findModel(X, y);
	}
	
	@Override
	public Matrix findModel(Matrix X, Matrix y) {
		centroids = kmeans(X);
		return new LinearRegressionTrainer().findModel(transform(X), y);
	}
	
	@Override
	public double predict(Matrix X, Matrix w) {
		if (centroids == null)
			throw new IllegalStateException("The centroids model is empty - please invoke findModel() first.");
		return new LinearRegressionTrainer().predict(transform(X), w);
	}

}
