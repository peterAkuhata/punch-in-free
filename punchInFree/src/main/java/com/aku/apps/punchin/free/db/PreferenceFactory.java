package com.aku.apps.punchin.free.db;

import com.aku.apps.punchin.free.domain.Preferences;

/**
 * Represents a {@link Preferences} datasource.
 * 
 * @author Peter Akuhata
 *
 */
public interface PreferenceFactory extends BaseFactory {
	/**
	 * Returns the preferences.
	 * @return
	 */
	public abstract Preferences get();

	/**
	 * Updates the preferences back to the database.
	 * 
	 * @param prefs
	 */
	public abstract void update(Preferences prefs);
}
