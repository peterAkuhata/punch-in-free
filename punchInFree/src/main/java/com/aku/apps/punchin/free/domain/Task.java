package com.aku.apps.punchin.free.domain;

import java.util.Calendar;
import java.util.Date;

import com.aku.apps.punchin.free.utils.TaskUtil;

/**
 * Represents a unit of work in the system.
 * @author Peter Akuhata
 *
 */
public class Task extends DomainObject implements Activatable {
	
	/**
	 * Represents the types of repeating options available to the user.
	 * @author Peter Akuhata
	 *
	 */
	public enum RepeatingType {
		DoesNotRepeat,
		EveryDay,
		WeekDays,
		Weekends,
		Weekly,
		Fortnightly,
		MonthlyOnDayX,
		Yearly
	}
	
	/**
	 * Allows tasks to be sorted.
	 */
	private int sort;
	
	/**
	 * Task description.
	 */
	private String description;
	
	/**
	 * The start date of the task.
	 */
	private Date startDate;
	
	/**
	 * The end date of the task.
	 */
	private Date endDate;
	
	/**
	 * The task repeating type.
	 */
	private RepeatingType repeatingType;
	
	/**
	 * Flag to say whether the client is active or not.
	 */
	private boolean isActive;
	
	/**
	 * The client that this task is working for.
	 */
	private long clientId;
	 
	/**
	 * Returns the task end date.
	 * @return
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * Sets the task end date.
	 * @param endDate
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	/**
	 * Returns the client id.
	 * @return
	 */
	public long getClientId() {
		return clientId;
	}
	
	/**
	 * Sets the client id.
	 * @param id
	 */
	public void setClientId(long id) {
		clientId = id;
	}
	
	/**
	 * Returns the active flag.
	 */
	public boolean getActive() {
		return isActive;
	}	
	
	/**
	 * Sets the active flag.
	 * @param active
	 */
	public void setActive(boolean active) {
		isActive = active;
	}
	
	/**
	 * Returns the start date of this task.
	 * @return
	 */
	public Date getStartDate() {
		return startDate;
	}
	
	/**
	 * Sets the start date.
	 * @param startDate
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	/**
	 * Returns the task repeating type.
	 * @return
	 */
	public RepeatingType getRepeatingType() {
		return repeatingType;
	}
	
	/**
	 * Sets the task repeating type.
	 * @param mRepeating
	 */
	public void setRepeatingType(RepeatingType mRepeating) {
		this.repeatingType = mRepeating;
	}
	
	/**
	 * Returns the task description.
	 * @return
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Sets the task description.
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * Returns the sort value.
	 * @return
	 */
	public int getSort() {
		return sort;
	}
	
	/**
	 * Sets the sort value.
	 * @param sort
	 */
	public void setSort(int sort) {
		this.sort = sort;
	}

	/**
	 * Creates a {@link Task} object.
	 * @param id
	 * @param description
	 * @param startDate
	 * @param repeating
	 * @param sort
	 * @param active
	 * @param androidCalendarId
	 */
	public Task(long id, String description, long startDate, long endDate, int repeating, int sort, int active,
			long created, long modified, int clientId) {
		super(id);
		
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(startDate);
		this.setStartDate(cal.getTime());
		
		if (endDate > 0) {
			cal.setTimeInMillis(endDate);
			this.setEndDate(cal.getTime());
			
		} else {
			this.setEndDate(null);
			
		}
		
		cal.setTimeInMillis(created);
		this.setCreated(cal.getTime());
		
		cal.setTimeInMillis(modified);
		this.setModified(cal.getTime());
		
		this.description = description;
		this.sort = sort;
		this.repeatingType = TaskUtil.toRepeatingType(repeating);
		this.isActive = (active == 0 ? false : true);
		this.clientId = clientId;
	}

	/**
	 * Creates a {@link Task} object.
	 * @param id
	 * @param description
	 * @param startDate
	 * @param repeating
	 * @param sort
	 * @param active
	 * @param androidCalendarId
	 */
	public Task(long id, String description, Date startDate, Date endDate, RepeatingType repeating, int sort, boolean active,
			Date created, Date modified, long clientId) {
		super(id, created, modified);
		
		this.description = description;
		this.startDate = startDate;
		this.endDate = endDate;
		this.sort = sort;
		this.repeatingType = repeating;
		this.isActive = active;
		this.clientId = clientId;
	}

	/**
	 * Creates a {@link Task} object.
	 * @param id
	 * @param description
	 * @param startDate
	 * @param repeating
	 * @param sort
	 * @param active
	 * @param androidCalendarId
	 */
	public Task(long id, String description, Date startDate, Date endDate, RepeatingType repeating, int sort, boolean active) {
		this(id, description, startDate, endDate, repeating, sort, true, new Date(), new Date(), -1);
	}
	
	/**
	 * Creates a {@link Task} object.
	 * @param id
	 * @param description
	 * @param startDate
	 * @param repeating
	 * @param sort
	 */
	public Task(long id, String description, Date startDate, Date endDate, RepeatingType repeating, int sort, long clientId) {
		this(id, description, startDate, endDate, repeating, sort, true);
		this.setClientId(clientId);
	}
	
	/**
	 * Creates a {@link Task} object.
	 * @param id
	 * @param description
	 * @param startDate
	 * @param repeating
	 * @param sort
	 */
	public Task(long id, String description, Date startDate, Date endDate, RepeatingType repeating, int sort) {
		this(id, description, startDate, endDate, repeating, sort, true);
	}
	
	/**
	 * Creates a {@link Task} object.
	 * @param id
	 * @param description
	 * @param startDate
	 * @param repeating
	 */
	public Task(long id, String description, Date startDate, Date endDate, RepeatingType repeating) {
		this(id, description, startDate, endDate, repeating, 0);
	}
	
	/**
	 * Creates a {@link Task} object.
	 * @param id
	 * @param description
	 */
	public Task(long id, String description) {
		this(id, description, new Date(), null, RepeatingType.DoesNotRepeat);
	}
	
	/**
	 * Creates a {@link Task} object.
	 */
	public Task() {
		this(0, "");
	}
	
	@Override
	public boolean equals(Object o) {
		return (o != null && this.getId() == ((Task)o).getId());
	}
}
