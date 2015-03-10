package com.cjwatts.wally.persistence;

public interface PersistenceHandler<I> {
	
	/**
	 * Load an instance from persistent storage
	 * @return true if successful, false if load failed
	 */
	public boolean load();

	/**
	 * Save instance to persistent storage
	 * @return true if successful, false if save failed
	 */
	public boolean save();
}
