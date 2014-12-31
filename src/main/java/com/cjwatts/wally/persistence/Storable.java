package com.cjwatts.wally.persistence;

public interface Storable<T> {
	
	/**
	 * Store an instance of the object in the storable class
	 * @param instance
	 */
	public void store(T instance);
	
	/**
	 * Populate a new instance with stored data
	 * @param instance
	 */
	public void restoreTo(T instance);
	
}
