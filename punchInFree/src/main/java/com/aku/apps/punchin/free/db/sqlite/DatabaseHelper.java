package com.aku.apps.punchin.free.db.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
	public static final String DATABASE_NAME = "punchin.db";
	public static final int DATABASE_VERSION = 1;
	
	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DatabaseTables.Preferences._TABLE_CREATE);
		db.execSQL(DatabaseTables.Day._TABLE_CREATE);
		db.execSQL(DatabaseTables.Client._TABLE_CREATE);
		db.execSQL(DatabaseTables.Checkpoint._TABLE_CREATE);
		db.execSQL(DatabaseTables.Task._TABLE_CREATE);
		db.execSQL(DatabaseTables.TaskDay._TABLE_CREATE);
		db.execSQL(DatabaseTables.TimeStamp._TABLE_CREATE);
		db.execSQL(DatabaseTables.CalendarEvent._TABLE_CREATE);
		db.execSQL(DatabaseTables.Expense._TABLE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}		
}