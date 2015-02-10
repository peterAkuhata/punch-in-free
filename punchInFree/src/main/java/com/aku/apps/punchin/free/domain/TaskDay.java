package com.aku.apps.punchin.free.domain;

import java.util.Calendar;
import java.util.Date;

/**
 * Represents a specific day in which a task belongs.
 * It is used to log expenses for a task on a particular day, as well as
 * adding events to the calendar.
 * 
 * @author Peter Akuhata
 *
 */
public class TaskDay extends DomainObject {
	
	/**
	 * The daily task notes.
	 */
	private String notes;
	
	/**
	 * The task unique id.
	 */
	private long taskId;

	/**
	 * The day unique id.
	 */
	private long dayId;

	/**
	 * Returns the day id.
	 * @return
	 */
	public long getDayId() {
		return dayId;
	}

	/**
	 * Sets the day id.
	 * @param dayId
	 */
	public void setDayId(long dayId) {
		this.dayId = dayId;
	}

	/**
	 * Returns the notes.
	 * @return
	 */
	public String getNotes() {
		return notes;
	}
	
	/**
	 * Sets the notes.
	 * @param notes
	 */
	public void setNotes(String notes) {
		this.notes = notes;
	}
	
	/**
	 * Returns the task id.
	 * @return
	 */
	public long getTaskId() {
		return taskId;
	}
	
	/**
	 * Sets the task id.
	 * @param taskId
	 */
	public void setTaskId(long taskId) {
		this.taskId = taskId;
	}
	
	/**
	 * Creates a {@link TaskDay}.
	 * @param id
	 * @param notes
	 * @param taskId
	 * @param dayId
	 */
	public TaskDay(long id, String notes, long taskId, long dayId, Date created, Date modified) {
		super(id, created, modified);

		this.notes = notes;
		this.taskId = taskId;
		this.dayId = dayId;
	}
	
	/**
	 * Creates a {@link TaskDay}.
	 * @param id
	 * @param notes
	 * @param taskId
	 * @param dayId
	 */
	public TaskDay(long id, String notes, long taskId, long dayId, long created, long modified) {
		super(id);

		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(created);
		this.setCreated(cal.getTime());
		cal.setTimeInMillis(modified);
		this.setModified(cal.getTime());
		
		this.notes = notes;
		this.taskId = taskId;
		this.dayId = dayId;
	}
	
	/**
	 * Creates a {@link TaskDay}.
	 * @param id
	 * @param notes
	 * @param taskId
	 * @param dayId
	 */
	public TaskDay(long id, String notes, long taskId, long dayId) {
		this(id, notes, taskId, dayId, new Date(), new Date());
	}
}
