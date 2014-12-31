package com.cjwatts.wally.persistence;

import java.util.Map.Entry;

import javax.xml.bind.annotation.XmlAttribute;

import com.cjwatts.wally.analysis.Category;
import com.cjwatts.wally.analysis.feature.Feature;

public class StorableFeature implements Comparable<StorableFeature> {
    @XmlAttribute
    public String name;
    public Integer category;

    public StorableFeature() {}
    
    public StorableFeature(Entry<Feature, Category> e) {
       name = e.getKey().toString();
       category = e.getValue().ordinal();
    }
    
    @Override
	public int compareTo(StorableFeature o) {
		return name.compareTo(o.name);
	}
}
