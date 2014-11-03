package com.cjwatts.wally.persistence;

import java.util.Map.Entry;

import javax.xml.bind.annotation.XmlAttribute;

public class StorableWeighting {
    @XmlAttribute
    public String name;

    @XmlAttribute
    public Float value;

    public StorableWeighting() {}
    
    public StorableWeighting(Entry<String, Float> e) {
       name = e.getKey();
       value = e.getValue();
    }
}
