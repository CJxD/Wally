package com.cjwatts.wally.persistence;

import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeSet;

public class StorableWeightingsMap {
    public SortedSet<StorableWeighting> weighting = new TreeSet<>();
    
    public StorableWeightingsMap() {}
    
    public StorableWeightingsMap(Map<Integer, Double> map) {
        for( Entry<Integer, Double> e : map.entrySet() )
            weighting.add(new StorableWeighting(e));
    } 
}
