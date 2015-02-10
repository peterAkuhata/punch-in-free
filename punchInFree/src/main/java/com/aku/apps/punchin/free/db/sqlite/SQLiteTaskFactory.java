package com.aku.apps.punchin.free.db.sqlite;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.aku.apps.punchin.free.db.TaskFactory;
import com.aku.apps.punchin.free.domain.Task;
import com.aku.apps.punchin.free.domain.Task.RepeatingType;
import com.aku.apps.punchin.free.utils.DateUtil;
import com.aku.apps.punchin.free.utils.TaskUtil;

public class SQLiteTaskFactory implements TaskFactory {
	/**
	 * Log tag.
	 */
	private final static String TAG = SQLiteTaskFactory.class.getSimpleName();
	
	/**
	 * Cached tasks.
	 */
	private static Hashtable<Long, Task> cache = new Hashtable<Long, Task>();
	
	/**
	 * Database helper.
	 */
	private DatabaseHelper helper;
	
	public SQLiteTaskFactory(DatabaseHelper helper) {
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
	public Task get(long id) {
		Log.d(TAG, "ENTER: get(id='" + id + "')");
		
		Task item = null;
		
		if (cache.containsValue(id)) {
			item = cache.get(id);
			
		} else {		
			SQLiteDatabase db = helper.getReadableDatabase();
			Cursor cursor = db.query(DatabaseTables.Task._TABLE_NAME, 
					DatabaseTables.Task._COLUMNS, 
					DatabaseTables.Task._ID + " = ?", 
					new String[]{String.valueOf(id)}, 
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
	public ArrayList<Task> getListByDate(Date date) {
		Log.d(TAG, "ENTER: getListByDate(date='" + date + "')");
		
		Date temp = DateUtil.removeTimeFromDate(date);
		ArrayList<Task> list = null;
		ArrayList<Task> list2 = null;
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(temp);
		long time = cal.getTimeInMillis();
		
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.query(DatabaseTables.Task._TABLE_NAME, 
				DatabaseTables.Task._COLUMNS, 
				DatabaseTables.Task.START_DATE + " <= ? AND (" + DatabaseTables.Task.END_DATE + " is null OR " + DatabaseTables.Task.END_DATE + " >= ?)", 
				new String[]{ String.valueOf(time), String.valueOf(time) }, 
				"", "", "");
		
		list2 = createList(cursor);
		list = new ArrayList<Task>();
		
		for (Task item : list2) {
			if (TaskUtil.isVisible(item, temp))
				list.add(item);
		}
		
		Log.d(TAG, "EXIT: getListByDate");

		return list;
	}

	@Override
	public Task add(long clientId, String description, Date startDate, Date endDate, RepeatingType type) {
		Log.d(TAG, "ENTER: add(description='" + description + "', startDate=" + startDate + ", " +
				"endDate=" + endDate + ", type=" + type.toString());

		Calendar cal = Calendar.getInstance();
		
		cal.setTime(DateUtil.removeTimeFromDate(startDate));
		long start = cal.getTimeInMillis();
		long end = 0;
		
		cal.setTime(new Date());
		long created = cal.getTimeInMillis();
		long modified = cal.getTimeInMillis();
		int repeating = TaskUtil.toRepeatingInt(type);
		int sort = getTaskCount();
		
		ContentValues values = new ContentValues();
		values.put(DatabaseTables.Task.DESCRIPTION, description);
		values.put(DatabaseTables.Task.START_DATE, start);
		
		if (endDate != null) {
			cal.setTime(DateUtil.removeTimeFromDate(endDate));
			end = cal.getTimeInMillis();
			values.put(DatabaseTables.Task.END_DATE, end);

		} else {
			values.putNull(DatabaseTables.Task.END_DATE);		

		}

		values.put(DatabaseTables.Task.REPEATING_TYPE, repeating);
		values.put(DatabaseTables.Task.SORT, sort);
		values.put(DatabaseTables.Task.ACTIVE, 1);
		values.put(DatabaseTables.Task.CREATED, created);
		values.put(DatabaseTables.Task.MODIFIED, modified);
		values.put(DatabaseTables.Task.CLIENT_ID, clientId);

		SQLiteDatabase db = helper.getWritableDatabase();
		long id = db.insert(DatabaseTables.Task._TABLE_NAME, null, values);
		Task item = null;
		
		if (id != -1) {
			item = new Task(id, description, start, end, repeating, 0, 1, created, modified, -1);
			cache.put(id, item);
		}
		
		Log.d(TAG, "EXIT: add");

		return item;
	}

	@Override
	public void update(Task item) {		
		Log.d(TAG, "ENTER: updateTask(task='" + (item == null ? "null" : item.getDescription()) + ")");

		int rows = 0;
		
		if (item != null) {
			item.setModified(new Date());
			Calendar cal = Calendar.getInstance();
			
			cal.setTime(DateUtil.removeTimeFromDate(item.getStartDate()));
			long start = cal.getTimeInMillis();
			cal.setTime(item.getModified());
			long modified = cal.getTimeInMillis();
			int repeating = TaskUtil.toRepeatingInt(item.getRepeatingType());
			
			ContentValues values = new ContentValues();
			values.put(DatabaseTables.Task.DESCRIPTION, item.getDescription());
			values.put(DatabaseTables.Task.START_DATE, start);

			if (item.getEndDate() != null) {
				cal.setTime(DateUtil.removeTimeFromDate(item.getEndDate()));
				long end = cal.getTimeInMillis();
				values.put(DatabaseTables.Task.END_DATE, end);

			} else {
				values.putNull(DatabaseTables.Task.END_DATE);		

			}

			values.put(DatabaseTables.Task.REPEATING_TYPE, repeating);
			values.put(DatabaseTables.Task.SORT, item.getSort());
			values.put(DatabaseTables.Task.ACTIVE, item.getActive() ? 1 : 0);
			values.put(DatabaseTables.Task.MODIFIED, modified);
			values.put(DatabaseTables.Task.CLIENT_ID, item.getClientId());
	
			SQLiteDatabase db = helper.getWritableDatabase();
			rows = db.update(DatabaseTables.Task._TABLE_NAME, values, 
					DatabaseTables.Task._ID + " = ?", 
					new String[] {String.valueOf(item.getId())});
		}
		
		Log.d(TAG, "EXIT: update - " + rows + " row(s) updated.");
	}

	@Override
	public ArrayList<Task> getList(boolean activeOnly) {	
		Log.d(TAG, "ENTER: getList(activeOnly='" + String.valueOf(activeOnly) + ")");
		
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = null;
		
		if (activeOnly) {
			cursor = db.query(DatabaseTables.Task._TABLE_NAME, 
					DatabaseTables.Task._COLUMNS, 
					DatabaseTables.Task.ACTIVE + " = 1", 
					new String[]{ }, 
					"", "", "");
			
		} else {
			cursor = db.query(DatabaseTables.Task._TABLE_NAME, 
					DatabaseTables.Task._COLUMNS, 
					"", 
					new String[]{ }, 
					"", "", "");
			
		}
		
		Log.d(TAG, "EXIT: getList");

		return createList(cursor);
	}

	@Override
	public ArrayList<Task> getList(boolean activeOnly, String filter) {
		Log.d(TAG, "ENTER: getList(activeOnly='" + String.valueOf(activeOnly) + ", filter='" + filter + "')");
		
		SQLiteDatabase db = helper.getReadableDatabase();
		ArrayList<Task> list = null;
		
		if (filter == null || filter.length() == 0) {
			list = getList(activeOnly);
		
		} else { 
			Cursor cursor = null;

			if (activeOnly) {
				cursor = db.query(DatabaseTables.Task._TABLE_NAME, 
						DatabaseTables.Task._COLUMNS, 
						DatabaseTables.Task.ACTIVE + " = 1 AND " + DatabaseTables.Task.DESCRIPTION + " LIKE ?", 
						new String[]{ filter + "%" }, 
						"", "", "");
				
			} else {
				cursor = db.query(DatabaseTables.Task._TABLE_NAME, 
						DatabaseTables.Task._COLUMNS, 
						DatabaseTables.Task.DESCRIPTION + " LIKE ?", 
						new String[]{ filter + "%" }, 
						"", "", "");
				
			}
			
			list = createList(cursor);
		}
		
		Log.d(TAG, "EXIT: getList");

		return list;
	}

	@Override
	public void resort(Task task, int newPosition) {
		Log.d(TAG, "ENTER: resort(client='" + (task == null ? "null" : task.getDescription()) + ", newPosition='" + String.valueOf(newPosition) + "')");

		if (task.getSort() != newPosition) {
			int increment = (task.getSort() > newPosition ? 1 : -1);
			int startPos = (task.getSort() > newPosition ? newPosition : task.getSort());
			int endPos = (task.getSort() > newPosition ? task.getSort() : newPosition + 1);
			
			for (int i = startPos; i < endPos; i++) {
				Task temp = getTaskBySort(i);
				
				if (temp != null) {
					temp.setSort(i + increment);
					updateSort(temp);
				}
			}
			
			task.setSort(newPosition);
			updateSort(task);
		}
		
		Log.d(TAG, "EXIT: resort");
	}

	
	
	
	
	
	
	
	
	
	
	
	

	/**
	 * Just updates the sort and modified values of the task to the database.
	 * @param task
	 */
	private void updateSort(Task task) {
		Log.d(TAG, "ENTER: updateSort(task='" + (task == null ? "null" : task.getDescription()) + ")");

		int rows = 0;
		
		if (task != null) {
			task.setModified(new Date());
			
			Calendar cal = Calendar.getInstance();
			cal.setTime(task.getModified());
			long modified = cal.getTimeInMillis();

			ContentValues values = new ContentValues();
			values.put(DatabaseTables.Task.SORT, task.getSort());
			values.put(DatabaseTables.Task.MODIFIED, modified);
	
			SQLiteDatabase db = helper.getWritableDatabase();
			rows = db.update(DatabaseTables.Task._TABLE_NAME, values, 
					DatabaseTables.Task._ID + " = ?", 
					new String[] {String.valueOf(task.getId())});
		}
		
		Log.d(TAG, rows + " row(s) updated.");
	}

	private Task getTaskBySort(int sort) {
		Log.d(TAG, "ENTER: getTaskCount(sort=" + sort + ")");
		
		Task item = null;
		
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.query(DatabaseTables.Task._TABLE_NAME, 
				DatabaseTables.Task._COLUMNS, 
				DatabaseTables.Task.SORT + " = ?", 
				new String[]{String.valueOf(sort)}, 
				"", "", "");
		
		if (cursor.moveToFirst()) {
			item = create(cursor);
			cache.put(item.getId(), item);
		}

		
		return item;
	}

	/**
	 * Returns the number of tasks in the database.
	 */
	private int getTaskCount() {
		Log.d(TAG, "ENTER: getTaskCount()");
		
		int count = 0;
		
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select count(*) from task", new String[] {});
		
		if (cursor.moveToFirst())
			count = cursor.getInt(0);

		return count;
	}

	/**
	 * Creates a task from the specified {@link Cursor}.
	 * @param cursor
	 * @return
	 */
	private Task create(Cursor cursor) {
		return create(cursor, true);
	}
	
	/**
	 * Creates a task from the specified {@link Cursor}.
	 * @param cursor
	 * @return
	 */
	private Task create(Cursor cursor, boolean closeCursor) {
		Task item;
		
		item = new Task(cursor.getLong(cursor.getColumnIndex(DatabaseTables.Task._ID)), 
				cursor.getString(cursor.getColumnIndex(DatabaseTables.Task.DESCRIPTION)), 
				cursor.getLong(cursor.getColumnIndex(DatabaseTables.Task.START_DATE)), 
				cursor.getLong(cursor.getColumnIndex(DatabaseTables.Task.END_DATE)),
				cursor.getInt(cursor.getColumnIndex(DatabaseTables.Task.REPEATING_TYPE)),
				cursor.getInt(cursor.getColumnIndex(DatabaseTables.Task.SORT)),
				cursor.getInt(cursor.getColumnIndex(DatabaseTables.Task.ACTIVE)),
				cursor.getLong(cursor.getColumnIndex(DatabaseTables.Task.CREATED)),
				cursor.getLong(cursor.getColumnIndex(DatabaseTables.Task.MODIFIED)),
				cursor.getInt(cursor.getColumnIndex(DatabaseTables.Task.CLIENT_ID)));
		
		if (closeCursor)
			cursor.close();
		
		return item;
	}

	/**
	 * Creates an array of tasks.
	 * @param cursor
	 * @return
	 */
	private ArrayList<Task> createList(Cursor cursor) {
		ArrayList<Task> list = new ArrayList<Task>();
		
		while (cursor.moveToNext()) {
			Task item = null;
			long id = cursor.getLong(cursor.getColumnIndex(DatabaseTables.Task._ID));
			
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
