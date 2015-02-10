package com.aku.apps.punchin.free.db;

import java.util.ArrayList;

import com.aku.apps.punchin.free.domain.Preferences;
import com.aku.apps.punchin.free.domain.TimeFormatter;

/**
 * Represents a {@link TimeFormatter} datasource.
 * @author Peter Akuhata
 *
 */
public interface TimeFormatterFactory extends BaseFactory {
	/**
	 * Returns the {@link TimeFormatter} associated with the id.
	 * @param id
	 * @return
	 */
	public abstract TimeFormatter get(long id);
	
	/**
	 * Returns the default {@link TimeFormatter} as specified in preferences.
	 * @param prefs
	 * @return
	 */
	public abstract TimeFormatter get(Preferences prefs);
	
	/**
	 * Returns the full list of {@link TimeFormatter} objects.
	 * @return
	 */
	public abstract ArrayList<TimeFormatter> getList();
}
