package com.aku.apps.punchin.free;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.aku.apps.punchin.free.db.DatasourceFactory;
import com.aku.apps.punchin.free.db.DatasourceFactoryFacade;
import com.aku.apps.punchin.free.db.ExpenseFactory;
import com.aku.apps.punchin.free.db.TaskFactory;
import com.aku.apps.punchin.free.domain.Day;
import com.aku.apps.punchin.free.domain.Expense;
import com.aku.apps.punchin.free.domain.Preferences;
import com.aku.apps.punchin.free.domain.Task;
import com.aku.apps.punchin.free.domain.Expense.ExpenseType;
import com.aku.apps.punchin.free.domain.TaskDay;
import com.aku.apps.punchin.free.utils.Constants;
import com.aku.apps.punchin.free.utils.FontUtil;
import com.aku.apps.punchin.free.widgets.greendroid.PunchInDescriptionItem;
import com.aku.apps.punchin.free.widgets.greendroid.PunchInSeparatorItem;

import greendroid.app.GDActivity;
import greendroid.widget.ActionBar;
import greendroid.widget.ItemAdapter;
import greendroid.widget.item.Item;

public class ExpensesActivity extends GDActivity {

	/**
	 * Creates factory objects.
	 */
	private DatasourceFactory datasourceFactory;

	/**
	 * Value that defines whether the list of activities have been sorted at
	 * least once.
	 */
	private boolean hasChangesBeenMade = false;

	private Task task = null;
	private Date date = null;

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.d(getLocalClassName(), "onCreate");

