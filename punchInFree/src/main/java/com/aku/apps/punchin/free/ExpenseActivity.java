package com.aku.apps.punchin.free;

import java.text.NumberFormat;
import java.util.ArrayList;
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
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.aku.apps.punchin.free.db.DatasourceFactory;
import com.aku.apps.punchin.free.db.DatasourceFactoryFacade;
import com.aku.apps.punchin.free.db.ExpenseFactory;
import com.aku.apps.punchin.free.db.TaskFactory;
import com.aku.apps.punchin.free.domain.Day;
import com.aku.apps.punchin.free.domain.Expense;
import com.aku.apps.punchin.free.domain.Expense.ExpenseType;
import com.aku.apps.punchin.free.domain.Preferences;
import com.aku.apps.punchin.free.domain.Task;
import com.aku.apps.punchin.free.domain.TaskDay;
import com.aku.apps.punchin.free.utils.Constants;
import com.aku.apps.punchin.free.utils.FontUtil;
import com.aku.apps.punchin.free.widgets.greendroid.OptionItem;
import com.aku.apps.punchin.free.widgets.greendroid.PunchInSeparatorItem;
import greendroid.app.GDActivity;
import greendroid.widget.ItemAdapter;
import greendroid.widget.item.Item;

public class ExpenseActivity extends GDActivity {

	/**
	 * The database factory
	 */
	private DatasourceFactory datasourceFactory;

	/**
	 * Progress dialog that shows a loading icon to the user.
	 */
	private ProgressDialog progressDialog;

	/**
	 * The task that the expense is linked to.
	 */
	private Task task = null;

	/**
	 * The expense to display (if the activity is in edit mode).
	 */
	private Expense expense = null;

	/**
	 * The date that the expense is linked to.
	 */
	private Date date = null;

	private ExpenseType expenseType = ExpenseType.COSTING;

	private double amount = 0;
	private OptionItem costOption = null;

	private String notes = "";
	private OptionItem notesOption = null;

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

		switch (id) {
		case Constants.Dialogs.DISTANCE_TRAVELLED:
			return createDistanceTravelledDialog();

		case Constants.Dialogs.COSTING_AMOUNT:
			return createCostingAmountDialog();
		}

