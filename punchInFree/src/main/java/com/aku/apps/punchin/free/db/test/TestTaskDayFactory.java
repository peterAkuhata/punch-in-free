package com.aku.apps.punchin.free.db.test;

import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;

import com.aku.apps.punchin.free.db.TaskDayFactory;
import com.aku.apps.punchin.free.domain.Day;
import com.aku.apps.punchin.free.domain.Task;
import com.aku.apps.punchin.free.domain.TaskDay;

public class TestTaskDayFactory implements TaskDayFactory {
	private static Hashtable<Long, TaskDay> taskDays = new Hashtable<Long, TaskDay>();	
	public static final long WALMART_YESTERDAY = IDGenerator.generate();
	
	static {
		taskDays.put(WALMART_YESTERDAY, new TaskDay(WALMART_YESTERDAY, "", TestTaskFactory.WALMART_ID, TestDayFactory.YESTERDAY_ID));
	}
	
	@Override
	public void clearCache() {
		// nothing to clear
	}

	@Override
	public TaskDay get(long taskDayId) {
		TaskDay item = null;
		
		if (taskDays.containsKey(taskDayId))
			item = taskDays.get(taskDayId);
		
		return item;
	}

	@Override
	public TaskDay get(Task task, Day day, boolean addIfNotFound) {
		TaskDay item = null;
		
		Enumeration<TaskDay> items = taskDays.elements();
		
		while (items.hasMoreElements()) {
			TaskDay i = items.nextElement();
			
			if (i.getTaskId() == task.getId() && i.getDayId() == day.getId()) {
				item = i;
				break;
			}
		}
		
		if (item == null && addIfNotFound)
			item = add(task, day, "");
		
		return item;
	}

	@Override
	public TaskDay add(Task task, Day day, String notes) {
		TaskDay td = new TaskDay(IDGenerator.generate(), notes, task.getId(), day.getId());
		taskDays.put(td.getId(), td);
		
		return td;
	}

	@Override
	public void update(TaskDay taskDay) {
		// nothing to do here, data just
		taskDay.setModified(new Date());
	}
}
