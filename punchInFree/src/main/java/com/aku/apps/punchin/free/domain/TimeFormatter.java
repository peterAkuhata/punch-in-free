package com.aku.apps.punchin.free.domain;

import java.util.Date;

/**
 * Represents the time format that gets displayed in each task on the ui.
 * 
 * @author Peter Akuhata
 *
 */
public interface TimeFormatter {
	
	/**
	 * Formats the specified fields and returns a string representation. 
	 * @param hours
	 * @param minutes
	 * @param seconds
	 * @return
	 */
	public abstract String formatTime(long hours, long minutes, long seconds);

	/**
	 * Calculates the length of time between the start and end dates and 
	 * returns a string representation for that time. 
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public abstract String formatTime(Date startDate, Date endDate);
	
	/** 
	 * Formats the specified milliseconds and returns a string representation. 
	 * @param time
	 * @return
	 */
	public abstract String formatTime(long time);

	/** 
	 * Returns a description of the time format. 
	 * @return
	 */
	public abstract int getDescription();
	
	/** 
	 * Returns a unique identifier for a specific time format. 
	 * @return
	 */
	public abstract int getId();
}
