package com.cjwatts.wally.analysis;

public class Measurement {
	private final String name;
	private float value;
	
	public Measurement(String name, float value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public float getValue() {
		return value;
	}

	public void setValue(float value) {
		this.value = value;
	}
}
