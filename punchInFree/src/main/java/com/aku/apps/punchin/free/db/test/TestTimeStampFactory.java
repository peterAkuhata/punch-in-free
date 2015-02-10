package com.aku.apps.punchin.free.db.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;

import android.util.Log;

import com.aku.apps.punchin.free.db.DatasourceFactory;
import com.aku.apps.punchin.free.db.DayFactory;
import com.aku.apps.punchin.free.db.TaskFactory;
import com.aku.apps.punchin.free.db.TimeStampFactory;
import com.aku.apps.punchin.free.domain.Day;
import com.aku.apps.punchin.free.domain.Task;
import com.aku.apps.punchin.free.domain.TaskDay;
import com.aku.apps.punchin.free.domain.TimeStamp;
import com.aku.apps.punchin.free.domain.TimeStamp.TimeStampType;
import com.aku.apps.punchin.free.utils.DateUtil;

public class TestTimeStampFactory implements TimeStampFactory {
	private static Hashtable<Date, ArrayList<TimeStamp>> timeStamps = 
		new Hashtable<Date, ArrayList<TimeStamp>>();
	
	private DatasourceFactory datasourceFactory;
	
	private static TimeStamp activeTimeStamp;
	private static TimeStamp lastTimeStamp = null;
	
	
	public TestTimeStampFactory(DatasourceFactory datasourceFactory) {
		super();
		
		this.datasourceFactory = datasourceFactory;
	}

	static {
		Date date = DateUtil.getYesterday();
		date = DateUtil.addHours(date, 13);
		date = DateUtil.addMinutes(date, 25);
		
		activeTimeStamp = new TimeStamp(IDGenerator.generate(), 
				TimeStampType.PunchIn, 
				TestTaskDayFactory.WALMART_YESTERDAY, 
				date);
		
		lastTimeStamp = activeTimeStamp;
		ArrayList<TimeStamp> ts = new ArrayList<TimeStamp>();
		ts.add(activeTimeStamp);
		
		timeStamps.put(DateUtil.getYesterday(), ts);
	}
	
	private ArrayList<TimeStamp> getTimeStamps(Date date) {
		Date temp = DateUtil.removeTimeFromDate(date);
		ArrayList<TimeStamp> stamps = null;
		
		if (timeStamps.containsKey(temp)) {
			stamps = timeStamps.get(temp);
			
		} else {
			stamps = new ArrayList<TimeStamp>();
			timeStamps.put(temp, stamps);
			
		}
		
		return stamps;
	}
	
	@Override
	public ArrayList<TimeStamp> getListByDate(Date date) {
		return getTimeStamps(date);
	}

	@Override
	public ArrayList<TimeStamp> getListBetween(Date startDate,
			Date endDate) {

		Date start = DateUtil.removeTimeFromDate(startDate);
		Date end = DateUtil.removeTimeFromDate(endDate);
		ArrayList<TimeStamp> stamps = null;
		
		if (start.compareTo(end) <= 0) {
			if (start.equals(end)) {
				stamps = getTimeStamps(start);
			} else {
				Date current = start;
				stamps = new ArrayList<TimeStamp>();
				
				while (!current.after(end)) {
					ArrayList<TimeStamp> temp = getTimeStamps(current);
					
					if (temp.size() > 0)
						stamps.addAll(temp);
					
					current = DateUtil.addDays(current, 1);
				}
			}
		}
		
		return stamps;
	}

	@Override
	public TimeStamp add(Task task, Date startDate, TimeStamp.TimeStampType type) {
		TimeStamp timeStamp = null;
		
		if (startDate != null && task != null) {
			Day day = this.datasourceFactory.createDayFactory().get(startDate, true);
			TaskDay taskDay = this.datasourceFactory.createTaskDayFactory().get(task, day, true);
			timeStamp = new TimeStamp(IDGenerator.generate(), type, taskDay.getId(), startDate);
			ArrayList<TimeStamp> stamps = getTimeStamps(startDate);
		
			stamps.add(timeStamp);
			lastTimeStamp = timeStamp;
			
			if (timeStamp.getType() == TimeStampType.PunchIn)
				activeTimeStamp = timeStamp;
			else
				activeTimeStamp = null;
		}
		
		return timeStamp;
	}

	@Override
	public TimeStamp getActive() {
		return activeTimeStamp;
	}
	
