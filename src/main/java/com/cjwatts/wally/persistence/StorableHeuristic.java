package com.cjwatts.wally.persistence;

import javax.xml.bind.annotation.XmlRootElement;

import com.cjwatts.wally.training.Heuristic;

@XmlRootElement(name = "heuristic")
public class StorableHeuristic {
	public float learningRate;
	public StorableWeightingsMap weightings;
	
	public StorableHeuristic(Heuristic h) {
		learningRate = h.getLearningRate();
		weightings = new StorableWeightingsMap(h.getWeightings());
	}
	
	public StorableHeuristic() {}
	
	public void restoreTo(Heuristic h) {
		h.setLearningRate(learningRate, false);
		
		for (StorableWeighting w : weightings.weighting) {
			h.setWeighting(w.name, w.value, false);
		}
	}
}