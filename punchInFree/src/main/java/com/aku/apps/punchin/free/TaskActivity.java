package com.aku.apps.punchin.free;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.aku.apps.punchin.free.db.ClientFactory;
import com.aku.apps.punchin.free.db.DatasourceFactory;
import com.aku.apps.punchin.free.db.DatasourceFactoryFacade;
import com.aku.apps.punchin.free.db.TaskFactory;
import com.aku.apps.punchin.free.domain.Client;
import com.aku.apps.punchin.free.domain.Preferences;
import com.aku.apps.punchin.free.domain.Task;
import com.aku.apps.punchin.free.domain.Task.RepeatingType;
import com.aku.apps.punchin.free.utils.BasicQuickAction;
import com.aku.apps.punchin.free.utils.Constants;
import com.aku.apps.punchin.free.utils.DateUtil;
import com.aku.apps.punchin.free.utils.FontUtil;
import com.aku.apps.punchin.free.utils.TaskUtil;
import com.aku.apps.punchin.free.utils.ToastUtil;
import com.aku.apps.punchin.free.widgets.DateSlider.DateSlider;
import com.aku.apps.punchin.free.widgets.DateSlider.DefaultDateSlider;
import com.aku.apps.punchin.free.widgets.greendroid.OptionItem;
import com.aku.apps.punchin.free.widgets.greendroid.PunchInQuickActionGrid;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import greendroid.app.GDActivity;
import greendroid.widget.ItemAdapter;
import greendroid.widget.QuickActionWidget;
import greendroid.widget.QuickActionWidget.OnQuickActionClickListener;
import greendroid.widget.item.Item;
import greendroid.widget.item.TextItem;

/**
 * This activity is used to add a task to the system.
 * @author Peter Akuhata
 *
 */
public class TaskActivity extends GDActivity {
	/**
	 * The tag used in log data.
	 */
	public static final String TAG = TaskActivity.class.getSimpleName();

	/** 
	 * The start date of the task
	 */
    private Date startDate = new Date();
    private OptionItem startDateOption = null;
    private boolean startDateChanged = false;
    
	/** 
	 * The end date of the task
	 */
    private Date endDate = null;
    private OptionItem endDateOption = null;
    
    
    /**
     * The item used to display the currently selected repeating option 
     */
    private OptionItem repeatingOption = null;
    
    /**
     * The repeating option that the user has selected from the dialog.
     */
    private int selectedRepeatingOption = 0;
	
    /**
     * Creates database objects.
     */
    private DatasourceFactory datasourceFactory;
    
    /**
     * Progress dialog that shows a loading icon to the user.
     */
    private ProgressDialog progressDialog;
    
    /**
     * The task that the user has selected to edit.
     */
    private Task selectedTask;
    
    /**
     * The list of available options for the repeating dialog.
     */
    private String[] availableRepeatingOptions;
    
    /**
     * The list of available options for the 'active' dialog.
     */
    private String[] availableActiveOptions;
    
    /**
     * The user-selected value that determines whether the task is active or not.
     */
    private boolean isActive;
    
    /**
     * The item used to show the active option to the user.
     */
    private OptionItem isActiveOption = null;
    
    /**
     * The user-selected client that the task will be included in.
     */
    private long selectedClientId = -1;
    
    /**
     * The item used to show the selected client to the user.
     */
    private OptionItem selectedClientOption = null;
    
    /** 
     * The end date context menu and associated actions. 
     */
    private PunchInQuickActionGrid endDateContextMenu;
	private BasicQuickAction editEndDateMenuItem;
	private BasicQuickAction clearEndDateMenuItem;

