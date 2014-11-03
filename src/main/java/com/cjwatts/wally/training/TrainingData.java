package com.cjwatts.wally.training;

import java.util.Iterator;
import java.util.Set;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.cjwatts.wally.analysis.Category;
import com.cjwatts.wally.analysis.Measurement;

public class TrainingData implements Iterable<TrainingPair> {
	private Map<Set<Measurement>, Category> data = new TreeMap<>();
	
	public Category getCategory(Set<Measurement> measurements) {
		return data.get(measurements);
	}
	
	public void setTrainingPair(Set<Measurement> measurements, Category category) {
		data.put(measurements, category);
	}
	
	public void setTrainingPair(TrainingPair p) {
		data.put(p.getMeasurements(), p.getCategory());
	}
	
	public void removeTrainingPair(Set<Measurement> measurements) {
		data.remove(measurements);
	}
	
	public void removeTrainingPair(TrainingPair p) {
		data.remove(p.getMeasurements());
	}

	@Override
	public Iterator<TrainingPair> iterator() {
		final Iterator<Entry<Set<Measurement>, Category>> dataIt = data.entrySet().iterator();
		
		return new Iterator<TrainingPair>() {

			@Override
			public boolean hasNext() {
				return dataIt.hasNext();
			}

			@Override
			public TrainingPair next() {
				Entry<Set<Measurement>, Category> next = dataIt.next();
				return new TrainingPair(next.getKey(), next.getValue());
			}

			@Override
			public void remove() {
				dataIt.remove();
			}
			
		};
	}
}
