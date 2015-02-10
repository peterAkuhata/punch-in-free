package com.aku.apps.punchin.free.reporting;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import com.aku.apps.punchin.free.R;
import com.aku.apps.punchin.free.db.ClientFactory;
import com.aku.apps.punchin.free.db.DatasourceFactory;
import com.aku.apps.punchin.free.db.DatasourceFactoryFacade;
import com.aku.apps.punchin.free.db.TimeStampFactory;
import com.aku.apps.punchin.free.domain.Client;
import com.aku.apps.punchin.free.domain.RequiresContext;
import com.aku.apps.punchin.free.domain.Task;
import com.aku.apps.punchin.free.domain.TaskDay;
import com.aku.apps.punchin.free.domain.TimeFormatter;
import com.aku.apps.punchin.free.domain.TimeStamp;
import com.aku.apps.punchin.free.domain.TimeStamp.TimeStampType;
import com.aku.apps.punchin.free.utils.DateUtil;
import com.aku.apps.punchin.free.utils.TaskUtil;

import android.content.Context;
import android.util.Log;

/**
 * Generates the punchin report.
 * 
 * @author Peter Akuhata
 * 
 */
public class TimesheetReportGenerator implements ReportGenerator, RequiresContext {

	private Date startDate;
	private Date endDate;
	private DatasourceFactory datasourceFactory;
	private Context context = null;
	private ArrayList<String> selectedColumns = null;
	
	public Date getEndDate() {
		return this.endDate;
	}
	
	public void setEndDate(Date date) {
		this.endDate = date;
	}

	public Date getStartDate() {
		return startDate;
	}
	
	public void setStartDate(Date date) {
		this.startDate = date;
	}

	public TimesheetReportGenerator(Date startDate, Date endDate) {
		super();
		this.startDate = DateUtil.removeTimeFromDate(startDate);
		this.endDate = DateUtil.removeTimeFromDate(endDate);
	}

	@Override
	public Report generate(ArrayList<String> selectedColumns) {
		Log.d(ReportManager.class.getSimpleName(), "generate");
		
		this.selectedColumns = selectedColumns;
		
		TimeStampFactory tsf = datasourceFactory.createTimeStampFactory();
		ClientFactory cf = datasourceFactory.createClientFactory();
		TimeFormatter timeFormat = datasourceFactory.createDefaultTimeFormat();
		
		ArrayList<String> columns = createColumns();
		ArrayList<Row> rows = new ArrayList<Row>();

		Date date = this.getStartDate();
		TimeStamp punchIn = null;
		TimeStamp punchOut = null;

		if (!startDate.equals(endDate))
			rows.add(new Row(DateFormat.getDateInstance().format(startDate) + " - " + DateFormat.getDateInstance().format(endDate)));
		else
			rows.add(new Row(DateFormat.getDateInstance().format(startDate)));

		while (!date.after(this.endDate)) {
			ArrayList<TimeStamp> timeStamps = tsf.getListByDate(date);
			
			for (TimeStamp item : timeStamps) {
				if (item.getType() == TimeStampType.PunchIn) {
					if (punchIn != null)
						Log.d(TimesheetReportGenerator.class.getSimpleName(), "Two punch ins in a row!");

					punchIn = item;
					punchOut = null;

				} else if (item.getType() == TimeStampType.PunchOut) {
					if (punchIn == null) {
						Log.d(TimesheetReportGenerator.class.getSimpleName(),
								"A punch out without a punch in!");

					} else if (punchIn.getTaskDayId() != item.getTaskDayId()) {
						Log.d(TimesheetReportGenerator.class.getSimpleName(),
								"The task for the punch in time does not match the task for the punch out time!");

					} else {
						punchOut = item;						
						Task task = tsf.getTask(punchIn);
						
						if (TaskUtil.isVisible(task, punchIn.getTime())) {
							Client client = cf.get(task.getClientId());
							double hourlyRate = (client == null ? 0 : client.getHourlyRate());
							TaskDay taskDay = tsf.getTaskDay(punchIn);
							String notes = taskDay.getNotes();

							String na = context.getString(R.string.label_na);
							String noNotes = context.getString(R.string.label_no_notes);
							
							ArrayList<String> row = new ArrayList<String>();
							
							if (addIdColumn())
								row.add(String.valueOf(task.getId()));
							
							if (addDescriptionColumn())
								row.add(task.getDescription());
							
							if (addDateColumn())
								row.add(DateUtil.toShortDateString(DateUtil.removeTimeFromDate(punchIn.getTime())));
							
							if (addInColumn())
								row.add(DateUtil.toShortTimeString(punchIn.getTime()));
							
							if (addOutColumn())
								row.add(DateUtil.toShortTimeString(punchOut.getTime()));
							
							if (addDurationColumn())
								row.add(timeFormat.formatTime(punchIn.getTime(), punchOut.getTime()));
							
							if (client != null) {
								double overtimeRate = hourlyRate * client.getOvertimeMultiplier();
								
								if (addClientColumn())
									row.add(client.getName());
								
								if (addHourlyRateColumn())
									row.add(NumberFormat.getCurrencyInstance().format(hourlyRate));
								
								if (addMileageRateColumn())
									row.add(NumberFormat.getCurrencyInstance().format(client.getMileageRate()));
								
								if (addOvertimeRateColumn())
									row.add(NumberFormat.getCurrencyInstance().format(overtimeRate));
								
							} else {
								if (addClientColumn())
									row.add(na);
	
								if (addHourlyRateColumn())
									row.add(na);
								
								if (addMileageRateColumn())
									row.add(na);
	
								if (addOvertimeRateColumn())
									row.add(na);
								
							}
							
							if (addNotesColumn())
								row.add(notes == null || notes.length() == 0 ? noNotes : notes);
							
							rows.add(new Row(row));
						}
					}

					punchIn = punchOut = null;
				}
			}
			
			date = DateUtil.addDays(date, 1);
		}

		String name = context.getString(R.string.label_timesheet_report);
		String description = context.getString(R.string.label_timesheet_report_description);

		Report r = new Report(name, description, columns, rows);

		return r;
	}
	
