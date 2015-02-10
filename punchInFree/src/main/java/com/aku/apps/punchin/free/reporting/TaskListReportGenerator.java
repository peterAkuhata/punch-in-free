package com.aku.apps.punchin.free.reporting;

import java.text.DateFormat;
import java.util.ArrayList;

import android.content.Context;
import android.util.Log;

import com.aku.apps.punchin.free.R;
import com.aku.apps.punchin.free.db.ClientFactory;
import com.aku.apps.punchin.free.db.DatasourceFactory;
import com.aku.apps.punchin.free.db.DatasourceFactoryFacade;
import com.aku.apps.punchin.free.db.TaskFactory;
import com.aku.apps.punchin.free.domain.Client;
import com.aku.apps.punchin.free.domain.RequiresContext;
import com.aku.apps.punchin.free.domain.Task;
import com.aku.apps.punchin.free.utils.TaskUtil;

public class TaskListReportGenerator implements ReportGenerator,
		RequiresContext {

	private DatasourceFactory datasourceFactory;
	private Context context = null;
	private ArrayList<String> selectedColumns = null;

	@Override
	public Report generate(ArrayList<String> selectedColumns) {
		Log.d(ReportManager.class.getSimpleName(), "generate");
		
		this.selectedColumns = selectedColumns;
		
		TaskFactory tf = datasourceFactory.createTaskFactory();
		ClientFactory cf = datasourceFactory.createClientFactory();
		
		ArrayList<Task> tasks = tf.getList(false);
		ArrayList<String> columns = createColumns();
		ArrayList<Row> rows = new ArrayList<Row>();

		for (Task task : tasks) {
			ArrayList<String> row = new ArrayList<String>();
			Client client = cf.get(task.getClientId());
			
			if (addIdColumn())
				row.add(String.valueOf(task.getId()));
			
			if (addDescriptionColumn())
				row.add(task.getDescription());

			if (addRepeatingColumn())
				row.add(TaskUtil.getRepeatingOptions(context)[TaskUtil.toRepeatingInt(task.getRepeatingType())]);
			
			if (addClientColumn()) {
				if (client == null)
					row.add(context.getString(R.string.label_none));
				else
					row.add(client.getName());
			}
			
			if (addStartDateColumn())
				row.add(DateFormat.getDateInstance().format(task.getStartDate()));
			
			if (addActiveColumn())
				row.add(task.getActive() ? context.getString(R.string.label_active) : context.getString(R.string.label_inactive));
			
			rows.add(new Row(row));
		}

		String name = context.getString(R.string.label_task_list_report);
		String description = context.getString(R.string.label_task_list_report_description);

		Report r = new Report(name, description, columns, rows);

		return r;
	}

	/**
	 * Returns the list of columns for this report.
	 * 
	 * @param columns
	 */
	private ArrayList<String> createColumns() {
		Log.d(ReportManager.class.getSimpleName(), "createColumns");

		ArrayList<String> columns = new ArrayList<String>();

		if (addIdColumn())
			columns.add(context.getString(R.string.label_id));

		if (addDescriptionColumn())
			columns.add(context.getString(R.string.label_description));

		if (addRepeatingColumn())
			columns.add(context.getString(R.string.label_repeating_option));

		if (addClientColumn())
			columns.add(context.getString(R.string.label_client));

		if (addStartDateColumn())
			columns.add(context.getString(R.string.label_start_date));

		if (addActiveColumn())
			columns.add(context.getString(R.string.label_active));
		
		return columns;
	}

	/**
	 * Contains the list of available columns.
	 */
	private static ArrayList<String> mAvailableColumns = new ArrayList<String>();

	/**
	 * Creates and returns the list of available columns for this report.
	 * 
	 * @param ctx
	 * @return
	 */
	public static ArrayList<String> getAvailableColumns(Context ctx) {
		if (mAvailableColumns.size() == 0) {
			mAvailableColumns.add(ctx.getString(R.string.label_id));
			mAvailableColumns.add(ctx.getString(R.string.label_description));
			mAvailableColumns.add(ctx.getString(R.string.label_repeating_option));
			mAvailableColumns.add(ctx.getString(R.string.label_client));
			mAvailableColumns.add(ctx.getString(R.string.label_start_date));
			mAvailableColumns.add(ctx.getString(R.string.label_active));
		}

		return mAvailableColumns;
	}
	
	/**
	 * Returns the columns as a comma separated list.
	 * @param ctx
	 * @return
	 */
	public static String getAvailableColumnsCommaSeparated(Context ctx) {
		String temp = "";
		ArrayList<String> list = getAvailableColumns(ctx);
		int i = 0;
		
		for (String item : list) {
			if (i > 0)
				temp += ",";
			
			temp += item;
			i++;
		}
		
		return temp;
	}

	/**
	 * Returns whether the hourly rate column is in the list columns to use in
	 * the report.
	 * 
	 * @return
	 */
	private boolean addClientColumn() {
		return addColumn(context.getString(R.string.label_client));
	}

	/**
	 * Returns whether the client column is in the list columns to use in the
	 * report.
	 * 
	 * @return
	 */
	private boolean addRepeatingColumn() {
		return addColumn(context.getString(R.string.label_repeating_option));
	}

	/**
	 * Returns whether the client column is in the list columns to use in the
	 * report.
	 * 
	 * @return
	 */
	private boolean addActiveColumn() {
		return addColumn(context.getString(R.string.label_active));
	}

	/**
	 * Returns whether the client column is in the list columns to use in the
	 * report.
	 * 
	 * @return
	 */
	private boolean addStartDateColumn() {
		return addColumn(context.getString(R.string.label_start_date));
	}

	/**
	 * Returns whether the client column is in the list columns to use in the
	 * report.
	 * 
	 * @return
	 */
	private boolean addDescriptionColumn() {
		return addColumn(context.getString(R.string.label_description));
	}

	/**
	 * Returns whether the id column is in the list columns to use in the
	 * report.
	 * 
	 * @return
	 */
	private boolean addIdColumn() {
		return addColumn(context.getString(R.string.label_id));
	}

	/**
	 * Returns whether the specified name is in the list columns to use in the
	 * report.
	 * 
	 * @param name
	 * @return
	 */
	private boolean addColumn(String name) {
		boolean add = true;

		if (selectedColumns != null) {
			add = selectedColumns.contains(name);
		}

		return add;
	}

	@Override
	public void setContext(Context ctx) {
		context = ctx;
		
		this.datasourceFactory = DatasourceFactoryFacade.getInstance(ctx);
	}
}
