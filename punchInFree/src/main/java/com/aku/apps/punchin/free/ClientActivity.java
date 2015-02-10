package com.aku.apps.punchin.free;

import java.text.NumberFormat;
import java.util.ArrayList;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
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

import com.aku.apps.punchin.free.db.ClientFactory;
import com.aku.apps.punchin.free.db.DatasourceFactory;
import com.aku.apps.punchin.free.db.DatasourceFactoryFacade;
import com.aku.apps.punchin.free.domain.Client;
import com.aku.apps.punchin.free.domain.OvertimeRate;
import com.aku.apps.punchin.free.domain.Preferences;
import com.aku.apps.punchin.free.utils.BasicQuickAction;
import com.aku.apps.punchin.free.utils.Constants;
import com.aku.apps.punchin.free.utils.FontUtil;
import com.aku.apps.punchin.free.utils.OvertimeUtil;
import com.aku.apps.punchin.free.utils.ToastUtil;
import com.aku.apps.punchin.free.widgets.greendroid.OptionItem;
import com.aku.apps.punchin.free.widgets.greendroid.PunchInQuickActionGrid;
import com.aku.apps.punchin.free.widgets.greendroid.PunchInTextItem;

import greendroid.app.GDActivity;
import greendroid.widget.ItemAdapter;
import greendroid.widget.QuickActionWidget;
import greendroid.widget.QuickActionWidget.OnQuickActionClickListener;
import greendroid.widget.item.Item;

public class ClientActivity extends GDActivity {
	/**
	 * The tag used in log data.
	 */
	public static final String TAG = ClientActivity.class.getSimpleName();

	/**
	 * Progress dialog that shows a loading icon to the user.
	 */
	private ProgressDialog progressDialog;

	/**
	 * The client name
	 */
	private String name = "";

	/**
	 * The client email
	 */
	private String email = "";

	/**
	 * The client mobile number
	 */
	private String mobile = "";

	/**
	 * The ui to display the client address
	 */
	private OptionItem emailOption;

	/**
	 * The ui to display the client mobile number.
	 */
	private OptionItem mobileOption;

	/**
	 * The database factory
	 */
	private DatasourceFactory datasourceFactory;

	/**
	 * The client that the user has selected to edit.
	 */
	private Client client = null;

	/**
	 * The user-selected hourly rate.
	 */
	private double hourlyRate = 0;

	/**
	 * The item used to display the hourly rate to the user.
	 */
	private OptionItem hourlyRateOptions = null;

	/**
	 * The normal working hours for this client.
	 */
	private double normalWorkingHours = 0;

	/**
	 * The ui to display the client normal working hours.
	 */
	private OptionItem normalWorkingHoursOption = null;

	/**
	 * The client overtime rate.
	 */
	private double overtimeRate = 0;

	/**
	 * The ui to display the overtime rate.
	 */
	private OptionItem overtimeRateOption = null;

	/**
	 * The client mileage rate.
	 */
	private double mileageRate = 0;

	/**
	 * The ui to display the mileage rate.
	 */
	private OptionItem mileageRateOption = null;

	/**
	 * Contains the list of overtime rates to display to the user.
	 */
	private ArrayList<OvertimeRate> overtimeRates = null;

    /** 
     * The mobile context menu and associated actions. 
     */
    private PunchInQuickActionGrid mobileContextMenu;
	private BasicQuickAction editMobileMenuItem;
	private BasicQuickAction ringMobileMenuItem;

    /** 
     * The email context menu and associated actions. 
     */
    private PunchInQuickActionGrid emailContextMenu;
	private BasicQuickAction editEmailMenuItem;
	private BasicQuickAction sendEmailMenuItem;


	/** 
	 * Sets up the mobile context menu. 
	 */
    private void prepareMobileContextMenu() {
		Log.d(getLocalClassName(), "prepareMobileContextMenu");

		mobileContextMenu = new PunchInQuickActionGrid(this);
    	editMobileMenuItem = new BasicQuickAction(this, R.drawable.gd_action_bar_edit, R.string.label_edit_mobile);
    	ringMobileMenuItem = new BasicQuickAction(this, R.drawable.gd_action_bar_all_friends, R.string.label_ring_mobile);
    	mobileContextMenu.setOnQuickActionClickListener(new OnQuickActionClickListener() {			
			@Override
			public void onQuickActionClicked(QuickActionWidget widget, int position) {
				switch (position) {
				case 0: // edit mobile
					showDialog(Constants.Dialogs.MOBILE_NUMBER);
					break;
					
				case 1: // ring mobile
					ringMobile();
					break;
				}
			}
		});
    }
    