	/** 
	 * Sets up the end date context menu. 
	 */
    private void prepareEndDateContextMenu() {
		Log.d(TAG, "ENTER: prepareEndDateContextMenu");

		endDateContextMenu = new PunchInQuickActionGrid(this);
		editEndDateMenuItem = new BasicQuickAction(this, R.drawable.gd_action_bar_edit, R.string.label_edit_date);
		clearEndDateMenuItem = new BasicQuickAction(this, R.drawable.gd_action_bar_trashcan, R.string.label_clear_date);
		
		endDateContextMenu.addQuickAction(editEndDateMenuItem);
		endDateContextMenu.addQuickAction(clearEndDateMenuItem);
		endDateContextMenu.setOnQuickActionClickListener(new OnQuickActionClickListener() {			
			@Override
			public void onQuickActionClicked(QuickActionWidget widget, int position) {
				switch (position) {
				case 0: // edit end date
					showDialog(Constants.Dialogs.END_DATE);
					break;
					
				case 1: // clear end date 
					clearEndDate();
					break;
				}
			}
		});

		Log.d(TAG, "EXIT: prepareEndDateContextMenu");
    }

    /**
     * Clears the end date, refreshes the ui.
     */
	private void clearEndDate() {
		ListView lv = (ListView)findViewById(R.id.listview_repeating_options);
		ItemAdapter adapter = (ItemAdapter)lv.getAdapter();

		endDate = null;
    	endDateOption.subtitle = formatPrettyDate(endDate);
    	
    	adapter.notifyDataSetChanged();
	}

    /**
     * Builds the view.
     */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
        Log.d(TAG, "onCreate");

