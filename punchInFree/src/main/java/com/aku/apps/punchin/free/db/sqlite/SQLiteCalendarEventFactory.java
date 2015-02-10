package com.aku.apps.punchin.free.db.sqlite;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.aku.apps.punchin.free.db.CalendarEventFactory;
import com.aku.apps.punchin.free.domain.CalendarEvent;
import com.aku.apps.punchin.free.domain.TaskDay;
import com.aku.apps.punchin.free.domain.TimeStamp;

public class SQLiteCalendarEventFactory implements CalendarEventFactory {
	/**
	 * Log tag.
	 */
	private final static String TAG = SQLiteCalendarEventFactory.class.getSimpleName();
	
	/**
	 * Cached days.
	 */
	private static Hashtable<Long, CalendarEvent> cache = new Hashtable<Long, CalendarEvent>();
	
	/**
	 * Database helper.
	 */
	private DatabaseHelper helper;
	
	public SQLiteCalendarEventFactory(DatabaseHelper helper) {
		super();
		
		this.helper = helper;
	}


	@Override
	public void clearCache() {
		Log.d(TAG, "ENTER: clearCache");
		
		cache.clear();
	
		Log.d(TAG, "EXIT: clearCache");
	}

	@Override
	public ArrayList<CalendarEvent> getList(TaskDay taskDay) {
		Log.d(TAG, "ENTER: getList");
		
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.query(DatabaseTables.CalendarEvent._TABLE_NAME, 
				DatabaseTables.CalendarEvent._COLUMNS, 
				DatabaseTables.CalendarEvent.TASK_DAY_ID + " = ?", 
				new String[]{ String.valueOf(taskDay.getId())  }, 
				"", "", "");
		
		Log.d(TAG, "EXIT: getList");
		
		return createList(cursor);
	}

	@Override
	public void update(CalendarEvent item) {
		Log.d(TAG, "ENTER: update");

		int rows = 0;
		
		if (item != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			long modified = cal.getTimeInMillis();
			
			ContentValues values = new ContentValues();
			values.put(DatabaseTables.CalendarEvent.ALL_DAY, item.isAllDay() ? 1 : 0);
			values.put(DatabaseTables.CalendarEvent.STATUS, item.getStatus());
			values.put(DatabaseTables.CalendarEvent.VISIBILITY, item.getVisibility());
			values.put(DatabaseTables.CalendarEvent.TRANSPARENCY, item.getTransparency());
			values.put(DatabaseTables.CalendarEvent.HAS_ALARM, item.getHasAlarm());
			values.put(DatabaseTables.CalendarEvent.ANDROID_EVENT_ID, item.getAndroidEventId());
			values.put(DatabaseTables.CalendarEvent.START_TIME, item.getStartTime());
			values.put(DatabaseTables.CalendarEvent.END_TIME, item.getEndTime());
			values.put(DatabaseTables.CalendarEvent.TIME_STAMP_ID_PUNCH_IN, item.getPunchInId());
			values.put(DatabaseTables.CalendarEvent.TIME_STAMP_ID_PUNCH_OUT, item.getPunchOutId());
			values.put(DatabaseTables.CalendarEvent.TASK_DAY_ID, item.getTaskDayId());
			values.put(DatabaseTables.CalendarEvent.MODIFIED, modified);

			SQLiteDatabase db = helper.getWritableDatabase();
			rows = db.update(DatabaseTables.CalendarEvent._TABLE_NAME, values, 
					DatabaseTables.CalendarEvent._ID + " = ?", 
					new String[] {String.valueOf(item.getId())});
		}
		
		Log.d(TAG, rows + "EXIT: update - row(s) updated.");
	}