		return super.onCreateDialog(id, args);
	}

	/**
	 * Creates and returns the costing amount dialog.
	 * @return
	 */
	private Dialog createCostingAmountDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		LayoutInflater factory = LayoutInflater.from(this);
		View textEntryView = null;
		textEntryView = factory.inflate(R.layout.dialog_hourly_rate, null);

		final EditText costingAmt = (EditText) textEntryView.findViewById(R.id.textbox_hourly_rate);
		costingAmt.setText(Double.toString(amount));

		final AlertDialog dialog = builder
			.setTitle(getString(R.string.label_cost))
			.setView(textEntryView)
			.setPositiveButton(R.string.label_set, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface d, int whichButton) {
					amount = Double.parseDouble(costingAmt.getText().toString());
					String formattedAmount = getFormattedCosting(amount);
					costOption.subtitle = formattedAmount;
					ListView lv = (ListView) findViewById(R.id.listview_repeating_options);
					((ItemAdapter) lv.getAdapter()).notifyDataSetChanged();
					costingAmt.selectAll();
					getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
				}
			})
			.setNegativeButton(R.string.label_cancel, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					costingAmt.selectAll();
					getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
				}
			})
			.create();

		costingAmt.setOnFocusChangeListener(new OnFocusChangeListener() {
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
	 * Returns the distance travelled dialog.
	 * @return
	 */
	private Dialog createDistanceTravelledDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		LayoutInflater factory = LayoutInflater.from(this);
		View textEntryView = null;
		textEntryView = factory.inflate(R.layout.dialog_hourly_rate, null);

		final EditText distanceTravelled = (EditText) textEntryView.findViewById(R.id.textbox_hourly_rate);
		distanceTravelled.setText(Double.toString(amount));

		final AlertDialog dialog = builder
			.setTitle(getString(R.string.label_distance_travelled))
			.setView(textEntryView)
			.setPositiveButton(R.string.label_set, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface d, int whichButton) {
					amount = Double.parseDouble(distanceTravelled.getText().toString());
					String formattedMileage = getFormattedMileage(amount);
					costOption.subtitle = formattedMileage;
					ListView lv = (ListView) findViewById(R.id.listview_repeating_options);
					((ItemAdapter) lv.getAdapter()).notifyDataSetChanged();
					distanceTravelled.selectAll();
					getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
				}
			})
			.setNegativeButton(R.string.label_cancel, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					distanceTravelled.selectAll();
					getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
				}
			})
			.create();

		distanceTravelled.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
				}
			}
		});

		return dialog;
	}

	/** Prepares the action bars by setting the menu item, name, etc. */
	private void prepareActionBar() {
		Log.d(getLocalClassName(), "prepareActionBar");

		setActionBarContentView(R.layout.expense);
		
        TextView v = (TextView)findViewById(com.cyrilmottier.android.greendroid.R.id.gd_action_bar_title);
        v.setTypeface(FontUtil.getTypeface(getBaseContext()));
	}

	/**
	 * Gets the selected date from the intent extras.
	 */
	private void extractIntentData() {
		Log.d(getLocalClassName(), "extractIntentData");

		Intent intent = getIntent();
		long expenseId = intent.getLongExtra(ExpenseActivity.class.getName() + ".expenseId", -1);
		long taskId = intent.getLongExtra(ExpenseActivity.class.getName() + ".taskId", -1);

		date = (Date)intent.getSerializableExtra(ExpenseActivity.class.getName() + ".currentDate");
		expenseType = (ExpenseType) intent.getSerializableExtra(ExpenseActivity.class.getName() + ".type");

		TaskFactory taskFactory = datasourceFactory.createTaskFactory();
		task = taskFactory.get(taskId);

		if (expenseId != -1) {
			ExpenseFactory factory = datasourceFactory.createExpenseFactory();
			expense = factory.get(expenseId);
			expenseType = expense.getType();
			amount = expense.getAmount();
			notes = expense.getNotes();

			if (expenseType == ExpenseType.MILEAGE)
				setTitle(getString(R.string.label_edit_mileage));
			else
				setTitle(getString(R.string.label_edit_costing));

		} else {
			if (expenseType == ExpenseType.MILEAGE)
				setTitle(getString(R.string.label_add_mileage));
			else
				setTitle(getString(R.string.label_add_costing));

			amount = 0;
			notes = "";
		}
	}

	/**
	 * Returns a formatted description of the specified expense type.
	 * 
	 * @param type
	 * @return
	 */
	private String getExpenseTypeTitle(ExpenseType type) {
		switch (type) {

		case MILEAGE:
			return getString(R.string.label_mileage_details);

		default:
			return getString(R.string.label_cost_details);
		}
	}

	/**
	 * Adds all items to the listview and sets up the item click listener.
	 */
	private void prepareListView() {
		Log.d(getLocalClassName(), "prepareListView");

		ArrayList<Item> items = new ArrayList<Item>();
		items.add(new PunchInSeparatorItem(getExpenseTypeTitle(expenseType)));

		switch (expenseType) {
		case COSTING:
			costOption = new OptionItem(getString(R.string.label_cost), getFormattedExpenseType(expense, expenseType));
			break;
			
		case MILEAGE:
			costOption = new OptionItem(getString(R.string.label_distance_travelled), getFormattedExpenseType(expense, expenseType));
			break;
		}

		notesOption = new OptionItem(getString(R.string.label_notes), getFormattedNotes());
		items.add(costOption);
		items.add(notesOption);

		final ListView lv = (ListView) findViewById(R.id.listview_repeating_options);
		lv.setAdapter(new ItemAdapter(lv.getContext(), items));
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {
				switch (position) {
				case 1: // either mileage or amount, depending on type
					if (expenseType == ExpenseType.COSTING)
						showDialog(Constants.Dialogs.COSTING_AMOUNT);
					else
						showDialog(Constants.Dialogs.DISTANCE_TRAVELLED);
					break;
				case 2: // notes
					editNotes(expense);
					break;
				}
			}
		});
	}
	
	/**
	 * Checks whether this is either a costing or mileage expense, and
	 * returns the appropriate formatted string.
	 * 
	 * @param expense
	 * @return
	 */
	private String getFormattedExpenseType(Expense expense, ExpenseType type) {
		String temp = "";
		
		switch (expense == null ? type : expense.getType()) {
		case COSTING:
			if (expense != null) 
				temp = getFormattedCosting(expense.getAmount());
			else
				temp = getFormattedCosting(0);
			
		case MILEAGE:
			if (expense != null)
				temp = getFormattedMileage(expense.getAmount());
			else
				temp = getFormattedMileage(0);
		}
		
		return temp;
	}

	/**
	 * Returns a string representation of the mileage variable.
	 * 
	 * @return
	 */
	private String getFormattedMileage(double amount) {
		Log.d(getLocalClassName(), "getFormattedMileage");

		Preferences prefs = datasourceFactory.createPreferences();
		String postfix = prefs.getDefaultMileageUnit();
		String value = "";

		if (amount == 0)
			value = getString(R.string.label_none);
		else
			value = NumberFormat.getNumberInstance().format(amount) + " " + postfix;

		return value;
	}

	/**
	 * Returns a string representation of the amount variable.
	 * 
	 * @return
	 */
	private String getFormattedCosting(double amount) {
		Log.d(getLocalClassName(), "getFormattedAmount");

		return NumberFormat.getCurrencyInstance().format(amount);
	}

	/**
	 * Returns a string representation of the notes variable.
	 * 
	 * @return
	 */
	private String getFormattedNotes() {
		Log.d(getLocalClassName(), "getFormattedNotes");

		String value = "";

		if (notes == null || notes.length() == 0)
			value = getString(R.string.label_no_notes);
		else
			value = notes;

		return value;
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

				saveExpense();
			}
		});

		b = (Button) findViewById(R.id.button_cancel);
		b.setClickable(true);
		b.setTypeface(FontUtil.getTypeface(getBaseContext()));
		b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d(getLocalClassName(), "onClick(cancel button)");

				cancelExpense();
			}
		});
	}

	/**
	 * Captures the back key and send a cancel intent.
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.d(getLocalClassName(), "onKeyDown");

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			cancelExpense();
		}

		return super.onKeyDown(keyCode, event);
	}

	/**
	 * Cancels this activity and closes it.
	 */
	private void cancelExpense() {
		Log.d(getLocalClassName(), "cancelExpense");

		Intent intent = new Intent();
		intent.putExtra(ExpenseActivity.class.getName() + ".expenseId", -1);
		setResult(RESULT_CANCELED, intent);
		finish();
	}

	/**
	 * Saves a new expense and closes the activity.
	 */
	protected void saveExpense() {
		Log.d(getLocalClassName(), "saveExpense");

		progressDialog = new ProgressDialog(this);
		progressDialog.setTitle(getString(R.string.label_saving));
		progressDialog.show();

		SaveExpenseAction t = new SaveExpenseAction();
		t.execute();
	}

	/**
	 * Shows a ui dialog that allows the user to edit the expense notes for the
	 * current day.
	 */
	private void editNotes(Expense expense) {
		Log.d(getLocalClassName(), "editNotes");

		Intent intent = new Intent();
		intent.setClassName("com.aku.apps.punchin.free", NotesActivity.class.getName());
		intent.putExtra(NotesActivity.class.getName() + ".currentDate", date);
		intent.putExtra(NotesActivity.class.getName() + ".expenseId", (expense == null ? -1 : expense.getId()));
		intent.putExtra(NotesActivity.class.getName() + ".type", expenseType);
		intent.putExtra(NotesActivity.class.getName() + ".currentMode", NotesActivity.ExpenseNotes);
		startActivityForResult(intent, Constants.RequestCodes.NOTES);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(getLocalClassName(), "onActivityResult(requestCode=" + requestCode + ", resultCode=" + resultCode);

		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case Constants.RequestCodes.NOTES:
			boolean saved = data.getBooleanExtra(NotesActivity.class.getName() + ".saved", false);

			if (saved) {
				notes = data.getStringExtra(NotesActivity.class.getName() + ".notes");
				notesOption.subtitle = getFormattedNotes();
				ListView lv = (ListView) findViewById(R.id.listview_repeating_options);
				((ItemAdapter) lv.getAdapter()).notifyDataSetChanged();
			}
			break;
		}
	}

	/**
	 * The async task to save the new expense to the database and to close the
	 * activity.
	 * 
	 * @author Peter Akuhata
	 * 
	 */
	private class SaveExpenseAction extends AsyncTask<String, Void, Expense> {
		@Override
		protected Expense doInBackground(String... args) {
			Log.d(getLocalClassName(), "doInBackground");

			ExpenseFactory factory = datasourceFactory.createExpenseFactory();
			Expense e = null;

			if (expense != null) {
				expense.setType(expenseType);
				expense.setAmount(amount);
				expense.setNotes(notes);

				factory.update(expense);
				e = expense;

			} else {
				Day day = datasourceFactory.createDayFactory().get(date, true);
				TaskDay taskDay = datasourceFactory.createTaskDayFactory().get(task, day, true);
				e = factory.add(taskDay, expenseType, amount, notes);

			}

			return e;
		}

		@Override
		protected void onPostExecute(Expense result) {
			super.onPostExecute(result);

			Log.d(getLocalClassName(), "onPostExecute");

			if (progressDialog != null)
				progressDialog.dismiss();

			Intent intent = new Intent();
			intent.putExtra(ExpenseActivity.class.getName() + ".expenseId",
					result.getId());
			setResult(RESULT_OK, intent);
			finish();
		}
	}
}
