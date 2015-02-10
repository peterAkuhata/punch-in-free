package com.aku.apps.punchin.free;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.aku.apps.punchin.free.db.DatasourceFactory;
import com.aku.apps.punchin.free.db.DatasourceFactoryFacade;
import com.aku.apps.punchin.free.db.TaskFactory;
import com.aku.apps.punchin.free.db.TimeStampFactory;
import com.aku.apps.punchin.free.domain.Preferences;
import com.aku.apps.punchin.free.domain.Task;
import com.aku.apps.punchin.free.domain.TimeStamp;
import com.aku.apps.punchin.free.domain.TimeStamp.TimeStampType;
import com.aku.apps.punchin.free.utils.Constants;
import com.aku.apps.punchin.free.utils.DateUtil;
import com.aku.apps.punchin.free.utils.FontUtil;
import com.aku.apps.punchin.free.utils.ToastUtil;
import com.aku.apps.punchin.free.widgets.DateSlider.DateSlider;
import com.aku.apps.punchin.free.widgets.DateSlider.TimeSlider;
import com.aku.apps.punchin.free.widgets.greendroid.OptionItem;
import com.aku.apps.punchin.free.widgets.greendroid.PunchInSeparatorItem;

import greendroid.app.GDActivity;
import greendroid.widget.ItemAdapter;
import greendroid.widget.item.Item;

public class TimeActivity extends GDActivity {

	/**
	 * The database factory
	 */
	private DatasourceFactory datasourceFactory;

	/**
	 * Progress dialog that shows a loading icon to the user.
	 */
	private ProgressDialog progressDialog;

	/**
	 * The start time
	 */
	private Date startTime;
	private OptionItem startTimeOption = null;

	/**
	 * The end time
	 */
	private Date endTime;
	private OptionItem endTimeOption = null;

	/**
	 * The duration, i.e, the number of minutes between start and end time.
	 */
	private OptionItem durationOption = null;
	
	/**
	 * The task to add some time to.
	 */
	private Task task = null;
	
	/**
	 * The current day to add some time to.
	 */
	private Date currentDate = null;

	/**
	 * Tells whether the activity is in edit mode or not.
	 */
	private boolean editMode = false;
	
	/**
	 * If the activity is in edit mode, then this holds the punch in time stamp.
	 */
	private TimeStamp punchIn;
	
	/**
	 * If the activity is in edit mode, then this holds the punch out time stamp.
	 */
	private TimeStamp punchOut;
	
	/**
	 * Prepares the activity for viewing.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.d(getLocalClassName(), "onCreate");

		datasourceFactory = DatasourceFactoryFacade.getInstance(this);
		prepareActionBar();
		extractIntentData();
		prepareListView();
		prepareButtons();
	}

	/**
	 * Shows activity dialogs to the user.
	 */
	@Override
	protected Dialog onCreateDialog(int id, Bundle args) {
		Log.d(getLocalClassName(), "onCreateDialog(id=" + id + ")");

		return super.onCreateDialog(id, args);
	}

	/** Prepares the action bars by setting the menu item, name, etc. */
	private void prepareActionBar() {
		Log.d(getLocalClassName(), "prepareActionBar");

		setActionBarContentView(R.layout.time);
		
        TextView v = (TextView)findViewById(com.cyrilmottier.android.greendroid.R.id.gd_action_bar_title);
        v.setTypeface(FontUtil.getTypeface(getBaseContext()));
	}

