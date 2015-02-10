package com.aku.apps.punchin.free.db.test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;

import com.aku.apps.punchin.free.db.TaskFactory;
import com.aku.apps.punchin.free.domain.Task;
import com.aku.apps.punchin.free.domain.Task.RepeatingType;
import com.aku.apps.punchin.free.utils.DateUtil;
import com.aku.apps.punchin.free.utils.TaskUtil;

public class TestTaskFactory implements TaskFactory {
	
	public static final ArrayList<Task> testTasks = new ArrayList<Task>();
	
	private static Hashtable<Date, ArrayList<Task>> datasource = new Hashtable<Date, ArrayList<Task>>();

	public static long WALMART_ID = IDGenerator.generate();
	
	static {
		// build test tasks
		Date date = null;
		ArrayList<Task> tasks = null;

		testTasks.add(new Task(IDGenerator.generate(), 
				"Gisborne City Council website updates", 
				DateUtil.getToday(), 
				null,
				RepeatingType.DoesNotRepeat, 1));
		
		testTasks.add(new Task(WALMART_ID, 
				"Go to Walmart and pay off account", 
				DateUtil.getYesterday(), 
				null, 
				RepeatingType.EveryDay, 2));
		
		testTasks.add(new Task(IDGenerator.generate(), 
				"Lunch", 
				DateUtil.getYesterday(), 
				null, 
				RepeatingType.EveryDay, 3));
		
		testTasks.add(new Task(IDGenerator.generate(), 
				"Finish off Acme Ltd website integration with SilverStrip cms", 
				DateUtil.getTomorrow(), 
				null, 
				RepeatingType.DoesNotRepeat, 4));
		
		date = DateUtil.getToday();
		tasks = new ArrayList<Task>();
		tasks.add(testTasks.get(0));
		tasks.add(testTasks.get(1));
		tasks.add(testTasks.get(2));
		datasource.put(date, tasks);
		
		date = DateUtil.getTomorrow();
		tasks = new ArrayList<Task>();
		tasks.add(testTasks.get(3));
		tasks.add(testTasks.get(2));
		datasource.put(date, tasks);
		
		date = DateUtil.getYesterday();
		tasks = new ArrayList<Task>();
		tasks.add(testTasks.get(0));
		tasks.add(testTasks.get(2));
		datasource.put(date, tasks);
	}
	
	@Override
	public Task get(long id) {
		Task item = null;
		
		for (Task t : testTasks) {
			if (t.getId() == id) {
				item = t;
				break;
			}
		}
		
		return item;
	}

	@Override
	public ArrayList<Task> getListByDate(Date d) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		ArrayList<Task> tasks = new ArrayList<Task>();

		for (Task task : testTasks) {
			if (TaskUtil.isVisible(task, d)) {
				tasks.add(task);
			}
		}
		
		return tasks;
	}

	@Override
	public Task add(long clientId, String description, Date startDate, Date endDate, RepeatingType type) {
		Date d = DateUtil.removeTimeFromDate(startDate);
		Date ed = DateUtil.removeTimeFromDate(endDate);
		
		Task t = new Task(IDGenerator.generate(), description, d, ed, type, testTasks.size(), clientId);
		testTasks.add(t);
		
		return t;
	}

	@Override
	public void update(Task task) {
		// no need to do anything in the test factory because the object is saved in memory.
		task.setModified(new Date());
	}

	@Override
	public ArrayList<Task> getList(boolean activeOnly) {
		ArrayList<Task> tasks = null;
		
		if (activeOnly) {
			tasks = new ArrayList<Task>();
			
			for (Task task : tasks) {
				if (task.getActive())
					tasks.add(task);
			}
		} else {
			tasks = testTasks;
		}
		
		return tasks;
	}

	@Override
	public ArrayList<Task> getList(boolean activeOnly, String filter) {
		ArrayList<Task> tasks = null;

		if (filter == null || filter.length() == 0) {
			tasks = getList(activeOnly);
			
		} else {
			tasks = new ArrayList<Task>();
			
			for (Task item : testTasks) {
				if (item.getDescription().toLowerCase().startsWith(filter.toLowerCase())) {
					if (item.getActive() || !activeOnly)
						tasks.add(item);
				}
			}
		}
		
		return tasks;
	}

	@Override
	public void resort(Task task, int newPosition) {		
		// just shift it in the array list
		testTasks.remove(task);
		testTasks.add(newPosition, task);

		int index = (task.getSort() > newPosition ? newPosition : task.getSort());
		
		for (int i = index; i < testTasks.size(); i++) {
			testTasks.get(i).setSort(i);
		}
	}

	@Override
	public void clearCache() {
		// no need to do anything
	}
}
