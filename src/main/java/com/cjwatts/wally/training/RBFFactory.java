package com.cjwatts.wally.training;

public class RBFFactory {

	public static RBF createGaussianRBF(final double sigma) {
		return new RBF() {
			
			@Override
			public double eval(double x) {
				return Math.exp(x / (sigma * sigma));
			}
			
		};
	}
	
}
