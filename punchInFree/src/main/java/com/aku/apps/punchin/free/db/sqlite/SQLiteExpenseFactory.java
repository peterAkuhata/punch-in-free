package com.aku.apps.punchin.free.db.sqlite;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.aku.apps.punchin.free.db.ExpenseFactory;
import com.aku.apps.punchin.free.domain.Expense;
import com.aku.apps.punchin.free.domain.TaskDay;
import com.aku.apps.punchin.free.domain.Expense.ExpenseType;

public class SQLiteExpenseFactory implements ExpenseFactory {
	/**
	 * Log tag.
	 */
	private final static String TAG = SQLiteExpenseFactory.class.getSimpleName();
	
	/**
	 * Cached expenses.
	 */
	private static Hashtable<Long, Expense> cache = new Hashtable<Long, Expense>();
	
	/**
	 * Database helper.
	 */
	private DatabaseHelper helper;
	
	public SQLiteExpenseFactory(DatabaseHelper helper) {
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
	public Expense get(long id) {
		Log.d(TAG, "ENTER: get(id=" + id + ")");
		
		Expense item = null;
		
		if (cache.containsKey(id)) {
			item = cache.get(id);
			
		} else {
			SQLiteDatabase db = helper.getReadableDatabase();
			Cursor cursor = db.query(DatabaseTables.Expense._TABLE_NAME, 
					DatabaseTables.Expense._COLUMNS, 
					DatabaseTables.Expense._ID + " = ?", 
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
	public ArrayList<Expense> getListByTaskDay(TaskDay taskDay) {
		Log.d(TAG, "ENTER: getListByTaskDay");
		
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.query(DatabaseTables.Expense._TABLE_NAME, 
				DatabaseTables.Expense._COLUMNS, 
				DatabaseTables.Expense.TASK_DAY_ID + " = ?", 
				new String[]{ String.valueOf(taskDay.getId())  }, 
				"", "", "");
		
		Log.d(TAG, "EXIT: getListByTaskDay");

		return createList(cursor);
	}

	@Override
	public void removeByTaskDay(TaskDay taskDay) {
		Log.d(TAG, "ENTER: removeByTaskDay");
		
		SQLiteDatabase db = helper.getReadableDatabase();
		int rows = db.delete(DatabaseTables.Expense._TABLE_NAME, 
				DatabaseTables.Expense.TASK_DAY_ID + " = ?", 
				new String[] { String.valueOf(taskDay.getId()) });
		
		Log.d(TAG, "EXIT: removeByTaskDay - " + rows + " row(s) removed.");
	}

	@Override
	public void remove(Expense expense) {
		Log.d(TAG, "ENTER: remove");
		
		SQLiteDatabase db = helper.getReadableDatabase();
		int rows = db.delete(DatabaseTables.Expense._TABLE_NAME, 
				DatabaseTables.Expense._ID + " = ?", 
				new String[] { String.valueOf(expense.getId()) });
		
		Log.d(TAG, "EXIT: remove - " + rows + " row(s) removed.");
	}

	@Override
	public void update(Expense item) {
		Log.d(TAG, "ENTER: update");

		int rows = 0;
		
		if (item != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			long modified = cal.getTimeInMillis();
			
			ContentValues values = new ContentValues();
			values.put(DatabaseTables.Expense.TASK_DAY_ID, item.getTaskDayId());
			values.put(DatabaseTables.Expense.TYPE, item.getType().ordinal());
			values.put(DatabaseTables.Expense.AMOUNT, item.getAmount());
			values.put(DatabaseTables.Expense.NOTES, item.getNotes());
			values.put(DatabaseTables.Expense.MODIFIED, modified);
			
			SQLiteDatabase db = helper.getWritableDatabase();
			rows = db.update(DatabaseTables.Expense._TABLE_NAME, values, 
					DatabaseTables.Expense._ID + " = ?", 
					new String[] {String.valueOf(item.getId())});
		}
		
		Log.d(TAG, "EXIT: update - " + rows + " row(s) updated.");
	}

	@Override
	public Expense add(TaskDay taskDay, ExpenseType type, double amount, String notes) {
		Log.d(TAG, "ENTER: add(amount='" + amount + "')");

		Calendar cal = Calendar.getInstance();
		
		cal.setTime(new Date());
		long created = cal.getTimeInMillis();
		long modified = cal.getTimeInMillis();
		
		ContentValues values = new ContentValues();
		values.put(DatabaseTables.Expense.TASK_DAY_ID, taskDay.getId());
		values.put(DatabaseTables.Expense.TYPE, type.ordinal());
		values.put(DatabaseTables.Expense.AMOUNT, amount);
		values.put(DatabaseTables.Expense.NOTES, notes);
		values.put(DatabaseTables.Expense.CREATED, created);
		values.put(DatabaseTables.Expense.MODIFIED, modified);

		SQLiteDatabase db = helper.getWritableDatabase();
		long id = db.insert(DatabaseTables.Expense._TABLE_NAME, null, values);
		Expense item = null;
		
		if (id != -1) {
			item = new Expense(id, type.ordinal(), amount, notes, taskDay.getId(), created, modified);
			cache.put(id, item);
		}

		Log.d(TAG, "EXIT: add");

		return item;
	}







	/**
	 * Creates a expense from the specified {@link Cursor}.
	 * @param cursor
	 * @return
	 */
	private Expense create(Cursor cursor) {
		return create(cursor, true);
	}

	/**
	 * Creates a expense from the specified {@link Cursor}.
	 * @param cursor
	 * @return
	 */
	private Expense create(Cursor cursor, boolean closeCursor) {
		Expense item;
		
		item = new Expense(cursor.getLong(cursor.getColumnIndex(DatabaseTables.Expense._ID)), 
				cursor.getInt(cursor.getColumnIndex(DatabaseTables.Expense.TYPE)),
				cursor.getDouble(cursor.getColumnIndex(DatabaseTables.Expense.AMOUNT)),
				cursor.getString(cursor.getColumnIndex(DatabaseTables.Expense.NOTES)),
				cursor.getLong(cursor.getColumnIndex(DatabaseTables.Expense.TASK_DAY_ID)),
				cursor.getLong(cursor.getColumnIndex(DatabaseTables.Expense.CREATED)),
				cursor.getLong(cursor.getColumnIndex(DatabaseTables.Expense.MODIFIED)));
		
		if (closeCursor)
			cursor.close();
		
		return item;
	}

	/**
	 * Creates an array of expenses.
	 * @param cursor
	 * @return
	 */
	private ArrayList<Expense> createList(Cursor cursor) {
		ArrayList<Expense> list = new ArrayList<Expense>();
		
		while (cursor.moveToNext()) {
			Expense item = null;
			long id = cursor.getLong(cursor.getColumnIndex(DatabaseTables.Expense._ID));
			
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
