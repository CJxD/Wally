package com.cjwatts.wally.training;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;

import org.openimaj.feature.DoubleFV;

import com.cjwatts.wally.analysis.Category;

/**
 * A set of {@link TrainingPair}s to train a particular {@link Heuristic} with multiple examples
 */
public class TrainingData extends HashMap<DoubleFV, Category> implements Iterable<TrainingPair>, Serializable {

	private static final long serialVersionUID = 1L;
	
	public void put(TrainingPair p) {
		put(p.getObservations(), p.getCategory());
	}
	
	public void remove(TrainingPair p) {
		remove(p.getObservations());
	}

	@Override
	public Iterator<TrainingPair> iterator() {
		final Iterator<Entry<DoubleFV, Category>> it = entrySet().iterator();
		
		return new Iterator<TrainingPair>() {

			@Override
			public boolean hasNext() {
				return it.hasNext();
			}

			@Override
			public TrainingPair next() {
				Entry<DoubleFV, Category> next = it.next();
				return new TrainingPair(next.getKey(), next.getValue());
			}

			@Override
			public void remove() {
				it.remove();
			}
			
		};
	}
}
