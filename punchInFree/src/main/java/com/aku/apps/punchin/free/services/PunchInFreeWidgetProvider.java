package com.aku.apps.punchin.free.services;

import com.aku.apps.punchin.free.R;
import com.aku.apps.punchin.free.db.DatasourceFactory;
import com.aku.apps.punchin.free.db.DatasourceFactoryFacade;
import com.aku.apps.punchin.free.db.TimeStampFactory;
import com.aku.apps.punchin.free.domain.TimeStamp;
import com.aku.apps.punchin.free.utils.Constants;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.SystemClock;
import android.util.Log;
import android.widget.RemoteViews;

public class PunchInFreeWidgetProvider extends AppWidgetProvider {
	/**
	 * The tag used in log data.
	 */
	public static final String TAG = PunchInFreeWidgetProvider.class.getSimpleName();
	
	/**
	 * The main activity tells the widget that the task has changed, i.e, refresh the widget views.
	 */
	public static final String ACTION_TASK_CHANGED = "com.aku.apps.punchin.free.services.PunchInFreeWidgetProvider.ACTION_TASK_CHANGED";

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		Log.d(TAG, "ENTER: onUpdate(appWidgetIds=" + appWidgetIds.toString() + ")");
		
	    for (int appWidgetId : appWidgetIds)
			setAlarm(context, appWidgetId);

	    Log.d(TAG, "EXIT: onUpdate");
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "ENTER: onReceive(intent action=" + (intent.getAction() == null ? "null" : intent.getAction()) + ")");
		
	    final String action = intent.getAction();
	    
        if (AppWidgetManager.ACTION_APPWIDGET_DELETED.equals(action)) {
            final int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, 
            		AppWidgetManager.INVALID_APPWIDGET_ID);
            
            if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                this.onDeleted(context, new int[] { appWidgetId });
            }
            
        } else {
			AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
			ComponentName thisAppWidget = new ComponentName(context, PunchInFreeWidgetProvider.class);

			int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);

			if (intent.getAction().equals(ACTION_TASK_CHANGED)) {
				onUpdate(context, appWidgetManager, appWidgetIds);            
            
            } else {
                super.onReceive(context, intent);
            
            }            
        }

        Log.d(TAG, "EXIT: onReceive");
	}

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		Log.d(TAG, "ENTER: onDeleted");

		for (int appWidgetId : appWidgetIds)    
            setAlarm(context, appWidgetId, -1);

        super.onDeleted(context, appWidgetIds);
        
		Log.d(TAG, "EXIT: onDeleted");
	}

	@Override
	public void onDisabled(Context context) {
        Log.d(TAG, "ENTER: onDisabled");

        context.stopService(new Intent(context, PunchInFreeService.class));
        super.onDisabled(context);
        
        Log.d(TAG, "EXIT: onDisabled");
	}
	
	public static void setAlarm(Context context, int appWidgetId) {
		SharedPreferences prefs = context.getSharedPreferences(context.getString(R.string.app_name), 0);
		int frequency = prefs.getInt(Constants.WidgetFrequency._NAME, Constants.WidgetFrequency.EVERY_MINUTE);		
		long updateRate = getUpdateRate(frequency);
		
		setAlarm(context, appWidgetId, updateRate);
	}
	
	public static void setAlarm(Context context, int appWidgetId, long updateRate) {
        Log.d(TAG, "ENTER: setAlarm");
		DatasourceFactory datasource = DatasourceFactoryFacade.getInstance(context);
		TimeStampFactory timeStamps = datasource.createTimeStampFactory();
		timeStamps.checkActive();

		TimeStamp ts = timeStamps.getActive();
        PendingIntent newPending = makeControlPendingIntent(context, PunchInFreeService.UPDATE, appWidgetId);
        Log.d(TAG, "pendingIntent=" + newPending.toString());
        AlarmManager alarms = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

		if (ts != null) {
			if (updateRate >= 0) {
	            alarms.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), updateRate, newPending);
	            
	        } else {
	        	Log.d(TAG, "updateRate=" + updateRate + ", cancelling the alarm.");
	            cancelAlarms(alarms, newPending, context, appWidgetId);
	        }
		} else {
        	Log.d(TAG, "No timestamp found, cancelling the alarm.");
            cancelAlarms(alarms, newPending, context, appWidgetId);

		}
		
        Log.d(TAG, "EXIT: setAlarm");
    }
	
	/**
	 * Cancels the alarm intent, sets the stopwatch button click and refreshes the ui.
	 * 
	 * This is invoked when the alarms need to be cancelled because there is no timestamp, i.e,
	 * a waste of energy repeating the alarm for no reason.
	 * 
	 * @param alarms
	 * @param newPending
	 * @param context
	 * @param appWidgetId
	 */
	private static void cancelAlarms(AlarmManager alarms, PendingIntent newPending, Context context, int appWidgetId) {
    	// on a negative updateRate stop the refreshing 
        alarms.cancel(newPending);
        
		RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
        PunchInFreeService.setStopWatchButtonClick(context, views);
        alarms.set(AlarmManager.ELAPSED_REALTIME, 0, newPending);
        
        AppWidgetManager.getInstance(context).updateAppWidget(appWidgetId, views);
	}

	/**
	 * Returns the update rate given the specified widget frequency.
	 * @param frequency
	 * @return
	 */
	private static long getUpdateRate(int frequency) {
        Log.d(TAG, "ENTER: getUpdateRate(frequency=" + frequency + ")");

		long rate = 0;
		
		final long second = 1000;
		final long minute = 60000;
		final long hour = minute * 60;
		final long day = hour * 24;
		
		switch (frequency) {
		case Constants.WidgetFrequency.EVERY_15_MINUTES:
			rate = minute * 15;
			break;
			
		case Constants.WidgetFrequency.EVERY_DAY:
			rate = day;
			break;
			
		case Constants.WidgetFrequency.EVERY_HALF_HOUR:
			rate = minute * 30;
			break;
			
		case Constants.WidgetFrequency.EVERY_HOUR:
			rate = 60 * minute;
			break;
			
		case Constants.WidgetFrequency.EVERY_MINUTE:
			rate = minute;
			break;
			
		case Constants.WidgetFrequency.EVERY_SECOND:
			rate = second;
			break;
			
		}
		
        Log.d(TAG, "ENTER: getUpdateRate(rate=" + rate + ")");

		return rate;
	}

	public static PendingIntent makeControlPendingIntent(Context context, String command, int appWidgetId) {
        Log.d(TAG, "ENTER: makeControlPendingIntent");

        Intent intent = new Intent(context, PunchInFreeService.class);
        intent.setAction(command);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        //this Uri data is to make the PendingIntent unique, so it wont be updated by FLAG_UPDATE_CURRENT
        //so if there are multiple widget instances they wont override each other
        Uri data = Uri.withAppendedPath(Uri.parse("freepunchinwidget://widget/id/#"+command+appWidgetId), String.valueOf(appWidgetId));
        intent.setData(data);
        
        Log.d(TAG, "EXIT: makeControlPendingIntent(intent='" + intent.toString() + "'");
        
        return PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
