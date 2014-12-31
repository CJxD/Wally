package com.cjwatts.wally.persistence;

import javax.xml.bind.annotation.XmlRootElement;

import com.cjwatts.wally.training.Heuristic;

@XmlRootElement(name = "heuristic")
public class StorableHeuristic implements Storable<Heuristic> {
	public StorableWeightingsMap weightings;
	
	public StorableHeuristic() {}
	
	@Override
	public void store(Heuristic h) {
		//learningRate = h.getLearningRate();
		weightings = new StorableWeightingsMap(h.getWeightings());
	}
	
	@Override
	public void restoreTo(Heuristic h) {
		//h.setLearningRate(learningRate, false);
		
		for (StorableWeighting w : weightings.weighting) {
			h.setWeighting(w.component, w.weight, false);
		}
	}
	
}