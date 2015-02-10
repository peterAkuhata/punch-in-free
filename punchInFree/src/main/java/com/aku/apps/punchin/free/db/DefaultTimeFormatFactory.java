package com.aku.apps.punchin.free.db;

import java.util.ArrayList;

import com.aku.apps.punchin.free.domain.TimeFormatterByMinutes;
import com.aku.apps.punchin.free.domain.TimeFormatterBySeconds;
import com.aku.apps.punchin.free.domain.Preferences;
import com.aku.apps.punchin.free.domain.TimeFormatter;

/**
 * Represents a {@link TimeFormat} datasource.
 * @author Peter Akuhata
 *
 */
public class DefaultTimeFormatFactory implements TimeFormatterFactory {
	/**
	 * The list of cached time formats.
	 */
	private static ArrayList<TimeFormatter> cache = null;
	
	static {
		// add all the time formats here.
		
		cache = new ArrayList<TimeFormatter>();
		cache.add(new TimeFormatterBySeconds());
		cache.add(new TimeFormatterByMinutes());
	}
	
	@Override
	public TimeFormatter get(long formatId) {
		TimeFormatter format = null;
		
		for (TimeFormatter i : cache) {
			if (i.getId() == formatId) {
				format = i;
				break;
			}
		}
		
		return format;
	}

	@Override
	public TimeFormatter get(Preferences prefs) {
		return get(prefs.getDefaultTimeFormatId());
	}

	@Override
	public void clearCache() {
		// no need to do anything
	}

	@Override
	public ArrayList<TimeFormatter> getList() {
		return cache;
	}
}
