package com.aku.apps.punchin.free.domain;

import java.util.Calendar;
import java.util.Date;

/**
 * Represents either a punch in or a punch out transaction in the system.
 * @author Peter Akuhata
 *
 */
public class TimeStamp extends DomainObject {
	/**
	 * Represents the time stamp type, either punch in or out.
	 * @author Peter Akuhata
	 *
	 */
	public enum TimeStampType {
		PunchIn,
		PunchOut
	}
		
	/**
	 * The time that this timestamp occurred.
	 */
	private Date time;
	
	/**
	 * The type of timestamp.
	 */
	private TimeStampType type;
	
	/**
	 * The task day that this timestamp occurred.
	 */
	private long taskDayId;
	
	/**
	 * Returns the task day id.
	 * @return
	 */
	public long getTaskDayId() {
		return taskDayId;
	}

	/**
	 * Sets the task day id.
	 * @param taskDayId
	 */
	public void setTaskDayId(long taskDayId) {
		this.taskDayId = taskDayId;
	}

	/**
	 * Returns the timestamp type.
	 * @return
	 */
	public TimeStampType getType() {
		return type;
	}
	
	/**
	 * Sets the timestamp type.
	 * @param type
	 */
	public void setType(TimeStampType type) {
		this.type = type;
	}
	
	/**
	 * Returns the time that this timestamp occurred.
	 * @return
	 */
	public Date getTime() {
		return time;
	}
	
	/**
	 * Sets the time that this timestamp occurred.
	 * @param time
	 */
	public void setTime(Date time) {
		this.time = time;
	}
	
	/**
	 * Creates a {@link TimeStamp}.
	 * @param id
	 * @param type
	 * @param taskDayId
	 * @param time
	 */
	public TimeStamp(long id, TimeStampType type, long taskDayId, Date time, Date created, Date modified) {
		super(id, created, modified);
		this.type = type;
		this.taskDayId = taskDayId;
		this.time = time;
	}
	
	/**
	 * Creates a {@link TimeStamp}.
	 * @param id
	 * @param type
	 * @param dayId
	 * @param time
	 * @param taskId
	 */
	public TimeStamp(long id, int type, long taskDayId, long time, long created, long modified) {
		super(id);
		
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(created);
		this.setCreated(cal.getTime());
		cal.setTimeInMillis(modified);
		this.setModified(cal.getTime());
		cal.setTimeInMillis(time);
		this.time = cal.getTime();
		this.type = TimeStampType.values()[type];
		this.taskDayId = taskDayId;
	}
	
	/**
	 * Creates a {@link TimeStamp}.
	 * @param id
	 * @param type
	 * @param dayId
	 * @param time
	 * @param taskId
	 */
	public TimeStamp(long id, TimeStampType type, long taskDayId, Date time) {
		this(id, type, taskDayId, time, new Date(), new Date());
	}
}
