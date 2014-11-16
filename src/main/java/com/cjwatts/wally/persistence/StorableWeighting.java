package com.cjwatts.wally.persistence;

import java.util.Map.Entry;

import javax.xml.bind.annotation.XmlAttribute;

public class StorableWeighting {
    @XmlAttribute
    public Integer name;

    @XmlAttribute
    public Float value;

    public StorableWeighting() {}
    
    public StorableWeighting(Entry<Integer, Float> e) {
       name = e.getKey();
       value = e.getValue();
    }
}
