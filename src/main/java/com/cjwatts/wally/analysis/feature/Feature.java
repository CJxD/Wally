package com.cjwatts.wally.analysis.feature;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.openimaj.feature.DoubleFV;

import com.cjwatts.wally.analysis.Analysable;
import com.cjwatts.wally.analysis.Category;
import com.cjwatts.wally.analysis.FeatureCategory;
import com.cjwatts.wally.training.Heuristic;
import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;

public abstract class Feature extends Heuristic implements Analysable, Comparable<Feature> {
	private static final long serialVersionUID = 1L;
	
	private static ImmutableSet<ClassInfo> classes;
	
	/**
	 * Get a feature by its case-insensitive name
	 * @param name
	 * @return
	 * @throws IllegalArgumentException If the feature could not be found
	 * @throws IOException If the classloader could not be read
	 */
	public static Feature forName(String name) throws IllegalArgumentException, IOException {
		if (classes == null) {
			ClassPath cp = ClassPath.from(Feature.class.getClassLoader());
			classes = cp.getTopLevelClasses(Feature.class.getPackage().getName());
		}
		
		try {
			for (ClassInfo c : classes) {
				if (c.getSimpleName().equalsIgnoreCase(name)) {
					Class<?> f = Class.forName(c.getName());
					Method getInstance = f.getDeclaredMethod("getInstance");
					return (Feature) getInstance.invoke(null);
				}
			}
			throw new ClassNotFoundException();
		} catch (ClassNotFoundException
				| NoSuchMethodException
				| IllegalAccessException
				| InvocationTargetException ex) {
			throw new IllegalArgumentException("No such feature: " + name, ex);
		}
	}
	
	@Override
	public Category analyse(DoubleFV components) {
		return estimate(components);
	}

	@Override
	public int compareTo(Feature o) {
		return this.toString().compareTo(o.toString());
	}

	public abstract FeatureCategory getFeatureCategory(int ordinal);
	
}
