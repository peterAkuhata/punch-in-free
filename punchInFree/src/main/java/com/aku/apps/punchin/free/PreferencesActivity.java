package com.aku.apps.punchin.free;

import java.text.NumberFormat;
import java.util.ArrayList;

import com.aku.apps.punchin.free.db.BackupProvider;
import com.aku.apps.punchin.free.db.ClientFactory;
import com.aku.apps.punchin.free.db.DatasourceFactory;
import com.aku.apps.punchin.free.db.DatasourceFactoryFacade;
import com.aku.apps.punchin.free.db.PreferenceFactory;
import com.aku.apps.punchin.free.db.TimeFormatterFactory;
import com.aku.apps.punchin.free.domain.Client;
import com.aku.apps.punchin.free.domain.OvertimeRate;
import com.aku.apps.punchin.free.domain.Preferences;
import com.aku.apps.punchin.free.domain.TimeFormatter;
import com.aku.apps.punchin.free.reporting.ReportFormatter;
import com.aku.apps.punchin.free.reporting.ReportSender;
import com.aku.apps.punchin.free.services.PunchInFreeConfiguration;
import com.aku.apps.punchin.free.services.PunchInFreeWidgetProvider;
import com.aku.apps.punchin.free.utils.Constants;
import com.aku.apps.punchin.free.utils.FontUtil;
import com.aku.apps.punchin.free.utils.OvertimeUtil;
import com.aku.apps.punchin.free.widgets.greendroid.OptionItem;
import com.aku.apps.punchin.free.widgets.greendroid.PunchInSeparatorItem;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import greendroid.app.GDActivity;
import greendroid.widget.ItemAdapter;
import greendroid.widget.item.Item;
import greendroid.widget.item.TextItem;

public class PreferencesActivity extends GDActivity {
	/**
	 * The tag used in log data.
	 */
	public static final String TAG = PreferencesActivity.class.getSimpleName();

	/**
	 * Creates factory objects.
	 */
	private DatasourceFactory datasourceFactory;

	/**
	 * Progress dialog that shows a loading icon to the user.
	 */
	private ProgressDialog progressDialog;
	
	/**
	 * The preferences object to display data.
	 */
	private Preferences preferences = null;

	/**
	 * Contains the list of overtime rates to display to the user.
	 */
	private ArrayList<OvertimeRate> overtimeRates = null;
	
	private String accountName;
	private String accountType;
	private OptionItem accountOption;
	
	private String calendarId;
	private String calendarName;
	private OptionItem calendarOption;
	
	private TimeFormatter defaultTimeFormat;
	private OptionItem defaultTimeFormatOption;

	private String defaultDateFormat;
	private OptionItem defaultDateFormatOption;

	private Client defaultClient;	
	private OptionItem defaultClientOption;

	private double defaultHourlyRate;
	private OptionItem defaultHourlyRateOption;
	
	private double defaultMileageRate;
	private OptionItem defaultMileageRateOption;

	private String defaultMileageUnit;
	private OptionItem defaultMileageUnitOption;

	private double defaultNormalWorkingHours;
	private OptionItem defaultNormalWorkingHoursOption;

	private double defaultOvertimeRate;
	private OptionItem defaultOvertimeRateOption;

	private boolean askBeforeRemovingTime;
	private OptionItem askBeforeRemovingTimeOption;

	private BackupProvider defaultBackupProvider;
	private OptionItem defaultBackupProviderOption;
	
	private ReportFormatter reportFormatter;
	private ReportSender reportSender;
	private OptionItem reportingPreferencesOption;
	

	/**
	 * Determines if the frequency has been changed or not
	 */
	private boolean frequencyChanged = false;
	
	/**
	 * The currently selected frequency
	 */
	private int frequency;
	
	/**
	 * The ui to display the frequency
	 */
	private OptionItem frequencyOption;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(getLocalClassName(), "onCreate");