		datasourceFactory = DatasourceFactoryFacade.getInstance(this);
		extractIntentData();
		prepareActionBar();
		prepareListView();
		prepareButtons();
	}

	/**
	 * Gets the selected date from the intent extras.
	 */
	void extractIntentData() {
		Log.d(getLocalClassName(), "extractIntentData");

		Intent intent = getIntent();
		long taskId = intent.getLongExtra(ExpensesActivity.class.getName() + ".taskId", -1);

		TaskFactory factory = datasourceFactory.createTaskFactory();
		task = factory.get(taskId);
		date = (Date)intent.getSerializableExtra(ExpensesActivity.class.getName()+ ".currentDate");
	}

	/**
	 * Sets up the two buttons to show the daily event list and to edit the
	 * daily notes.
	 */
	private void prepareButtons() {
		Button b = null;

		b = (Button) findViewById(R.id.button_add_mileage);
		b.setClickable(true);
		b.setTypeface(FontUtil.getTypeface(getBaseContext()));
		b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				addExpense(ExpenseType.MILEAGE);
			}
		});

		b = (Button) findViewById(R.id.button_add_costing);
		b.setClickable(true);
		b.setTypeface(FontUtil.getTypeface(getBaseContext()));
		b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				addExpense(ExpenseType.COSTING);
			}
		});
	}

	/**
	 * Opens the expense activity in add mode.
	 */
	private void addExpense(ExpenseType type) {
		Intent intent = new Intent();
		
		intent.setClassName("com.aku.apps.punchin.free", ExpenseActivity.class.getName());		
		intent.putExtra(ExpenseActivity.class.getName() + ".currentDate", date);		
		intent.putExtra(ExpenseActivity.class.getName() + ".taskId", task.getId());		
		intent.putExtra(ExpenseActivity.class.getName() + ".type", type);
		
		startActivityForResult(intent, Constants.RequestCodes.EDIT_EXPENSE);
	}

	/**
	 * Adds clients the the listview, sets the listeners.
	 */
	private void prepareListView() {
		Log.d(getLocalClassName(), "prepareListView");

		final ListView lv = (ListView) findViewById(R.id.listview_expenses);

		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long arg3) {
				ItemAdapter adapter = (ItemAdapter) lv.getAdapter();
				Item item = (Item) adapter.getItem(position);
				Object tag = item.getTag();
				
				if (tag != null) {
					Expense expense = (Expense) item.getTag();
					editExpense(expense);
				}
			}
		});

		refreshListView();
	}

	/**
	 * Refreshes the list of expenses.
	 */
	private void refreshListView() {
		Log.d(getLocalClassName(), "refreshListView");

		ListView lv = (ListView) findViewById(R.id.listview_expenses);

		ExpenseFactory ef = datasourceFactory.createExpenseFactory();
		Day day = datasourceFactory.createDayFactory().get(date, true);
		TaskDay taskDay = datasourceFactory.createTaskDayFactory().get(task, day, true);
		ArrayList<Expense> list = ef.getListByTaskDay(taskDay);
		ArrayList<Item> items = new ArrayList<Item>();

		items.add(new PunchInSeparatorItem(getString(R.string.label_expense_list)));

		if (list.size() > 0) {
			for (Expense item : list) {
				PunchInDescriptionItem d = new PunchInDescriptionItem(getFormattedExpenseType(item));
				d.enabled = true;
				d.setTag(item);
				items.add(d);
			}
		} else {
			PunchInDescriptionItem d = new PunchInDescriptionItem(getString(R.string.label_no_expenses_found));
			items.add(d);
		}
		ItemAdapter adapter = new ItemAdapter(getBaseContext(), items);
		lv.setAdapter(adapter);
	}
	
	/**
	 * Checks whether this is either a costing or mileage expense, and
	 * returns the appropriate formatted string.
	 * 
	 * @param expense
	 * @return
	 */
	private String getFormattedExpenseType(Expense expense) {
		String temp = "";
		
		switch (expense.getType()) {
		case COSTING:
			return getFormattedCosting(expense.getAmount(), expense.getNotes());
			
		case MILEAGE:
			return getFormattedMileage(expense.getAmount(), expense.getNotes());
		}
		
		return temp;
	}

	/**
	 * Returns a string representation of the mileage variable.
	 * 
	 * @return
	 */
	private String getFormattedMileage(double amount, String notes) {
		Log.d(getLocalClassName(), "getFormattedMileage");

		Preferences prefs = datasourceFactory.createPreferences();
		String postfix = prefs.getDefaultMileageUnit();
		String temp = getString(R.string.label_mileage_expense) + "\r\n";

		temp += getString(R.string.label_distance_travelled) + ": " + NumberFormat.getNumberInstance().format(amount) + " " + postfix + "\r\n";
		temp += getString(R.string.label_notes) + ": " + notes;

		return temp;
	}

	/**
	 * Returns a string representation of the amount variable.
	 * 
	 * @return
	 */
	private String getFormattedCosting(double amount, String notes) {
		String temp = getString(R.string.label_costing_expense) + "\r\n";
		
		temp += getString(R.string.label_cost) + ": " + NumberFormat.getCurrencyInstance().format(amount) + "\r\n";
		temp += getString(R.string.label_notes) + ": " + notes;
		
		return temp;	
	}

	/**
	 * Gets the results of editing a client, reset the mHasChangesBeenMade
	 * variable.
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case Constants.RequestCodes.EDIT_EXPENSE:
			long expenseId = data.getLongExtra(ExpenseActivity.class.getName()
					+ ".expenseId", -1);

			if (!hasChangesBeenMade)
				hasChangesBeenMade = (expenseId != -1);

			if (expenseId != -1) {
				refreshListView();
			}

			getWindow().setSoftInputMode(
					WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
			break;
		}
	}

	/**
	 * Opens the expense activity in edit mode.
	 * 
	 * @param expense
	 */
	private void editExpense(Expense expense) {
		Log.d(getLocalClassName(), "editExpense");

		Intent intent = new Intent();
		intent.setClassName("com.aku.apps.punchin.free", ExpenseActivity.class.getName());

		intent.putExtra(ExpenseActivity.class.getName() + ".currentDate", date);
		intent.putExtra(ExpenseActivity.class.getName() + ".taskId", task.getId());
		intent.putExtra(ExpenseActivity.class.getName() + ".expenseId", expense.getId());		
		intent.putExtra(ExpenseActivity.class.getName() + ".type", expense.getType());

		startActivityForResult(intent, Constants.RequestCodes.EDIT_EXPENSE);
	}

	/**
	 * Prepares the action bars by setting the menu item, name, etc.
	 */
	private void prepareActionBar() {
		Log.d(getLocalClassName(), "prepareActionBar");

		setActionBarContentView(R.layout.expenses);
		ActionBar actionBar = getActionBar();
		actionBar.setType(ActionBar.Type.Empty);
		
        TextView v = (TextView)findViewById(com.cyrilmottier.android.greendroid.R.id.gd_action_bar_title);
        v.setTypeface(FontUtil.getTypeface(getBaseContext()));
	}

	/**
	 * Captures the back key and sets the return data.
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (hasChangesBeenMade) {
				Intent intent = new Intent();
				intent.putExtra(ExpensesActivity.class.getName()
						+ ".hasBeenSortedOnce", hasChangesBeenMade);
				setResult(RESULT_OK, intent);
			}
		}

		return super.onKeyDown(keyCode, event);
	}
}