package com.cjwatts.wally.analysis;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TreeMap;

import org.openimaj.feature.DoubleFV;
import org.openimaj.util.function.Operation;
import org.openimaj.util.parallel.Parallel;

import com.cjwatts.wally.analysis.feature.*;
import com.cjwatts.wally.persistence.PersistenceHandler;
import com.cjwatts.wally.training.TrainingAlgorithm;
import com.cjwatts.wally.training.TrainingData;
import com.cjwatts.wally.training.TrainingPair;
import com.cjwatts.wally.training.TrainingSet;

public class Subject implements Iterable<Entry<Feature, Category>> {
	
	/*
	 * For subjects without an ID, generate one in this range
	 */
	private static final int defaultIDStart = Integer.MAX_VALUE / 2;
	private static final int defaultIDEnd = Integer.MAX_VALUE;
	
	protected final PersistenceHandler<Subject> h;
	protected String id;
	protected Map<Feature, Category> features = new TreeMap<>();
	
	private boolean loaded = false;
	
	public static Subject fromData(final DoubleFV components, PersistenceHandler<Subject> ph) {
		Subject s = new Subject(ph);
		
		Age a = Age.getInstance();
		s.features.put(a, a.analyse(components));
		
		ArmLength al = ArmLength.getInstance();
		s.features.put(al, al.analyse(components));
		
		ArmThickness at = ArmThickness.getInstance();
		s.features.put(at, at.analyse(components));
		
		Chest cw = Chest.getInstance();
		s.features.put(cw, cw.analyse(components));
		
		FacialHairColour fhc = FacialHairColour.getInstance();
		s.features.put(fhc, fhc.analyse(components));
		
		FacialHairLength fhl = FacialHairLength.getInstance();
		s.features.put(fhl, fhl.analyse(components));
		
		Figure fs = Figure.getInstance();
		s.features.put(fs, fs.analyse(components));
		
		HairColour hc = HairColour.getInstance();
		s.features.put(hc, hc.analyse(components));
		
		HairLength hl = HairLength.getInstance();
		s.features.put(hl, hl.analyse(components));
		
		Height h = Height.getInstance();
		s.features.put(h, h.analyse(components));
		
		Hips hw = Hips.getInstance();
		s.features.put(hw, hw.analyse(components));
		
		LegLength ll = LegLength.getInstance();
		s.features.put(ll, ll.analyse(components));
		
		LegThickness lt = LegThickness.getInstance();
		s.features.put(lt, lt.analyse(components));
		
		MuscleBuild mb = MuscleBuild.getInstance();
		s.features.put(mb, mb.analyse(components));
		
		NeckLength nl = NeckLength.getInstance();
		s.features.put(nl, nl.analyse(components));
		
		NeckThickness nt = NeckThickness.getInstance();
		s.features.put(nt, nt.analyse(components));
		
		Sex sx = Sex.getInstance();
		s.features.put(sx, sx.analyse(components));
		
		ShoulderShape ss = ShoulderShape.getInstance();
		s.features.put(ss, ss.analyse(components));
		
		SkinColour sc = SkinColour.getInstance();
		s.features.put(sc, sc.analyse(components));
		
		Weight w = Weight.getInstance();
		s.features.put(w, w.analyse(components));
		
		return s;
	}
	
	Subject(PersistenceHandler<Subject> ph) {
		this("" + new Random().nextInt(defaultIDEnd - defaultIDStart) + defaultIDStart, ph);
	}
	
	Subject(String id, PersistenceHandler<Subject> ph) {
		this.id = id;
		this.h = ph;
		
		// Defaults
		Age a = Age.getInstance();
		features.put(a, Category.MEDIUM);
		
		ArmLength al = ArmLength.getInstance();
		features.put(al, Category.MEDIUM);
		
		ArmThickness at = ArmThickness.getInstance();
		features.put(at, Category.MEDIUM);
		
		Chest cw = Chest.getInstance();
		features.put(cw, Category.MEDIUM);
		
		FacialHairColour fhc = FacialHairColour.getInstance();
		features.put(fhc, Category.MEDIUM);
		
		FacialHairLength fhl = FacialHairLength.getInstance();
		features.put(fhl, Category.MEDIUM);
		
		Figure fs = Figure.getInstance();
		features.put(fs, Category.MEDIUM);
		
		HairColour hc = HairColour.getInstance();
		features.put(hc, Category.MEDIUM);
		
		HairLength hl = HairLength.getInstance();
		features.put(hl, Category.MEDIUM);
		
		Height h = Height.getInstance();
		features.put(h, Category.MEDIUM);
		
		Hips hw = Hips.getInstance();
		features.put(hw, Category.MEDIUM);
		
		LegLength ll = LegLength.getInstance();
		features.put(ll, Category.MEDIUM);
		
		LegThickness lt = LegThickness.getInstance();
		features.put(lt, Category.MEDIUM);
		
		MuscleBuild mb = MuscleBuild.getInstance();
		features.put(mb, Category.MEDIUM);
		
		NeckLength nl = NeckLength.getInstance();
		features.put(nl, Category.MEDIUM);
		
		NeckThickness nt = NeckThickness.getInstance();
		features.put(nt, Category.MEDIUM);
		
		Sex sx = Sex.getInstance();
		features.put(sx, Category.VERY_LOW);
		
		ShoulderShape ss = ShoulderShape.getInstance();
		features.put(ss, Category.MEDIUM);
		
		SkinColour sc = SkinColour.getInstance();
		features.put(sc, Category.MEDIUM);
		
		Weight w = Weight.getInstance();
		features.put(w, Category.MEDIUM);
	}
	
