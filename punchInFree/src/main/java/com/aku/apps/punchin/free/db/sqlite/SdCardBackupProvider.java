package com.aku.apps.punchin.free.db.sqlite;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.aku.apps.punchin.free.R;
import com.aku.apps.punchin.free.db.BackupProvider;
import com.aku.apps.punchin.free.domain.Checkpoint;
import com.aku.apps.punchin.free.domain.ProgressListener;
import com.aku.apps.punchin.free.utils.Constants;
import com.aku.apps.punchin.free.utils.FileUtil;

public class SdCardBackupProvider implements BackupProvider {
	/**
	 * Log tag.
	 */
	private final static String TAG = SdCardBackupProvider.class.getSimpleName();
	
	/**
	 * Cached checkpoints.
	 */
	private static Hashtable<Long, Checkpoint> cache = new Hashtable<Long, Checkpoint>();

	/**
	 * The database helper.
	 */
	private DatabaseHelper helper = null;
	
	/**
	 * Creates a {@link SdCardBackupProvider}.
	 * @param helper
	 */
	public SdCardBackupProvider(DatabaseHelper helper) {
		super();
		this.helper = helper;
	}

	@Override
	public void clearCache() {
		cache.clear();
	}

	@Override
	public long getId() {
		return Constants.BackupProviders.SD_CARD;
	}

	@Override
	public int getName() {
		return R.string.label_sd_card;
	}

	@Override
	public Checkpoint backup(String description, ProgressListener progress) {
		Log.d(TAG, "ENTER: backup");

		boolean rc = false;
	    String name = createFilename();
	    
	    boolean writeable = FileUtil.canWriteToSDCard();
	    if (writeable) {
	      File file = new File(Constants.Defaults.FILE_LOCATION_DATABASE);

	      File fileBackupDir = new File(Constants.Defaults.FOLDER_LOCATION_BACKUP);
	      if (!fileBackupDir.exists())
	        fileBackupDir.mkdirs();

	      if (file.exists()) {
	        File fileBackup = new File(fileBackupDir, name);
	        
	        try {
	          fileBackup.createNewFile();
	          FileUtil.copyFile(file, fileBackup);
	          rc = true;
	          
	        } catch (IOException e) {
	          e.printStackTrace();
	          
	        } catch (Exception e) {
	          e.printStackTrace();
	          
	        }
	      }
	    }
	    
	    Checkpoint cp = null;
	    
	    if (rc)
	    	cp = add(name, description, null);

		Log.d(TAG, "EXIT: backup");

		return cp;
	}

	
	@Override
	public void restore(Checkpoint chk, ProgressListener progress) {
		Log.d(TAG, "ENTER: restore");

		String name = chk.getName();
	    boolean writeable = FileUtil.canWriteToSDCard();
	    if (writeable) {
			File file = new File(Constants.Defaults.FOLDER_LOCATION_BACKUP
					+ name);
			File fileRestore = new File(
					Constants.Defaults.FILE_LOCATION_DATABASE);

			if (file.exists()) {
				try {
					fileRestore.createNewFile();
					FileUtil.copyFile(file, fileRestore);
					file.delete();

				} catch (IOException e) {
					e.printStackTrace();

				} catch (Exception e) {
					e.printStackTrace();

				}
			}
	    }
	    
		Log.d(TAG, "EXIT: restore");
	}

	@Override
	public ArrayList<Checkpoint> getCheckpoints() {
		Log.d(TAG, "ENTER: getCheckpoints");
		
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.query(DatabaseTables.Checkpoint._TABLE_NAME, 
				DatabaseTables.Checkpoint._COLUMNS, 
				"", new String[]{ }, "", "", 
				DatabaseTables.Checkpoint.CREATED);
		
		Log.d(TAG, "EXIT: getCheckpoints");
		
		return createList(cursor);
	}

	@Override
	public int getCheckPointCount() {
		Log.d(TAG, "ENTER: getCheckPointCount");
		
		int count = 0;
		
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select count(*) from check_point", new String[] {});
		
		if (cursor.moveToFirst())
			count = cursor.getInt(0);

		Log.d(TAG, "EXIT: getCheckPointCount");

		return count;
	}


























	
	
	private Checkpoint add(String name, String description, String extraData) {
		Log.d(TAG, "ENTER: add(name='" + (name == null ? "null" : name) + "', extraData='" + (extraData == null ? "null" : extraData) + "')");

		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		long created = cal.getTimeInMillis();
		long modified = cal.getTimeInMillis();
		long date = cal.getTimeInMillis();
		
		ContentValues values = new ContentValues();
		values.put(DatabaseTables.Checkpoint.DATE, date);
		values.put(DatabaseTables.Checkpoint.NAME, name);
		
		if (extraData == null || extraData.length() == 0)
			values.putNull(DatabaseTables.Checkpoint.EXTRA_DATA);
		else
			values.put(DatabaseTables.Checkpoint.EXTRA_DATA, extraData);
		
		values.put(DatabaseTables.Checkpoint.CREATED, created);
		values.put(DatabaseTables.Checkpoint.MODIFIED, modified);
		values.put(DatabaseTables.Checkpoint.DESCRIPTION, description);

		SQLiteDatabase db = helper.getWritableDatabase();
		long id = db.insert(DatabaseTables.Checkpoint._TABLE_NAME, null, values);
		Checkpoint cp = null;
		
		if (id != -1) {
			cp = new Checkpoint(id, name, date, extraData, created, modified, description);
			cache.put(id, cp);
		}
		
		Log.d(TAG, "EXIT: add");

		return cp;
	}

	private String createFilename() {
		Date date = new Date();
		String name = DateFormat.getDateInstance(DateFormat.LONG).format(date);
		name += "_" + DateFormat.getTimeInstance(DateFormat.LONG).format(date);
		name = FileUtil.formatForFilename(name);
		
		return name;
	}

  /**
	 * Creates a client from the specified {@link Cursor}.
	 * @param cursor
	 * @return
	 */
	private Checkpoint create(Cursor cursor, boolean closeCursor) {
		Checkpoint item;
		
		item = new Checkpoint(cursor.getLong(cursor.getColumnIndex(DatabaseTables.Checkpoint._ID)), 
				cursor.getString(cursor.getColumnIndex(DatabaseTables.Checkpoint.NAME)),
				cursor.getLong(cursor.getColumnIndex(DatabaseTables.Checkpoint.DATE)),
				cursor.getString(cursor.getColumnIndex(DatabaseTables.Checkpoint.EXTRA_DATA)),
				cursor.getLong(cursor.getColumnIndex(DatabaseTables.Checkpoint.CREATED)),
				cursor.getLong(cursor.getColumnIndex(DatabaseTables.Checkpoint.MODIFIED)),
				cursor.getString(cursor.getColumnIndex(DatabaseTables.Checkpoint.DESCRIPTION)));

		if (closeCursor)
			cursor.close();
		
		return item;
	}

	/**
	 * Creates an array of checkpoints.
	 * @param cursor
	 * @return
	 */
	private ArrayList<Checkpoint> createList(Cursor cursor) {
		ArrayList<Checkpoint> list = new ArrayList<Checkpoint>();
		
		while (cursor.moveToNext()) {
			Checkpoint item = null;
			long id = cursor.getLong(cursor.getColumnIndex(DatabaseTables.Checkpoint._ID));
			
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
