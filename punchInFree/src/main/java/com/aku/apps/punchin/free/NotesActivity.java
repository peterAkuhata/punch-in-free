package com.aku.apps.punchin.free;

import java.util.Date;

import com.aku.apps.punchin.free.db.ClientFactory;
import com.aku.apps.punchin.free.db.DatasourceFactory;
import com.aku.apps.punchin.free.db.DatasourceFactoryFacade;
import com.aku.apps.punchin.free.db.DayFactory;
import com.aku.apps.punchin.free.db.ExpenseFactory;
import com.aku.apps.punchin.free.db.TaskFactory;
import com.aku.apps.punchin.free.domain.Client;
import com.aku.apps.punchin.free.domain.Day;
import com.aku.apps.punchin.free.domain.Expense;
import com.aku.apps.punchin.free.domain.Task;
import com.aku.apps.punchin.free.domain.Expense.ExpenseType;
import com.aku.apps.punchin.free.domain.TaskDay;
import com.aku.apps.punchin.free.utils.FontUtil;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import greendroid.app.GDActivity;
import greendroid.widget.ActionBar;

public class NotesActivity extends GDActivity {
    
	/**
	 * Represents the daily notes mode of the activity.
	 */
	public static final int DailyNotes = 0;

	/**
	 * Represents the task notes mode of the activity.
	 */
	public static final int TaskNotes = 1;
	
	/**
	 * Represents the client address mode of the activity.
	 */
	public static final int ClientAddress = 2;
	
	/**
	 * Represents the expense notes mode of the activity.
	 */
	public static final int ExpenseNotes = 3;
	
	
	
    /** 
     * Creates database objects 
     */
    private DatasourceFactory datasourceFactory;
    
	/** 
	 * The date that the user has selected. 
	 */
    private static Date selectedDate = new Date();
    
    /**
     * Represents the selected task to grab the notes for (if the current mode is for task notes).
     */
    private Task selectedTask = null;
    
    /**
     * The current mode of the activity, defaults to daily notes.
     */
    private int currentMode = DailyNotes;
    
    /**
     * Represents the selected client to grab the address from (if the current mode is for a client address)
     */
    private Client selectedClient = null;
    
    /**
     * Represents the selected expense to grab the notes from (if the current mode is for an expense note)
     */
    private Expense selectedExpense = null;

    /**
     * Setup the activity.
     */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
        Log.d(getLocalClassName(), "onCreate");

