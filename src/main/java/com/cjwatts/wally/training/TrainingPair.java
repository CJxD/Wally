package com.cjwatts.wally.training;

import java.io.Serializable;

import org.openimaj.feature.DoubleFV;

import com.cjwatts.wally.analysis.Category;

public final class TrainingPair implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private final DoubleFV measurements;
	private final Category category;
	
	public TrainingPair(DoubleFV measurements, Category category) {
		this.measurements = measurements;
		this.category = category;
	}
	
	public DoubleFV getMeasurements() {
		return measurements;
	}

	public Category getCategory() {
		return category;
	}
}