	@Override
	public void clear(TaskDay item) {
		Log.d(TAG, "ENTER: clear");

		int rows = 0;
		
		if (item != null) {
			SQLiteDatabase db = helper.getWritableDatabase();
			rows = db.delete(DatabaseTables.CalendarEvent._TABLE_NAME, 
					DatabaseTables.CalendarEvent.TASK_DAY_ID + " = ?", 
					new String[] { String.valueOf(item.getId()) });
		}
		
		Log.d(TAG, "EXIT: clear - " + rows + " row(s) cleared.");
	}

	@Override
	public CalendarEvent get(TimeStamp punchIn, TimeStamp punchOut) {
		Log.d(TAG, "ENTER: get");
		
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.query(DatabaseTables.CalendarEvent._TABLE_NAME, 
				DatabaseTables.CalendarEvent._COLUMNS, 
				DatabaseTables.CalendarEvent.TIME_STAMP_ID_PUNCH_IN + " = ? AND " + DatabaseTables.CalendarEvent.TIME_STAMP_ID_PUNCH_OUT + " = ?", 
				new String[]{ String.valueOf(punchIn.getId()), String.valueOf(punchOut.getId())  }, 
				"", "", "");
		
		CalendarEvent event = null;
		
		if (cursor.moveToFirst()) {
			event = create(cursor);
			
			if (!cache.containsKey(event.getId()))
				cache.put(event.getId(), event);
		}
		
		Log.d(TAG, "EXIT: get");

		return event;
	}

	@Override
	public CalendarEvent add(TaskDay taskDay, long startTime, long endTime, 
			boolean allDay, int status, int visibility, int transparency,
			int hasAlarm, String androidEventId) {
		
		return add(taskDay, startTime, endTime, allDay, status, visibility, transparency, hasAlarm, androidEventId, null, null);
	}

	@Override
	public CalendarEvent add(TaskDay taskDay, long startTime, long endTime,
			boolean allDay, int status, int visibility, int transparency,
			int hasAlarm, String androidEventId, TimeStamp punchIn,
			TimeStamp punchOut) {

		Log.d(TAG, "ENTER: add");

		Calendar cal = Calendar.getInstance();
		
		cal.setTime(new Date());
		long created = cal.getTimeInMillis();
		long modified = cal.getTimeInMillis();
		
		ContentValues values = new ContentValues();
		values.put(DatabaseTables.CalendarEvent.ALL_DAY, allDay ? 1 : 0);
		values.put(DatabaseTables.CalendarEvent.STATUS, status);
		values.put(DatabaseTables.CalendarEvent.VISIBILITY, visibility);
		values.put(DatabaseTables.CalendarEvent.TRANSPARENCY, transparency);
		values.put(DatabaseTables.CalendarEvent.HAS_ALARM, hasAlarm);
		values.put(DatabaseTables.CalendarEvent.ANDROID_EVENT_ID, androidEventId);
		values.put(DatabaseTables.CalendarEvent.START_TIME, startTime);
		values.put(DatabaseTables.CalendarEvent.END_TIME, endTime);
		
		if (punchIn == null)
			values.putNull(DatabaseTables.CalendarEvent.TIME_STAMP_ID_PUNCH_IN);
		else
			values.put(DatabaseTables.CalendarEvent.TIME_STAMP_ID_PUNCH_IN, punchIn.getId());
		
		if (punchOut == null)
			values.putNull(DatabaseTables.CalendarEvent.TIME_STAMP_ID_PUNCH_OUT);
		else
			values.put(DatabaseTables.CalendarEvent.TIME_STAMP_ID_PUNCH_OUT, punchOut.getId());
		
		values.put(DatabaseTables.CalendarEvent.TASK_DAY_ID, taskDay.getId());
		values.put(DatabaseTables.CalendarEvent.CREATED, created);
		values.put(DatabaseTables.CalendarEvent.MODIFIED, modified);

		SQLiteDatabase db = helper.getWritableDatabase();
		long id = db.insert(DatabaseTables.CalendarEvent._TABLE_NAME, null, values);
		CalendarEvent event = null;
		
		if (id != -1) {
			event = new CalendarEvent(id, taskDay.getId(), startTime, endTime, (int)(allDay ? 1 : 0), status, 
					visibility, 
					transparency, 
					hasAlarm,
					androidEventId, 
					punchIn == null ? -1 : punchIn.getId(), 
					punchOut == null ? -1 : punchOut.getId(), 
					created, modified);
			
			cache.put(id, event);
		}

		Log.d(TAG, "EXIT: add");

		return event;
	}