	@Override
	public TimeStamp checkActive() {
		TimeStamp timestamp = null;
		
		if (activeTimeStamp != null) {
			TaskFactory taskFactory = this.datasourceFactory.createTaskFactory();
			TaskDay taskDay = this.datasourceFactory.createTaskDayFactory().get(activeTimeStamp.getTaskDayId());
			
			Task task = taskFactory.get(taskDay.getTaskId());
			DayFactory dayFactory = this.datasourceFactory.createDayFactory();
			Day day = dayFactory.get(taskDay.getDayId());
			Date startDate = day.getDate();
			
			if (!startDate.equals(DateUtil.getToday())) {
				Date date = DateUtil.setEndOfDay(startDate);				
				timestamp = this.add(task, date, TimeStampType.PunchOut);
				lastTimeStamp = timestamp;
			}
		}
		
		return timestamp;
	}

	@Override
	public long getTimeSpentOnTask(Task task, Date date) {
		Log.d(TestTimeStampFactory.class.getSimpleName(), "getTimeSpentOnTask(date='" + date + "', task='" + task.getDescription() +"')");
		
		ArrayList<TimeStamp> list = getTimeStamps(date);
		Date punchIn = null;
		Date punchOut = null;
		long timeOnTask = 0;
		TaskFactory tf = this.datasourceFactory.createTaskFactory();
		
		for (TimeStamp item : list) {
			TaskDay td = this.datasourceFactory.createTaskDayFactory().get(item.getTaskDayId());
			Task t = tf.get(td.getTaskId());
			
			if (t.equals(task)) {
				switch (item.getType()) {
				case PunchIn:
					punchIn = item.getTime();
					break;
					
				case PunchOut:
					punchOut = item.getTime();
					break;

				}
				
				if (punchIn != null && punchOut != null) {
					timeOnTask += DateUtil.getMilliseconds(punchIn, punchOut);
					punchIn = null;
					punchOut = null;
				}
			}
		}
		
		Log.v(TestTimeStampFactory.class.getSimpleName(), "getTimeSpentOnTask='" + timeOnTask + " milliseconds'");

		return timeOnTask;
	}

	@Override
	public void clearTimeSpentOnTask(Task task, Date date) {
		ArrayList<TimeStamp> list = getTimeStamps(date);
		ArrayList<TimeStamp> removeList = new ArrayList<TimeStamp>();
		TaskFactory tf = this.datasourceFactory.createTaskFactory();

		for (TimeStamp item : list) {
			TaskDay taskDay = this.datasourceFactory.createTaskDayFactory().get(item.getTaskDayId());
			Task t = tf.get(taskDay.getTaskId());
			
			if (t.equals(task))
				removeList.add(item);
		}
		
		list.removeAll(removeList);
	}

	@Override
	public void clearCache() {
		// no need to do anything
	}

	@Override
	public TimeStamp getLastTimeStamp(Task task, Date date) {
		Log.d(TestTimeStampFactory.class.getSimpleName(), "getLastTimeStamp(date='" + date + "', task='" + task.getDescription() +"')");
		
		ArrayList<TimeStamp> list = getTimeStamps(date);
		TimeStamp ts = null;
		TaskFactory tf = this.datasourceFactory.createTaskFactory();

		
		for (TimeStamp item : list) {
			TaskDay taskDay = this.datasourceFactory.createTaskDayFactory().get(item.getTaskDayId());
			Task t = tf.get(taskDay.getTaskId());
			
			if (t.equals(task)) 
				ts = item;
		}
		
		return ts;
	}

	@Override
	public TimeStamp get(long id) {
		TimeStamp ts = null;
		
		Enumeration<Date> dates = timeStamps.keys();
		
		while (dates.hasMoreElements()) {
			ArrayList<TimeStamp> items = timeStamps.get(dates.nextElement());
			
			for (TimeStamp item : items) {
				if (item.getId() == id) {
					ts = item;
					break;
				}
			}
			
			if (ts != null)
				break;
		}
		
		return ts;
	}

	@Override
	public void update(TimeStamp ts) {
		// no need to do anything, the object is updated in memory
		ts.setModified(new Date());
	}

	@Override
	public Task getTask(TimeStamp ts) {
		TaskDay td = getTaskDay(ts);
		Task t = this.datasourceFactory.createTaskFactory().get(td.getTaskId());
		
		return t;
	}

	@Override
	public Day getDay(TimeStamp ts) {
		TaskDay td = getTaskDay(ts);
		Day d = this.datasourceFactory.createDayFactory().get(td.getDayId());
		
		return d;
	}

	@Override
	public TaskDay getTaskDay(TimeStamp ts) {
		TaskDay td = this.datasourceFactory.createTaskDayFactory().get(ts.getTaskDayId());

		return td;
	}

	@Override
	public TimeStamp getLastTimeStamp() {
		return lastTimeStamp;
	}
}
