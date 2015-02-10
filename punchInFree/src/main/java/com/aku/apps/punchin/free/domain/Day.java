package com.aku.apps.punchin.free.domain;

import java.util.Calendar;
import java.util.Date;

/**
 * Represents a day in the life of a person :).  The day object
 * keeps track of daily notes and expenses, as well as links in
 * any tasks that had any work done on it through it's timestamps.
 * 
 * @author Peter Akuhata
 *
 */
public class Day extends DomainObject {
	/**
	 * The date that this {@link Day} represents.
	 */
	private Date date;
	
	/**
	 * The daily notes.
	 */
	private String dailyNotes;
	
	/**
	 * Returns the date.
	 * @return
	 */
	public Date getDate() {
		return date;
	}
	
	/**
	 * Sets the date.
	 * @param date
	 */
	public void setDate(Date date) {
		this.date = date;
	}
	
	/**
	 * Returns the daily notes.
	 * @return
	 */
	public String getDailyNotes() {
		return dailyNotes;
	}
	
	/**
	 * Sets the daily notes.
	 * @param notes
	 */
	public void setDailyNotes(String notes) {
		this.dailyNotes = notes;
	}
	
	/**
	 * Creates a {@link Day}.
	 * @param id
	 * @param date
	 * @param notes
	 */
	public Day(long id, Date date, String notes, Date created, Date modified) {
		super(id, created, modified);
		this.date = date;
		this.dailyNotes = notes;
	}
	
	/**
	 * Creates a {@link Day}.
	 * @param id
	 * @param date
	 * @param notes
	 */
	public Day(long id, long date, String notes, long created, long modified) {
		super(id);
		
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(date);
		this.date = cal.getTime();
		cal.setTimeInMillis(created);
		this.setCreated(cal.getTime());
		cal.setTimeInMillis(modified);
		this.setModified(cal.getTime());		
		this.dailyNotes = notes;
	}
	
	/**
	 * Creates a {@link Day}.
	 * @param id
	 * @param date
	 * @param notes
	 */
	public Day(long id, Date date, String notes) {
		this(id, date, notes, new Date(), new Date());
	}
	
	/**
	 * Creates a {@link Day}.
	 * @param id
	 * @param date
	 */
	public Day(long id, Date date) {
		this(id, date, null, new Date(), new Date());
	}	
}
