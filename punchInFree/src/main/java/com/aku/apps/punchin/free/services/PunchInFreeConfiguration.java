package com.aku.apps.punchin.free.services;

import greendroid.widget.ItemAdapter;
import greendroid.widget.item.Item;
import greendroid.widget.item.TextItem;

import java.util.ArrayList;

import com.aku.apps.punchin.free.R;
import com.aku.apps.punchin.free.utils.Constants;
import com.aku.apps.punchin.free.utils.DialogUtil;
import com.aku.apps.punchin.free.widgets.greendroid.OptionItem;
import com.aku.apps.punchin.free.widgets.greendroid.PunchInSeparatorItem;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

public class PunchInFreeConfiguration extends Activity {
	/**
	 * The tag used in log data.
	 */
	public static final String TAG = PunchInFreeConfiguration.class.getSimpleName();

	private Context self = this;
	private int appWidgetId;

	/**
	 * The currently selected frequency
	 */
	private int frequency;
	
	/**
	 * The ui to display the frequency
	 */
	private OptionItem frequencyOption;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.widget_configuration);
		extractData();
		prepareButtons();
		prepareListview();
	}

	/**
	 * Prepares the listview for viewing.
	 */
	private void prepareListview() {
		final ListView lv = (ListView) findViewById(R.id.widget_configuration_listview);
		ArrayList<Item> items = new ArrayList<Item>();

		frequencyOption = new OptionItem(getString(R.string.label_widget_refresh_rate), getFrequencyFormatted(getBaseContext(), this.frequency));
		
		items.add(new PunchInSeparatorItem(getString(R.string.label_widget_configuration)));		
		items.add(frequencyOption);
		
		lv.setAdapter(new ItemAdapter(lv.getContext(), items));
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {
				switch (position) {
				case 1: // widget frequency
					showDialog(Constants.Dialogs.SELECT_WIDGET_FREQUENCY);
					break;
				}
			}
		});
	}

	/**
	 * Tells the main listview to refresh itself.
	 */
	private void refreshListView() {
		final ListView lv = (ListView) findViewById(R.id.widget_configuration_listview);
		((ItemAdapter)lv.getAdapter()).notifyDataSetChanged();
	}

	/**
	 * Creates the config dialogs.
	 */
	@Override
	protected Dialog onCreateDialog(int id) {
		
		switch (id) {
		case Constants.Dialogs.SELECT_WIDGET_FREQUENCY:
			return createWidgetFrequencyDialog();
		}
		
		return null;
	}

	/**
	 * Creates the widget frequency dialog.
	 * @return
	 */
	private Dialog createWidgetFrequencyDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		LayoutInflater inflater = LayoutInflater.from(this);
		View view = inflater.inflate(R.layout.dialog_select_item, null);
		
		builder.setView(view);
		builder.setTitle(getString(R.string.label_widget_refresh_rate));
		
		final AlertDialog dialog = builder.create();
		final ListView lv = (ListView)view.findViewById(R.id.listview_list);	
		ArrayList<Item> items = new ArrayList<Item>();

		items.add(createTextItem(R.string.widget_every_second, Constants.WidgetFrequency.EVERY_SECOND));
		items.add(createTextItem(R.string.widget_every_minute, Constants.WidgetFrequency.EVERY_MINUTE));
		items.add(createTextItem(R.string.widget_every_15_minutes, Constants.WidgetFrequency.EVERY_15_MINUTES));
		items.add(createTextItem(R.string.widget_every_half_hour, Constants.WidgetFrequency.EVERY_HALF_HOUR));
		items.add(createTextItem(R.string.widget_every_hour, Constants.WidgetFrequency.EVERY_HOUR));
		items.add(createTextItem(R.string.widget_every_day, Constants.WidgetFrequency.EVERY_DAY));
		
		lv.setAdapter(new ItemAdapter(getBaseContext(), items));
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {
				Item item = (Item)lv.getAdapter().getItem(position);
				frequency = (Integer)item.getTag();
				frequencyOption.subtitle = getFrequencyFormatted(getBaseContext(), frequency);
				
				Log.d(TAG, "Selected frequency='" + frequencyOption.subtitle + "'");
				refreshListView();
				dialog.dismiss();
			}
		});
	    
		dialog.setOnShowListener(new OnShowListener() {			
			@Override
			public void onShow(DialogInterface d) {
				DialogUtil.setTypeFace(getBaseContext(), dialog.findViewById(android.R.id.content));
			}
		});

		return dialog;
	}

	/**
	 * Creates a simple greendroid {@link TextItem}.
	 * @param text
	 * @param tag
	 * @return
	 */
	public TextItem createTextItem(int text, Object tag) {
		TextItem item;
		item = new TextItem(getString(text));
		item.setTag(tag);
		return item;
	}

	/**
	 * Returns the widget frequency as a formatted string.
	 * @return
	 */
	public static String getFrequencyFormatted(Context context, int freq) {
		String rate = "";
		
		switch (freq) {
		case Constants.WidgetFrequency.EVERY_15_MINUTES:
			rate = context.getString(R.string.widget_every_15_minutes);
			break;
			
		case Constants.WidgetFrequency.EVERY_DAY:
			rate = context.getString(R.string.widget_every_day);
			break;
			
		case Constants.WidgetFrequency.EVERY_HALF_HOUR:
			rate = context.getString(R.string.widget_every_half_hour);
			break;
			
		case Constants.WidgetFrequency.EVERY_HOUR:
			rate = context.getString(R.string.widget_every_hour);
			break;
			
		case Constants.WidgetFrequency.EVERY_MINUTE:
			rate = context.getString(R.string.widget_every_minute);
			break;
			
		case Constants.WidgetFrequency.EVERY_SECOND:
			rate = context.getString(R.string.widget_every_second);
			break;
			
		}
		
		return rate;
	}

	/**
	 * Prepares the button click events.
	 */
	public void prepareButtons() {
		// the OK button
		Button ok = (Button) findViewById(R.id.button_save);
		ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Log.d(TAG, "ENTER: ok.click");
				
				SharedPreferences prefs = self.getSharedPreferences(getBaseContext().getString(R.string.app_name), 0);				
				SharedPreferences.Editor edit = prefs.edit();
				edit.putInt(Constants.WidgetFrequency._NAME, frequency);
				edit.commit();
				
				// fire an update to display initial state of the widget
				Intent intent = new Intent(PunchInFreeWidgetProvider.ACTION_TASK_CHANGED);
				PendingIntent updatePending = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
				
				try {
					updatePending.send();
					
				} catch (CanceledException e) {
					e.printStackTrace();
					
				}
				
				Log.d(TAG, "EXIT: ok.click");

				// change the result to OK
				Intent resultValue = new Intent();
				resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
				setResult(RESULT_OK, resultValue);
				finish();
			}
		});

		// cancel button
		Button cancel = (Button) findViewById(R.id.button_cancel);
		cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
	}

	/**
	 * Extracts the app widget id from the intent.
	 */
	public void extractData() {
		// get the appWidgetId of the appWidget being configured
		Intent launchIntent = getIntent();
		Bundle extras = launchIntent.getExtras();
		appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

		// set the result for cancel first
		Intent cancelResultValue = new Intent();
		cancelResultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
		setResult(RESULT_CANCELED, cancelResultValue);

		SharedPreferences prefs = self.getSharedPreferences(self.getString(R.string.app_name), 0);
		this.frequency = prefs.getInt(Constants.WidgetFrequency._NAME, 
				Constants.WidgetFrequency.EVERY_MINUTE);
	}
	
}
