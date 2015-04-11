package com.cjwatts.wally.persistence;

import javax.xml.bind.annotation.XmlAttribute;

public class StorableAlgorithm {
	@XmlAttribute
	public String name;
	public String data;
	
	public StorableAlgorithm() {}
}