        datasourceFactory = DatasourceFactoryFacade.getInstance(this);
		prepareActionBar();
		prepareAvailableOptions();
		extractIntentData();
		prepareListViewOptions();		
		prepareListView();
		prepareButtons();
		prepareTaskName();
		prepareEndDateContextMenu();
	}

	/**
	 * Sets the typeface for the task name textbox.
	 */
	private void prepareTaskName() {
		EditText editText = (EditText)findViewById(R.id.textbox_title);
		editText.setTypeface(FontUtil.getTypeface(getBaseContext()));
	}

	/**
	 * Creates the list of repeating options available, and used in the repeating task dialog.
	 */
	private void prepareAvailableOptions() {
		Log.d(TAG, "prepareAvailableOptions");

		availableRepeatingOptions = TaskUtil.getRepeatingOptions(this);
		
		availableActiveOptions = new String[] {
			this.getString(R.string.label_activate),
			this.getString(R.string.label_deactivate)
		};
	}

	/**
	 * Prepares all the listview options for viewing.
	 */
	void prepareListViewOptions() {
		Log.d(TAG, "prepareListViewOptions");

	    String sDate = "";
		String eDate = "";
		
	    if (selectedTask != null) {
	    	sDate = formatPrettyDate(selectedTask.getStartDate());
	    	eDate = formatPrettyDate(selectedTask.getEndDate());
	    	
	    	selectedRepeatingOption = TaskUtil.toRepeatingInt(selectedTask.getRepeatingType());
	    	String label = availableRepeatingOptions[selectedRepeatingOption];
	    	selectedClientId = selectedTask.getClientId();
	    	
	    	repeatingOption = new OptionItem(
		    		getString(R.string.label_repeating_option), 
		    		label);

	    } else {
	    	sDate = formatPrettyDate(startDate);
	    	eDate = formatPrettyDate(null);
	    	
	    	repeatingOption = new OptionItem(
		    		getString(R.string.label_repeating_option), 
		    		getString(R.string.task_repeat_no_repeat));
	    }	
		
	    startDateOption = new OptionItem(getString(R.string.label_start_date), sDate);
	    endDateOption = new OptionItem(getString(R.string.label_end_date), eDate);

		isActiveOption = new OptionItem(
	    		getString(R.string.label_status), 
	    		(isActive ? getString(R.string.label_active) : getString(R.string.label_inactive)));

		selectedClientOption = new OptionItem(
	    		getString(R.string.label_client), 
	    		getClientName(selectedClientId));
	}
	
	/**
	 * Returns the name of a client given it's associated id.
	 * @param id
	 * @return
	 */
	private String getClientName(long id) {
		Log.d(TAG, "getClientName");

		String temp = getString(R.string.label_no_client_selected);

		ClientFactory cf = datasourceFactory.createClientFactory();
		Client c = cf.get(id);
		
		if (c != null)
			temp = c.getName();
		
		return temp;
	}

	/**
	 * Gets the selected date from the intent extras.
	 */
	void extractIntentData() {
		Log.d(TAG, "extractIntentData");

		Intent intent = getIntent();
		startDate = (Date)intent.getSerializableExtra(TaskActivity.class.getName() + ".currentDate");
        long taskId = intent.getLongExtra(TaskActivity.class.getName() + ".taskId", -1);
        
        if (taskId != -1) {
        	TaskFactory factory = datasourceFactory.createTaskFactory();
        	selectedTask = factory.get(taskId);

        	EditText editText = (EditText)findViewById(R.id.textbox_title);
        	editText.setText(selectedTask.getDescription());
        	
    		isActive = selectedTask.getActive();
    		setTitle(getString(R.string.label_edit_task));
    		startDate = selectedTask.getStartDate();
    		endDate = selectedTask.getEndDate();
    		
        } else {
            isActive = true;
        	setTitle(getString(R.string.label_add_task));
    		
    		ClientFactory clientFactory = datasourceFactory.createClientFactory();
    		Preferences prefs = datasourceFactory.createPreferences();
    		Client client = clientFactory.get(prefs.getDefaultClientId());

    		if (client != null)
    			selectedClientId = client.getId();

        }
	}

	/**
	 * Adds the repeating option to the listview and sets up the item click listener.
	 */
	private void prepareListView() {
		Log.d(TAG, "prepareListView");
		
		ListView lv = (ListView)findViewById(R.id.listview_repeating_options);
		ArrayList<Item> items = new ArrayList<Item>();
		items.add(startDateOption);
		items.add(endDateOption);
		items.add(repeatingOption);
		items.add(isActiveOption);
		items.add(selectedClientOption);

		lv.setAdapter(new ItemAdapter(lv.getContext(), items));
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {
				switch(position) {
				case 0: // start date
					showDialog(Constants.Dialogs.START_DATE);
					break;
					
				case 1: // end date
					if (endDate != null)
						endDateContextMenu.show(view);
					
					else
						showDialog(Constants.Dialogs.END_DATE);
					
					break;
					
				case 2:  // repeating
					showDialog(Constants.Dialogs.REPEATING_TASK);
					break;
					
				case 3: // active 
					showDialog(Constants.Dialogs.ACTIVE_TASK);
					break;
					
				case 4: // clients dialog
					showClientsDialog();
					break;
				}
			}
		});
	}
	
	/**
	 * Checks to see if there are any clients.  If there aren't then a question is asked to the user (create new client y/n?)
	 * If there are, then display a list of clients.
	 */
	protected void showClientsDialog() {
		ClientFactory cf = datasourceFactory.createClientFactory();
		int count = cf.getCount();
		
		if (count == 0) {
			showDialog(Constants.Dialogs.YES_NO_CREATE_NEW_CLIENT);
		} else {
			showDialog(Constants.Dialogs.SELECT_CLIENT);
		}
	}
	
	/**
	 * Opens the client activity in add mode.
	 */
	private void addClient() {
		Intent intent = new Intent();
		intent.setClassName("com.aku.apps.punchin.free", ClientActivity.class.getName());
		intent.putExtra(ClientActivity.class.getName() + ".currentDate", startDate);
		startActivityForResult(intent, Constants.RequestCodes.EDIT_CLIENT);
	}
	
	
	/**
	 * Check for results back from other activities.
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		switch (requestCode) {		
		case Constants.RequestCodes.EDIT_CLIENT:
			long clientId = data.getLongExtra("com.aku.apps.punchin.free.ClientActivity.clientId", -1);
			
			if (clientId != -1) {
				ClientFactory cf = datasourceFactory.createClientFactory();
				Client c = cf.get(clientId);
				
				if (c != null) {
					selectedClientOption.subtitle = c.getName();
					selectedClientId = c.getId();
					
					ListView lv = (ListView)findViewById(R.id.listview_repeating_options);
					((ItemAdapter)lv.getAdapter()).notifyDataSetChanged();
				}
			}

			getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
			break;
		}
	}
	
	/**
	 * Creates all relevant dialogs.
	 */
	@Override
	protected Dialog onCreateDialog(int id) {
		Log.d(TAG, "onCreateDialog(id=" + id + ")");

		switch(id) {
		case Constants.Dialogs.SELECT_CLIENT:
			return createSelectClientDialog();

		case Constants.Dialogs.YES_NO_CREATE_NEW_CLIENT:
			return createYesNoCreateClientDialog();

		case Constants.Dialogs.ACTIVE_TASK:
			return createActiveTaskDialog();
			
		case Constants.Dialogs.START_DATE:
		   	return createStartDateDialog();
			
		case Constants.Dialogs.END_DATE:
		   	return createEndDateDialog();
		   	
		case Constants.Dialogs.REPEATING_TASK:
			return createRepeatingTaskDialog();
		}
		
		return super.onCreateDialog(id);
	}
	
	/**
	 * Prepares the dialog for viewing.
	 */
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		super.onPrepareDialog(id, dialog);
		Log.d(TAG, "ENTER: onPrepareDialog(id=" + id + ")");

		switch(id) {
		case Constants.Dialogs.SELECT_CLIENT:
			break;

		case Constants.Dialogs.YES_NO_CREATE_NEW_CLIENT:
			break;

		case Constants.Dialogs.ACTIVE_TASK:
			break;
			
		case Constants.Dialogs.START_DATE:
			Calendar start = Calendar.getInstance();
			start.setTime(startDate);
			((DefaultDateSlider)dialog).setTime(start);
			break;
			
		case Constants.Dialogs.END_DATE:
			Calendar end = Calendar.getInstance();
			end.setTime(endDate == null ? startDate : endDate);
			
			((DefaultDateSlider)dialog).setTime(end);
			break;
		   	
		case Constants.Dialogs.REPEATING_TASK:
			break;
		}

		Log.v(TAG, "EXIT: onPrepareDialog");
	}

	/**
	 * Creates and returns the repeating task dialog.
	 * @return
	 */
	private Dialog createRepeatingTaskDialog() {
		Log.d(TAG, "showRepeatingDialog");

		final ListView lv = (ListView)findViewById(R.id.listview_repeating_options);
		final ItemAdapter adapter = (ItemAdapter)lv.getAdapter();
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.label_repeating_option));		
		builder.setSingleChoiceItems(availableRepeatingOptions, selectedRepeatingOption, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				selectedRepeatingOption = which;
				repeatingOption.subtitle = availableRepeatingOptions[which].toString();
				adapter.notifyDataSetChanged();
				dialog.dismiss();
			}
		});
		
		final AlertDialog dialog = builder.create();
	    
		return dialog;
	}

	/**
	 * Creates and returns the end date dialog.
	 * @return
	 */
	private Dialog createEndDateDialog() {
		Calendar c = Calendar.getInstance();
		c.setTime(endDate == null ? startDate : endDate);

		final DefaultDateSlider dialog = new DefaultDateSlider(this, onEndDateSetListener, c);

		return dialog;
	}

	/**
	 * Creates and returns the start date dialog.
	 * @return
	 */
	private Dialog createStartDateDialog() {
		Calendar c = Calendar.getInstance();
		c.setTime(startDate);

		final Dialog dialog = new DefaultDateSlider(this, onStartDateSetListener, c);

		return dialog;
	}
	
	/**
	 * Creates and returns the is active dialog.
	 * @return
	 */
	private Dialog createActiveTaskDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.label_status));		
		builder.setSingleChoiceItems(availableActiveOptions, (isActive ? 0 : 1), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				ListView lv = (ListView)findViewById(R.id.listview_repeating_options);
				ItemAdapter adapter = (ItemAdapter)lv.getAdapter();

				isActive = (which == 0);
				isActiveOption.subtitle = (isActive ? getString(R.string.label_active) : getString(R.string.label_inactive));
				
				adapter.notifyDataSetChanged();
				dialog.dismiss();
			}
		});
		
		final AlertDialog dialog = builder.create();

		return dialog;
	}

	/**
	 * Creates and returns the create client question dialog.
	 * @return
	 */
	private Dialog createYesNoCreateClientDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(getString(R.string.message_no_clients_create_new))
	       .setCancelable(false)
	       .setPositiveButton(R.string.label_yes, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					addClient();
				}
	       })
	       .setNegativeButton(R.string.label_no, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {						
				}
	       });
		
		final AlertDialog dialog = builder.create();

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
		builder.setTitle(getString(R.string.label_status));
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
					
					Log.d(TAG, "onItemClick(client='" + (c == null ? "null" : c.getName()) + "')");
	
					selectedClientId = c.getId();
					selectedClientOption.subtitle = c.getName();
					
				} else {
					selectedClientId = -1;
					selectedClientOption.subtitle = getString(R.string.label_no_client_selected);
				}
				
				ListView taskListView = (ListView)findViewById(R.id.listview_repeating_options);
				((ItemAdapter)taskListView.getAdapter()).notifyDataSetChanged();
				dialog.dismiss();
			}
		});

		return dialog;
	}

	/**
	 * The {@link DateSlider.OnDateSetListener} for when the user wants to edit the date.
	 */
	private DateSlider.OnDateSetListener onEndDateSetListener = new DateSlider.OnDateSetListener() {	    
		@Override
		public void onDateSet(DateSlider view, Calendar selectedDate) {
    		Log.d(TAG, "onDateSet(selectedDate=" + selectedDate + ")");

    		ListView lv = (ListView)findViewById(R.id.listview_repeating_options);
    		ItemAdapter adapter = (ItemAdapter)lv.getAdapter();

        	Date testDate = selectedDate.getTime();
        	
        	if (testDate.after(startDate) || testDate.equals(startDate)) {
        		endDate = testDate; 		
	        	endDateOption.subtitle = formatPrettyDate(endDate);
	        	
	        	adapter.notifyDataSetChanged();
	        	
        	} else {
        		ToastUtil.show(getBaseContext(), getString(R.string.message_invalid_end_date));
        		
        	}
		}
	};

	/**
	 * The {@link DateSlider.OnDateSetListener} for when the user wants to edit the date.
	 */
	private DateSlider.OnDateSetListener onStartDateSetListener = new DateSlider.OnDateSetListener() {	    
		@Override
		public void onDateSet(DateSlider view, Calendar selectedDate) {
    		Log.d(TAG, "onDateSet(selectedDate=" + selectedDate + ")");

    		ListView lv = (ListView)findViewById(R.id.listview_repeating_options);
    		ItemAdapter adapter = (ItemAdapter)lv.getAdapter();

            startDate = selectedDate.getTime();
        	startDateOption.subtitle = formatPrettyDate(startDate);
        	startDateChanged = true;	        	
        	
        	if (endDate != null && (endDate.before(startDate))) {
        		endDate = startDate; 		
	        	endDateOption.subtitle = formatPrettyDate(endDate);
	        	
	        	ToastUtil.show(getBaseContext(), getString(R.string.message_end_date_updated));
        	}

        	adapter.notifyDataSetChanged();
		}
	};

	/**
	 * Returns the date formatted *nicely*, i.e, the phrases 'Today', 'Tomorrow' and 'Yesterday' gets
	 * used if appropriate.
	 * @return
	 */
	private String formatPrettyDate(Date date) {
		Log.d(TAG, "formatPrettyDate(date=" + (date == null ? "null" : date) + ")");

		Preferences prefs = datasourceFactory.createPreferences();
		String temp;

		if (date == null)
			temp = getString(R.string.label_no_date_set);
		
		else if (DateUtil.isToday(date))
	    	temp = getString(R.string.label_today);
	    
	    else if (DateUtil.isYesterday(date))
	    	temp = getString(R.string.label_yesterday);
	    
	    else if (DateUtil.isTomorrow(date))
	    	temp = getString(R.string.label_tomorrow);
	    
	    else
	    	temp = DateFormat.format(prefs.getDefaultDateFormat(), date).toString();
		
		return temp;
	}

	/** Prepares the action bars by setting the menu item, name, etc. */
	private void prepareActionBar() {
		Log.d(TAG, "prepareActionBar");
        
		setActionBarContentView(R.layout.task);
		
        TextView v = (TextView)findViewById(com.cyrilmottier.android.greendroid.R.id.gd_action_bar_title);
        v.setTypeface(FontUtil.getTypeface(getBaseContext()));
	}

	/**
	 * Adds click listeners to the save and cancel buttons.
	 */
	private void prepareButtons() {
		Log.d(TAG, "prepareButtons");

		Button b = null;
		
		b = (Button)findViewById(R.id.button_save);
		b.setClickable(true);
		b.setTypeface(FontUtil.getTypeface(getBaseContext()));
		b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d(TAG, "onClick(save button)");

				saveTask();
			}
		});
		
		b = (Button)findViewById(R.id.button_cancel);
		b.setClickable(true);
		b.setTypeface(FontUtil.getTypeface(getBaseContext()));
		b.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				Log.d(TAG, "onClick(cancel button)");
				
				cancelTask();
			}
		});
	}

	/**
	 * Cancels this activity and closes it.
	 */
	private void cancelTask() {
		Intent intent = new Intent();
		intent.putExtra(TaskActivity.class.getName() + ".taskId", -1);
		setResult(RESULT_CANCELED, intent);
		finish();
	}

	/**
	 * Captures the back key and send a cancel intent.
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			cancelTask();
		}
		
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * Saves a new task and closes the activity.
	 */
	protected void saveTask() {
		Log.d(TAG, "saveTask");

		EditText editText = (EditText)findViewById(R.id.textbox_title);
		String description = editText.getText().toString();
		
		// simple validation
		
		if (description == null || description.length() == 0) {
			ToastUtil.show(getBaseContext(), getString(R.string.message_task_requires_description));
			editText.requestFocus();

		} else {
			if (description != null && description.length() > Constants.Tasks.MAX_LENGTH_DESCRIPTION)
				description = description.substring(0, Constants.Tasks.MAX_LENGTH_DESCRIPTION);

			progressDialog = new ProgressDialog(this);
			progressDialog.setTitle(getString(R.string.label_saving));
			progressDialog.show();
			
			SaveTaskAction t = new SaveTaskAction();
			t.execute(description);
		}
	}
	
	/**
	 * The async task to save the new task to the database and to close the activity.
	 * @author Peter Akuhata
	 *
	 */
	private class SaveTaskAction extends AsyncTask<String, Void, Task> {
		@Override
		protected Task doInBackground(String... args) {
			Log.d(TAG, "doInBackground");

			RepeatingType type = TaskUtil.toRepeatingType(selectedRepeatingOption);
			Date sd = DateUtil.removeTimeFromDate(startDate);
			Date ed = DateUtil.removeTimeFromDate(endDate);
			String description = args[0];
			
			TaskFactory factory = datasourceFactory.createTaskFactory();
			Task task = null;
			
			if (selectedTask != null) {
				selectedTask.setRepeatingType(type);
				
				if (startDateChanged)
					selectedTask.setStartDate(sd);
				
				selectedTask.setDescription(description);
				selectedTask.setActive(isActive);
				selectedTask.setClientId(selectedClientId);
				selectedTask.setEndDate(ed);
				
				factory.update(selectedTask);
				task = selectedTask;
				
			} else {
				task = factory.add(selectedClientId, description, sd, ed, type);

			}
			
			return task;
		}


		@Override
		protected void onPostExecute(Task result) {		
			super.onPostExecute(result);
			
			Log.d(TAG, "onPostExecute(task=" + (result == null ? "null" : result.getDescription()) + ")");

			if (progressDialog != null)
				progressDialog.dismiss();
			
			Intent intent = new Intent();
			intent.putExtra(TaskActivity.class.getName() + ".taskId", result.getId());
			setResult(RESULT_OK, intent);
			finish();
		}		
	}
}
