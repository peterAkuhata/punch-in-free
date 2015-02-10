package com.aku.apps.punchin.free.reporting;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;

import android.content.Context;
import android.util.Log;

import com.aku.apps.punchin.free.R;
import com.aku.apps.punchin.free.db.ClientFactory;
import com.aku.apps.punchin.free.db.DatasourceFactory;
import com.aku.apps.punchin.free.db.DatasourceFactoryFacade;
import com.aku.apps.punchin.free.db.ExpenseFactory;
import com.aku.apps.punchin.free.db.TimeStampFactory;
import com.aku.apps.punchin.free.domain.Client;
import com.aku.apps.punchin.free.domain.Day;
import com.aku.apps.punchin.free.domain.Expense;
import com.aku.apps.punchin.free.domain.RequiresContext;
import com.aku.apps.punchin.free.domain.Task;
import com.aku.apps.punchin.free.domain.TaskDay;
import com.aku.apps.punchin.free.domain.TimeFormatter;
import com.aku.apps.punchin.free.domain.TimeStamp;
import com.aku.apps.punchin.free.domain.TimeStamp.TimeStampType;
import com.aku.apps.punchin.free.utils.DateUtil;
import com.aku.apps.punchin.free.utils.TaskUtil;

public class IncomeReportGenerator implements ReportGenerator,
		RequiresContext {

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

	public IncomeReportGenerator(Date startDate, Date endDate) {
		super();
		this.startDate = DateUtil.removeTimeFromDate(startDate);
		this.endDate = DateUtil.removeTimeFromDate(endDate);
	}

	private class Summary {
		public long duration;
		public double expenses;

		public Summary(long duration, double expenses) {
			super();
			this.duration = duration;
			this.expenses = expenses;
		}
	}

	private class FinalSummary extends Summary {
		public double income;
		public double overtime;

		public FinalSummary(long duration, double expenses, double income,
				double overtime) {
			super(duration, expenses);

			this.income = income;
			this.overtime = overtime;
		}
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
		Hashtable<Client, FinalSummary> finalSummaries = new Hashtable<Client, FinalSummary>();

		while (!date.after(this.endDate)) {
			ArrayList<TimeStamp> timeStamps = tsf.getListByDate(date);
			Hashtable<Client, Summary> summaries = new Hashtable<Client, Summary>();
			
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
						Client client = cf.get(task.getClientId());
						
						if (client != null && TaskUtil.isVisible(task, punchIn.getTime())) {
							Date startTime = punchIn.getTime();
							Date endTime = punchOut.getTime();
							
							long duration = DateUtil.getMilliseconds(startTime, endTime);
							
							if (!summaries.containsKey(client)) {
								double expenses = getTotalExpenses(client, date, task);
								Summary s = new Summary(duration, expenses);
								summaries.put(client, s);
								
							} else {
								Summary s = summaries.get(client);
								s.duration += duration;
							}
						}
					}

					punchIn = punchOut = null;
				}
			}

			Enumeration<Client> en = summaries.keys();
			
			while (en.hasMoreElements()) {
				Client client = en.nextElement();
				Summary s = summaries.get(client);

				double totalIncome = getTotalIncome(client, s.duration);
				double totalOvertime = getTotalOvertime(client, s.duration);

				if (!finalSummaries.containsKey(client)) {
					finalSummaries.put(client, new FinalSummary(s.duration, s.expenses, totalIncome, totalOvertime));
					
				} else {
					FinalSummary fs = finalSummaries.get(client);
					fs.duration += s.duration;
					fs.expenses += s.expenses;
					fs.income += totalIncome;
					fs.overtime += totalOvertime;
				}
			}
			
			date = DateUtil.addDays(date, 1);
		}

		if (!startDate.equals(endDate))
			rows.add(new Row(DateFormat.getDateInstance().format(startDate) + " - " + DateFormat.getDateInstance().format(endDate)));
		else
			rows.add(new Row(DateFormat.getDateInstance().format(startDate)));

		Enumeration<Client> en = finalSummaries.keys();
		
		while (en.hasMoreElements()) {
			Client client = en.nextElement();
			FinalSummary s = finalSummaries.get(client);
			
			double hourlyRate = client.getHourlyRate();
			
			ArrayList<String> row = new ArrayList<String>();
			
			if (addIdColumn())
				row.add(String.valueOf(client.getId()));
			
			if (addClientColumn())
				row.add(client.getName());

			if (addDateColumn())
				row.add(DateUtil.toShortDateString(startDate));
			
			if (addDurationColumn())
				row.add(timeFormat.formatTime(s.duration));
			
			double overtimeRate = hourlyRate * client.getOvertimeMultiplier();
			
			if (addHourlyRateColumn())
				row.add(NumberFormat.getCurrencyInstance().format(hourlyRate));
			
			if (addMileageRateColumn())
				row.add(NumberFormat.getCurrencyInstance().format(client.getMileageRate()));
			
			if (addOvertimeRateColumn())
				row.add(NumberFormat.getCurrencyInstance().format(overtimeRate));

			if (addIncomeColumn())
				row.add(NumberFormat.getCurrencyInstance().format(s.income));
			
			if (addExpensesColumn())
				row.add(NumberFormat.getCurrencyInstance().format(s.expenses));
			
			if (addOvertimeColumn())
				row.add(NumberFormat.getCurrencyInstance().format(s.overtime));
			
			rows.add(new Row(row));
		}

		String name = context.getString(R.string.label_income_report);
		String description = context.getString(R.string.label_income_report_description);

		Report r = new Report(name, description, columns, rows);

		return r;
	}

	/**
	 * Returns the total amount of overtime incurred.
	 * 
	 * @param client
	 * @param punchIn
	 * @param punchOut
	 * @return
	 */
	private double getTotalOvertime(Client client, long duration) {
		Log.d(TimesheetReportGenerator.class.getSimpleName(),
				"getTotalOvertime");

		double hours = DateUtil.getHours(duration);
		double minutes = DateUtil.getMinutes(duration);
		double seconds = DateUtil.getSeconds(duration);
		double income = 0;

		if (client != null) {
			double normalWorkingHours = client.getNormalWorkingHours();
			double hourlyRate = (client == null ? 0 : client.getHourlyRate());
			double overtimeRate = (client == null ? hourlyRate : hourlyRate
					* client.getOvertimeMultiplier());

			if (overtimeRate != hourlyRate
					&& hasOvertime(normalWorkingHours, hours, minutes, seconds)) {

				long normalWorkingMillis = (long) normalWorkingHours * 3600000;
				long totalTimeInMillis = ((long) hours * 3600000)
						+ ((long) minutes * 60000) + ((long) seconds * 1000);
				long diff = totalTimeInMillis - normalWorkingMillis;

				hours = DateUtil.getHours(diff);
				minutes = DateUtil.getMinutes(diff);
				seconds = DateUtil.getSeconds(diff);

				income += ((double) hours) * overtimeRate;
				income += ((double) minutes / 60) * overtimeRate;
				income += ((double) seconds / 3600) * overtimeRate;
			}
		}

		return income;
	}

	/**
	 * Returns the total amount of expenses incurred.
	 * 
	 * @param client
	 * @param punchIn
	 * @param punchOut
	 * @param task
	 * @return
	 */
	private double getTotalExpenses(Client client, Date date, Task task) {
		Log.d(TimesheetReportGenerator.class.getSimpleName(),
				"getTotalExpenses");

		ExpenseFactory ef = datasourceFactory.createExpenseFactory();
		Day day = datasourceFactory.createDayFactory().get(date, true);
		TaskDay taskDay = datasourceFactory.createTaskDayFactory().get(task, day, true);
		
		ArrayList<Expense> list = ef.getListByTaskDay(taskDay);
		double expenses = 0;

		for (Expense item : list) {
			switch (item.getType()) {
			case COSTING:
				expenses += item.getAmount();
				break;

			case MILEAGE:
				expenses += ((client == null ? 0 : client.getMileageRate()) * item.getAmount());
				break;
			}
		}

		return expenses;
	}

	/**
	 * Returns the total income for the duration between the specified punch in
	 * and punch out times. This is done by getting the hourly rate from the
	 * client. NOTE: This only calculates the hourly rate for the number of
	 * normal working hours. Overtime is calculated elsewhere.
	 * 
	 * @param client
	 * @param punchIn
	 * @param punchOut
	 * @return
	 */
	private double getTotalIncome(Client client, long duration) {
		Log.d(TimesheetReportGenerator.class.getSimpleName(), "getTotalIncome");

		double hours = DateUtil.getHours(duration);
		double minutes = DateUtil.getMinutes(duration);
		double seconds = DateUtil.getSeconds(duration);
		double income = 0;

		if (client != null) {
			double normalWorkingHours = client.getNormalWorkingHours();
			double hourlyRate = client.getHourlyRate();
			double overtimeRate = hourlyRate * client.getOvertimeMultiplier();

			if (overtimeRate != hourlyRate
					&& hasOvertime(normalWorkingHours, hours, minutes, seconds)) {

				long normalWorkingMillis = (long) normalWorkingHours * 3600000;
				hours = DateUtil.getHours(normalWorkingMillis);
				minutes = DateUtil.getMinutes(normalWorkingMillis);
				seconds = DateUtil.getSeconds(normalWorkingMillis);
			}

			income += ((double) hours) * hourlyRate;
			income += ((double) minutes / 60) * hourlyRate;
			income += ((double) seconds / 3600) * hourlyRate;
		}

		return income;
	}

	/**
	 * Returns whether the specified hours, minutes and seconds is greater than
	 * the normal working hours.
	 * 
	 * @param normalWorkingHours
	 * @param hours
	 * @param minutes
	 * @param seconds
	 * @return
	 */
	private boolean hasOvertime(double normalWorkingHours, double hours,
			double minutes, double seconds) {
		double normalWorkingSeconds = normalWorkingHours * 3600;
		double totalTimeInSeconds = (hours * 3600) + (minutes * 60) + seconds;

		return (totalTimeInSeconds > normalWorkingSeconds);
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

		if (addClientColumn())
			columns.add(context.getString(R.string.label_client));

		if (addDateColumn())
			columns.add(context.getString(R.string.label_date));

		if (addDurationColumn())
			columns.add(context.getString(R.string.label_duration));

		if (addHourlyRateColumn())
			columns.add(context.getString(R.string.label_hourly_rate));

		if (addMileageRateColumn())
			columns.add(context.getString(R.string.label_mileage_rate));

		if (addOvertimeRateColumn())
			columns.add(context.getString(R.string.label_overtime_rate));

		if (addIncomeColumn())
			columns.add(context.getString(R.string.label_income));

		if (addExpensesColumn())
			columns.add(context.getString(R.string.label_expenses));

		if (addOvertimeColumn())
			columns.add(context.getString(R.string.label_overtime));

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
			mAvailableColumns.add(ctx.getString(R.string.label_client));
			mAvailableColumns.add(ctx.getString(R.string.label_date));
			mAvailableColumns.add(ctx.getString(R.string.label_duration));
			mAvailableColumns.add(ctx.getString(R.string.label_hourly_rate));
			mAvailableColumns.add(ctx.getString(R.string.label_mileage_rate));
			mAvailableColumns.add(ctx.getString(R.string.label_overtime_rate));
			mAvailableColumns.add(ctx.getString(R.string.label_income));
			mAvailableColumns.add(ctx.getString(R.string.label_expenses));
			mAvailableColumns.add(ctx.getString(R.string.label_overtime));
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
	 * Returns whether the overtime column is in the list columns to use in the
	 * report.
	 * 
	 * @return
	 */
	private boolean addOvertimeColumn() {
		return addColumn(context.getString(R.string.label_overtime));
	}

	/**
	 * Returns whether the expenses column is in the list columns to use in the
	 * report.
	 * 
	 * @return
	 */
	private boolean addExpensesColumn() {
		return addColumn(context.getString(R.string.label_expenses));
	}

	/**
	 * Returns whether the income column is in the list columns to use in the
	 * report.
	 * 
	 * @return
	 */
	private boolean addIncomeColumn() {
		return addColumn(context.getString(R.string.label_income));
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
	private boolean addClientColumn() {
		return addColumn(context.getString(R.string.label_client));
	}

	/**
	 * Returns whether the duration column is in the list columns to use in the
	 * report.
	 * 
	 * @return
	 */
	private boolean addDurationColumn() {
		return addColumn(context.getString(R.string.label_duration));
	}

	/**
	 * Returns whether the date column is in the list columns to use in the
	 * report.
	 * 
	 * @return
	 */
	private boolean addDateColumn() {
		return addColumn(context.getString(R.string.label_date));
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
