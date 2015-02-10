package com.aku.apps.punchin.free.db.sqlite;

import java.util.Calendar;
import java.util.Date;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.aku.apps.punchin.free.db.PreferenceFactory;
import com.aku.apps.punchin.free.domain.Preferences;
import com.aku.apps.punchin.free.utils.ArrayListUtil;

public class SQLitePreferenceFactory implements PreferenceFactory {
	/**
	 * Log tag.
	 */
	private final static String TAG = SQLitePreferenceFactory.class.getSimpleName();

	/**
	 * The list of system preferences
	 */
	private Preferences preferences = null;
	
	/**
	 * Database helper.
	 */
	private DatabaseHelper helper;
	
	public SQLitePreferenceFactory(Context context, DatabaseHelper helper) {
		super();
		
		this.helper = helper;
		checkPreferencesExist(context);
	}
	
	/**
	 * Ensures that the preferences exist on startup.
	 */
	private void checkPreferencesExist(Context context) {
		Preferences prefs = get();
		
		if (prefs == null) {
			SQLiteDatabase dbWriter = helper.getWritableDatabase();
			dbWriter.execSQL(DatabaseTables.Preferences.getInsert(context));
		}
	}

	@Override
	public void clearCache() {
		this.preferences = null;
	}

	@Override
	public Preferences get() {
		Log.d(TAG, "ENTER: get");
		
		Preferences item = this.preferences;
		
		if (item == null) {
			SQLiteDatabase db = helper.getReadableDatabase();
			Cursor cursor = db.query(DatabaseTables.Preferences._TABLE_NAME, 
					DatabaseTables.Preferences._COLUMNS, 
					"", 
					new String[]{  }, 
					"", "", "");
			
			if (cursor.moveToFirst()) {
				item = create(cursor);
				this.preferences = item;
				
			} 
		}
			
		Log.d(TAG, "EXIT: get");

		return item;
	}

