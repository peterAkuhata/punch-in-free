package com.aku.apps.punchin.free.db;

import java.util.ArrayList;
import com.aku.apps.punchin.free.domain.CalendarEvent;
import com.aku.apps.punchin.free.domain.TaskDay;
import com.aku.apps.punchin.free.domain.TimeStamp;

/**
 * Responsible for reading and writing calendar events to and from the datasource.
 * @author Peter Akuhata
 *
 */
public interface CalendarEventFactory extends BaseFactory {
	/**
	 * Returns the list of calendar events for a given {@link TaskDay}.
	 * @param taskDay
	 * @return
	 */
	public ArrayList<CalendarEvent> getList(TaskDay taskDay);
	
	
	/**
	 * Updates the specified calendar event.
	 * @param event
	 */
	public void update(CalendarEvent event);
	
	/**
	 * Clears the list of events.
	 * @param taskDay
	 */
	public void clear(TaskDay taskDay);

	/**
	 * Finds a {@link CalendarEvent} with the specified punchIn and punchOut timestamps.
	 * @param punchIn
	 * @param punchOut
	 * @return
	 */
	public CalendarEvent get(TimeStamp punchIn, TimeStamp punchOut);

	/**
	 * Creates a new calendar event and returns it.
	 * @param taskDayId
	 * @param startTime
	 * @param endTime
	 * @param allDay
	 * @param status
	 * @param visibility
	 * @param transparency
	 * @param hasAlarm
	 * @param androidEventId
	 * @return
	 */
	public CalendarEvent add(TaskDay taskDay, long startTime,
			long endTime, boolean allDay, int status, int visibility,
			int transparency, int hasAlarm, String androidEventId);

	/**
	 * Creates a new calendar event and returns it.
	 * @param taskDay
	 * @param startTime
	 * @param endTime
	 * @param allDay
	 * @param status
	 * @param visibility
	 * @param transparency
	 * @param hasAlarm
	 * @param androidEventId
	 * @param punchInId
	 * @param punchOutId
	 * @return
	 */
	public CalendarEvent add(TaskDay taskDay, long startTime,
			long endTime, boolean allDay, int status, int visibility,
			int transparency, int hasAlarm, String androidEventId, 
			TimeStamp punchIn, TimeStamp punchOut);


	/**
	 * Removes the specified list of events from the datasource.
	 * @param events
	 */
	public void remove(ArrayList<CalendarEvent> events);
}
