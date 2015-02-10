package com.aku.apps.punchin.free.db;

import java.util.Date;

import com.aku.apps.punchin.free.domain.Day;

/**
 * Represents a {@link Day} datasource.
 * 
 * @author Peter Akuhata
 *
 */
public interface DayFactory extends BaseFactory {
	/**
	 * Creates a new day in the datasource, returns it.
	 * @param date
	 * @param notes
	 * @return
	 */
	public abstract Day add(Date date, String notes);
	
	/**
	 * Returns a {@link Day} for the specific date.
	 * @param date
	 * @param addIfNotFound Adds a new {@link Day} object if none is found.
	 * @return
	 */
	public abstract Day get(Date date, boolean addIfNotFound);
	
	/**
	 * Updates the day notes to the datasource.
	 * @param day
	 * @param notes
	 */
	public abstract void updateNotes(Day day, String notes);
	
	/**
	 * Returns the {@link Day} object associated with the id.
	 * @param dayId
	 * @return
	 */
	public abstract Day get(long dayId);
}