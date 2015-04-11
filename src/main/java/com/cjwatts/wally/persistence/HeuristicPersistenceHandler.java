package com.cjwatts.wally.persistence;

import com.cjwatts.wally.training.Heuristic;

public class HeuristicPersistenceHandler extends XMLPersistenceHandler<Heuristic, StorableHeuristic> {

	public static String dbPath = "heuristics/";
	
	public HeuristicPersistenceHandler(Heuristic heuristic) {
		super(dbPath, heuristic);
	}

	@Override
	protected StorableHeuristic createStore() {
		return new StorableHeuristic();
	}
	
}