	/**
	 * Gets the selected date from the intent extras.
	 */
	void extractIntentData() {
		Log.d(getLocalClassName(), "extractIntentData");

		Intent intent = getIntent();
		
		editMode = intent.getBooleanExtra(TimeActivity.class.getName() + ".editMode", false);
		TimeStampFactory tsf = datasourceFactory.createTimeStampFactory();
		
		if (editMode) {
			long stampId = intent.getLongExtra(TimeActivity.class.getName() + ".id1", -1);
			TimeStamp ts = tsf.get(stampId);
			stampId = intent.getLongExtra(TimeActivity.class.getName() + ".id2", -1);
			TimeStamp ts2 = tsf.get(stampId);
			task = tsf.getTask(ts);
			
			if (ts.getType() == TimeStampType.PunchIn) {
				currentDate = DateUtil.removeTimeFromDate(ts.getTime());
				startTime = ts.getTime();
				endTime = ts2.getTime();
				punchIn = ts;
				punchOut = ts2;
			} else {
				currentDate = DateUtil.removeTimeFromDate(ts2.getTime());
				startTime = ts2.getTime();
				endTime = ts.getTime();
				punchIn = ts2;
				punchOut = ts;
			}
			
		} else {		
			long taskId = intent.getLongExtra(TimeActivity.class.getName() + ".taskId", -1);
	
			Date date = (Date)intent.getSerializableExtra(TimeActivity.class.getName() + ".currentDate");
			currentDate = DateUtil.removeTimeFromDate(date);
			
			TaskFactory factory = datasourceFactory.createTaskFactory();
			task = factory.get(taskId);
			
			TimeStamp ts = tsf.getLastTimeStamp(task, currentDate);
	
			if (ts != null) {
				startTime = endTime = DateUtil.removeSecondsFromDate(ts.getTime());
				
			} else {
				currentDate = DateUtil.addTime(currentDate);				
				startTime = endTime = DateUtil.removeSecondsFromDate(currentDate);
			}
	
			startTime = endTime = DateUtil.removeMillisecondsFromDate(startTime);		    
		}

		Preferences prefs = datasourceFactory.createPreferences();
	    String temp = android.text.format.DateFormat.format(prefs.getDefaultDateFormat(), currentDate).toString();
	    getActionBar().setTitle(temp);
	}

