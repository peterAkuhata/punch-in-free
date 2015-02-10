package com.aku.apps.punchin.free.db;

import com.aku.apps.punchin.free.domain.Day;
import com.aku.apps.punchin.free.domain.Task;
import com.aku.apps.punchin.free.domain.TaskDay;

/**
 * Responsible for managing {@link TaskDay} access to a datasource.
 * @author Peter Akuhata
 *
 */
public interface TaskDayFactory extends BaseFactory {
	/**
	 * Returns a {@link TaskDay} given it's unique id.
	 * @param taskDayId
	 * @return
	 */
	TaskDay get(long taskDayId);
	
	/**
	 * Returns a {@link TaskDay} given a combination of a task and a date.
	 * @param taskId
	 * @param date
	 * @param addIfNotFound
	 * @return
	 */
	TaskDay get(Task task, Day day, boolean addIfNotFound);
	
	/**
	 * Creates a new {@link TaskDay}.
	 * @param task
	 * @param day
	 * @param notes
	 * @return The newly created {@link TaskDay}.
	 */
	TaskDay add(Task task, Day day, String notes);
	
	/**
	 * Updates the {@link TaskDay} data back to the datasource.
	 * @param taskDay
	 */
	void update(TaskDay taskDay);
}
