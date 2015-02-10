package com.aku.apps.punchin.free.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.channels.FileChannel;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Environment;
import android.util.Log;

public class FileUtil {
	/**
	 * Tag used in log messages.
	 */
	public final static String TAG = FileUtil.class.getSimpleName();

	/**
	 * Check to make sure that this is a valid sqlite file.
	 * @param filePath
	 * @throws InvalidSQLiteFileException 
	 */
	public static void checkSQLiteFile(File db) throws InvalidSQLiteFileException {
		try {
			SQLiteDatabase sqlDb = SQLiteDatabase.openDatabase
				(db.getPath(), null, SQLiteDatabase.OPEN_READONLY);
 
			Cursor cursor = sqlDb.query(true, "preferences",
					null, null, null, null, null, null, null
            );
 
			// ALL_COLUMN_KEYS should be an array of keys of essential columns.
			// Throws exception if any column is missing
			for ( String s : new String[] {"_id", "created", "modified"} ){
				cursor.getColumnIndexOrThrow(s);
			}
 
			sqlDb.close();
			cursor.close();

		} catch( IllegalArgumentException e ) {
			Log.d(TAG, "Database valid but not the right type");
			e.printStackTrace();
			throw new InvalidSQLiteFileException(e.getLocalizedMessage());
			
		} catch( SQLiteException e ) {
			Log.d(TAG, "Database file is invalid");
			e.printStackTrace();
			throw new InvalidSQLiteFileException(e.getLocalizedMessage());
			
		} catch( Exception e){
			Log.d(TAG, "checkSQLiteFile encountered an exception");
			e.printStackTrace();
			throw new InvalidSQLiteFileException(e.getLocalizedMessage());
			
		}
 	}
	
	/**
	 * Checks whether the app can write to the sd card or not.
	 * @return
	 */
	public static boolean canWriteToSDCard() {
		boolean rc = false;
		
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			rc = true;
		}
		
		return rc;
  }

	/**
	 * Returns the text file referenced by the specified resource id.
	 * @param ctx
	 * @param resId
	 * @return
	 */
	public static String readRawTextFile(Context ctx, int resId)
	{
		InputStream inputStream = ctx.getResources().openRawResource(resId);
		
		InputStreamReader inputreader = new InputStreamReader(inputStream);
		BufferedReader buffreader = new BufferedReader(inputreader);
		String line;
		StringBuilder text = new StringBuilder();
		
		try {
			while (( line = buffreader.readLine()) != null) {
				text.append(line);
				text.append('\n');
			}
		} catch (IOException e) {
		    return null;
		}
		
		return text.toString();
	}

	/**
	 * Returns a lower case version of the specified name, with any spaces replaced with underscores.
	 * @param name
	 * @return
	 */
	public static String formatForFilename(String name) {
		String temp = name.toLowerCase();
		temp = temp.replace(" ", "_");
		temp = temp.replace(":", "_");
		temp = temp.replace(",", "");
		
		return temp;
	}

	/**
	 * Writes the contents to the specified file.
	 * @param folderPath
	 * @param fileName
	 * @param contents
	 * @throws IOException
	 */
	public static void write(String folderPath, String fileName, String contents) throws IOException {
		Log.d(TAG, "write(folderPath=" + folderPath + ", " +
				"fileName = " + fileName + ")");

		String tempFileName = folderPath + fileName;
		
		java.io.File f = new java.io.File(folderPath);
		f.mkdirs();
		
		FileWriter writer = new FileWriter(tempFileName);
		writer.write(contents);
		writer.flush();
		writer.close();
	}

	/**
	 * Copies the source file to the destination file.
	 * @param source
	 * @param destination
	 * @throws IOException
	 */
	public static void copyFile(File source, File destination) throws IOException {
        if (source.exists()) {
            FileChannel src = new FileInputStream(source).getChannel();
            FileChannel dst = new FileOutputStream(destination).getChannel();
            dst.transferFrom(src, 0, src.size()); 
            src.close();
            dst.close();
        }
	}
}
