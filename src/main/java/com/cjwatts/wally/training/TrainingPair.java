package com.cjwatts.wally.training;

import java.io.Serializable;

import org.openimaj.feature.DoubleFV;

import com.cjwatts.wally.analysis.Category;

/**
 * A relation between some observations/measurements and a {@link Category} for a particular {@link Heuristic}
 */
public final class TrainingPair implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private final DoubleFV observations;
	private final Category category;
	
	public TrainingPair(DoubleFV observations, Category category) {
		this.observations = observations;
		this.category = category;
	}
	
	public DoubleFV getObservations() {
		return observations;
	}

	public Category getCategory() {
		return category;
	}
}