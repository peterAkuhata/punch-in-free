package com.aku.apps.punchin.free;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.aku.apps.punchin.free.utils.FontUtil;
import com.aku.apps.punchin.free.widgets.greendroid.OptionItem;
import com.aku.apps.punchin.free.widgets.greendroid.PunchInSeparatorItem;

import greendroid.app.GDActivity;
import greendroid.widget.ItemAdapter;
import greendroid.widget.item.Item;

public class ReportsActivity extends GDActivity {

	/**
	 * Prepares the activity for viewing.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.d(getLocalClassName(), "onCreate");

		prepareActionBar();
		prepareListView();
	}

	/** Prepares the action bars by setting the menu item, name, etc. */
	private void prepareActionBar() {
		Log.d(getLocalClassName(), "prepareActionBar");

		setActionBarContentView(R.layout.reports);
		
        TextView v = (TextView)findViewById(com.cyrilmottier.android.greendroid.R.id.gd_action_bar_title);
        v.setTypeface(FontUtil.getTypeface(getBaseContext()));
	}

	/**
	 * Adds all items to the listview and sets up the item click listener.
	 */
	private void prepareListView() {
		Log.d(getLocalClassName(), "prepareListView");

		final ListView lv = (ListView) findViewById(R.id.listview_list);
		ArrayList<Item> items = new ArrayList<Item>();
		items.add(new PunchInSeparatorItem(getString(R.string.label_timesheets)));
		items.add(new OptionItem(getString(R.string.label_timesheet_report), getString(R.string.label_timesheet_report_description)));
		items.add(new PunchInSeparatorItem(getString(R.string.label_tasks)));
		items.add(new OptionItem(getString(R.string.label_task_list_report), getString(R.string.label_task_list_report_description)));
		items.add(new PunchInSeparatorItem(getString(R.string.label_clients)));
		items.add(new OptionItem(getString(R.string.label_client_list_report), getString(R.string.label_client_list_report_description)));
 		items.add(new OptionItem(getString(R.string.label_income_report), getString(R.string.label_income_report_description)));

		lv.setAdapter(new ItemAdapter(lv.getContext(), items));
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {
				switch (position) {
				case 1: // timesheet report
					showTimesheetReport();
					break;
					
				case 3: // task list report
					showTaskListReport();
					break;
					
				case 5: // client list report
					showClientListReport();
					break;
					
				case 6: // income by client report
					showIncomeByClientReport();
					break;
				}
			}
		});
	}

	/**
	 * Opens the task list report activity.
	 */
	protected void showTaskListReport() {
		Intent intent = new Intent();
		intent.setClassName("com.aku.apps.punchin.free", TaskListReportActivity.class.getName());
		startActivity(intent);
	}

	/**
	 * Opens the client list report activity.
	 */
	protected void showClientListReport() {
		Intent intent = new Intent();
		intent.setClassName("com.aku.apps.punchin.free", ClientListReportActivity.class.getName());
		startActivity(intent);
	}

	/**
	 * Opens the income by client report activity.
	 */
	protected void showIncomeByClientReport() {
		Intent intent = new Intent();
		intent.setClassName("com.aku.apps.punchin.free", IncomeReportActivity.class.getName());
		startActivity(intent);
	}

	/**
	 * Opens up the punchin report activity.
	 */
	protected void showTimesheetReport() {
		Intent intent = new Intent();
		intent.setClassName("com.aku.apps.punchin.free", TimesheetReportActivity.class.getName());
		startActivity(intent);
	}
}
