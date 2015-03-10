package com.cjwatts.wally.persistence;

import com.cjwatts.wally.training.Heuristic;

public class HeuristicPersistenceHandler extends XMLPersistenceHandler<Heuristic, StorableHeuristic> {

	public HeuristicPersistenceHandler(Heuristic heuristic) {
		super("heuristics/", heuristic);
	}

	@Override
	protected StorableHeuristic createStore() {
		return new StorableHeuristic();
	}
	
}