	@Override
	public void remove(ArrayList<CalendarEvent> events) {
		Log.d(TAG, "ENTER: remove");

		if (events != null && events.size() > 0) {
			SQLiteDatabase db = helper.getWritableDatabase();

			for (CalendarEvent event : events) {
				db.delete(DatabaseTables.CalendarEvent._TABLE_NAME, 
						DatabaseTables.CalendarEvent._ID + " = ?", 
						new String[] { String.valueOf(event.getId()) });
			}
		}

		Log.d(TAG, "EXIT: remove");
	}


















	/**
	 * Creates a calendar event from the specified {@link Cursor}.
	 * @param cursor
	 * @return
	 */
	private CalendarEvent create(Cursor cursor) {
		return create(cursor, true);
	}
	
	/**
	 * Creates a calendar event from the specified {@link Cursor}.
	 * @param cursor
	 * @return
	 */
	private CalendarEvent create(Cursor cursor, boolean closeCursor) {
		CalendarEvent event;
		
		event = new CalendarEvent(cursor.getLong(cursor.getColumnIndex(DatabaseTables.CalendarEvent._ID)), 
				cursor.getLong(cursor.getColumnIndex(DatabaseTables.CalendarEvent.TASK_DAY_ID)), 
				cursor.getLong(cursor.getColumnIndex(DatabaseTables.CalendarEvent.START_TIME)), 
				cursor.getLong(cursor.getColumnIndex(DatabaseTables.CalendarEvent.END_TIME)), 
				cursor.getInt(cursor.getColumnIndex(DatabaseTables.CalendarEvent.ALL_DAY)), 
				cursor.getInt(cursor.getColumnIndex(DatabaseTables.CalendarEvent.STATUS)), 
				cursor.getInt(cursor.getColumnIndex(DatabaseTables.CalendarEvent.VISIBILITY)), 
				cursor.getInt(cursor.getColumnIndex(DatabaseTables.CalendarEvent.TRANSPARENCY)), 
				cursor.getInt(cursor.getColumnIndex(DatabaseTables.CalendarEvent.HAS_ALARM)), 
				cursor.getString(cursor.getColumnIndex(DatabaseTables.CalendarEvent.ANDROID_EVENT_ID)), 
				cursor.getLong(cursor.getColumnIndex(DatabaseTables.CalendarEvent.TIME_STAMP_ID_PUNCH_IN)), 
				cursor.getLong(cursor.getColumnIndex(DatabaseTables.CalendarEvent.TIME_STAMP_ID_PUNCH_OUT)), 
				cursor.getLong(cursor.getColumnIndex(DatabaseTables.CalendarEvent.CREATED)), 
				cursor.getLong(cursor.getColumnIndex(DatabaseTables.CalendarEvent.MODIFIED)));
		
		if (closeCursor)
			cursor.close();

		return event;
	}

	/**
	 * Creates an array of calendar events.
	 * @param cursor
	 * @return
	 */
	private ArrayList<CalendarEvent> createList(Cursor cursor) {
		ArrayList<CalendarEvent> list = new ArrayList<CalendarEvent>();
		
		while (cursor.moveToNext()) {
			CalendarEvent item = null;
			long id = cursor.getLong(cursor.getColumnIndex(DatabaseTables.CalendarEvent._ID));
			
			if (cache.containsKey(id)) {
				item = cache.get(id);
				
			} else {
				item = create(cursor, false);
				cache.put(id, item);
				
			}
			
			list.add(item);
		}
		
		cursor.close();
		
		return list;
	}
}
