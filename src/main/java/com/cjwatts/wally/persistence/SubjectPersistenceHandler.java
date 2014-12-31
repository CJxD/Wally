package com.cjwatts.wally.persistence;

import com.cjwatts.wally.analysis.Subject;

public class SubjectPersistenceHandler extends PersistenceHandler<Subject, StorableSubject> {

	public SubjectPersistenceHandler(String path, Subject subject) {
		super(path, subject);
	}

	@Override
	protected StorableSubject createStore() {
		return new StorableSubject();
	}
	
}