	/**
	 * Get the subject's category for a feature
	 * @param f
	 * @return
	 */
	public Category getCategory(Feature f) {
		return features.get(f);
	}
	
	/**
	 * Train all features with the given feature vector
	 * and the subject's known metrics.
	 * Uses parallel processing
	 * @param components
	 */
	public void trainFeatures(final DoubleFV components) {
		/*Parallel.forEach(features.entrySet(), new Operation<Entry<Feature, Category>>() {

			@Override
			public void perform(Entry<Feature, Category> e) {
				TrainingData data = new TrainingData();
				data.setTrainingPair(components, e.getValue());
				
				e.getKey().train(data);
			}
						
		});*/
		
		for (Entry<Feature, Category> e : features.entrySet()) {
			TrainingData data = new TrainingData();
			data.put(components, e.getValue());
			
			e.getKey().train(data);
		}
	}
	
	/**
	 * Generate a training set from this subject and the given feature vector
	 * @param components
	 */
	public TrainingSet createTrainingSet(final DoubleFV components) {
		TrainingSet s = new TrainingSet();
		
		for (Entry<Feature, Category> e : features.entrySet()) {
			TrainingPair p = new TrainingPair(components, e.getValue());
			s.add(e.getKey(), p);
		}
		
		return s;
	}

	/**
	 * @param other Subject to compare to
	 * @return Difference from other's feature categories
	 */
	public float distanceFrom(Subject other) {
		float distance = 0;
		
		for (Entry<Feature, Category> e : features.entrySet()) {
			Category c = other.features.get(e.getKey());
			if (c != null) {
				int temp = other.features.get(e.getKey()).compareTo(e.getValue());
				distance += Math.abs(temp);
			} else {
				distance += e.getValue().ordinal();
			}
		}
		
		return distance;
	}
	
	/**
	 * Set the subject's category for a feature
	 * @param f
	 * @param c
	 */
	public void setCategory(Feature f, Category c) {
		features.put(f, c);
	}
	
	/**
	 * Set the subject's category for a feature
	 * @param f
	 * @param c
	 * @param autosave Whether to automatically save or not
	 */
	public void setCategory(Feature f, Category c, boolean autosave) {
		features.put(f, c);
		if (autosave) save();
	}
	
	/**
	 * Get the map containing mappings between features and categories
	 * @return
	 */
	public Map<Feature, Category> getFeatures() {
		return features;
	}
	
	/**
	 * Get the subject's ID
	 * @return
	 */
	public String getID() {
		return id;
	}
	
	/**
	 * Set the subject's ID
	 * @param id
	 */
	public void setID(String id) {
		setID(id, true);
	}
	
	/**
	 * Set the subject's ID
	 * @param id
	 * @param autosave Whether to automatically save or not
	 */
	public void setID(String id, boolean autosave) {
		this.id = id;
		if (autosave) save();
	}

	/**
	 * Load instance from storage
	 */
	public boolean load() {
		boolean result = h.load();
		loaded = result;
		return result;
	}
	
	/**
	 * Save this instance
	 */
	public void save() {
		h.save();
		loaded = true;
	}
	
	/**
	 * @return True if subject persists on the disk
	 */
	public boolean isLoaded() {
		return loaded;
	}

	/**
	 * An alphabetically ordered iterator of features => categories.
	 */
	@Override
	public Iterator<Entry<Feature, Category>> iterator() {
		return features.entrySet().iterator();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Subject other = (Subject) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return this.id;
	}

}
