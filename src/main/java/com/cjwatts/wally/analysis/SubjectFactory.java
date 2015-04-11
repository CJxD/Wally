package com.cjwatts.wally.analysis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.openimaj.feature.DoubleFV;

import com.cjwatts.wally.analysis.feature.Feature;
import com.cjwatts.wally.persistence.DatabaseSubjectPersistenceHandler;
import com.cjwatts.wally.persistence.PersistenceHandler;
import com.cjwatts.wally.persistence.XMLSubjectPersistenceHandler;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

public class SubjectFactory {

	/**
	 * Generate a new database backed subject with a blank ID.
	 * Uses the default database handler
	 * @return New Subject with default feature categories
	 */
	public static Subject databaseBackedSubject() {
		return databaseBackedSubject("jdbc:derby:db");
	}
	
	/**
	 * Generate a new database backed subject with a blank ID.
	 * @param dbUri Database URI to use (JDBC)
	 * @return New Subject with default feature categories
	 */
	public static Subject databaseBackedSubject(String dbUri) {
		DatabaseSubjectPersistenceHandler dsph = new DatabaseSubjectPersistenceHandler(dbUri);
		Subject s = new Subject(dsph);
		dsph.setInstance(s);
		return s;
	}
	
	/**
	 * Load a database backed subject by their ID, creating a new subject if the given ID does not exist.
	 * If a subject is created, it is not automatically saved to the database
	 * @param id Subject ID number/qualifier
	 * @param dbUri Database URI to use (JDBC)
	 * @return Retrieved subject, otherwise a new Subject with default feature categories
	 */
	public static Subject databaseBackedSubject(String id, String dbUri) {
		DatabaseSubjectPersistenceHandler dsph = new DatabaseSubjectPersistenceHandler(dbUri);
		Subject s = new Subject(id, dsph);
		dsph.setInstance(s);
		s.load();
		return s;
	}
	
	/**
	 * Create a database backed subject with a blank ID, using component data from one or more reference
	 * images to estimate feature categories.
	 * Uses default database handler.
	 * @param componentData One or more feature vectors from principal component analysis
	 * @return New Subject with estimated feature categories
	 */
	public static Subject databaseBackedSubjectFromData(final DoubleFV[] componentData) {
		return databaseBackedSubjectFromData(componentData, "jdbc:derby:db");
	}
	
	/**
	 * Create a database backed subject with a blank ID, using component data from one or more reference
	 * images to estimate feature categories.
	 * @param componentData One or more feature vectors from principal component analysis
	 * @param dbUri Database URI to use (JDBC)
	 * @return New Subject with estimated feature categories
	 */
	public static Subject databaseBackedSubjectFromData(final DoubleFV[] componentData, String dbUri) {
		DatabaseSubjectPersistenceHandler dsph = new DatabaseSubjectPersistenceHandler(dbUri);
		Subject s = estimateSubject(componentData, dsph);
		dsph.setInstance(s);
		return s;
	}
	
	/**
	 * Create a database backed subject using component data from one or more reference
	 * images to estimate feature categories.
	 * The subject is not automatically saved to the database
	 * @param id Subject ID number/qualifier to give new subject
	 * @param componentData One or more feature vectors from principal component analysis
	 * @return New Subject with estimated feature categories
	 */
	public static Subject databaseBackedSubjectFromData(String id, final DoubleFV[] components) {
		Subject s = databaseBackedSubjectFromData(components);
		s.setID(id, false);
		return s;
	}
	
	/**
	 * Create a database backed subject using component data from one or more reference
	 * images to estimate feature categories.
	 * The subject is not automatically saved to the database
	 * @param id Subject ID number/qualifier to give new subject
	 * @param componentData One or more feature vectors from principal component analysis
	 * @param dbUri Database URI to use (JDBC)
	 * @return New Subject with estimated feature categories
	 */
	public static Subject databaseBackedSubjectFromData(String id, final DoubleFV[] components, String dbUri) {
		Subject s = databaseBackedSubjectFromData(components, dbUri);
		s.setID(id, false);
		return s;
	}
	
	/**
	 * Generate a new XML backed subject with a blank ID.
	 * Uses the default XML path
	 * @return New Subject with default feature categories
	 */
	public static Subject xmlBackedSubject() {
		return xmlBackedSubject("subjects/");
	}
	
	/**
	 * Generate a new XML backed subject with a blank ID.
	 * @param savePath Path to save XML file to if required
	 * @return New Subject with default feature categories
	 */
	public static Subject xmlBackedSubject(String savePath) {
		XMLSubjectPersistenceHandler xsph = new XMLSubjectPersistenceHandler(savePath);
		Subject s = new Subject(xsph);
		xsph.setInstance(s);
		return s;
	}
	
