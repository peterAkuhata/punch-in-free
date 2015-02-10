package com.aku.apps.punchin.free.db.sqlite;

import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.aku.apps.punchin.free.db.TaskDayFactory;
import com.aku.apps.punchin.free.domain.Day;
import com.aku.apps.punchin.free.domain.Task;
import com.aku.apps.punchin.free.domain.TaskDay;

public class SQLiteTaskDayFactory implements TaskDayFactory {
	/**
	 * Log tag.
	 */
	private final static String TAG = SQLiteTaskDayFactory.class.getSimpleName();
	
	/**
	 * Cached expenses.
	 */
	private static Hashtable<Long, TaskDay> cache = new Hashtable<Long, TaskDay>();
	
	/**
	 * Database helper.
	 */
	private DatabaseHelper helper;
	
	public SQLiteTaskDayFactory(DatabaseHelper helper) {
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
	public TaskDay get(long id) {
		Log.d(TAG, "ENTER: get(id=" + id + ")");
		
		TaskDay item = null;
		
		if (cache.containsKey(id)) {
			item = cache.get(id);
			
		} else {
			SQLiteDatabase db = helper.getReadableDatabase();
			Cursor cursor = db.query(DatabaseTables.TaskDay._TABLE_NAME, 
					DatabaseTables.TaskDay._COLUMNS, 
					DatabaseTables.TaskDay._ID + " = ?", 
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
	public TaskDay get(Task task, Day day, boolean addIfNotFound) {
		Log.d(TAG, "ENTER: get(task=" + (task == null ? "null" : task.getDescription()) + 
				", day=" + (day == null ? "null" : day.getDate()) + 
				", addIfNotFound=" + addIfNotFound + ")");
		
		TaskDay item = null;
		
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.query(DatabaseTables.TaskDay._TABLE_NAME, 
				DatabaseTables.TaskDay._COLUMNS, 
				DatabaseTables.TaskDay.TASK_ID + " = ? AND " + DatabaseTables.TaskDay.DAY_ID + " = ?", 
				new String[]{ String.valueOf(task.getId()), String.valueOf(day.getId())  }, 
				"", "", "");
		
		if (cursor.moveToFirst()) {
			item = create(cursor);
			
			if (!cache.containsKey(item.getId()))
				cache.put(item.getId(), item);
		
		} else if (addIfNotFound) {
			item = add(task, day, "");
			
		}
		
		Log.d(TAG, "EXIT: get");

		return item;
	}

	@Override
	public TaskDay add(Task task, Day day, String notes) {
		Log.d(TAG, "ENTER: add(task=" + (task == null ? "null" : task.getDescription()) + 
				", day=" + (day == null ? "null" : day.getDate()) + 
				", notes=" + notes + ")");

		Calendar cal = Calendar.getInstance();
		
		cal.setTime(new Date());
		long created = cal.getTimeInMillis();
		long modified = cal.getTimeInMillis();
		
		ContentValues values = new ContentValues();
		values.put(DatabaseTables.TaskDay.NOTES, notes);
		values.put(DatabaseTables.TaskDay.TASK_ID, task.getId());
		values.put(DatabaseTables.TaskDay.DAY_ID, day.getId());
		values.put(DatabaseTables.TaskDay.CREATED, created);
		values.put(DatabaseTables.TaskDay.MODIFIED, modified);

		SQLiteDatabase db = helper.getWritableDatabase();
		long id = db.insert(DatabaseTables.TaskDay._TABLE_NAME, null, values);
		TaskDay item = null;
		
		if (id != -1) {
			item = new TaskDay(id, notes, task.getId(), day.getId(), created, modified);
			cache.put(id, item);
		}

		Log.d(TAG, "EXIT: add");

		return item;
	}

	@Override
	public void update(TaskDay item) {
		Log.d(TAG, "ENTER: update");

		int rows = 0;
		
		if (item != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			long modified = cal.getTimeInMillis();
			
			ContentValues values = new ContentValues();
			values.put(DatabaseTables.TaskDay.NOTES, item.getNotes());
			values.put(DatabaseTables.TaskDay.TASK_ID, item.getTaskId());
			values.put(DatabaseTables.TaskDay.DAY_ID, item.getDayId());
			values.put(DatabaseTables.TaskDay.MODIFIED, modified);

			SQLiteDatabase db = helper.getWritableDatabase();
			rows = db.update(DatabaseTables.TaskDay._TABLE_NAME, values, 
					DatabaseTables.TaskDay._ID + " = ?", 
					new String[] {String.valueOf(item.getId())});
		}
		
		Log.d(TAG, "EXIT: update - " + rows + " row(s) updated.");
	}











	/**
	 * Creates a task day from the specified {@link Cursor}.
	 * @param cursor
	 * @return
	 */
	private TaskDay create(Cursor cursor) {
		return create(cursor, true);
	}

	/**
	 * Creates a expense from the specified {@link Cursor}.
	 * @param cursor
	 * @return
	 */
	private TaskDay create(Cursor cursor, boolean closeCursor) {
		TaskDay item;
		
		item = new TaskDay(cursor.getLong(cursor.getColumnIndex(DatabaseTables.TaskDay._ID)), 
				cursor.getString(cursor.getColumnIndex(DatabaseTables.TaskDay.NOTES)),
				cursor.getLong(cursor.getColumnIndex(DatabaseTables.TaskDay.TASK_ID)),
				cursor.getLong(cursor.getColumnIndex(DatabaseTables.TaskDay.DAY_ID)),
				cursor.getLong(cursor.getColumnIndex(DatabaseTables.TaskDay.CREATED)),
				cursor.getLong(cursor.getColumnIndex(DatabaseTables.TaskDay.MODIFIED)));

		if (closeCursor)
			cursor.close();
		
		return item;
	}
}
