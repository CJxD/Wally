package com.cjwatts.wally.persistence;

import com.cjwatts.wally.analysis.Subject;

public class XMLSubjectPersistenceHandler extends XMLPersistenceHandler<Subject, StorableSubject> {

	public XMLSubjectPersistenceHandler(String path) {
		super(path);
	}
	
	public XMLSubjectPersistenceHandler(String path, Subject subject) {
		super(path, subject);
	}

	@Override
	protected StorableSubject createStore() {
		return new StorableSubject();
	}
	
}
