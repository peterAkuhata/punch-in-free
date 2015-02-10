package com.aku.apps.punchin.free.db;

import java.util.ArrayList;
import java.util.Date;

import com.aku.apps.punchin.free.domain.Day;
import com.aku.apps.punchin.free.domain.Task;
import com.aku.apps.punchin.free.domain.TaskDay;
import com.aku.apps.punchin.free.domain.TimeStamp;

/**
 * Represents a {@link TimeStamp} datasource.
 * 
 * @author Peter Akuhata
 *
 */
public interface TimeStampFactory extends BaseFactory {
	/**
	 * Returns the list of timestamps for the specified day.
	 * @param date
	 * @return
	 */
	public abstract ArrayList<TimeStamp> getListByDate(Date date);
	
	/**
	 * Returns a list of (ordered) timestamps between the two specified dates.
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public abstract ArrayList<TimeStamp> getListBetween(Date startDate, Date endDate);
	
	/**
	 * Returns the task associated with the specified timestamp.
	 * @param ts
	 * @return
	 */
	public abstract Task getTask(TimeStamp ts);
	
	/**
	 * Returns the day associated with the specified timestamp.
	 * @param ts
	 * @return
	 */
	public abstract Day getDay(TimeStamp ts);
	
	/**
	 * Returns the task day associated with the specified timestamp.
	 * @param ts
	 * @return
	 */
	public abstract TaskDay getTaskDay(TimeStamp ts);
	
	/**
	 * Adds a new timestamp to the datasource and returns that timestamp.
	 * @param taskDay
	 * @param type
	 * @return
	 */
	public abstract TimeStamp add(Task task, Date startDate, TimeStamp.TimeStampType type);
	
	/**
	 * Returns the currently active timestamp stored in the datasource.
	 * Null is returned if there is no active timestamp.
	 * @return
	 */
	public abstract TimeStamp getActive();
	
	/**
	 * Calculates the amount of time (in milliseconds) that the user spent
	 * on the specified task, on the given day.
	 * @param task
	 * @param date
	 * @return
	 */
	public abstract long getTimeSpentOnTask(Task task, Date date);
	
	/**
	 * Removes any logged time for the specified task, on the given day.
	 * @param task
	 * @param date
	 */
	public abstract void clearTimeSpentOnTask(Task task, Date date);
	
	/**
	 * Returns the last timestamp logged for a specific task on the given day.
	 * @param task
	 * @param date
	 * @return
	 */
	public abstract TimeStamp getLastTimeStamp(Task task, Date date);
	
	/**
	 * Returns the timestamp associated with the specified unique identifier.
	 * @param id
	 * @return
	 */
	public abstract TimeStamp get(long id);
	
	/**
	 * Updates the specified timestamp back to the datasource.
	 * @param ts
	 */
	public abstract void update(TimeStamp ts);
	
	/**
	 * If the active timestamp is relevant for, say, yesterday, then it is checked in
	 * at 11:59:59 pm of that day.
	 * @return The punch out timestamp for the active, punch in timestamp.
	 */
	public abstract TimeStamp checkActive();

	/**
	 * Returns the last saved timestamp, if one exists.
	 * @return
	 */
	public abstract TimeStamp getLastTimeStamp();
}