	/**
	 * Adds all items to the listview and sets up the item click listener.
	 */
	private void prepareListView() {
		Log.d(getLocalClassName(), "prepareListView");

		startTimeOption = new OptionItem(getString(R.string.label_punch_in),
				getTimeFormatted(startTime));
		
		endTimeOption = new OptionItem(getString(R.string.label_punch_out),
				getTimeFormatted(endTime));
		
		durationOption = new OptionItem(getString(R.string.label_duration),
				getDuration());

		ListView lv = (ListView) findViewById(R.id.listview_repeating_options);
		ArrayList<Item> items = new ArrayList<Item>();
		items.add(new PunchInSeparatorItem(getTaskDescription()));
		items.add(startTimeOption);
		items.add(endTimeOption);
		items.add(durationOption);

		lv.setAdapter(new ItemAdapter(lv.getContext(), items));
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long arg3) {
				switch (position) {
				case 1: // start time
					showDialog(Constants.Dialogs.FROM_DATE);
					break;

				case 2: // end time
					showDialog(Constants.Dialogs.TO_DATE);
					break;
					
				case 3: // duration
					showDialog(Constants.Dialogs.DURATION);
					break;
				}
			}
		});
	}

	/**
	 * Returns a summarised description of the current task.
	 * @return
	 */
	private String getTaskDescription() {
		String description = task.getDescription();
		
		return description.replace("\r\n", " ");
	}

	/**
	 * Converts the difference between the start and end time into a formatted string.
	 * @param seconds
	 * @return
	 */
	private String getDuration() {
		Log.d(getLocalClassName(), "getDuration");

		String duration = "";
		
		if (startTime.compareTo(endTime) == 0) {
			duration = getString(R.string.label_none);
		} else {
			long hours = DateUtil.getHours(startTime, endTime);
			long minutes = DateUtil.getMinutes(startTime, endTime);
			long seconds = DateUtil.getSeconds(startTime, endTime);
			
			if (hours == 0 && minutes == 0 && seconds == 0) {
				duration = getString(R.string.label_none);
				
			} else {			
				if (hours > 0) {
					duration = hours + " " + (hours == 1 ? getString(R.string.label_hour) : getString(R.string.label_hours));
				}
				
				if (minutes > 0) {
					if (hours > 0)
						duration += ", ";
					
					duration += minutes + " " + (minutes == 1 ? getString(R.string.label_minute) : getString(R.string.label_minutes));
				}
				
				if (seconds > 0) {
					if (hours > 0 || minutes > 0)
						duration += " " + getString(R.string.label_and) + " ";
					
					duration += seconds + " " + (seconds == 1 ? getString(R.string.label_second) : getString(R.string.label_seconds));
				}
			}
		}
		
		return duration;
	}
	
	/**
	 * Creates all dialogs used by the system.
	 */
	@Override
	protected Dialog onCreateDialog(int id) {
		Log.d(getLocalClassName(), "onCreateDialog");

		switch (id) {
		case Constants.Dialogs.FROM_DATE:
			return createFromDateDialog();

		case Constants.Dialogs.TO_DATE:
			return createToDateDialog();
		
		case Constants.Dialogs.DURATION:
			return createDurationDialog();

		}

		return super.onCreateDialog(id);
	}

	/**
	 * Creates and returns the to date dialog.
	 * @return
	 */
	private Dialog createToDateDialog() {
	   	Calendar cal = Calendar.getInstance();
		cal.setTime(endTime);
		
		final TimeSlider dialog = new TimeSlider(this, new TimeSlider.OnDateSetListener() {
			@Override
			public void onDateSet(DateSlider view, Calendar selectedDate) {
				endTime = DateUtil.removeSecondsFromDate(selectedDate.getTime());
				endTimeOption.subtitle = getTimeFormatted(endTime);
				
				Log.d(getLocalClassName(), "onTimeSet(current start time=" + startTime + ", new end time=" + endTime + ")");

				if (startTime.compareTo(endTime) > 0) {
					startTime = endTime;
					startTimeOption.subtitle = getTimeFormatted(startTime);
				}

				durationOption.subtitle = getDuration();

				refreshListview();
			}
		}, cal);

		return dialog;
	}

	/**
	 * Creates and returns the from date dialog.
	 * @return
	 */
	private Dialog createFromDateDialog() {
	   	Calendar cal = Calendar.getInstance();
		cal.setTime(startTime);
		
		final TimeSlider dialog = new TimeSlider(this, new TimeSlider.OnDateSetListener() {
			@Override
			public void onDateSet(DateSlider view, Calendar selectedDate) {
				startTime = DateUtil.removeSecondsFromDate(selectedDate.getTime());
				startTimeOption.subtitle = getTimeFormatted(startTime);
				
				Log.d(getLocalClassName(), "onTimeSet(new start time=" + startTime + ", current end time=" + endTime + ")");
				
				if (startTime.compareTo(endTime) > 0) {
					endTime = startTime;
					endTimeOption.subtitle = getTimeFormatted(endTime);
				}

				durationOption.subtitle = getDuration();
				
				refreshListview();
			}
		}, cal);

		return dialog;
	}

	/**
	 * Creates and returns a dialog to change the duration of an item.
	 * @return
	 */
	private Dialog createDurationDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		LayoutInflater factory = LayoutInflater.from(this);
		View textEntryView = null;

		textEntryView = factory.inflate(R.layout.dialog_hourly_rate, null);
		long hours = DateUtil.getHours(startTime, endTime);
		long minutes = DateUtil.getMinutes(startTime, endTime);
		double minutesAsHours = (minutes/60);
		double duration = hours + minutesAsHours;
		
		final EditText durationTextbox = (EditText) textEntryView
				.findViewById(R.id.textbox_hourly_rate);
		durationTextbox.setText(Double.toString(duration));

		final AlertDialog dialog = builder
			.setTitle(getString(R.string.label_duration_in_hours))
			.setView(textEntryView)
			.setPositiveButton(R.string.label_set, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface d, int whichButton) {
					double duration = Double.parseDouble(durationTextbox.getText().toString());
					if (duration > 0) {
						int minutes = (int) (duration * 60);
						Date testTime = DateUtil.addMinutes(startTime, minutes);
						
						// check if the prospective new end time still within today, and
						// doesn't flow into tomorrow.
						if (DateUtil.removeTimeFromDate(testTime).equals(DateUtil.removeTimeFromDate(currentDate))) {
							endTime = testTime;
							endTimeOption.subtitle = getTimeFormatted(endTime);
							durationOption.subtitle = getDuration();
							refreshListview();
							
						} else {
							String message = getString(R.string.label_invalid_duration);
							ToastUtil.show(getBaseContext(), message);

						}
						
					} else {
						String message = getString(R.string.label_invalid_duration);
						ToastUtil.show(getBaseContext(), message);
					}
					
					durationTextbox.selectAll();
					getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
				}
			})
			.setNegativeButton(R.string.label_cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
					durationTextbox.selectAll();
					getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
				}
			})
			.create();

		durationTextbox.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
				}
			}
		});
	    
		return dialog;
	}

	/**
	 * Refreshes the listview.
	 */
	protected void refreshListview() {
		ListView lv = (ListView) findViewById(R.id.listview_repeating_options);
		((ItemAdapter) lv.getAdapter()).notifyDataSetChanged();
	}

	/**
	 * Formats the specified time for the ui.
	 * @param time
	 * @return
	 */
	private String getTimeFormatted(Date time) {
		Log.d(getLocalClassName(), "getTimeFormatted");

		return DateFormat.getTimeInstance().format(time);
	}

	/**
	 * Adds click listeners to the save and cancel buttons.
	 */
	private void prepareButtons() {
		Log.d(getLocalClassName(), "prepareButtons");

		Button b = null;

		b = (Button) findViewById(R.id.button_save);
		b.setClickable(true);
		b.setTypeface(FontUtil.getTypeface(getBaseContext()));
		b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d(getLocalClassName(), "onClick(save button)");

				saveTimeToTask();
			}
		});

		b = (Button) findViewById(R.id.button_cancel);
		b.setClickable(true);
		b.setTypeface(FontUtil.getTypeface(getBaseContext()));
		b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d(getLocalClassName(), "onClick(cancel button)");

				cancelTimeToTask();
			}
		});
	}

	/**
	 * Captures the back key and send a cancel intent.
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			cancelTimeToTask();
		}

		return super.onKeyDown(keyCode, event);
	}

	/**
	 * Cancels this activity and closes it.
	 */
	private void cancelTimeToTask() {
		Log.d(getLocalClassName(), "cancelClient");

		Intent intent = new Intent();
		intent.putExtra(TimeActivity.class.getName() + ".timeAdded", false);
		setResult(RESULT_CANCELED, intent);
		finish();
	}

	/**
	 * Saves some time against the current task and day.
	 */
	protected void saveTimeToTask() {
		Log.d(getLocalClassName(), "saveTimeToTask");

		if (startTime.compareTo(endTime) == 0) {
			ToastUtil.show(getBaseContext(), R.string.message_no_duration_found);
			
		} else {			
			progressDialog = new ProgressDialog(this);
			progressDialog.setTitle(getString(R.string.label_saving));
			progressDialog.show();

			SaveTimeToTaskAction t = new SaveTimeToTaskAction();
			t.execute();
		}
	}

	/**
	 * The async task to save the new client to the database and to close the
	 * activity.
	 * 
	 * @author Peter Akuhata
	 * 
	 */
	private class SaveTimeToTaskAction extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... args) {
			Log.d(getLocalClassName(), "doInBackground");

			TimeStampFactory tsf = datasourceFactory.createTimeStampFactory();
			
			if (editMode) {
				punchIn.setTime(startTime);
				punchOut.setTime(endTime);
				tsf.update(punchIn);
				tsf.update(punchOut);
				
			} else {
				tsf.add(task, startTime, TimeStampType.PunchIn);
				tsf.add(task, endTime, TimeStampType.PunchOut);
			}
			
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			Log.d(getLocalClassName(), "onPostExecute");

			if (progressDialog != null)
				progressDialog.dismiss();

			Intent intent = new Intent();
			intent.putExtra(TimeActivity.class.getName() + ".timeAdded", true);
			setResult(RESULT_OK, intent);
			finish();
		}
	}
}
