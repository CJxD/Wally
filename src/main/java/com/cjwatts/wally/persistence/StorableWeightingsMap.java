package com.cjwatts.wally.persistence;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

public class StorableWeightingsMap {
    public HashSet<StorableWeighting> weighting = new HashSet<>();
    
    public StorableWeightingsMap(Map<String, Float> map) {
        for( Entry<String, Float> e : map.entrySet() )
            weighting.add(new StorableWeighting(e));
    }
    
    public StorableWeightingsMap() {}
}