	/**
	 * Load a XML backed subject by their ID, creating a new subject if the given ID does not exist.
	 * If a subject is created, it is not automatically saved
	 * @param id Subject ID number/qualifier
	 * @param savePath Path to save XML file to if required
	 * @return Retrieved subject, otherwise a new Subject with default feature categories
	 */
	public static Subject xmlBackedSubject(String savePath, String id) {
		XMLSubjectPersistenceHandler xsph = new XMLSubjectPersistenceHandler(savePath);
		Subject s = new Subject(id, xsph);
		xsph.setInstance(s);
		s.load();
		return s;
	}
	
	/**
	 * Create a XML backed subject with a blank ID, using component data from one or more reference
	 * images to estimate feature categories.
	 * Uses default database handler.
	 * @param componentData One or more feature vectors from principal component analysis
	 * @return New Subject with estimated feature categories
	 */
	public static Subject xmlBackedSubjectFromData(final DoubleFV[] components) {
		return xmlBackedSubjectFromData(components, "subjects/");
	}
	
	/**
	 * Create a XML backed subject with a blank ID, using component data from one or more reference
	 * images to estimate feature categories.
	 * @param componentData One or more feature vectors from principal component analysis
	 * @param savePath Path to save XML file to if required
	 * @return New Subject with estimated feature categories
	 */
	public static Subject xmlBackedSubjectFromData(final DoubleFV[] components, String savePath) {
		XMLSubjectPersistenceHandler xsph = new XMLSubjectPersistenceHandler(savePath);
		Subject s = estimateSubject(components, xsph);
		xsph.setInstance(s);
		return s;
	}
	
	/**
	 * Create a XML backed subject using component data from one or more reference
	 * images to estimate feature categories.
	 * The subject is not automatically saved
	 * @param id Subject ID number/qualifier to give new subject
	 * @param componentData One or more feature vectors from principal component analysis
	 * @param savePath Path to save XML file to if required
	 * @return New Subject with estimated feature categories
	 */
	public static Subject xmlBackedSubjectFromData(String id, final DoubleFV[] components) {
		Subject s = xmlBackedSubjectFromData(components);
		s.setID(id, false);
		return s;
	}
	
	/**
	 * Create a XML backed subject using component data from one or more reference
	 * images to estimate feature categories.
	 * The subject is not automatically saved
	 * @param id Subject ID number/qualifier to give new subject
	 * @param componentData One or more feature vectors from principal component analysis
	 * @param savePath Path to save XML file to if required
	 * @return New Subject with estimated feature categories
	 */
	public static Subject xmlBackedSubjectFromData(String id, final DoubleFV[] components, String savePath) {
		Subject s = xmlBackedSubjectFromData(components, savePath);
		s.setID(id, false);
		return s;
	}
	
	/**
	 * Estimates a Subject from multiple component analysis samples (from multiple images)
	 * and returns the mode of each feature.
	 * @param componentData One or more principal component analysis data
	 * @param ph Persistence handler to use
	 * @return New Subject with estimated feature categories
	 */
	private static Subject estimateSubject(final DoubleFV[] componentData, PersistenceHandler<Subject> ph) {
		if (componentData.length == 0)
			throw new IllegalArgumentException("Must have at least 1 full set of components to estimate features");
		
		Subject output = new Subject(ph);
		ph.setInstance(output);
		
		Map<Feature, List<Category>> estimations = new HashMap<>();
		// Initialise estimations
		for (Feature f : output.features.keySet()) {
			estimations.put(f, new ArrayList<Category>());
		}
		
		// Populate estimations
		for (DoubleFV components : componentData) {
			Subject s = Subject.fromData(components, ph);
			for (Entry<Feature, Category> e : s.features.entrySet()) {
				estimations.get(e.getKey()).add(e.getValue());
			}
		}
		
		// Create an averaged subject
		for (Feature f : output.features.keySet()) {
			output.setCategory(f, mode(estimations.get(f)));
		}
		
		return output;
	}

	private static Category mode(Collection<Category> list) {
		Multiset<Category> multiset = HashMultiset.create(list);

		List<Category> modes = new ArrayList<>();
		int max = 0;

		for (Multiset.Entry<Category> e : multiset.entrySet()) {
			if (e.getCount() == max) {
				modes.add(e.getElement());
			} else if (e.getCount() > max) {
				max = e.getCount();
				modes.clear();
				modes.add(e.getElement());
			}
		}

		int output = 0;
		for (Category c : modes) {
			output += c.ordinal();
		}
		output /= modes.size();
		
		return Category.values()[output];
	}
}
