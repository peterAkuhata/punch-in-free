package com.aku.apps.punchin.free.db.sqlite;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.aku.apps.punchin.free.db.DatasourceFactory;
import com.aku.apps.punchin.free.db.TimeStampFactory;
import com.aku.apps.punchin.free.domain.Day;
import com.aku.apps.punchin.free.domain.Task;
import com.aku.apps.punchin.free.domain.TaskDay;
import com.aku.apps.punchin.free.domain.TimeStamp;
import com.aku.apps.punchin.free.domain.TimeStamp.TimeStampType;
import com.aku.apps.punchin.free.utils.DateUtil;

public class SQLiteTimeStampFactory implements TimeStampFactory {
	/**
	 * Log tag.
	 */
	private final static String TAG = SQLiteTimeStampFactory.class.getSimpleName();
	
	/**
	 * Cached expenses.
	 */
	private static Hashtable<Long, TimeStamp> cache = new Hashtable<Long, TimeStamp>();
	
	/**
	 * The datasource factory.
	 */
	private DatasourceFactory datasourceFactory;
	
	/**
	 * Database helper.
	 */
	private DatabaseHelper helper;
	
	/**
	 * The active timestamp.
	 */
	private TimeStamp activeTimeStamp = null;
	
	public SQLiteTimeStampFactory(DatabaseHelper helper, DatasourceFactory factory) {
		super();
		
		this.helper = helper;
		this.datasourceFactory = factory;
	}

	@Override
	public void clearCache() {
		Log.d(TAG, "ENTER: clearCache");
		
		cache.clear();
		
		Log.d(TAG, "EXIT: clearCache");
	}

	@Override
	public ArrayList<TimeStamp> getListByDate(Date date) {
		Log.d(TAG, "ENTER: getListByDate(date=" + date + ")");

		Calendar cal = Calendar.getInstance();
		cal.setTime(DateUtil.removeTimeFromDate(date));
		long startInMillis = cal.getTimeInMillis();
		cal.setTime(DateUtil.setEndOfDay(date));
		long endInMillis = cal.getTimeInMillis();
		
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.query(DatabaseTables.TimeStamp._TABLE_NAME, 
				DatabaseTables.TimeStamp._COLUMNS, 
				DatabaseTables.TimeStamp.TIME + " >= ? AND " + DatabaseTables.TimeStamp.TIME + " <= ? ", 
				new String[]{ String.valueOf(startInMillis), String.valueOf(endInMillis)  }, 
				"", "", "");
		
		Log.d(TAG, "EXIT: getListByDate");

		return createList(cursor);
	}

	@Override
	public ArrayList<TimeStamp> getListBetween(Date startDate, Date endDate) {
		Log.d(TAG, "ENTER: getListBetween(startDate=" + startDate + ", endDate=" + endDate + ")");

		Calendar cal = Calendar.getInstance();
		cal.setTime(DateUtil.removeTimeFromDate(startDate));
		long startInMillis = cal.getTimeInMillis();
		cal.setTime(DateUtil.removeTimeFromDate(endDate));
		long endInMillis = cal.getTimeInMillis();
		
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.query(DatabaseTables.TimeStamp._TABLE_NAME, 
				DatabaseTables.TimeStamp._COLUMNS, 
				DatabaseTables.TimeStamp.TIME + " >= ? AND " + DatabaseTables.TimeStamp.TIME + " <= ? ", 
				new String[]{ String.valueOf(startInMillis), String.valueOf(endInMillis)  }, 
				"", "", "");
		
		Log.d(TAG, "EXIT: getListBetween");

		return createList(cursor);
	}

	@Override
	public TimeStamp add(Task task, Date startDate, TimeStampType type) {
		Log.d(TAG, "ENTER: add(task='" + (task == null ? "null" : task.getDescription()) + "', " +
				"startDate=" + startDate + ", type=" + type.toString() + ")");

		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		long created = cal.getTimeInMillis();
		long modified = cal.getTimeInMillis();
		cal.setTime(startDate);
		long time = cal.getTimeInMillis();
		
		Day day = this.datasourceFactory.createDayFactory().get(DateUtil.removeTimeFromDate(startDate), true);
		TaskDay taskDay = this.datasourceFactory.createTaskDayFactory().get(task, day, true);
		
		ContentValues values = new ContentValues();
		values.put(DatabaseTables.TimeStamp.TIME, time);
		values.put(DatabaseTables.TimeStamp.TYPE, type.ordinal());
		values.put(DatabaseTables.TimeStamp.TASK_DAY_ID, taskDay.getId());
		values.put(DatabaseTables.TimeStamp.CREATED, created);
		values.put(DatabaseTables.TimeStamp.MODIFIED, modified);

		SQLiteDatabase db = helper.getWritableDatabase();
		long id = db.insert(DatabaseTables.TimeStamp._TABLE_NAME, null, values);
		TimeStamp item = null;
		
		if (id != -1) {
			item = new TimeStamp(id, type.ordinal(), taskDay.getId(), time, created, modified);
			cache.put(id, item);

			if (item.getType() == TimeStampType.PunchIn)
				activeTimeStamp = item;
			else
				activeTimeStamp = null;
		}

		Log.d(TAG, "EXIT: add");

		return item;
	}

