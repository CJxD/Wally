package com.cjwatts.wally.persistence;

import java.util.Map.Entry;

import javax.xml.bind.annotation.XmlAttribute;

public class StorableWeighting implements Comparable<StorableWeighting> {
    @XmlAttribute
    public Integer component;
    public Double weight;

    public StorableWeighting() {}
    
    public StorableWeighting(Entry<Integer, Double> e) {
       component = e.getKey();
       weight = e.getValue();
    }

	@Override
	public int compareTo(StorableWeighting o) {
		return component.compareTo(o.component);
	}
}
