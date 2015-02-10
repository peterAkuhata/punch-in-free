package com.aku.apps.punchin.free.db;

/**
 * Base factory interface for all factories.
 * 
 * @author Peter Akuhata
 *
 */
public interface BaseFactory {
	/**
	 * Clears all cached, unneeded data off the application.  This is used when
	 * the device requests the app to shed a few pounds.
	 */
	void clearCache();
}