        datasourceFactory = DatasourceFactoryFacade.getInstance(this);
		prepareActionBar();
        extractIntentData();		
		prepareButtons();
		prepareText();
	}

	/**
	 * Gets the selected date from the intent extras.
	 */
	void extractIntentData() {
        Log.d(getLocalClassName(), "extractIntentData");

		Intent intent = getIntent();
		selectedDate = (Date)intent.getSerializableExtra(NotesActivity.class.getName() + ".currentDate");
        currentMode = intent.getIntExtra(NotesActivity.class.getName() + ".currentMode", DailyNotes);
        
        switch (currentMode) {
        case DailyNotes:
        	this.setTitle(getString(R.string.label_daily_note));
        	break;
        	
        case TaskNotes:
        	long taskId = intent.getLongExtra(NotesActivity.class.getName() + ".taskId", -1);
            
            if (taskId != -1) {
            	TaskFactory tf = datasourceFactory.createTaskFactory();
            	selectedTask = tf.get(taskId);
            	this.setTitle(getString(R.string.label_task_note));
            }
        	break;
        	
        case ClientAddress:
        	long clientId = intent.getLongExtra(NotesActivity.class.getName() + ".clientId", -1);
            
            if (clientId != -1) {
            	ClientFactory tf = datasourceFactory.createClientFactory();
            	selectedClient = tf.get(clientId);
            	this.setTitle(getString(R.string.label_client_address));
            }            
        	break;
        
        case ExpenseNotes:
        	long expenseId = intent.getLongExtra(NotesActivity.class.getName() + ".expenseId", -1);
            
            if (expenseId != -1) {
            	ExpenseFactory tf = datasourceFactory.createExpenseFactory();

            	selectedExpense = tf.get(expenseId);
            	setExpenseNotesTitle(selectedExpense.getType());
            	setExpenseNotesHint(selectedExpense.getType());

            } else {
            	ExpenseType type = (ExpenseType)intent.getSerializableExtra(NotesActivity.class.getName() + ".type");
            	setExpenseNotesTitle(type);
            	setExpenseNotesHint(type);
            }
        	break;
        }
	}
	
	/**
	 * Checks the expense type and sets the appropriate hint for the activity.
	 * @param type
	 */
	private void setExpenseNotesHint(ExpenseType type) {
		EditText editText = (EditText)findViewById(R.id.textbox_notes);

		if (type == ExpenseType.MILEAGE)
			editText.setHint(getString(R.string.hint_enter_mileage_notes));
		else
			editText.setHint(getString(R.string.hint_enter_costing_notes));
	}
	
	/**
	 * Checks the expense type, and sets the appropriate title for the activity.
	 * @param type
	 */
	private void setExpenseNotesTitle(ExpenseType type) {
    	if (type == ExpenseType.MILEAGE)
    		this.setTitle(getString(R.string.label_mileage_notes));
    	else
    		this.setTitle(getString(R.string.label_costing_notes));
	}

	/**
	 * Retrieves the current notes and adds it to the textbox.
	 */
	void prepareText() {
        Log.d(getLocalClassName(), "prepareText");

		String text = "";
		EditText editText = (EditText)findViewById(R.id.textbox_notes);
		editText.setTypeface(FontUtil.getTypeface(getBaseContext()));
		Day day;
		
		switch (currentMode) {
		case DailyNotes:
			day = datasourceFactory.createDayFactory().get(selectedDate, true);
			text = day.getDailyNotes();
			editText.setHint(getString(R.string.hint_daily_notes_here));
			break;
		case TaskNotes:
			day = datasourceFactory.createDayFactory().get(selectedDate, true);
			TaskDay taskDay = datasourceFactory.createTaskDayFactory().get(selectedTask, day, true);
			text = taskDay.getNotes();
			editText.setHint(getString(R.string.hint_task_notes_here));
			break;
		case ClientAddress:
			if (selectedClient != null)
				text = selectedClient.getEmail();
			
			editText.setHint(getString(R.string.hint_client_address_here));
			break;
		case ExpenseNotes:
			if (selectedExpense != null)
				text = selectedExpense.getNotes();
		}
		
		editText.setText(text);
	}

	/** Prepares the action bars by setting the menu item, name, etc. */
	private void prepareActionBar() {
		Log.d(getLocalClassName(), "prepareActionBar");

		ActionBar actionBar = getActionBar();
        
		setActionBarContentView(R.layout.daily_notes);
        actionBar.setType(ActionBar.Type.Normal);
		
        TextView v = (TextView)findViewById(com.cyrilmottier.android.greendroid.R.id.gd_action_bar_title);
        v.setTypeface(FontUtil.getTypeface(getBaseContext()));
	}

	/**
	 * Adds click listeners to the save and cancel buttons.
	 */
	private void prepareButtons() {
        Log.d(getLocalClassName(), "prepareButtons");

		Button b = null;
		
		b = (Button)findViewById(R.id.button_save);
		b.setClickable(true);
		b.setTypeface(FontUtil.getTypeface(getBaseContext()));
		b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d(getLocalClassName(), "onClick(button=save)");

				EditText editText = (EditText)findViewById(R.id.textbox_notes);
				String text = editText.getText().toString();
				Day day;
				
				switch (currentMode) {
				case DailyNotes:
					DayFactory df = datasourceFactory.createDayFactory();
					day = df.get(selectedDate, true);
					df.updateNotes(day, text);
					break;
					
				case TaskNotes:
					day = datasourceFactory.createDayFactory().get(selectedDate, true);
					TaskDay taskDay = datasourceFactory.createTaskDayFactory().get(selectedTask, day, true);
					taskDay.setNotes(text);
					datasourceFactory.createTaskDayFactory().update(taskDay);
					break;
					
				case ClientAddress:
					// do nothing for this one, let the caller save it, or not.
					break;
					
				case ExpenseNotes:
					// do nothing for this one, let the caller save it, or not.
					break;
					
				}
				
				closeActivity(RESULT_OK, text);
			}
		});
		
		b = (Button)findViewById(R.id.button_cancel);
		b.setClickable(true);
		b.setTypeface(FontUtil.getTypeface(getBaseContext()));
		b.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {				
				Log.d(getLocalClassName(), "onClick(button=cancel)");

				closeActivity(RESULT_CANCELED, null);
			}
		});
	}

	/**
	 * Closes the activity, and sends an intent back to the calling activity.
	 * @param result
	 * @param notes
	 */
	private void closeActivity(int result, String notes) {
		Log.d(getLocalClassName(), "closeActivity(notes=" + notes + ")");

		Intent intent = new Intent();
		intent.putExtra(NotesActivity.class.getName() + ".saved", (result == RESULT_OK));
		intent.putExtra(NotesActivity.class.getName() + ".notes", notes);
		setResult(result, intent);
		
		// TODO: the update day notes needs to accept an async callback for when 
		// calling a live database, then it should finish.
		finish();
	}

	/**
	 * Captures the back key and send a cancel intent.
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.d(getLocalClassName(), "onKeyDown(keyCode=" + keyCode + ")");

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			closeActivity(RESULT_CANCELED, null);
		}
		
		return super.onKeyDown(keyCode, event);
	}
	
}