	@Override
	public TimeStamp getActive() {
		return activeTimeStamp;
	}

	/**
	 * Queries the database to return the active timestamp.
	 * The active timestamp is the last timestamp entered IF it is a punch in, NOT a punch out.
	 * @return
	 */
	private TimeStamp getActiveFromDb() {
		Log.d(TAG, "ENTER: getActiveFromDb");
		
		TimeStamp item = null;
		
		if (activeTimeStamp == null) {
			SQLiteDatabase db = helper.getReadableDatabase();
//			db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy, limit)
			Cursor cursor = db.query(DatabaseTables.TimeStamp._TABLE_NAME, 
					DatabaseTables.TimeStamp._COLUMNS, 
					"", 
					new String[]{ }, 
					"", 
					"", 
					DatabaseTables.TimeStamp._ID + " desc",
					"1");
			
			if (cursor.moveToFirst()) {
				item = create(cursor);
				cache.put(item.getId(), item);
				
				if (item.getType() == TimeStampType.PunchIn)
					this.activeTimeStamp = item;
			}
		}

		Log.d(TAG, "EXIT: getActiveFromDb");

		return activeTimeStamp;
	}

	private ArrayList<TimeStamp> getTimeStamps(TaskDay taskDay) {
		Log.d(TAG, "ENTER: getTimeStamps");
		
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.query(DatabaseTables.TimeStamp._TABLE_NAME, 
				DatabaseTables.TimeStamp._COLUMNS, 
				DatabaseTables.TimeStamp.TASK_DAY_ID + " = ?", 
				new String[]{ String.valueOf(taskDay.getId()) }, 
				"", "", "");
		
		Log.d(TAG, "EXIT: getTimeStamps");

		return createList(cursor);
	}
	