        datasourceFactory = DatasourceFactoryFacade.getInstance(this);
        prepareActionBar();
        prepareData();
        prepareListView();
        prepareButtons();
    }
	



	private void prepareData() {
		this.preferences = datasourceFactory.createPreferences();
		this.overtimeRates = OvertimeUtil.getOvertimeRates();
		
		this.askBeforeRemovingTime = this.preferences.getAskBeforeRemovingTime();
		this.defaultTimeFormat = datasourceFactory.createDefaultTimeFormat();
		this.defaultBackupProvider = datasourceFactory.createDefaultBackupProvider();
		this.defaultHourlyRate = this.preferences.getDefaultHourlyRate();
		this.defaultDateFormat = this.preferences.getDefaultDateFormat();
		this.defaultClient = datasourceFactory.createDefaultClient();
		this.defaultMileageRate = this.preferences.getDefaultMileageRate();
		this.defaultMileageUnit = this.preferences.getDefaultMileageUnit();
		this.defaultNormalWorkingHours = this.preferences.getDefaultNormalWorkingHours();
		this.defaultOvertimeRate = this.preferences.getDefaultOvertimeMultiplier();
		this.accountName = this.preferences.getDefaultAccountName();
		this.accountType = this.preferences.getDefaultAccountType();
		this.calendarId = this.preferences.getDefaultCalendarId();
		this.calendarName = this.preferences.getDefaultCalendarName();
		this.reportFormatter = this.datasourceFactory.createDefaultReportFormat();
		this.reportSender = this.datasourceFactory.createDefaultReportSender();
		
		SharedPreferences prefs = getBaseContext().getSharedPreferences(getBaseContext().getString(R.string.app_name), 0);				
		this.frequency = prefs.getInt(Constants.WidgetFrequency._NAME, 
				Constants.WidgetFrequency.EVERY_MINUTE);
	}
    
	/**
     * Loads all the preferences into the list view.
     */
	private void prepareListView() {
		final ListView lv = (ListView) findViewById(R.id.listview_repeating_options);
		ArrayList<Item> items = new ArrayList<Item>();

		defaultTimeFormatOption = new OptionItem(getString(R.string.label_time_format), getString(this.defaultTimeFormat.getDescription()));
		defaultDateFormatOption = new OptionItem(getString(R.string.label_date_format), this.defaultDateFormat);		
		defaultClientOption = new OptionItem(getString(R.string.label_default_client), getTextDefaultClient());
		defaultHourlyRateOption = new OptionItem(getString(R.string.label_hourly_rate), getHourlyRateFormatted(this.defaultHourlyRate));
		defaultMileageRateOption = new OptionItem(getString(R.string.label_mileage_rate), getMileageRateFormatted(this.defaultMileageRate));
		defaultMileageUnitOption = new OptionItem(getString(R.string.label_mileage_unit), this.defaultMileageUnit);
		defaultNormalWorkingHoursOption = new OptionItem(getString(R.string.label_normal_working_hours), getWorkingHoursFormatted(this.defaultNormalWorkingHours));
		defaultOvertimeRateOption = new OptionItem(getString(R.string.label_overtime_rate), getOvertimeFormatted(this.defaultOvertimeRate));
		reportingPreferencesOption = new OptionItem(getString(R.string.label_reporting_preferences), getReportingPreferences());
		askBeforeRemovingTimeOption = new OptionItem(getString(R.string.label_ask_before_removing_time), getAskBeforeRemovingTimeFormatted(this.askBeforeRemovingTime));
		defaultBackupProviderOption = new OptionItem(getString(R.string.label_backup_provider), getString(this.defaultBackupProvider.getName()));
		accountOption = new OptionItem(getString(R.string.label_sync_contacts), getAccountNameFormatted(this.accountName));
		calendarOption = new OptionItem(getString(R.string.label_sync_calendar), getCalendarNameFormatted(calendarName));
		frequencyOption = new OptionItem(getString(R.string.label_widget_refresh_rate), PunchInFreeConfiguration.getFrequencyFormatted(getBaseContext(), this.frequency));
		
		items.add(new PunchInSeparatorItem(getString(R.string.label_time_and_date_settings)));		
		items.add(defaultTimeFormatOption);
		items.add(defaultDateFormatOption);
		items.add(new PunchInSeparatorItem(getString(R.string.label_default_client_settings)));		
		items.add(defaultClientOption);
		items.add(defaultHourlyRateOption);
		items.add(defaultMileageRateOption);
		items.add(defaultMileageUnitOption);
		items.add(defaultNormalWorkingHoursOption);
		items.add(defaultOvertimeRateOption);		
		
		items.add(new PunchInSeparatorItem(getString(R.string.label_sync_settings)));
		items.add(accountOption);
		items.add(calendarOption);
		items.add(defaultBackupProviderOption);
		
		items.add(new PunchInSeparatorItem(getString(R.string.label_miscellaneous_settings)));
		items.add(reportingPreferencesOption);
		items.add(askBeforeRemovingTimeOption);
		items.add(frequencyOption);
		

		lv.setAdapter(new ItemAdapter(lv.getContext(), items));
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {
				switch (position) {
				case 1: // time format
					showDialog(Constants.Dialogs.SELECT_TIME_FORMAT);
					break;
					
				case 2: // date format
					showDialog(Constants.Dialogs.SELECT_DATE_FORMAT);
					break;
					
				case 4: // default client
					showDialog(Constants.Dialogs.SELECT_CLIENT);
					break;
					
				case 5: // hourly rate
					showDialog(Constants.Dialogs.HOURLY_RATE);
					break;
					
				case 6: // mileage rate
					showDialog(Constants.Dialogs.MILEAGE_RATE);
					break;
					
				case 7: // mileage unit
					showDialog(Constants.Dialogs.MILEAGE_UNIT);
					break;
					
				case 8: // normal working hours
					showDialog(Constants.Dialogs.NORMAL_WORKING_HOURS);
					break;
					
				case 9: // overtime rate
					showDialog(Constants.Dialogs.OVERTIME_RATE);
					break;
					
										
				case 11: // sync contacts
					showDialog(Constants.Dialogs.SELECT_ACCOUNT);
					break;
					
				case 12: // sync calendar
					showDialog(Constants.Dialogs.SYNC_CALENDAR);
					break;
					
				case 13: // backup provider
					showBackupProvider();
					break;					

					
				case 15: // reporting preferences
					showReportingPreferences();
					break;
					
				case 16: // ask before removing time
					showDialog(Constants.Dialogs.ASK_BEFORE_REMOVING_TIME);
					break;
					
				case 17: // widget frequency
					showDialog(Constants.Dialogs.SELECT_WIDGET_FREQUENCY);
					break;
				}
			}
		});
	}

	/**
	 * Returns a string to represent current reporting prefs.
	 * @return
	 */
	private String getReportingPreferences() {
		Log.d(TAG, "ENTER: getReportingPreferences");

		String temp = "";

		if (this.reportFormatter != null)
			temp += getString(this.reportFormatter.getName());
		
		if (this.reportSender != null) {
			if (this.reportFormatter != null)
				temp += ", ";
			
			temp += getString(this.reportSender.getName());
		}
		
		if (temp == null || temp.length() == 0)
			temp = getString(R.string.label_none);
		
		Log.d(TAG, "EXIT: getReportingPreferences");
		
		return temp;
	}

	/**
	 * Shows the backup provider preferences activity to the user.
	 */
	private void showBackupProvider() {
		Log.d(TAG, "ENTER: showBackupProvider");
		
		Intent intent = new Intent();
		intent.setClassName("com.aku.apps.punchin.free", PreferencesBackupProviderActivity.class.getName());
		startActivityForResult(intent, Constants.RequestCodes.PREFERENCES_BACKUP_PROVIDER);

		Log.d(TAG, "EXIT: showBackupProvider");
	}

	/**
	 * Shows the reporting preferences activity to the user.
	 */
	private void showReportingPreferences() {
		Log.d(TAG, "ENTER: showReportingPreferences");
		
		Intent intent = new Intent();
		intent.setClassName("com.aku.apps.punchin.free", PreferencesReportingActivity.class.getName());
		startActivityForResult(intent, Constants.RequestCodes.PREFERENCES_REPORTING);

		Log.d(TAG, "EXIT: showReportingPreferences");
	}

	/**
	 * Tells the main listview to refresh itself.
	 */
	private void refreshListView() {
		final ListView lv = (ListView) findViewById(R.id.listview_repeating_options);
		((ItemAdapter)lv.getAdapter()).notifyDataSetChanged();
	}

	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case Constants.Dialogs.SELECT_TIME_FORMAT:
			return createTimeFormatDialog();
			
		case Constants.Dialogs.SELECT_DATE_FORMAT:
			return createDateFormatDialog();
			
		case Constants.Dialogs.SELECT_CLIENT:
			return createSelectClientDialog();
			
		case Constants.Dialogs.HOURLY_RATE:
			return createHourlyRateDialog();
			
		case Constants.Dialogs.MILEAGE_RATE:
			return createMileageRateDialog();
			
		case Constants.Dialogs.MILEAGE_UNIT:
			return createMileageUnitDialog();
			
		case Constants.Dialogs.NORMAL_WORKING_HOURS:
			return createNormalWorkingHoursDialog();
		
		case Constants.Dialogs.OVERTIME_RATE:
			return createOvertimeRateDialog();
			
		case Constants.Dialogs.ASK_BEFORE_REMOVING_TIME:
			return createAskBeforeRemovingTimeDialog();
			
		case Constants.Dialogs.SELECT_ACCOUNT:
			return createSyncContactDialog();
			
		case Constants.Dialogs.SYNC_CALENDAR:
			return createSyncCalendarDialog();
			
		case Constants.Dialogs.SELECT_WIDGET_FREQUENCY:
			return createWidgetFrequencyDialog();

		}
		
		return super.onCreateDialog(id);
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
				frequencyOption.subtitle = PunchInFreeConfiguration.getFrequencyFormatted(getBaseContext(), frequency);
				frequencyChanged = true;
				
				Log.d(TAG, "Selected frequency='" + frequencyOption.subtitle + "'");
				refreshListView();
				dialog.dismiss();
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
	 * Creates and returns the sync calendar dialog.
	 * @return
	 */
	private Dialog createSyncCalendarDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		LayoutInflater inflater = LayoutInflater.from(this);
		View view = inflater.inflate(R.layout.dialog_select_item_info, null);
		
		TextView tv = (TextView)view.findViewById(R.id.messageTextView);
		tv.setText(R.string.label_feature_not_exist_in_free);
		
		ListView lv = (ListView)view.findViewById(R.id.listview_list);
		builder.setView(view);
		builder.setTitle(getString(R.string.label_sync_calendar));
		final AlertDialog dialog = builder.create();
		ArrayList<Item> items = new ArrayList<Item>();
		items.add(new TextItem(getString(R.string.label_cancel)));
		
		final ItemAdapter adapter = new ItemAdapter(getBaseContext(), items);
		lv.setAdapter(adapter);
		
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {
				dialog.dismiss();
			}
		});

		return dialog;	
	}

	/**
	 * Creates and returns the select account dialog.
	 * @return
	 */
	private Dialog createSyncContactDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		LayoutInflater inflater = LayoutInflater.from(this);
		View view = inflater.inflate(R.layout.dialog_select_item_info, null);
		
		TextView tv = (TextView)view.findViewById(R.id.messageTextView);
		tv.setText(R.string.label_feature_not_exist_in_free);
		
		ListView lv = (ListView)view.findViewById(R.id.listview_list);
		builder.setView(view);
		builder.setTitle(getString(R.string.label_sync_contacts));
		final AlertDialog dialog = builder.create();
		ArrayList<Item> items = new ArrayList<Item>();
		items.add(new TextItem(getString(R.string.label_cancel)));
		
		final ItemAdapter adapter = new ItemAdapter(getBaseContext(), items);
		lv.setAdapter(adapter);
		
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {
				dialog.dismiss();
			}
		});

		return dialog;	
	}

	/**
	 * Creates and returns the 'ask before removing time' dialog.
	 * @return
	 */
	private Dialog createAskBeforeRemovingTimeDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		LayoutInflater inflater = LayoutInflater.from(this);
		View view = inflater.inflate(R.layout.dialog_select_item, null);
		
		ArrayList<Item> items = new ArrayList<Item>();
		TextItem item = null;
		
		item = new TextItem(getAskBeforeRemovingTimeFormatted(true));
		item.setTag(true);
		items.add(item);
		
		item = new TextItem(getAskBeforeRemovingTimeFormatted(false));
		item.setTag(false);
		items.add(item);
		
		builder.setView(view);
		builder.setTitle(getString(R.string.label_ask_before_removing_time));
		final AlertDialog dialog = builder.create();
		final ItemAdapter adapter = new ItemAdapter(view.getContext(), items);

		ListView lv = (ListView)view.findViewById(R.id.listview_list);
		lv.setAdapter(adapter);	
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {
				Item item = (Item)adapter.getItem(position);
				boolean ask = ((Boolean)item.getTag()).booleanValue();
				
				Log.d(getLocalClassName(), "onItemClick(ask='" + ask + "')");
				askBeforeRemovingTime = ask;
				askBeforeRemovingTimeOption.subtitle = getAskBeforeRemovingTimeFormatted(ask);
				
				refreshListView();
				dialog.dismiss();
			}
		});

		return dialog;
	}

	/**
	 * Creates and returns the overtime rate dialog.
	 * @return
	 */
	private Dialog createOvertimeRateDialog() {
		Log.d(getLocalClassName(), "createOvertimeRateDialog");

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		LayoutInflater inflater = LayoutInflater.from(this);
		View view = inflater.inflate(R.layout.dialog_select_item, null);

		ListView lv = (ListView) view.findViewById(R.id.listview_list);
		ArrayList<Item> items = new ArrayList<Item>();

		for (OvertimeRate c : overtimeRates) {
			TextItem item = new TextItem(getString(c.getDescription()));
			item.setTag(c);
			items.add(item);
		}

		builder.setView(view);
		builder.setTitle(getString(R.string.label_overtime_rate));
		final AlertDialog dialog = builder.create();
		final ItemAdapter adapter = new ItemAdapter(view.getContext(),
				items);
		lv.setAdapter(adapter);

		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {
				Item item = (Item) adapter.getItem(position);
				OvertimeRate ot = (OvertimeRate) item.getTag();

				Log.d(getLocalClassName(), "onItemClick(overtime rate='" + (ot == null ? "null" : ot.getDescription()) + "')");

				defaultOvertimeRate = ot.getMultiplier();
				defaultOvertimeRateOption.subtitle = getString(ot.getDescription());

				refreshListView();
				dialog.dismiss();
			}
		});

		return dialog;
	}

	/**
	 * Creates and returns the normal working hours dialog.
	 * @return
	 */
	private Dialog createNormalWorkingHoursDialog() {
		Log.d(getLocalClassName(), "createNormalWorkingHoursDialog");

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		LayoutInflater factory = LayoutInflater.from(this);
		View textEntryView = null;

		textEntryView = factory.inflate(R.layout.dialog_hourly_rate, null);

		final EditText workingHours = (EditText) textEntryView.findViewById(R.id.textbox_hourly_rate);
		workingHours.setText(Double.toString(defaultNormalWorkingHours));

		final AlertDialog dialog = builder
			.setTitle(getString(R.string.label_normal_working_hours))
			.setView(textEntryView)
			.setPositiveButton(R.string.label_set,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface d, int whichButton) {
						defaultNormalWorkingHours = Double.parseDouble(workingHours.getText().toString());
						String formattedWorkingHours = getWorkingHoursFormatted(defaultNormalWorkingHours);
						defaultNormalWorkingHoursOption.subtitle = formattedWorkingHours;
						refreshListView();
						
						workingHours.selectAll();
						getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
					}
				})
			.setNegativeButton(R.string.label_cancel,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						workingHours.selectAll();
						getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
					}
				})
			.create();

		workingHours.setOnFocusChangeListener(new OnFocusChangeListener() {
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
	 * Creates and returns the mileage unit dialog.
	 * @return
	 */
	private Dialog createMileageUnitDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		LayoutInflater inflater = LayoutInflater.from(this);
		View view = inflater.inflate(R.layout.dialog_text, null);
		
		final EditText editText = (EditText)view.findViewById(R.id.text);
		editText.setText(defaultMileageUnit);
		
		final AlertDialog dialog = builder
			.setView(view)
			.setTitle(R.string.label_mileage_unit)
			.setPositiveButton(R.string.label_set, new OnClickListener() {			
				@Override
				public void onClick(DialogInterface dialog, int which) {
					defaultMileageUnit = editText.getText().toString();
					defaultMileageUnitOption.subtitle = defaultMileageUnit;
					refreshListView();
					
					editText.selectAll();
					getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
				}
			})
			.setNegativeButton(R.string.label_cancel, new OnClickListener() {				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					editText.selectAll();
					getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
				}
			})
			.create();
	
		return dialog;	
	}

	/**
	 * Creates and returns the milage rate dialog.
	 * @return
	 */
	private Dialog createMileageRateDialog() {
		Log.d(getLocalClassName(), "createMileageRateDialog");

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		LayoutInflater factory = LayoutInflater.from(this);
		View textEntryView = null;

		textEntryView = factory.inflate(R.layout.dialog_hourly_rate, null);
		final EditText mileageRateTextbox = (EditText) textEntryView.findViewById(R.id.textbox_hourly_rate);
		mileageRateTextbox.setText(Double.toString(defaultMileageRate));

		final AlertDialog dialog = builder
				.setTitle(getString(R.string.label_mileage_rate))
				.setView(textEntryView)
				.setPositiveButton(R.string.label_set,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface d, int whichButton) {
							defaultMileageRate = Double.parseDouble(mileageRateTextbox.getText().toString());
							String formattedMileageRate = getMileageRateFormatted(defaultMileageRate);
							defaultMileageRateOption.subtitle = formattedMileageRate;
							refreshListView();
							mileageRateTextbox.selectAll();
							getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
						}
					})
				.setNegativeButton(R.string.label_cancel,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							mileageRateTextbox.selectAll();
							getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
						}
					})
				.create();

		mileageRateTextbox.setOnFocusChangeListener(new OnFocusChangeListener() {
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
	 * Creates and returns the hourly rate dialog.
	 * @return
	 */
	private Dialog createHourlyRateDialog() {
		Log.d(getLocalClassName(), "createHourlyRateDialog");

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		LayoutInflater factory = LayoutInflater.from(this);
		View textEntryView = null;

		textEntryView = factory.inflate(R.layout.dialog_hourly_rate, null);

		final EditText hourlyRateTextbox = (EditText) textEntryView.findViewById(R.id.textbox_hourly_rate);
		hourlyRateTextbox.setText(Double.toString(defaultHourlyRate));

		final AlertDialog dialog = builder
			.setTitle(getString(R.string.label_hourly_rate))
			.setView(textEntryView)
			.setPositiveButton(R.string.label_set,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface d, int whichButton) {
						defaultHourlyRate = Double.parseDouble(hourlyRateTextbox.getText().toString());
						String formattedHourlyRate = getHourlyRateFormatted(defaultHourlyRate);
						defaultHourlyRateOption.subtitle = formattedHourlyRate;
						refreshListView();
						hourlyRateTextbox.selectAll();
						getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
					}
				})
			.setNegativeButton(R.string.label_cancel,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						hourlyRateTextbox.selectAll();
						getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
					}
				})
			.create();

		hourlyRateTextbox.setOnFocusChangeListener(new OnFocusChangeListener() {
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
	 * Creates and returns the select client dialog.
	 * @return
	 */
	private Dialog createSelectClientDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		LayoutInflater inflater = LayoutInflater.from(this);
		View view = inflater.inflate(R.layout.dialog_select_item, null);
		
		ListView lv = (ListView)view.findViewById(R.id.listview_list);
		ClientFactory cf = datasourceFactory.createClientFactory();
		ArrayList<Client> clients = cf.getList();
		ArrayList<Item> items = new ArrayList<Item>();
		
		items.add(new TextItem(getString(R.string.label_no_client)));
		
		for (Client c : clients) {
			TextItem item = new TextItem(c.getName());
			item.setTag(c);
			items.add(item);
		}
		
		builder.setView(view);
		builder.setTitle(getString(R.string.label_select_client));
		final AlertDialog dialog = builder.create();
		final ItemAdapter adapter = new ItemAdapter(view.getContext(), items);
		lv.setAdapter(adapter);
		
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {
				Item item = (Item)adapter.getItem(position);
				Object o = item.getTag();
				
				if (o != null) {
					Client c = (Client)o;
					
					Log.d(getLocalClassName(), "onItemClick(client='" + (c == null ? "null" : c.getName()) + "')");
	
					defaultClient = c;
					
				} else {
					defaultClient = null;
					
				}

				defaultClientOption.subtitle = getTextDefaultClient();				
				refreshListView();
				dialog.dismiss();
			}
		});

		return dialog;
	}

	/**
	 * Creates and returns the date format dialog.
	 * @return
	 */
	private Dialog createDateFormatDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		LayoutInflater inflater = LayoutInflater.from(this);
		View view = inflater.inflate(R.layout.dialog_text, null);
		
		final EditText editText = (EditText)view.findViewById(R.id.text);
		editText.setText(defaultDateFormat);
		
		final AlertDialog dialog = builder
			.setView(view)
			.setTitle(R.string.label_select_date_format)
			.setPositiveButton(R.string.label_set, new OnClickListener() {			
				@Override
				public void onClick(DialogInterface dialog, int which) {
					defaultDateFormat = editText.getText().toString();
					defaultDateFormatOption.subtitle = defaultDateFormat;
					refreshListView();
					
					editText.selectAll();
					getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
				}
			})
			.setNegativeButton(R.string.label_cancel, new OnClickListener() {				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					editText.selectAll();
					getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
				}
			})
			.create();

		return dialog;	
	}

	/**
	 * Creates and returns the time format dialog.
	 * @return
	 */
	private Dialog createTimeFormatDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		LayoutInflater inflater = LayoutInflater.from(this);
		View view = inflater.inflate(R.layout.dialog_select_item, null);
		
		builder.setView(view);
		builder.setTitle(getString(R.string.label_select_time_format));
		
		final AlertDialog dialog = builder.create();
		final ListView lv = (ListView)view.findViewById(R.id.listview_list);
		
		TimeFormatterFactory tff = datasourceFactory.createTimeFormatFactory();
		ArrayList<TimeFormatter> formatters = tff.getList();
		ArrayList<Item> items = new ArrayList<Item>();
		
		for (TimeFormatter f : formatters) {
			TextItem item = new TextItem(getString(f.getDescription()));
			item.setTag(f);
			items.add(item);
		}
		
		ItemAdapter adapter = new ItemAdapter(getBaseContext(), items);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {
				Log.d(getLocalClassName(), "onItemClick");
				Item item = (Item)lv.getAdapter().getItem(position);
				defaultTimeFormat = (TimeFormatter)item.getTag();
				defaultTimeFormatOption.subtitle = getString(defaultTimeFormat.getDescription());
				refreshListView();
				dialog.dismiss();
			}
		});

		return dialog;
	}

	/**
	 * Returns a string representation of the overtime value.
	 * 
	 * @param otRate
	 * @return
	 */
	private String getOvertimeFormatted(double otRate) {
		Log.d(getLocalClassName(), "getOvertimeFormatted(otRate="
				+ otRate + ")");

		OvertimeRate rate = getRate(otRate);
		String formattedOvertime = null;

		if (rate == null) {
			formattedOvertime = getString(R.string.label_hourly_rate) + " * " + NumberFormat.getNumberInstance().format(this.defaultOvertimeRate);
			
		} else {
			formattedOvertime = getString(rate.getDescription());
			
		}

		return formattedOvertime;
	}

	/**
	 * Returns the overtime rate to the user.
	 * 
	 * @param rate
	 * @return
	 */
	private OvertimeRate getRate(double rate) {
		Log.d(getLocalClassName(), "getRate");

		OvertimeRate r = null;

		for (OvertimeRate item : overtimeRates) {
			if (item.getMultiplier() == rate) {
				r = item;
				break;
			}
		}

		return r;
	}

	/**
	 * Gets the text representation of the default client.
	 * @return
	 */
	private String getTextDefaultClient() {
		String temp = "";
		
		if (this.defaultClient == null)
			temp = getString(R.string.label_none);
		else
			temp = this.defaultClient.getName();
		
		return temp;
	}

	/**
	 * Returns a string representation of the mileage rate variable.
	 * 
	 * @param mileageRate
	 * @return
	 */
	private String getMileageRateFormatted(double mileageRate) {
		Log.d(getLocalClassName(), "getMileageRateFormatted(mileageRate="
				+ mileageRate + ")");

		Preferences prefs = datasourceFactory.createPreferences();

		return NumberFormat.getCurrencyInstance().format(mileageRate) + " "
				+ getString(R.string.label_per) + " "
				+ prefs.getDefaultMileageUnit();
	}

	/**
	 * Returns a string representation of the normal working hours variable.
	 * 
	 * @param normalWorkingHours
	 * @return
	 */
	private String getWorkingHoursFormatted(double normalWorkingHours) {
		Log.d(getLocalClassName(),
				"getWorkingHoursFormatted(normalWorkingHours="
						+ normalWorkingHours + ")");

		return NumberFormat.getNumberInstance().format(normalWorkingHours)
				+ " " + getString(R.string.label_hours_per_day);
	}

	/**
	 * Gets text to represent the boolean value askBeforeRemovingTime
	 * @return
	 */
	private String getAskBeforeRemovingTimeFormatted(boolean value) {
		String temp = "";
		
		if (value)
			temp = getString(R.string.label_ask_before_removing_time_yes);
		else
			temp = getString(R.string.label_ask_before_removing_time_no);
				
		return temp;
	}

	/**
	 * Returns a string representation of the specified account name.
	 * @param calendarName
	 * @return
	 */
	private String getAccountNameFormatted(String accountName) {
		String temp = "";
		
		if (accountName == null || accountName.length() == 0)
			temp = getString(R.string.label_none);
		else
			temp = accountName;
		
		return temp;			
	}

	/**
	 * Returns a string representation of the specified calendar name.
	 * @param calendarName
	 * @return
	 */
	private String getCalendarNameFormatted(String calendarName) {
		String temp = "";
		
		if (calendarName == null || calendarName.length() == 0)
			temp = getString(R.string.label_none);
		else
			temp = calendarName;
		
		return temp;			
	}

	/**
	 * Returns a string representation of the hourly rate variable.
	 * 
	 * @param hourlyRate
	 * @return
	 */
	private String getHourlyRateFormatted(double hourlyRate) {
		Log.d(getLocalClassName(), "getHourlyRateFormatted(hourlyRate="
				+ hourlyRate + ")");

		return NumberFormat.getCurrencyInstance().format(hourlyRate) + " "
				+ getString(R.string.label_per_hour);
	}

	/**
	 * Sets the content view for the action bar.
	 */
	private void prepareActionBar() {
		setActionBarContentView(R.layout.preferences);
		
        TextView v = (TextView)findViewById(com.cyrilmottier.android.greendroid.R.id.gd_action_bar_title);
        v.setTypeface(FontUtil.getTypeface(getBaseContext()));
	}

	/**
	 * Captures the back key and send a cancel intent.
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			closeActivity(RESULT_CANCELED);
		}

		return super.onKeyDown(keyCode, event);
	}
	
	/**
	 * Checks the results of child activities.
	 */
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		switch (requestCode) {
		case Constants.RequestCodes.PREFERENCES_BACKUP_PROVIDER:
			if (resultCode == Activity.RESULT_OK) {
				this.defaultBackupProvider = datasourceFactory.createDefaultBackupProvider();
				this.defaultBackupProviderOption.subtitle = getString(this.defaultBackupProvider.getName());
				refreshListView();
			}
			break;
			
		case Constants.RequestCodes.PREFERENCES_REPORTING:
			if (resultCode == Activity.RESULT_OK) {
				this.reportFormatter = this.datasourceFactory.createDefaultReportFormat();
				this.reportSender = this.datasourceFactory.createDefaultReportSender();
				this.reportingPreferencesOption.subtitle = getReportingPreferences();
				refreshListView();
			}
			break;
		}
	}

	/**
	 * Closes the activity and uses the specified result as the intent result.
	 * @param result
	 */
	private void closeActivity(int result) {
		Intent intent = new Intent();
		intent.putExtra(PreferencesActivity.class.getName() + ".changesMade", (result == RESULT_OK));
		setResult(RESULT_OK, intent);
		
		finish();	
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
		b.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d(getLocalClassName(), "onClick(save button)");

				savePreferences();
			}
		});

		b = (Button) findViewById(R.id.button_cancel);
		b.setClickable(true);
		b.setTypeface(FontUtil.getTypeface(getBaseContext()));
		b.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d(getLocalClassName(), "onClick(cancel button)");

				closeActivity(RESULT_CANCELED);
			}
		});
	}

	/**
	 * Saves the preferences and closes the activity.
	 */
	protected void savePreferences() {
		progressDialog = new ProgressDialog(this);
		progressDialog.setTitle(getString(R.string.label_saving));
		progressDialog.show();

		SavePreferencesAsyncTask t = new SavePreferencesAsyncTask();
		t.execute();
	}
	
	/**
	 * Responsible for saving the preferences back to the datasource.
	 * @author Peter Akuhata
	 *
	 */
	private class SavePreferencesAsyncTask extends AsyncTask<Void, String, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			preferences.setDefaultBackupProviderId(defaultBackupProvider.getId());
			preferences.setDefaultClientId((defaultClient == null ? -1 : defaultClient.getId()));
			preferences.setDefaultMileageRate(defaultMileageRate);
			preferences.setDefaultMileageUnit(defaultMileageUnit);
			preferences.setDefaultNormalWorkingHours(defaultNormalWorkingHours);
			preferences.setDefaultOvertimeMultiplier(defaultOvertimeRate);
			preferences.setAskBeforeRemovingTime(askBeforeRemovingTime);
			preferences.setDefaultDateFormat(defaultDateFormat);
			preferences.setDefaultHourlyRate(defaultHourlyRate);
			preferences.setDefaultTimeFormatId(defaultTimeFormat.getId());
			preferences.setDefaultAccountName(accountName);
			preferences.setDefaultAccountType(accountType);
			preferences.setDefaultCalendarId(calendarId);
			preferences.setDefaultCalendarName(calendarName);
			
			PreferenceFactory factory = datasourceFactory.createPreferenceFactory();
			factory.update(preferences);
			
			if (frequencyChanged) {
				Log.d(TAG, "New widget frequency; " + frequency);
				SharedPreferences prefs = getBaseContext().getSharedPreferences(getBaseContext().getString(R.string.app_name), 0);				
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
			}
			
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			Log.d(getLocalClassName(), "onPostExecute");

			if (progressDialog != null)
				progressDialog.dismiss();

			closeActivity(RESULT_OK);
		}
	}
}
