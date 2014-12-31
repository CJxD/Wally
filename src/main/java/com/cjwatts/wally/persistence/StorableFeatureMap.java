package com.cjwatts.wally.persistence;

import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeSet;

import com.cjwatts.wally.analysis.Category;
import com.cjwatts.wally.analysis.feature.Feature;

public class StorableFeatureMap {
    public SortedSet<StorableFeature> feature = new TreeSet<>();
    
    public StorableFeatureMap() {}

	public StorableFeatureMap(Map<Feature, Category> map) {
		for( Entry<Feature, Category> e : map.entrySet() )
            feature.add(new StorableFeature(e));
	}
}