	/** 
	 * Sets up the email context menu. 
	 */
    private void prepareEmailContextMenu() {
		Log.d(getLocalClassName(), "prepareEmailContextMenu");

		emailContextMenu = new PunchInQuickActionGrid(this);
    	editEmailMenuItem = new BasicQuickAction(this, R.drawable.gd_action_bar_edit, R.string.label_edit_email);
    	sendEmailMenuItem = new BasicQuickAction(this, R.drawable.gd_action_bar_all_friends, R.string.label_send_email);
    	emailContextMenu.setOnQuickActionClickListener(new OnQuickActionClickListener() {			
			@Override
			public void onQuickActionClicked(QuickActionWidget widget, int position) {
				switch (position) {
				case 0: // edit email
					showDialog(Constants.Dialogs.EMAIL);
					break;
					
				case 1: // send email
					sendEmail();
					break;
				}
			}
		});
    }
    
    /**
     * Creates the 'send email' intent and starts the activity.
     */
    private void sendEmail() {
		Log.d(getLocalClassName(), "sendEmail");

        final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        
        emailIntent .setType("plain/text");         
        emailIntent .putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{email});         
//        emailIntent .putExtra(android.content.Intent.EXTRA_SUBJECT, "");         
//        emailIntent .putExtra(android.content.Intent.EXTRA_TEXT, "");         
        startActivity(Intent.createChooser(emailIntent, "Send mail..."));
    }

    /** 
     * Checks the state of the activity and adds all menu items as appropriate. 
     */
	private void buildEmailContextMenu() {
		Log.d(getLocalClassName(), "buildTaskContextMenu");

		emailContextMenu.clearAllQuickActions();
		emailContextMenu.addQuickAction(editEmailMenuItem);

		if (email != null && email.length() > 0)
			emailContextMenu.addQuickAction(sendEmailMenuItem);
	}

    /** 
     * Checks the state of the activity and adds all menu items as appropriate. 
     */
	private void buildMobileContextMenu() {
		Log.d(getLocalClassName(), "buildTaskContextMenu");

		mobileContextMenu.clearAllQuickActions();
		mobileContextMenu.addQuickAction(editMobileMenuItem);

		if (mobile != null && mobile.length() > 0)
			mobileContextMenu.addQuickAction(ringMobileMenuItem);
	}

	/**
	 * Rings the client's mobile number
	 */
	private void ringMobile() {
		Log.d(getLocalClassName(), "ringMobile");

		Intent intent = new Intent(Intent.ACTION_CALL);

		intent.setData(Uri.parse("tel:" + mobile));
		startActivity(intent);
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
	 * Prepares the activity for viewing.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.d(getLocalClassName(), "onCreate");

		datasourceFactory = DatasourceFactoryFacade.getInstance(this);
		prepareOvertimeRates();
		prepareActionBar();
		extractIntentData();
		prepareMobileContextMenu();
		prepareEmailContextMenu();
		prepareListView();
		prepareButtons();
		prepareClientName();
	}

	/**
	 * Sets the typeface for the task name textbox.
	 */
	private void prepareClientName() {
		EditText editText = (EditText)findViewById(R.id.textbox_title);
		editText.setTypeface(FontUtil.getTypeface(getBaseContext()));
	}

	/**
	 * Add the overtime rates if they don't already exist.
	 */
	private void prepareOvertimeRates() {
		Log.d(getLocalClassName(), "prepareOvertimeRates");
		
		this.overtimeRates = OvertimeUtil.getOvertimeRates();
	}

	/**
	 * Shows activity dialogs to the user.
	 */
	@Override
	protected Dialog onCreateDialog(int id) {
		Log.d(getLocalClassName(), "onCreateDialog(id=" + id + ")");

		switch (id) {
		case Constants.Dialogs.EMAIL:
			return createEmailDialog();
		
		case Constants.Dialogs.MILEAGE_RATE:
			return createMileageRateDialog();

		case Constants.Dialogs.OVERTIME_RATE:
			return createOvertimeRateDialog();


		case Constants.Dialogs.NORMAL_WORKING_HOURS:
			return createNormalWorkingHoursDialog();


		case Constants.Dialogs.HOURLY_RATE:
			return createHourlyRateDialog();


		case Constants.Dialogs.MOBILE_NUMBER:
			return createMobileNumberDialog();
			
		}

		return super.onCreateDialog(id);
	}

	private Dialog createEmailDialog() {
		Log.d(getLocalClassName(), "createEmailDialog");

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		LayoutInflater factory = LayoutInflater.from(this);
		final View view = factory.inflate(R.layout.dialog_email, null);

		final EditText emailTextbox = (EditText) view.findViewById(R.id.textbox_email);
		emailTextbox.setText(this.email);
		
		final AlertDialog dialog = builder
			.setTitle(getString(R.string.label_email))
			.setView(view)
			.setPositiveButton(R.string.label_set, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface d, int whichButton) {
					email = emailTextbox.getText().toString();
					emailOption.subtitle = getEmail();
					ListView lv = (ListView) findViewById(R.id.listview_repeating_options);
					((ItemAdapter) lv.getAdapter()).notifyDataSetChanged();
					emailTextbox.selectAll();
					getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
				}
			})
			.setNegativeButton(R.string.label_cancel, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					emailTextbox.selectAll();
					getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
				}
			})
			.create();
		
		emailTextbox.setOnFocusChangeListener(new OnFocusChangeListener() {
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
	 * Creates and returns the mobile number dialog.
	 * @return
	 */
	private Dialog createMobileNumberDialog() {
		Log.d(getLocalClassName(), "createMobileNumberDialog");

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		LayoutInflater factory = LayoutInflater.from(this);
		final View view = factory.inflate(R.layout.dialog_mobile, null);

		final EditText mobileNumber = (EditText) view.findViewById(R.id.textbox_mobile);
		mobileNumber.setText(mobile);
		
		final AlertDialog dialog = builder
			.setTitle(R.string.label_mobile)
			.setView(view)
			.setPositiveButton(R.string.label_set, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface d, int whichButton) {
					mobile = mobileNumber.getText().toString();
					mobileOption.subtitle = getMobile();
					ListView lv = (ListView) findViewById(R.id.listview_repeating_options);
					((ItemAdapter) lv.getAdapter()).notifyDataSetChanged();
					mobileNumber.selectAll();
					getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
				}
			})
			.setNegativeButton(R.string.label_cancel, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					mobileNumber.selectAll();
					getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
				}
			})
			.create();

		mobileNumber.setOnFocusChangeListener(new OnFocusChangeListener() {
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
		final View view = factory.inflate(R.layout.dialog_hourly_rate, null);

		final EditText hourlyRateTextbox = (EditText) view.findViewById(R.id.textbox_hourly_rate);
		hourlyRateTextbox.setText(Double.toString(hourlyRate));

		final AlertDialog dialog = builder
			.setTitle(getString(R.string.label_hourly_rate))
			.setView(view)
			.setPositiveButton(R.string.label_set,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface d, int whichButton) {
						try {
							hourlyRate = Double.parseDouble(hourlyRateTextbox.getText().toString());
							String formattedHourlyRate = getHourlyRateFormatted(hourlyRate);
							hourlyRateOptions.subtitle = formattedHourlyRate;
							ListView lv = (ListView) findViewById(R.id.listview_repeating_options);
							((ItemAdapter) lv.getAdapter()).notifyDataSetChanged();

						} catch (NumberFormatException ex) {
							Log.d(TAG, ex.getLocalizedMessage());
							
						} finally {
							hourlyRateTextbox.selectAll();
							getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
							
						}
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
	 * Creates and returns the normal working hours dialog.
	 * @return
	 */
	private Dialog createNormalWorkingHoursDialog() {
		Log.d(getLocalClassName(), "createNormalWorkingHoursDialog");

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		LayoutInflater factory = LayoutInflater.from(this);
		final View view = factory.inflate(R.layout.dialog_hourly_rate, null);

		final EditText workingHours = (EditText) view.findViewById(R.id.textbox_hourly_rate);
		workingHours.setText(Double.toString(normalWorkingHours));

		final AlertDialog dialog = builder
			.setTitle(getString(R.string.label_normal_working_hours))
			.setView(view)
			.setPositiveButton(R.string.label_set,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface d, int whichButton) {
						try {
							normalWorkingHours = Double.parseDouble(workingHours.getText().toString());
							String formattedWorkingHours = getWorkingHoursFormatted(normalWorkingHours);
							normalWorkingHoursOption.subtitle = formattedWorkingHours;
							ListView lv = (ListView) findViewById(R.id.listview_repeating_options);
							((ItemAdapter) lv.getAdapter()).notifyDataSetChanged();
							
						}  catch (NumberFormatException ex) {
							Log.d(TAG, ex.getLocalizedMessage());
							
						} finally {
							workingHours.selectAll();
							getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
							
						}
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
	 * Creates and returns the overtime rate dialog.
	 * @return
	 */
	private Dialog createOvertimeRateDialog() {
		Log.d(getLocalClassName(), "createOvertimeRateDialog");

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		LayoutInflater inflater = LayoutInflater.from(this);
		final View view = inflater.inflate(R.layout.dialog_select_item, null);

		ListView lv = (ListView) view.findViewById(R.id.listview_list);
		ArrayList<Item> items = new ArrayList<Item>();

		for (OvertimeRate c : overtimeRates) {
			PunchInTextItem item = new PunchInTextItem(getString(c.getDescription()));
			item.setTag(c);
			items.add(item);
		}

		builder.setView(view);
		builder.setTitle(getString(R.string.label_overtime_rate));
		final AlertDialog dialog = builder.create();
		final ItemAdapter adapter = new ItemAdapter(view.getContext(), items);
		lv.setAdapter(adapter);

		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {
				Item item = (Item) adapter.getItem(position);
				OvertimeRate ot = (OvertimeRate) item.getTag();

				Log.d(getLocalClassName(), "onItemClick(overtime rate='"
						+ (ot == null ? "null" : ot.getDescription())
						+ "')");

				overtimeRate = ot.getMultiplier();
				overtimeRateOption.subtitle = getString(ot.getDescription());

				ListView overtimeListview = (ListView) findViewById(R.id.listview_repeating_options);
				((ItemAdapter) overtimeListview.getAdapter()).notifyDataSetChanged();
				dialog.dismiss();
			}
		});

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
		final View view = factory.inflate(R.layout.dialog_hourly_rate, null);

		final EditText mileageRateTextbox = (EditText) view.findViewById(R.id.textbox_hourly_rate);
		mileageRateTextbox.setText(Double.toString(mileageRate));

		final AlertDialog dialog = builder
			.setTitle(getString(R.string.label_mileage_rate))
			.setView(view)
			.setPositiveButton(R.string.label_set, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface d, int whichButton) {
					// TODO: check that the milage rate is a number; it could be empty, or just a decimal point
					try {
						mileageRate = Double.parseDouble(mileageRateTextbox.getText().toString());
						String formattedMileageRate = getMileageRateFormatted(mileageRate);
						mileageRateOption.subtitle = formattedMileageRate;
						ListView lv = (ListView) findViewById(R.id.listview_repeating_options);
						((ItemAdapter) lv.getAdapter()).notifyDataSetChanged();
						
					} catch (NumberFormatException ex) {
						Log.d(TAG, ex.getLocalizedMessage());
						
					} finally {
						mileageRateTextbox.selectAll();
						getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
						
					}
				}
			})
			.setNegativeButton(R.string.label_cancel, new DialogInterface.OnClickListener() {
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
			formattedOvertime = getString(R.string.label_hourly_rate) + " * "
					+ NumberFormat.getNumberInstance().format(overtimeRate);
		} else {
			formattedOvertime = getString(rate.getDescription());
		}

		return formattedOvertime;
	}

	/** Prepares the action bars by setting the menu item, name, etc. */
	private void prepareActionBar() {
		Log.d(getLocalClassName(), "prepareActionBar");

		setActionBarContentView(R.layout.client);
		
        TextView v = (TextView)findViewById(com.cyrilmottier.android.greendroid.R.id.gd_action_bar_title);
        v.setTypeface(FontUtil.getTypeface(getBaseContext()));
	}

	/**
	 * Gets the selected date from the intent extras.
	 */
	void extractIntentData() {
		Log.d(getLocalClassName(), "extractIntentData");

		Intent intent = getIntent();
		long clientId = intent.getLongExtra(ClientActivity.class.getName() + ".clientId", -1);
		EditText editText = (EditText) findViewById(R.id.textbox_title);
		editText.setTypeface(FontUtil.getTypeface(getBaseContext()));

		if (clientId != -1) {
			ClientFactory factory = datasourceFactory.createClientFactory();
			client = factory.get(clientId);
			name = client.getName();
			email = client.getEmail();
			mobile = client.getMobile();
			hourlyRate = client.getHourlyRate();
			normalWorkingHours = client.getNormalWorkingHours();
			overtimeRate = client.getOvertimeMultiplier();
			mileageRate = client.getMileageRate();

			editText.setText(name);
			setTitle(getString(R.string.label_edit_client));

		} else {
			setTitle(getString(R.string.label_add_client));
			// set the hourly rate to be the same as the default hourly rate in
			// prefs
			Preferences prefs = datasourceFactory.createPreferences();
			hourlyRate = prefs.getDefaultHourlyRate();
			normalWorkingHours = prefs.getDefaultNormalWorkingHours();
			overtimeRate = prefs.getDefaultOvertimeMultiplier();
			mileageRate = prefs.getDefaultMileageRate();

		}
	}

	/**
	 * Adds all items to the listview and sets up the item click listener.
	 */
	private void prepareListView() {
		Log.d(getLocalClassName(), "prepareListView");

		emailOption = new OptionItem(getString(R.string.label_email), getEmail());
		mobileOption = new OptionItem(getString(R.string.label_mobile), getMobile());
		mileageRateOption = new OptionItem(getString(R.string.label_mileage_rate), getMobile());
		String formattedHourlyRate = getHourlyRateFormatted(hourlyRate);
		hourlyRateOptions = new OptionItem(getString(R.string.label_hourly_rate), formattedHourlyRate);

		String formattedWorkingHours = getWorkingHoursFormatted(normalWorkingHours);
		normalWorkingHoursOption = new OptionItem(getString(R.string.label_normal_working_hours),formattedWorkingHours);

		String formattedOvertime = getOvertimeFormatted(overtimeRate);
		overtimeRateOption = new OptionItem(getString(R.string.label_overtime_rate), formattedOvertime);

		String formattedMileageRate = getMileageRateFormatted(mileageRate);
		mileageRateOption.subtitle = formattedMileageRate;

		final ListView lv = (ListView) findViewById(R.id.listview_repeating_options);
		ArrayList<Item> items = new ArrayList<Item>();
		items.add(emailOption);
		items.add(mobileOption);
		items.add(hourlyRateOptions);
		items.add(overtimeRateOption);
		items.add(mileageRateOption);
		items.add(normalWorkingHoursOption);

		lv.setAdapter(new ItemAdapter(lv.getContext(), items));
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long arg3) {
				switch (position) {
				case 0: // email
					showEmailContextMenu(view);
					break;

				case 1: // mobile
					showMobileContextMenu(view);
					break;

				case 2: // hourly rate
					showDialog(Constants.Dialogs.HOURLY_RATE);
					break;

				case 3: // overtime rate
					showDialog(Constants.Dialogs.OVERTIME_RATE);
					break;
					
				case 4: // mileage rate
					showDialog(Constants.Dialogs.MILEAGE_RATE);
					break;

				case 5: // normal working hours
					showDialog(Constants.Dialogs.NORMAL_WORKING_HOURS);
					break;
				}
			}
		});
	}

	/**
	 * Displays the email context menu to the user.
	 * @param view
	 */
	private void showEmailContextMenu(View view) {
		Log.d(getLocalClassName(), "showEmailContextMenu");

		if (email != null && email.length() > 0) {
			buildEmailContextMenu();
			emailContextMenu.show(view);
			
		} else {
			showDialog(Constants.Dialogs.EMAIL);
		
		}
	}

	/**
	 * Displays the mobile context menu to the user.
	 * @param view
	 */
	private void showMobileContextMenu(View view) {
		Log.d(getLocalClassName(), "showMobileContextMenu");

		if (mobile != null && mobile.length() > 0) {
			buildMobileContextMenu();
			mobileContextMenu.show(view);
		} else {
			showDialog(Constants.Dialogs.MOBILE_NUMBER);
		}
	}
	
	/**
	 * If no mobile is found, use the text 'No mobile'. If found, use the actual
	 * mobile.
	 * 
	 * @return
	 */
	private String getMobile() {
		Log.d(getLocalClassName(), "getMobile");

		String text = (mobile == null || mobile.length() == 0 ? getString(R.string.label_no_mobile)
				: mobile);

		return text;
	}

	/**
	 * If no email address is not found, use the text 'No email'. If found, use the
	 * actual address.
	 * 
	 * @return
	 */
	private String getEmail() {
		Log.d(getLocalClassName(), "getEmail");

		String text = (email == null || email.length() == 0 ? getString(R.string.label_no_email) : email);

		return text;
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

				saveClient();
			}
		});

		b = (Button) findViewById(R.id.button_cancel);
		b.setClickable(true);
		b.setTypeface(FontUtil.getTypeface(getBaseContext()));
		b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d(getLocalClassName(), "onClick(cancel button)");

				cancelClient();
			}
		});
	}

	/**
	 * Captures the back key and send a cancel intent.
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			cancelClient();
		}

		return super.onKeyDown(keyCode, event);
	}

	/**
	 * Cancels this activity and closes it.
	 */
	private void cancelClient() {
		Log.d(getLocalClassName(), "cancelClient");

		Intent intent = new Intent();
		intent.putExtra(ClientActivity.class.getName() + ".clientId", -1);
		setResult(RESULT_CANCELED, intent);
		finish();
	}

	/**
	 * Saves a new client and closes the activity.
	 */
	protected void saveClient() {
		Log.d(getLocalClassName(), "saveClient");

		EditText editText = (EditText) findViewById(R.id.textbox_title);
		String name = editText.getText().toString();

		// simple validation

		if (name == null || name.length() == 0) {
			ToastUtil.show(getBaseContext(), R.string.message_client_requires_name);
			editText.requestFocus();

		} else {
			if (name != null && name.length() > Constants.Clients.MAX_LENGTH_NAME)
				name = name.substring(0, Constants.Clients.MAX_LENGTH_NAME);

			progressDialog = new ProgressDialog(this);
			progressDialog.setTitle(getString(R.string.label_saving));
			progressDialog.show();

			SaveClientAction t = new SaveClientAction();
			t.execute(name);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(getLocalClassName(), "onActivityResult(requestCode="
				+ requestCode + ", resultCode=" + resultCode);

		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case Constants.RequestCodes.NOTES:
			boolean saved = data.getBooleanExtra(NotesActivity.class.getName()
					+ ".saved", false);

			if (saved) {
				email = data.getStringExtra(NotesActivity.class.getName()
						+ ".notes");
				emailOption.subtitle = getEmail();
				ListView lv = (ListView) findViewById(R.id.listview_repeating_options);
				((ItemAdapter) lv.getAdapter()).notifyDataSetChanged();
			}
			break;
		}
	}

	/**
	 * The async task to save the new client to the database and to close the
	 * activity.
	 * 
	 * @author Peter Akuhata
	 * 
	 */
	private class SaveClientAction extends AsyncTask<String, Void, Client> {
		@Override
		protected Client doInBackground(String... args) {
			Log.d(getLocalClassName(), "doInBackground");

			String name = args[0];

			ClientFactory factory = datasourceFactory.createClientFactory();
			Client c = null;
			
			if (client != null) {
				client.setName(name);
				client.setEmail(email);
				client.setMobile(mobile);
				client.setHourlyRate(hourlyRate);
				client.setNormalWorkingHours(normalWorkingHours);
				client.setOvertimeMultiplier(overtimeRate);
				client.setMileageRate(mileageRate);
				factory.update(client);
				c = client;

			} else {
				c = factory.add(name, email, mobile,
						normalWorkingHours, hourlyRate, overtimeRate,
						mileageRate);
				
			}
			
			return c;
		}

		@Override
		protected void onPostExecute(Client result) {
			super.onPostExecute(result);

			Log.d(getLocalClassName(), "onPostExecute(task="
					+ (result == null ? "null" : result.getName()) + ")");

			if (progressDialog != null)
				progressDialog.dismiss();

			Intent intent = new Intent();
			intent.putExtra(ClientActivity.class.getName() + ".clientId",
					result.getId());
			setResult(RESULT_OK, intent);
			finish();
		}
	}
}