	/**
	 * Returns the list of columns for this report.
	 * @param columns
	 */
	private ArrayList<String> createColumns() {
		Log.d(ReportManager.class.getSimpleName(), "buildColumns");
		
		ArrayList<String> columns = new ArrayList<String>();
		
		if (addIdColumn())
			columns.add(context.getString(R.string.label_id));
		
		if (addDescriptionColumn())
			columns.add(context.getString(R.string.label_description));
		
		if (addDateColumn())
			columns.add(context.getString(R.string.label_date));
		
		if (addInColumn())
			columns.add(context.getString(R.string.label_in));
		
		if (addOutColumn())
			columns.add(context.getString(R.string.label_out));
		
		if (addDurationColumn())
			columns.add(context.getString(R.string.label_duration));
		
		if (addClientColumn())
			columns.add(context.getString(R.string.label_client));
		
		if (addHourlyRateColumn())
			columns.add(context.getString(R.string.label_hourly_rate));
		
		if (addMileageRateColumn())
			columns.add(context.getString(R.string.label_mileage_rate));
		
		if (addOvertimeRateColumn())
			columns.add(context.getString(R.string.label_overtime_rate));
		
		if (addNotesColumn())
			columns.add(context.getString(R.string.label_notes));
		
		return columns;
	}

	/**
	 * Contains the list of available columns.
	 */
	private static ArrayList<String> mAvailableColumns = new ArrayList<String>();
	
	/**
	 * Creates and returns the list of available columns for this report.
	 * @param ctx
	 * @return
	 */
	public static ArrayList<String> getAvailableColumns(Context ctx) {
		if (mAvailableColumns.size() == 0) {
			mAvailableColumns.add(ctx.getString(R.string.label_id));
			mAvailableColumns.add(ctx.getString(R.string.label_description));
			mAvailableColumns.add(ctx.getString(R.string.label_date));
			mAvailableColumns.add(ctx.getString(R.string.label_in));
			mAvailableColumns.add(ctx.getString(R.string.label_out));
			mAvailableColumns.add(ctx.getString(R.string.label_duration));
			mAvailableColumns.add(ctx.getString(R.string.label_client));
			mAvailableColumns.add(ctx.getString(R.string.label_hourly_rate));
			mAvailableColumns.add(ctx.getString(R.string.label_mileage_rate));
			mAvailableColumns.add(ctx.getString(R.string.label_overtime_rate));
			mAvailableColumns.add(ctx.getString(R.string.label_notes));
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
	 * Returns whether the notes column is in the list columns to use in the report.
	 * @return
	 */
	private boolean addNotesColumn() {
		return addColumn(context.getString(R.string.label_notes));
	}

	/**
	 * Returns whether the overtime rate column is in the list columns to use in the report.
	 * @return
	 */
	private boolean addOvertimeRateColumn() {
		return addColumn(context.getString(R.string.label_overtime_rate));
	}

	/**
	 * Returns whether the mileage rate column is in the list columns to use in the report.
	 * @return
	 */
	private boolean addMileageRateColumn() {
		return addColumn(context.getString(R.string.label_mileage_rate));
	}

	/**
	 * Returns whether the hourly rate column is in the list columns to use in the report.
	 * @return
	 */
	private boolean addHourlyRateColumn() {
		return addColumn(context.getString(R.string.label_hourly_rate));
	}

	/**
	 * Returns whether the client column is in the list columns to use in the report.
	 * @return
	 */
	private boolean addClientColumn() {
		return addColumn(context.getString(R.string.label_client));
	}

	/**
	 * Returns whether the duration column is in the list columns to use in the report.
	 * @return
	 */
	private boolean addDurationColumn() {
		return addColumn(context.getString(R.string.label_duration));
	}

	/**
	 * Returns whether the punch out column is in the list columns to use in the report.
	 * @return
	 */
	private boolean addOutColumn() {
		return addColumn(context.getString(R.string.label_out));
	}

	/**
	 * Returns whether the punch in column is in the list columns to use in the report.
	 * @return
	 */
	private boolean addInColumn() {
		return addColumn(context.getString(R.string.label_in));
	}

	/**
	 * Returns whether the date column is in the list columns to use in the report.
	 * @return
	 */
	private boolean addDateColumn() {
		return addColumn(context.getString(R.string.label_date));
	}

	/**
	 * Returns whether the description column is in the list columns to use in the report.
	 * @return
	 */
	private boolean addDescriptionColumn() {
		return addColumn(context.getString(R.string.label_description));
	}

	/**
	 * Returns whether the id column is in the list columns to use in the report.
	 * @return
	 */
	private boolean addIdColumn() {
		return addColumn(context.getString(R.string.label_id));
	}
	
	/**
	 * Returns whether the specified name is in the list columns to use in the report.
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