	@Override
	public void update(Preferences item) {
		Log.d(TAG, "ENTER: update");

		int rows = 0;
		
		if (item != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			long modified = cal.getTimeInMillis();
			
			ContentValues values = new ContentValues();
			values.put(DatabaseTables.Preferences.DEFAULT_HOURLY_RATE, item.getDefaultHourlyRate());
			values.put(DatabaseTables.Preferences.DEFAULT_TIME_FORMAT_ID, item.getDefaultTimeFormatId());
			values.put(DatabaseTables.Preferences.DEFAULT_DATE_FORMAT, item.getDefaultDateFormat());
			values.put(DatabaseTables.Preferences.DEFAULT_NORMAL_WORKING_HOURS, item.getDefaultNormalWorkingHours());
			values.put(DatabaseTables.Preferences.DEFAULT_OVERTIME_MULTIPLIER, item.getDefaultOvertimeMultiplier());
			values.put(DatabaseTables.Preferences.DEFAULT_CLIENT_ID, item.getDefaultClientId());
			values.put(DatabaseTables.Preferences.DEFAULT_MILEAGE_UNIT, item.getDefaultMileageUnit());
			values.put(DatabaseTables.Preferences.DEFAULT_MILEAGE_RATE, item.getDefaultMileageRate());
			values.put(DatabaseTables.Preferences.DEFAULT_REPORT_FORMAT_ID, item.getDefaultReportFormatId());
			values.put(DatabaseTables.Preferences.DEFAULT_REPORT_SENDER_ID, item.getDefaultReportSenderId());
			values.put(DatabaseTables.Preferences.ASK_BEFORE_REMOVING_TIME, item.getAskBeforeRemovingTime() ? 1 : 0);
			values.put(DatabaseTables.Preferences.COLUMNS_TIMESHEET, ArrayListUtil.join(item.getTimesheetReportColumns()));
			values.put(DatabaseTables.Preferences.COLUMNS_INCOME, ArrayListUtil.join(item.getIncomeReportColumns()));
			values.put(DatabaseTables.Preferences.COLUMNS_CLIENT_LIST, ArrayListUtil.join(item.getClientListReportColumns()));
			values.put(DatabaseTables.Preferences.COLUMNS_TASK_LIST, ArrayListUtil.join(item.getTaskListReportColumns()));
			values.put(DatabaseTables.Preferences.DEFAULT_BACKUP_PROVIDER_ID, item.getDefaultBackupProviderId());
			values.put(DatabaseTables.Preferences.DEFAULT_ACCOUNT_TYPE, item.getDefaultAccountType());
			values.put(DatabaseTables.Preferences.DEFAULT_ACCOUNT_NAME, item.getDefaultAccountName());
			values.put(DatabaseTables.Preferences.DEFAULT_CALENDAR_ID, item.getDefaultCalendarId());
			values.put(DatabaseTables.Preferences.DEFAULT_CALENDAR_NAME, item.getDefaultCalendarName());
			
			if (item.getHttpPostUrl() == null || item.getHttpPostUrl().length() == 0)
				values.putNull(DatabaseTables.Preferences.HTTP_POST_URL);
			else
				values.put(DatabaseTables.Preferences.HTTP_POST_URL, item.getHttpPostUrl());
			
			// report sender ftp data
			
			if (item.getReportSenderFtpPassword() == null || item.getReportSenderFtpPassword().length() == 0)
				values.putNull(DatabaseTables.Preferences.REPORT_SENDER_FTP_PASSWORD);
			else
				values.put(DatabaseTables.Preferences.REPORT_SENDER_FTP_PASSWORD, item.getReportSenderFtpPassword());
			
			if (item.getReportSenderFtpServerName() == null || item.getReportSenderFtpServerName().length() == 0)
				values.putNull(DatabaseTables.Preferences.REPORT_SENDER_FTP_SERVERNAME);
			else
				values.put(DatabaseTables.Preferences.REPORT_SENDER_FTP_SERVERNAME, item.getReportSenderFtpServerName());
			
			if (item.getReportSenderFtpUsername() == null || item.getReportSenderFtpUsername().length() == 0)
				values.putNull(DatabaseTables.Preferences.REPORT_SENDER_FTP_USERNAME);
			else
				values.put(DatabaseTables.Preferences.REPORT_SENDER_FTP_USERNAME, item.getReportSenderFtpUsername());
			
			if (item.getReportSenderFtpSubFolder() == null || item.getReportSenderFtpSubFolder().length() == 0)
				values.putNull(DatabaseTables.Preferences.REPORT_SENDER_FTP_SUBFOLDER);
			else
				values.put(DatabaseTables.Preferences.REPORT_SENDER_FTP_SUBFOLDER, item.getReportSenderFtpSubFolder());			
			
			// backup provider ftp data
			
			if (item.getBackupProviderFtpPassword() == null || item.getBackupProviderFtpPassword().length() == 0)
				values.putNull(DatabaseTables.Preferences.BACKUP_PROVIDER_FTP_PASSWORD);
			else
				values.put(DatabaseTables.Preferences.BACKUP_PROVIDER_FTP_PASSWORD, item.getBackupProviderFtpPassword());
			
			if (item.getBackupProviderFtpServerName() == null || item.getBackupProviderFtpServerName().length() == 0)
				values.putNull(DatabaseTables.Preferences.BACKUP_PROVIDER_FTP_SERVERNAME);
			else
				values.put(DatabaseTables.Preferences.BACKUP_PROVIDER_FTP_SERVERNAME, item.getBackupProviderFtpServerName());
			
			if (item.getBackupProviderFtpUsername() == null || item.getBackupProviderFtpUsername().length() == 0)
				values.putNull(DatabaseTables.Preferences.BACKUP_PROVIDER_FTP_USERNAME);
			else
				values.put(DatabaseTables.Preferences.BACKUP_PROVIDER_FTP_USERNAME, item.getBackupProviderFtpUsername());
			
			if (item.getBackupProviderFtpSubFolder() == null || item.getBackupProviderFtpSubFolder().length() == 0)
				values.putNull(DatabaseTables.Preferences.BACKUP_PROVIDER_FTP_SUBFOLDER);
			else
				values.put(DatabaseTables.Preferences.BACKUP_PROVIDER_FTP_SUBFOLDER, item.getBackupProviderFtpSubFolder());

			values.put(DatabaseTables.Expense.MODIFIED, modified);

			SQLiteDatabase db = helper.getWritableDatabase();
			rows = db.update(DatabaseTables.Preferences._TABLE_NAME, values, 
					DatabaseTables.Preferences._ID + " = ?", 
					new String[] {String.valueOf(item.getId())});
		}
		
		Log.d(TAG, "EXIT: update - " + rows + " row(s) updated.");
	}












	/**
	 * Creates a client from the specified {@link Cursor}.
	 * @param cursor
	 * @return
	 */
	private Preferences create(Cursor cursor) {
		return create(cursor, true);
	}

