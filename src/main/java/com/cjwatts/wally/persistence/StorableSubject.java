package com.cjwatts.wally.persistence;

import java.io.IOException;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import com.cjwatts.wally.analysis.Category;
import com.cjwatts.wally.analysis.Subject;
import com.cjwatts.wally.analysis.feature.Feature;

@XmlRootElement(name = "subject")
public class StorableSubject implements Storable<Subject> {
	@XmlAttribute
	public String id;
	public StorableFeatureMap features;
	
	public StorableSubject() {}
	
	@Override
	public void store(Subject s) {
		id = s.getID();
		features = new StorableFeatureMap(s.getFeatures());
	}
	
	@Override
	public void restoreTo(Subject s) {
		s.setID(id, false);
		
		for (StorableFeature f : features.feature) {
			Feature feature;
			try {
				feature = Feature.forName(f.name);
				s.setCategory(feature, Category.values()[f.category], false);
			} catch (IllegalArgumentException | IOException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}
			
		}
	}
	
}