package com.aku.apps.punchin.free.reporting;

import java.text.NumberFormat;
import java.util.ArrayList;
import com.aku.apps.punchin.free.R;
import com.aku.apps.punchin.free.db.ClientFactory;
import com.aku.apps.punchin.free.db.DatasourceFactory;
import com.aku.apps.punchin.free.db.DatasourceFactoryFacade;
import com.aku.apps.punchin.free.domain.Client;
import com.aku.apps.punchin.free.domain.RequiresContext;

import android.content.Context;
import android.util.Log;

/**
 * Responsible for generating the client list report. 
 * @author Peter Akuhata
 *
 */
public class ClientListReportGenerator implements ReportGenerator, RequiresContext {

	private DatasourceFactory datasourceFactory;
	private Context context = null;
	private ArrayList<String> selectedColumns = null;

	@Override
	public Report generate(ArrayList<String> selectedColumns) {
		Log.d(ReportManager.class.getSimpleName(), "generate");
		
		this.selectedColumns = selectedColumns;
		
		ClientFactory cf = datasourceFactory.createClientFactory();

		ArrayList<Client> clients = cf.getList();
		ArrayList<String> columns = createColumns();
		ArrayList<Row> rows = new ArrayList<Row>();

		for (Client client : clients) {
			double hourlyRate = client.getHourlyRate();
			
			ArrayList<String> row = new ArrayList<String>();
			
			if (addIdColumn())
				row.add(String.valueOf(client.getId()));
			
			if (addNameColumn())
				row.add(client.getName());

			if (addEmailColumn())
				row.add(client.getEmail());
			
			if (addMobileColumn())
				row.add(client.getMobile());
			
			double overtimeRate = hourlyRate * client.getOvertimeMultiplier();
			
			if (addHourlyRateColumn())
				row.add(NumberFormat.getCurrencyInstance().format(hourlyRate));
			
			if (addMileageRateColumn())
				row.add(NumberFormat.getCurrencyInstance().format(client.getMileageRate()));
			
			if (addOvertimeRateColumn())
				row.add(NumberFormat.getCurrencyInstance().format(overtimeRate));
			
			if (addActiveColumn())
				row.add(client.getActive() ? context.getString(R.string.label_active) : context.getString(R.string.label_inactive));
			
			rows.add(new Row(row));
		}

		String name = context.getString(R.string.label_client_list_report);
		String description = context.getString(R.string.label_client_list_report_description);

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

		if (addNameColumn())
			columns.add(context.getString(R.string.label_name));

		if (addEmailColumn())
			columns.add(context.getString(R.string.label_email));

		if (addMobileColumn())
			columns.add(context.getString(R.string.label_mobile));

		if (addHourlyRateColumn())
			columns.add(context.getString(R.string.label_hourly_rate));

		if (addMileageRateColumn())
			columns.add(context.getString(R.string.label_mileage_rate));

		if (addOvertimeRateColumn())
			columns.add(context.getString(R.string.label_overtime_rate));

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
			mAvailableColumns.add(ctx.getString(R.string.label_name));
			mAvailableColumns.add(ctx.getString(R.string.label_email));
			mAvailableColumns.add(ctx.getString(R.string.label_mobile));
			mAvailableColumns.add(ctx.getString(R.string.label_hourly_rate));
			mAvailableColumns.add(ctx.getString(R.string.label_mileage_rate));
			mAvailableColumns.add(ctx.getString(R.string.label_overtime_rate));
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
	 * Returns whether the overtime rate column is in the list columns to use in
	 * the report.
	 * 
	 * @return
	 */
	private boolean addOvertimeRateColumn() {
		return addColumn(context.getString(R.string.label_overtime_rate));
	}

	/**
	 * Returns whether the mileage rate column is in the list columns to use in
	 * the report.
	 * 
	 * @return
	 */
	private boolean addMileageRateColumn() {
		return addColumn(context.getString(R.string.label_mileage_rate));
	}

	/**
	 * Returns whether the hourly rate column is in the list columns to use in
	 * the report.
	 * 
	 * @return
	 */
	private boolean addHourlyRateColumn() {
		return addColumn(context.getString(R.string.label_hourly_rate));
	}

	/**
	 * Returns whether the client column is in the list columns to use in the
	 * report.
	 * 
	 * @return
	 */
	private boolean addNameColumn() {
		return addColumn(context.getString(R.string.label_name));
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
	private boolean addEmailColumn() {
		return addColumn(context.getString(R.string.label_email));
	}

	/**
	 * Returns whether the client column is in the list columns to use in the
	 * report.
	 * 
	 * @return
	 */
	private boolean addMobileColumn() {
		return addColumn(context.getString(R.string.label_mobile));
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
