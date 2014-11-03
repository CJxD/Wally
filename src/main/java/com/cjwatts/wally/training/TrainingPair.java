package com.cjwatts.wally.training;

import java.util.Set;

import com.cjwatts.wally.analysis.Category;
import com.cjwatts.wally.analysis.Measurement;

public final class TrainingPair {
	private final Set<Measurement> measurements;
	private final Category category;
	
	public TrainingPair(Set<Measurement> measurements, Category category) {
		this.measurements = measurements;
		this.category = category;
	}
	
	public Set<Measurement> getMeasurements() {
		return measurements;
	}

	public Category getCategory() {
		return category;
	}
}