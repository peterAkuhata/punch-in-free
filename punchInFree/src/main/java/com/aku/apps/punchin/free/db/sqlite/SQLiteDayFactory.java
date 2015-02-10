package com.aku.apps.punchin.free.db.sqlite;

import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.aku.apps.punchin.free.db.DayFactory;
import com.aku.apps.punchin.free.domain.Day;
import com.aku.apps.punchin.free.utils.DateUtil;

public class SQLiteDayFactory implements DayFactory {
	/**
	 * Log tag.
	 */
	private final static String TAG = SQLiteDayFactory.class.getSimpleName();
	
	/**
	 * Cached days.
	 */
	private static Hashtable<Long, Day> cache = new Hashtable<Long, Day>();
	
	/**
	 * Database helper.
	 */
	private DatabaseHelper helper;
	
	public SQLiteDayFactory(DatabaseHelper helper) {
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
	public Day add(Date date, String notes) {
		Log.d(TAG, "ENTER: add(date='" + (date == null ? "null" : date) + "')");

		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		long created = cal.getTimeInMillis();
		long modified = cal.getTimeInMillis();
		cal.setTime(DateUtil.removeTimeFromDate(date));
		long ddate = cal.getTimeInMillis();
		
		ContentValues values = new ContentValues();
		values.put(DatabaseTables.Day.DATE, ddate);
		values.put(DatabaseTables.Day.DAILY_NOTES, notes);
		values.put(DatabaseTables.Day.CREATED, created);
		values.put(DatabaseTables.Day.MODIFIED, modified);

		SQLiteDatabase db = helper.getWritableDatabase();
		long id = db.insert(DatabaseTables.Day._TABLE_NAME, null, values);
		Day day = null;
		
		if (id != -1) {
			day = new Day(id, ddate, notes, created, modified);
			cache.put(id, day);
		}
		
		Log.d(TAG, "EXIT: add");

		return day;
	}

	@Override
	public Day get(Date date, boolean addIfNotFound) {
		Log.d(TAG, "ENTER: get(date=" + date + ")");
		
		Day day = null;
		Calendar cal = Calendar.getInstance();
		cal.setTime(DateUtil.removeTimeFromDate(date));
		long ddate = cal.getTimeInMillis();
		
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.query(DatabaseTables.Day._TABLE_NAME, 
				DatabaseTables.Day._COLUMNS, 
				DatabaseTables.Day.DATE + " = ?", 
				new String[]{ String.valueOf(ddate) }, 
				"", "", "");
		
		if (cursor.moveToFirst()) {
			day = create(cursor);
			
			if (day == null) {
				if (addIfNotFound)
					day = add(date, "");
				
			} else {
				if (!cache.containsKey(day.getId()))
					cache.put(day.getId(), day);
				
			}
		} else if (addIfNotFound) {
			day = add(date, "");
			
		}
		
		Log.d(TAG, "EXIT: get");

		return day;
	}

	@Override
	public void updateNotes(Day day, String notes) {
		Log.d(TAG, "ENTER: update(day='" + (day == null ? "null" : day.getDate()) + "')");

		int rows = 0;
		
		if (day != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			long modified = cal.getTimeInMillis();
			
			ContentValues values = new ContentValues();
			values.put(DatabaseTables.Day.DAILY_NOTES, notes);
			values.put(DatabaseTables.Day.MODIFIED, modified);
	
			SQLiteDatabase db = helper.getWritableDatabase();
			rows = db.update(DatabaseTables.Day._TABLE_NAME, values, 
					DatabaseTables.Day._ID + " = ?", 
					new String[] {String.valueOf(day.getId())});
		}
		
		Log.d(TAG, "EXIT: updateNotes - " + rows + " row(s) updated.");
	}

	@Override
	public Day get(long id) {
		Log.d(TAG, "ENTER: get(id=" + id + ")");
		
		Day day = null;
		
		if (cache.containsKey(id)) {
			day = cache.get(id);
			
		} else {
			SQLiteDatabase db = helper.getReadableDatabase();
			Cursor cursor = db.query(DatabaseTables.Day._TABLE_NAME, 
					DatabaseTables.Day._COLUMNS, 
					DatabaseTables.Day._ID + " = ?", 
					new String[]{ String.valueOf(id)  }, 
					"", "", "");
			
			if (cursor.moveToFirst()) {
				day = create(cursor);
				cache.put(id, day);
			}
		}
		
		Log.d(TAG, "EXIT: get");

		return day;
	}




















	/**
	 * Creates a day from the specified {@link Cursor}.
	 * @param cursor
	 * @return
	 */
	private Day create(Cursor cursor) {
		Day item;
		
		item = new Day(cursor.getLong(cursor.getColumnIndex(DatabaseTables.Day._ID)), 
				cursor.getLong(cursor.getColumnIndex(DatabaseTables.Day.DATE)), 
				cursor.getString(cursor.getColumnIndex(DatabaseTables.Day.DAILY_NOTES)), 
				cursor.getLong(cursor.getColumnIndex(DatabaseTables.Day.CREATED)),
				cursor.getLong(cursor.getColumnIndex(DatabaseTables.Day.MODIFIED)));
		
		cursor.close();

		return item;
	}
}