	/**
	 * Creates a client from the specified {@link Cursor}.
	 * @param cursor
	 * @return
	 */
	private Preferences create(Cursor cursor, boolean closeCursor) {
		Preferences item;
		
		item = new Preferences(cursor.getLong(cursor.getColumnIndex(DatabaseTables.Preferences._ID)), 
				cursor.getInt(cursor.getColumnIndex(DatabaseTables.Preferences.ASK_BEFORE_REMOVING_TIME)),
				cursor.getInt(cursor.getColumnIndex(DatabaseTables.Preferences.DEFAULT_TIME_FORMAT_ID)),
				cursor.getDouble(cursor.getColumnIndex(DatabaseTables.Preferences.DEFAULT_HOURLY_RATE)),
				cursor.getString(cursor.getColumnIndex(DatabaseTables.Preferences.DEFAULT_DATE_FORMAT)),
				cursor.getDouble(cursor.getColumnIndex(DatabaseTables.Preferences.DEFAULT_NORMAL_WORKING_HOURS)),
				cursor.getDouble(cursor.getColumnIndex(DatabaseTables.Preferences.DEFAULT_OVERTIME_MULTIPLIER)),
				cursor.getLong(cursor.getColumnIndex(DatabaseTables.Preferences.DEFAULT_CLIENT_ID)),
				cursor.getString(cursor.getColumnIndex(DatabaseTables.Preferences.DEFAULT_MILEAGE_UNIT)),
				cursor.getDouble(cursor.getColumnIndex(DatabaseTables.Preferences.DEFAULT_MILEAGE_RATE)),
				cursor.getInt(cursor.getColumnIndex(DatabaseTables.Preferences.DEFAULT_REPORT_FORMAT_ID)),
				cursor.getInt(cursor.getColumnIndex(DatabaseTables.Preferences.DEFAULT_REPORT_SENDER_ID)),
				cursor.getString(cursor.getColumnIndex(DatabaseTables.Preferences.COLUMNS_TIMESHEET)),
				cursor.getString(cursor.getColumnIndex(DatabaseTables.Preferences.COLUMNS_INCOME)),
				cursor.getString(cursor.getColumnIndex(DatabaseTables.Preferences.COLUMNS_CLIENT_LIST)),
				cursor.getString(cursor.getColumnIndex(DatabaseTables.Preferences.COLUMNS_TASK_LIST)),
				cursor.getLong(cursor.getColumnIndex(DatabaseTables.Preferences.DEFAULT_BACKUP_PROVIDER_ID)),
				cursor.getString(cursor.getColumnIndex(DatabaseTables.Preferences.DEFAULT_ACCOUNT_NAME)),
				cursor.getString(cursor.getColumnIndex(DatabaseTables.Preferences.DEFAULT_ACCOUNT_TYPE)),
				cursor.getString(cursor.getColumnIndex(DatabaseTables.Preferences.DEFAULT_CALENDAR_ID)),
				cursor.getString(cursor.getColumnIndex(DatabaseTables.Preferences.DEFAULT_CALENDAR_NAME)),
				cursor.getLong(cursor.getColumnIndex(DatabaseTables.Preferences.CREATED)),
				cursor.getLong(cursor.getColumnIndex(DatabaseTables.Preferences.MODIFIED)),
				cursor.getString(cursor.getColumnIndex(DatabaseTables.Preferences.HTTP_POST_URL)),
				cursor.getString(cursor.getColumnIndex(DatabaseTables.Preferences.REPORT_SENDER_FTP_USERNAME)),
				cursor.getString(cursor.getColumnIndex(DatabaseTables.Preferences.REPORT_SENDER_FTP_PASSWORD)),
				cursor.getString(cursor.getColumnIndex(DatabaseTables.Preferences.REPORT_SENDER_FTP_SERVERNAME)),
				cursor.getString(cursor.getColumnIndex(DatabaseTables.Preferences.REPORT_SENDER_FTP_SUBFOLDER)),
				cursor.getString(cursor.getColumnIndex(DatabaseTables.Preferences.BACKUP_PROVIDER_FTP_USERNAME)),
				cursor.getString(cursor.getColumnIndex(DatabaseTables.Preferences.BACKUP_PROVIDER_FTP_PASSWORD)),
				cursor.getString(cursor.getColumnIndex(DatabaseTables.Preferences.BACKUP_PROVIDER_FTP_SERVERNAME)),
				cursor.getString(cursor.getColumnIndex(DatabaseTables.Preferences.BACKUP_PROVIDER_FTP_SUBFOLDER)));
		
		if (closeCursor)
			cursor.close();

		return item;
	}
}
