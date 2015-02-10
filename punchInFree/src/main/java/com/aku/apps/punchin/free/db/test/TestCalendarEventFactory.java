package com.aku.apps.punchin.free.db.test;

import java.util.ArrayList;
import java.util.Date;

import com.aku.apps.punchin.free.db.CalendarEventFactory;
import com.aku.apps.punchin.free.domain.CalendarEvent;
import com.aku.apps.punchin.free.domain.TaskDay;
import com.aku.apps.punchin.free.domain.TimeStamp;

public class TestCalendarEventFactory implements CalendarEventFactory {
	/**
	 * Cached list of calendar events.
	 */
	private static ArrayList<CalendarEvent> events = new ArrayList<CalendarEvent>();

	@Override
	public ArrayList<CalendarEvent> getList(TaskDay taskDay) {
		ArrayList<CalendarEvent> subset = new ArrayList<CalendarEvent>();
		
		for (CalendarEvent ce : events) {
			if (ce.getTaskDayId() == taskDay.getId())
				subset.add(ce);
		}
		
		return subset;
	}

	@Override
	public void update(CalendarEvent event) {
		event.setModified(new Date());
	}

	@Override
	public void clear(TaskDay taskDay) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void clearCache() {
		// never mind clearing.
	}

	@Override
	public CalendarEvent get(TimeStamp punchIn, TimeStamp punchOut) {
		CalendarEvent item = null;
		
		for (CalendarEvent ce : events) {
			if (ce.getPunchInId() == punchIn.getId() && ce.getPunchOutId() == punchOut.getId()) {
				item = ce;
				break;
			}
		}
		
		return item;
	}

	@Override
	public CalendarEvent add(TaskDay taskDay, long startTime,
			long endTime, boolean allDay, int status, int visibility,
			int transparency, int hasAlarm, String androidEventId) {
		
		return add(taskDay, startTime, endTime, allDay, status, visibility, transparency, hasAlarm, androidEventId, null, null);
	}
		

	@Override
	public CalendarEvent add(TaskDay taskDay, long startTime,
			long endTime, boolean allDay, int status, int visibility,
			int transparency, int hasAlarm, String androidEventId,
			TimeStamp punchIn, TimeStamp punchOut) {
		
		CalendarEvent c = new CalendarEvent(IDGenerator.generate(), 
				taskDay.getId(), 
				startTime, 
				endTime, 
				allDay, 
				status, 
				visibility, 
				transparency, 
				hasAlarm,
				androidEventId, 
				(null == punchIn ? -1 : punchIn.getId()), 
				(null == punchOut ? -1 : punchOut.getId()),
				new Date(),
				new Date());
		
		events.add(c);
		
		// TODO: add this new event to the android calendar.
		
		return c;
	}

	@Override
	public void remove(ArrayList<CalendarEvent> e) {
		events.removeAll(e);
	}
}
