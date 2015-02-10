package com.aku.apps.punchin.free.db;

import java.util.ArrayList;
import java.util.Date;

import com.aku.apps.punchin.free.domain.Task;
import com.aku.apps.punchin.free.domain.Task.RepeatingType;

/**
 * Represents a {@link Task} datasource.
 * 
 * @author Peter Akuhata
 *
 */
public interface TaskFactory extends BaseFactory {
	/**
	 * Returns a task referenced by it's unique identifier.
	 * @param id
	 * @return
	 */
	public Task get(long id);
	
	/**
	 * Returns a list of tasks that are to be shown on the specified day.
	 * @param date
	 * @return
	 */
	public ArrayList<Task> getListByDate(Date date);
	
	/**
	 * Creates a new {@link Task}, adds it to the datasource and returns it.
	 * @param description
	 * @param startDate
	 * @param type
	 * @return
	 */
	public Task add(long clientId, String description, Date startDate, Date endDate, RepeatingType type);
	
	/**
	 * Updates the specified task to the datasource.
	 * @param task
	 */
	public void update(Task task);
	
	/**
	 * Returns the full list of tasks available in the system.
	 * @return
	 */
	public ArrayList<Task> getList(boolean activeOnly);
	
	/**
	 * Returns the full list of tasks available in the system.
	 * @return
	 */
	public ArrayList<Task> getList(boolean activeOnly, String filter);
	
	/**
	 * Re-sorts the specified task, placing it at the specified new position.
	 * @param task
	 * @param newPosition
	 */
	public void resort(Task task, int newPosition);
}
