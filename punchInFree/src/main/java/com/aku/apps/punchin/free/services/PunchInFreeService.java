package com.aku.apps.punchin.free.services;

import java.util.Date;

import com.aku.apps.punchin.free.MainActivity;
import com.aku.apps.punchin.free.R;
import com.aku.apps.punchin.free.db.DatasourceFactory;
import com.aku.apps.punchin.free.db.DatasourceFactoryFacade;
import com.aku.apps.punchin.free.db.TimeStampFactory;
import com.aku.apps.punchin.free.domain.Client;
import com.aku.apps.punchin.free.domain.Task;
import com.aku.apps.punchin.free.domain.TimeFormatter;
import com.aku.apps.punchin.free.domain.TimeStamp;
import com.aku.apps.punchin.free.domain.TimeStamp.TimeStampType;
import com.aku.apps.punchin.free.utils.DateUtil;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

public class PunchInFreeService extends Service {
	/**
	 * The tag used in log data.
	 */
	public static final String TAG = PunchInFreeService.class.getSimpleName();

	public static final String UPDATE = "com.aku.apps.punchin.free.services.PunchInFreeService.UPDATE";

	@Override
	public void onStart(Intent intent, int startId) {
		Log.d(TAG, "ENTER: onStart");

		int appWidgetId = intent.getExtras().getInt(AppWidgetManager.EXTRA_APPWIDGET_ID);
		Log.d(TAG, "appWidgetId=" + appWidgetId);
		
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());

    	updateAppWidget(getApplicationContext(), appWidgetManager, appWidgetId);

        super.onStart(intent, startId);

        Log.d(TAG, "EXIT: onStart");
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}


	public static void updateAppWidget(Context context, AppWidgetManager manager, int appWidgetId) {
		Log.d(TAG, "ENTER: updateAppWidget");

		String clientName = context.getString(R.string.label_no_client_selected);
		String taskDescription = context.getString(R.string.label_no_task_selected);
		
		DatasourceFactory datasource = DatasourceFactoryFacade.getInstance(context);
    	TimeFormatter timeFormat = datasource.createDefaultTimeFormat();
		String time = timeFormat.formatTime(0);

    	TimeStampFactory timeStamps = datasource.createTimeStampFactory();
		timeStamps.checkActive();
		
		TimeStamp ts = timeStamps.getActive();
		Task task = null;
		Client client = null;
		
		if (ts != null) {	    	
	    	task = timeStamps.getTask(ts);
			client = datasource.createClientFactory().get(task.getClientId());

			long timeInMillis = timeStamps.getTimeSpentOnTask(task, new Date());
			
			if (ts.getType() == TimeStampType.PunchIn) {
				// this is the active task, need to add the current elapsed time
				Date fromDate = ts.getTime();
				long curTimeInMillis = DateUtil.getMilliseconds(fromDate, new Date());
				timeInMillis += curTimeInMillis;
			}
			
	    	time = timeFormat.formatTime(timeInMillis);
			
			if (client != null)
				clientName = client.getName();
			
			if (task != null) {
				taskDescription = task.getDescription();
				
				if (client == null)
					clientName = context.getString(R.string.label_no_client);
			}
			
			Log.v(TAG, "task='" + taskDescription + "', client='" + clientName + "', time='" + time + "'");
			
		} else {
			Log.v(TAG, "No active timestamp found");
			
		}
		
		RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
		views.setTextViewText(R.id.clientTextview, clientName);
		views.setTextViewText(R.id.taskTextview, taskDescription);
		views.setImageViewBitmap(R.id.timeImageView, createTimeBitmap(context, time));

		setStopWatchButtonClick(context, views);   
		
		manager.updateAppWidget(appWidgetId, views);

		Log.v(TAG, "EXIT: updateAppWidget");
	}

	/**
	 * Sets the button click on the stopwatch.
	 * @param context
	 * @param views
	 */
	public static void setStopWatchButtonClick(Context context, RemoteViews views) {
		Log.v(TAG, "ENTER: setStopWatchButtonClick");
		
		Intent intent = new Intent(Intent.ACTION_MAIN, null);
	    intent.addCategory(Intent.CATEGORY_LAUNCHER);
	    ComponentName cn = new ComponentName("com.aku.apps.punchin.free", MainActivity.class.getName());
	    intent.setComponent(cn);
	    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    PendingIntent actionPendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
	    views.setOnClickPendingIntent(R.id.stopwatchImageView, actionPendingIntent);
		
	    Log.v(TAG, "EXIT: setStopWatchButtonClick");
	}

	private static Bitmap createTimeBitmap(Context context, String time) {
        Bitmap bitmap = Bitmap.createBitmap(60, 20, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        Typeface clock = Typeface.createFromAsset(context.getAssets(),"fonts/LCD-BOLD.TTF");
        paint.setAntiAlias(true);
        paint.setSubpixelText(true);
        paint.setTypeface(clock);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLACK);
        paint.setTextSize((float) 15);
        canvas.drawText(time, 2, 13, paint);

        return bitmap;
	}
}