	@Override
	public long getTimeSpentOnTask(Task task, Date date) {
		Log.d(TAG, "ENTER: getTimeSpentOnTask(task='" + (task == null ? "null" : task.getDescription()) +"', date='" + date + ")");
		
		Day day = this.datasourceFactory.createDayFactory().get(DateUtil.removeTimeFromDate(date), true);
		TaskDay taskDay = this.datasourceFactory.createTaskDayFactory().get(task, day, true);
		ArrayList<TimeStamp> list = getTimeStamps(taskDay);
		Date punchIn = null;
		Date punchOut = null;
		long timeOnTask = 0;
		
		for (TimeStamp item : list) {
			Task t = getTask(item);
			
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
		
		Log.v(TAG, "EXIT: getTimeSpentOnTask - timeOnTask='" + timeOnTask + " milliseconds'");

		return timeOnTask;
	}

	@Override
	public void clearTimeSpentOnTask(Task task, Date date) {
		Log.d(TAG, "ENTER: clearTimeSpentOnTask(date='" + date + "', task='" + task.getDescription() +"')");
		
		Day day = this.datasourceFactory.createDayFactory().get(DateUtil.removeTimeFromDate(date), true);
		TaskDay taskDay = this.datasourceFactory.createTaskDayFactory().get(task, day, true);

		SQLiteDatabase db = helper.getReadableDatabase();
		int rows = db.delete(DatabaseTables.TimeStamp._TABLE_NAME, 
				DatabaseTables.TimeStamp.TASK_DAY_ID + " = ?", 
				new String[] { String.valueOf(taskDay.getId()) });
		
		if (activeTimeStamp != null && getTask(activeTimeStamp).equals(task))
			activeTimeStamp = null;
		
		Log.v(TAG, "EXIT: clearTimeSpentOnTask - rows deleted='" + rows);
}

	@Override
	public TimeStamp getLastTimeStamp(Task task, Date date) {
		Log.d(TAG, "ENTER: getLastTimeStamp(date='" + date + "', task='" + task.getDescription() +"')");
		
		Day day = this.datasourceFactory.createDayFactory().get(DateUtil.removeTimeFromDate(date), true);
		TaskDay taskDay = this.datasourceFactory.createTaskDayFactory().get(task, day, true);

		ArrayList<TimeStamp> list = getTimeStamps(taskDay);
		TimeStamp ts = null;
		
		if (list.size() > 0)
			ts = list.get(list.size() - 1);
		
		return ts;
	}

	@Override
	public TimeStamp get(long id) {
		Log.d(TAG, "ENTER: get(id=" + id + ")");
		
		TimeStamp item = null;
		
		if (cache.containsKey(id)) {
			item = cache.get(id);
			
		} else {
			SQLiteDatabase db = helper.getReadableDatabase();
			Cursor cursor = db.query(DatabaseTables.TimeStamp._TABLE_NAME, 
					DatabaseTables.TimeStamp._COLUMNS, 
					DatabaseTables.TimeStamp._ID + " = ?", 
					new String[]{ String.valueOf(id)  }, 
					"", "", "");
			
			if (cursor.moveToFirst()) {
				item = create(cursor);
				cache.put(id, item);
			}
		}
		
		Log.d(TAG, "EXIT: get");

		return item;
	}

	@Override
	public TimeStamp getLastTimeStamp() {
		Log.d(TAG, "ENTER: getLastTimeStamp");
		
		TimeStamp item = null;
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.query(DatabaseTables.TimeStamp._TABLE_NAME, 
				DatabaseTables.TimeStamp._COLUMNS, 
				"", 
				new String[]{ }, 
				"", 
				"", 
				DatabaseTables.TimeStamp.CREATED + " desc",
				"1");
		
		if (cursor.moveToFirst()) {
			item = create(cursor);
			
			if (!cache.containsValue(item.getId()))
				cache.put(item.getId(), item);
		}


		Log.d(TAG, "EXIT: getLastTimeStamp");		

		return item;
	}

	@Override
	public void update(TimeStamp item) {
		Log.d(TAG, "ENTER: update");

		int rows = 0;
		
		if (item != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			long modified = cal.getTimeInMillis();
			cal.setTime(item.getTime());
			long time = cal.getTimeInMillis();
			
			ContentValues values = new ContentValues();
			values.put(DatabaseTables.TimeStamp.TIME, time);
			values.put(DatabaseTables.TimeStamp.TYPE, item.getType().ordinal());
			values.put(DatabaseTables.TimeStamp.TASK_DAY_ID, item.getTaskDayId());
			values.put(DatabaseTables.TimeStamp.MODIFIED, modified);

			SQLiteDatabase db = helper.getWritableDatabase();
			rows = db.update(DatabaseTables.TimeStamp._TABLE_NAME, values, 
					DatabaseTables.TimeStamp._ID + " = ?", 
					new String[] {String.valueOf(item.getId())});
		}
		
		Log.d(TAG, "EXIT: update - " + rows + " row(s) updated.");
	}

	@Override
	public TimeStamp checkActive() {
		Log.d(TAG, "ENTER: checkActive");

		TimeStamp timestamp = null;
		
		if (activeTimeStamp == null)
			activeTimeStamp = getActiveFromDb();
		
		if (activeTimeStamp != null) {
			Task task = getTask(activeTimeStamp);
			Day day = getDay(activeTimeStamp);
			Date startDate = day.getDate();
			
			if (!startDate.equals(DateUtil.getToday())) {
				Date date = DateUtil.setEndOfDay(startDate);				
				timestamp = this.add(task, date, TimeStampType.PunchOut);
			}
		}
		
		Log.d(TAG, "EXIT: checkActive");

		return timestamp;
	}

	@Override
	public Task getTask(TimeStamp ts) {
		Log.d(TAG, "ENTER: getTask");

		TaskDay td = getTaskDay(ts);
		Task t = this.datasourceFactory.createTaskFactory().get(td.getTaskId());

		Log.d(TAG, "EXIT: getTask");

		return t;
	}

	@Override
	public Day getDay(TimeStamp ts) {
		Log.d(TAG, "ENTER: getDay");
		
		TaskDay td = getTaskDay(ts);
		Day d = this.datasourceFactory.createDayFactory().get(td.getDayId());

		Log.d(TAG, "EXIT: getDay");
		
		return d;
	}

	@Override
	public TaskDay getTaskDay(TimeStamp ts) {
		Log.d(TAG, "ENTER: getTaskDay");
		
		TaskDay td = this.datasourceFactory.createTaskDayFactory().get(ts.getTaskDayId());

		Log.d(TAG, "EXIT: getTaskDay");		

		return td;
	}

















	/**
	 * Creates a expense from the specified {@link Cursor}.
	 * @param cursor
	 * @return
	 */
	private TimeStamp create(Cursor cursor) {
		return create(cursor, true);
	}

	/**
	 * Creates a expense from the specified {@link Cursor}.
	 * @param cursor
	 * @return
	 */
	private TimeStamp create(Cursor cursor, boolean closeCursor) {
		TimeStamp item;
		
		item = new TimeStamp(cursor.getLong(cursor.getColumnIndex(DatabaseTables.TimeStamp._ID)), 
				cursor.getInt(cursor.getColumnIndex(DatabaseTables.TimeStamp.TYPE)),
				cursor.getLong(cursor.getColumnIndex(DatabaseTables.TimeStamp.TASK_DAY_ID)),
				cursor.getLong(cursor.getColumnIndex(DatabaseTables.TimeStamp.TIME)),
				cursor.getLong(cursor.getColumnIndex(DatabaseTables.TimeStamp.CREATED)),
				cursor.getLong(cursor.getColumnIndex(DatabaseTables.TimeStamp.MODIFIED)));

		if (closeCursor)
			cursor.close();
		
		return item;
	}

	/**
	 * Creates an array of expenses.
	 * @param cursor
	 * @return
	 */
	private ArrayList<TimeStamp> createList(Cursor cursor) {
		ArrayList<TimeStamp> list = new ArrayList<TimeStamp>();
		
		while (cursor.moveToNext()) {
			TimeStamp item = null;
			long id = cursor.getLong(cursor.getColumnIndex(DatabaseTables.TimeStamp._ID));
			
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
