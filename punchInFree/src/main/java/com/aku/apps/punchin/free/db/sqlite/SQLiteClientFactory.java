package com.aku.apps.punchin.free.db.sqlite;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.aku.apps.punchin.free.db.ClientFactory;
import com.aku.apps.punchin.free.domain.Client;

public class SQLiteClientFactory implements ClientFactory {
	/**
	 * Log tag.
	 */
	private final static String TAG = SQLiteClientFactory.class.getSimpleName();
	
	/**
	 * Cached clients.
	 */
	private static Hashtable<Long, Client> cache = new Hashtable<Long, Client>();
	
	/**
	 * Database helper.
	 */
	private DatabaseHelper helper;
	
	public SQLiteClientFactory(DatabaseHelper helper) {
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
	public ArrayList<Client> getList() {
		Log.d(TAG, "ENTER: getList");
		
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.query(DatabaseTables.Client._TABLE_NAME, 
				DatabaseTables.Client._COLUMNS, 
				DatabaseTables.Client.ACTIVE + " = 1", 
				new String[]{ }, 
				"", "", "");
		
		Log.d(TAG, "EXIT: getList");
		
		return createList(cursor);
	}

	@Override
	public Client get(long id) {
		Log.d(TAG, "ENTER: get(id=" + id + ")");
		
		Client item = null;
		
		if (cache.containsKey(id)) {
			item = cache.get(id);
			
		} else {
			SQLiteDatabase db = helper.getReadableDatabase();
			Cursor cursor = db.query(DatabaseTables.Client._TABLE_NAME, 
					DatabaseTables.Client._COLUMNS, 
					DatabaseTables.Client._ID + " = ?", 
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
	public Client add(String name, String address, String mobile, double normalWorkingHours, double hourlyRate, double overtimeRate, double mileageRate) {
		Log.d(TAG, "ENTER: add(name='" + name + "')");

		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		long created = cal.getTimeInMillis();
		long modified = cal.getTimeInMillis();
		int sort = getCount();
		
		Client client = new Client(-1, name, "", mobile, sort, 1, normalWorkingHours, hourlyRate, overtimeRate, mileageRate, address, "", "", "", created, modified);
		add(client);
		
		Log.d(TAG, "EXIT: add");

		return client;
	}

	@Override
	public void update(Client item) {
		Log.d(TAG, "ENTER: update(client='" + (item == null ? "null" : item.getName()) + "')");

		int rows = 0;
		
		if (item != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			long modified = cal.getTimeInMillis();
			
			ContentValues values = new ContentValues();
			values.put(DatabaseTables.Client.NAME, item.getName());
			values.put(DatabaseTables.Client.EMAIL, item.getEmail());
			values.put(DatabaseTables.Client.ADDRESS, item.getAddress());
			values.put(DatabaseTables.Client.MOBILE, item.getMobile());
			values.put(DatabaseTables.Client.HOURLY_RATE, item.getHourlyRate());
			values.put(DatabaseTables.Client.NORMAL_WORKING_HOURS, item.getNormalWorkingHours());
			values.put(DatabaseTables.Client.MILEAGE_RATE, item.getMileageRate());
			values.put(DatabaseTables.Client.OVERTIME_MULTIPLIER, item.getOvertimeMultiplier());
			values.put(DatabaseTables.Client.ACTIVE, item.getActive() ? 1 : 0);
			values.put(DatabaseTables.Client.SORT, item.getSort());
			values.put(DatabaseTables.Client.ANDROID_CONTACT_ID, item.getAndroidLookupKey());
			values.put(DatabaseTables.Client.ANDROID_CONTACT_EMAIL_ID, item.getAndroidEmailLookupKey());
			values.put(DatabaseTables.Client.ANDROID_CONTACT_MOBILE_ID, item.getAndroidMobileLookupKey());
			values.put(DatabaseTables.Client.MODIFIED, modified);
	
			SQLiteDatabase db = helper.getWritableDatabase();
			rows = db.update(DatabaseTables.Client._TABLE_NAME, values, 
					DatabaseTables.Client._ID + " = ?", 
					new String[] {String.valueOf(item.getId())});
		}
		
		Log.d(TAG, "EXIT: update - " + rows + " row(s) updated.");
	}

	@Override
	public int getCount() {
		Log.d(TAG, "ENTER: getCount");
		
		int count = 0;
		
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select count(*) from client", new String[] {});
		
		if (cursor.moveToFirst())
			count = cursor.getInt(0);

		Log.d(TAG, "EXIT: getCount");

		return count;
	}

	@Override
	public void resort(Client client, int newPosition) {
		Log.d(TAG, "ENTER: resort(client='" + (client == null ? "null" : client.getName()) + ", newPosition='" + String.valueOf(newPosition) + "')");
		
		if (client.getSort() != newPosition) {
			int increment = (client.getSort() > newPosition ? 1 : -1);
			int startPos = (client.getSort() > newPosition ? newPosition : client.getSort());
			int endPos = (client.getSort() > newPosition ? client.getSort() : newPosition + 1);
			
			for (int i = startPos; i < endPos; i++) {
				Client temp = getClientBySort(i);
				
				if (temp != null) {
					temp.setSort(i + increment);
					updateSort(temp);
				}
			}
			
			client.setSort(newPosition);
			updateSort(client);
		}
		
		Log.d(TAG, "EXIT: resort");
	}

	@Override
	public ArrayList<Client> getList(boolean activeOnly, String filter) {
		Log.d(TAG, "ENTER: getList(activeOnly='" + String.valueOf(activeOnly) + ", filter='" + filter + "')");
		
		SQLiteDatabase db = helper.getReadableDatabase();
		ArrayList<Client> list = null;
		
		if (filter == null || filter.length() == 0) {
			list = getList(activeOnly);
		
		} else { 
			Cursor cursor = null;

			if (activeOnly) {
				cursor = db.query(DatabaseTables.Client._TABLE_NAME, 
						DatabaseTables.Client._COLUMNS, 
						DatabaseTables.Client.ACTIVE + " = 1 AND " + DatabaseTables.Client.NAME + " LIKE ?", 
						new String[]{ filter + "%" }, 
						"", "", "");
				
			} else {
				cursor = db.query(DatabaseTables.Client._TABLE_NAME, 
						DatabaseTables.Client._COLUMNS, 
						DatabaseTables.Client.NAME + " LIKE ?", 
						new String[]{ filter + "%" }, 
						"", "", "");
				
			}
			
			list = createList(cursor);
		}
		
		Log.d(TAG, "EXIT: getList");

		return list;
	}

	@Override
	public ArrayList<Client> getList(boolean activeOnly) {
		Log.d(TAG, "ENTER: getList(activeOnly='" + String.valueOf(activeOnly) + ")");
		
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = null;
		
		if (activeOnly) {
			cursor = db.query(DatabaseTables.Client._TABLE_NAME, 
					DatabaseTables.Client._COLUMNS, 
					DatabaseTables.Client.ACTIVE + " = 1", 
					new String[]{ }, 
					"", "", "");
			
		} else {
			cursor = db.query(DatabaseTables.Client._TABLE_NAME, 
					DatabaseTables.Client._COLUMNS, 
					"", 
					new String[]{ }, 
					"", "", "");
			
		}
		
		Log.d(TAG, "EXIT: getList");

		return createList(cursor);
	}

	@Override
	public void add(Client client) {
		Log.d(TAG, "ENTER: add(client='" + (client == null ? "null" : client.getName()) + "')");

		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		long created = cal.getTimeInMillis();
		long modified = cal.getTimeInMillis();
		
		ContentValues values = new ContentValues();
		values.put(DatabaseTables.Client.NAME, client.getName());
		values.put(DatabaseTables.Client.EMAIL, client.getEmail());
		values.put(DatabaseTables.Client.ADDRESS, client.getAddress());
		values.put(DatabaseTables.Client.MOBILE, client.getMobile());
		values.put(DatabaseTables.Client.HOURLY_RATE, client.getHourlyRate());
		values.put(DatabaseTables.Client.NORMAL_WORKING_HOURS, client.getNormalWorkingHours());
		values.put(DatabaseTables.Client.MILEAGE_RATE, client.getMileageRate());
		values.put(DatabaseTables.Client.OVERTIME_MULTIPLIER, client.getOvertimeMultiplier());
		values.put(DatabaseTables.Client.ACTIVE, client.getActive() ? 1 : 0);
		values.put(DatabaseTables.Client.SORT, client.getSort());
		values.put(DatabaseTables.Client.ANDROID_CONTACT_ID, client.getAndroidLookupKey());
		values.put(DatabaseTables.Client.ANDROID_CONTACT_EMAIL_ID, client.getAndroidEmailLookupKey());
		values.put(DatabaseTables.Client.ANDROID_CONTACT_MOBILE_ID, client.getAndroidMobileLookupKey());
		values.put(DatabaseTables.Client.CREATED, created);
		values.put(DatabaseTables.Client.MODIFIED, modified);

		SQLiteDatabase db = helper.getWritableDatabase();
		long id = db.insert(DatabaseTables.Client._TABLE_NAME, null, values);
		
		if (id != -1) {
			client.setId(id);
			cache.put(id, client);
		}
		
		Log.d(TAG, "EXIT: add");
	}

	@Override
	public Client getByAndroidLookupKey(String lookupKey) {
		Log.d(TAG, "ENTER: getByAndroidLookupKey(lookupKey=" + lookupKey + ")");
		
		Client client = null;
		
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.query(DatabaseTables.Client._TABLE_NAME, 
				DatabaseTables.Client._COLUMNS, 
				DatabaseTables.Client.ANDROID_CONTACT_ID + " = ?", 
				new String[]{ lookupKey  }, 
				"", "", "");
		
		if (cursor.moveToFirst()) {
			client = create(cursor);
			
			if (!cache.containsKey(client.getId()))
				cache.put(client.getId(), client);
		}

		Log.d(TAG, "EXIT: getByAndroidLookupKey");

		return client;
	}













	/**
	 * Just updates the sort and modified values of the client to the database.
	 * @param client
	 */
	private void updateSort(Client client) {
		Log.d(TAG, "ENTER: updateSort(client='" + (client == null ? "null" : client.getName()) + ")");

		int rows = 0;
		
		if (client != null) {
			client.setModified(new Date());
			
			Calendar cal = Calendar.getInstance();
			cal.setTime(client.getModified());
			long modified = cal.getTimeInMillis();

			ContentValues values = new ContentValues();
			values.put(DatabaseTables.Client.SORT, client.getSort());
			values.put(DatabaseTables.Client.MODIFIED, modified);
	
			SQLiteDatabase db = helper.getWritableDatabase();
			rows = db.update(DatabaseTables.Client._TABLE_NAME, values, 
					DatabaseTables.Client._ID + " = ?", 
					new String[] {String.valueOf(client.getId())});
		}
		
		Log.d(TAG, "EXIT: updateSort - " + rows + " row(s) updated.");
	}

	private Client getClientBySort(int sort) {
		Log.d(TAG, "ENTER: getClientBySort(sort=" + sort + ")");
		
		Client client = null;
		
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.query(DatabaseTables.Client._TABLE_NAME, 
				DatabaseTables.Client._COLUMNS, 
				DatabaseTables.Client.SORT + " = ?", 
				new String[]{String.valueOf(sort)}, 
				"", "", "");
		
		if (cursor.moveToFirst()) {
			client = create(cursor);
			cache.put(client.getId(), client);
		}

		Log.d(TAG, "EXIT: getClientBySort");

		return client;
	}

	/**
	 * Creates a client from the specified {@link Cursor}.
	 * @param cursor
	 * @return
	 */
	private Client create(Cursor cursor) {
		return create(cursor, true);
	}
	
	/**
	 * Creates a client from the specified {@link Cursor}.
	 * @param cursor
	 * @return
	 */
	private Client create(Cursor cursor, boolean closeCursor) {
		Client item;
		
		item = new Client(cursor.getLong(cursor.getColumnIndex(DatabaseTables.Client._ID)), 
				cursor.getString(cursor.getColumnIndex(DatabaseTables.Client.NAME)), 
				cursor.getString(cursor.getColumnIndex(DatabaseTables.Client.EMAIL)), 
				cursor.getString(cursor.getColumnIndex(DatabaseTables.Client.MOBILE)),
				cursor.getInt(cursor.getColumnIndex(DatabaseTables.Client.SORT)),
				cursor.getInt(cursor.getColumnIndex(DatabaseTables.Client.ACTIVE)),
				cursor.getDouble(cursor.getColumnIndex(DatabaseTables.Client.NORMAL_WORKING_HOURS)),
				cursor.getDouble(cursor.getColumnIndex(DatabaseTables.Client.HOURLY_RATE)),
				cursor.getDouble(cursor.getColumnIndex(DatabaseTables.Client.OVERTIME_MULTIPLIER)),
				cursor.getDouble(cursor.getColumnIndex(DatabaseTables.Client.MILEAGE_RATE)),
				cursor.getString(cursor.getColumnIndex(DatabaseTables.Client.ADDRESS)),
				cursor.getString(cursor.getColumnIndex(DatabaseTables.Client.ANDROID_CONTACT_ID)),
				cursor.getString(cursor.getColumnIndex(DatabaseTables.Client.ANDROID_CONTACT_EMAIL_ID)),
				cursor.getString(cursor.getColumnIndex(DatabaseTables.Client.ANDROID_CONTACT_MOBILE_ID)),
				cursor.getLong(cursor.getColumnIndex(DatabaseTables.Client.CREATED)),
				cursor.getLong(cursor.getColumnIndex(DatabaseTables.Client.MODIFIED)));
		
		if (closeCursor)
			cursor.close();
		
		return item;
	}

	/**
	 * Creates an array of clients.
	 * @param cursor
	 * @return
	 */
	private ArrayList<Client> createList(Cursor cursor) {
		ArrayList<Client> list = new ArrayList<Client>();
		
		while (cursor.moveToNext()) {
			Client item = null;
			long id = cursor.getLong(cursor.getColumnIndex(DatabaseTables.Client._ID));
			
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
