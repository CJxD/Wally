package com.cjwatts.wally.analysis.feature;

import java.io.IOException;
import java.io.ObjectInputStream;

import com.cjwatts.wally.analysis.FeatureCategory;

public class MuscleBuild extends Feature {
	private static final long serialVersionUID = 3L;
	private static MuscleBuild instance;
	
	public enum Category implements FeatureCategory {
		NULL, VERY_LEAN, LEAN, AVERAGE, MUSCLY, VERY_MUSCLY;

		public static Category fromInt(int value) {
			Category[] values = Category.values();
			
			if (value >= values.length) {
				value = values.length - 1;
			} else if (value < 1) {
				value = 1;
			}
			
			return values[value];
		}
		
		@Override
		public int toInt() {
			return ordinal();
		}
	}
	
	public static MuscleBuild getInstance() {
		return instance == null ? instance = new MuscleBuild() : instance;
	}
	
	public static Class<? extends FeatureCategory> getCategoryClass() {
		return Category.class;
	}
	
	public FeatureCategory getFeatureCategory(int ordinal) {
		return Category.values()[ordinal];
	}
	
	private synchronized void readObject(ObjectInputStream ois)
			throws IOException, ClassNotFoundException {
		instance = this;
	}
	
}
