package com.aku.apps.punchin.free.domain;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.aku.apps.punchin.free.utils.ArrayListUtil;

/**
 * Represents the list of preferences available in the system.
 * @author Peter Akuhata
 *
 */
public class Preferences extends DomainObject {
	/**
	 * Determines whether the user gets ask before clearing 
	 * the time value from a task, for a specific day.
	 */
	private boolean askBeforeRemovingTime;
	
	/**
	 * The default time format used by the system.
	 */
	private int defaultTimeFormatId;
	
	/**
	 * The default hourly rate when creating a new client.
	 */
	private double defaultHourlyRate;
	
	/**
	 * The default date format used by the system.
	 */
	private String defaultDateFormat;
	
	/**
	 * The default normal working hours when creating a new client.
	 */
	private double defaultNormalWorkingHours;
	
	/**
	 * The default client to use when creating a new task.
	 */
	private long defaultClientId;
	
	/**
	 * The default mileage unit used by the system.
	 */
	private String defaultMileageUnit;
	
	/**
	 * The default mileage rate used when creating a new client.
	 */
	private double defaultMileageRate;
	
	/**
	 * This is a multiplier used to calculate the actual wages, e.g, $20 per
	 * hour * overtime rate = the hourly rate to charge for overtime.
	 */
	private double defaultOvertimeMultiplier;
	
	/**
	 * The default report formatter used when viewing a report.
	 */
	private int defaultReportFormatId;
	
	/**
	 * The default report sender used when transferring a report.
	 */
	private int defaultReportSenderId;
	
	/**
	 * The list of columns to be displayed on the timesheet report.
	 */
	private ArrayList<String> timesheetReportColumns;
	
	/**
	 * The list of columns to be displayed on the income by client report.
	 */
	private ArrayList<String> incomeReportColumns;
	
	/**
	 * The list of columns to be displayed on the client list report.
	 */
	private ArrayList<String> clientListReportColumns;
	
	/**
	 * The list of columns to be displayed on the task list report.
	 */
	private ArrayList<String> taskListReportColumns;
	
	/**
	 * The default backup provider used when backing up and restoring the datasource.
	 */
	private long defaultBackupProviderId;
	
	/**
	 * The contact account to use when syncing.
	 */
	private String defaultAccountType;

	/**
	 * The contact account name.
	 */
	private String defaultAccountName;
	
	/**
	 * The id for the calendar to sync to.
	 */
	private String defaultCalendarId;
	
	/**
	 * The name of the calendar to sync to.
	 */
	private String defaultCalendarName;
	
	/**
	 * The url to use when the user has selected the 'http post' report sender.
	 */
	private String httpPostUrl;

	/**
	 * The username used for the ftp report sender.
	 */
	private String reportSenderFtpUsername;
	
	/**
	 * The password used for the ftp report sender.
	 */
	private String reportSenderFtpPassword;
	
	/**
	 * The server name used for the ftp report sender.
	 */
	private String reportSenderFtpServerName;
	
	/**
	 * The subfolder for the ftp report sender.
	 */
	private String reportSenderFtpSubFolder;

	/**
	 * The username used for the ftp backup provider.
	 */
	private String backupProviderFtpUsername;
	
	/**
	 * The password used for the ftp backup provider.
	 */
	private String backupProviderFtpPassword;
	
	/**
	 * The server name used for the ftp backup provider.
	 */
	private String backupProviderFtpServerName;
	
	/**
	 * The subfolder for the ftp backup provider.
	 */
	private String backupProviderFtpSubFolder;

	/**
	 * Returns the ftp username.	
	 * @return
	 */
	public String getBackupProviderFtpUsername() {
		return backupProviderFtpUsername;
	}

	/**
	 * Sets the ftp username.
	 * @param backupProviderFtpUsername
	 */
	public void setBackupProviderFtpUsername(String backupProviderFtpUsername) {
		this.backupProviderFtpUsername = backupProviderFtpUsername;
	}

	/**
	 * Returns the ftp password.
	 * @return
	 */
	public String getBackupProviderFtpPassword() {
		return backupProviderFtpPassword;
	}

	/**
	 * Sets the ftp password.
	 * @param backupProviderFtpPassword
	 */
	public void setBackupProviderFtpPassword(String backupProviderFtpPassword) {
		this.backupProviderFtpPassword = backupProviderFtpPassword;
	}

	/**
	 * Returns the ftp server name.
	 * @return
	 */
	public String getBackupProviderFtpServerName() {
		return backupProviderFtpServerName;
	}

	/**
	 * Sets the ftp server name.
	 * @param backupProviderFtpServerName
	 */
	public void setBackupProviderFtpServerName(String backupProviderFtpServerName) {
		this.backupProviderFtpServerName = backupProviderFtpServerName;
	}

	/**
	 * Returns the ftp sub folder.
	 * @return
	 */
	public String getBackupProviderFtpSubFolder() {
		return backupProviderFtpSubFolder;
	}

	/**
	 * Sets the ftp sub folder.
	 * @param backupProviderFtpSubFolder
	 */
	public void setBackupProviderFtpSubFolder(String backupProviderFtpSubFolder) {
		this.backupProviderFtpSubFolder = backupProviderFtpSubFolder;
	}

	/**
	 * Returns the ftp subfolder.
	 * @return
	 */
	public String getReportSenderFtpSubFolder() {
		return reportSenderFtpSubFolder;
	}

	/**
	 * Sets the ftp subfolder.
	 * @param reportSenderFtpSubFolder
	 */
	public void setReportSenderFtpSubFolder(String reportSenderFtpSubFolder) {
		this.reportSenderFtpSubFolder = reportSenderFtpSubFolder;
	}

	/**
	 * Returns the ftp username.
	 * @return
	 */
	public String getReportSenderFtpUsername() {
		return reportSenderFtpUsername;
	}

	/**
	 * Sets the ftp username.
	 * @param reportSenderFtpUsername
	 */
	public void setReportSenderFtpUsername(String reportSenderFtpUsername) {
		this.reportSenderFtpUsername = reportSenderFtpUsername;
	}

	/**
	 * Returns the ftp password.
	 * @return
	 */
	public String getReportSenderFtpPassword() {
		return reportSenderFtpPassword;
	}

	/**
	 * Sets the ftp password.
	 * @param reportSenderFtpPassword
	 */
	public void setReportSenderFtpPassword(String reportSenderFtpPassword) {
		this.reportSenderFtpPassword = reportSenderFtpPassword;
	}

	/**
	 * Returns the ftp server name.
	 * @return
	 */
	public String getReportSenderFtpServerName() {
		return reportSenderFtpServerName;
	}

	/**
	 * Sets the ftp server name.
	 * @param reportSenderFtpServerName
	 */
	public void setReportSenderFtpServerName(String reportSenderFtpServerName) {
		this.reportSenderFtpServerName = reportSenderFtpServerName;
	}

	/**
	 * Returns the http post url.	
	 * @return
	 */
	public String getHttpPostUrl() {
		return httpPostUrl;
	}

	/**
	 * Sets the http post url.
	 * @param httpPostUrl
	 */
	public void setHttpPostUrl(String httpPostUrl) {
		this.httpPostUrl = httpPostUrl;
	}

	/**
	 * Returns the sync calendar name.
	 * @return
	 */
	public String getDefaultCalendarName() {
		return defaultCalendarName;
	}

	/**
	 * Sets the sync calendar name.
	 * @param defaultCalendarName
	 */
	public void setDefaultCalendarName(String defaultCalendarName) {
		this.defaultCalendarName = defaultCalendarName;
	}

	/**
	 * Returns the sync calendar id.	
	 * @return
	 */
	public String getDefaultCalendarId() {
		return defaultCalendarId;
	}

	/**
	 * Sets the sync calendar id.
	 * @param defaultCalendarId
	 */
	public void setDefaultCalendarId(String defaultCalendarId) {
		this.defaultCalendarId = defaultCalendarId;
	}

	/**
	 * Returns the account name.
	 * @return
	 */
	public String getDefaultAccountName() {
		return defaultAccountName;
	}

	/**
	 * Sets the account name.
	 * @param accountName
	 */
	public void setDefaultAccountName(String accountName) {
		this.defaultAccountName = accountName;
	}

	/**
	 * Returns the account type.
	 * @return
	 */
	public String getDefaultAccountType() {
		return defaultAccountType;
	}

	/**
	 * Sets the account type.
	 * @param accountType
	 */
	public void setDefaultAccountType(String accountType) {
		this.defaultAccountType = accountType;
	}

	/**
	 * Returns the default backup provider.
	 * @return
	 */
	public long getDefaultBackupProviderId() {
		return defaultBackupProviderId;
	}

	/**
	 * Sets the default backup provider.
	 * @param defaultBackupProviderId
	 */
	public void setDefaultBackupProviderId(long defaultBackupProviderId) {
		this.defaultBackupProviderId = defaultBackupProviderId;
	}

	/**
	 * Returns the list of columns for the income by client report.
	 * @return
	 */
	public ArrayList<String> getIncomeReportColumns() {
		return incomeReportColumns;
	}

	/**
	 * Sets the list of columns for the income by client report.
	 * @param columns
	 */
	public void setIncomeReportColumns(ArrayList<String> columns) {
		this.incomeReportColumns = columns;
	}

	/**
	 * Returns the list of columns for the task list report.
	 * @return
	 */
	public ArrayList<String> getTaskListReportColumns() {
		return taskListReportColumns;
	}

	/**
	 * Sets the list of columns for the task list report.
	 * @param columns
	 */
	public void setTaskListReportColumns(ArrayList<String> columns) {
		this.taskListReportColumns = columns;
	}

	/**
	 * Returns the list of columns for the client list report.
	 * @return
	 */
	public ArrayList<String> getClientListReportColumns() {
		return clientListReportColumns;
	}

	/**
	 * Sets the list of columns for the client list report.
	 * @param columns
	 */
	public void setClientListReportColumns(ArrayList<String> columns) {
		this.clientListReportColumns = columns;
	}

	/**
	 * Returns the list of columns for the timesheet report.
	 * @return
	 */
	public ArrayList<String> getTimesheetReportColumns() {
		return timesheetReportColumns;
	}

	/**
	 * Sets the list of columns for the timesheet report.
	 * @param punchInReportColumns
	 */
	public void setTimesheetReportColumns(ArrayList<String> punchInReportColumns) {
		this.timesheetReportColumns = punchInReportColumns;
	}

	/**
	 * Returns the default report formatter.
	 * @return
	 */
	public int getDefaultReportFormatId() {
		return defaultReportFormatId;
	}

	/**
	 * Sets the default report formatter.
	 * @param defaultReportFormatId
	 */
	public void setDefaultReportFormatId(int defaultReportFormatId) {
		this.defaultReportFormatId = defaultReportFormatId;
	}

	/**
	 * Returns the default report sender.
	 * @return
	 */
	public int getDefaultReportSenderId() {
		return defaultReportSenderId;
	}

	/**
	 * Sets the default report sender.
	 * @param defaultReportSenderId
	 */
	public void setDefaultReportSenderId(int defaultReportSenderId) {
		this.defaultReportSenderId = defaultReportSenderId;
	}

	/**
	 * Returns the default mileage unit.
	 * @return
	 */
	public String getDefaultMileageUnit() {
		return defaultMileageUnit;
	}

	/**
	 * Sets the default mileage unit.
	 * @param defaultMileageUnit
	 */
	public void setDefaultMileageUnit(String defaultMileageUnit) {
		this.defaultMileageUnit = defaultMileageUnit;
	}

	/**
	 * Returns the default mileage rate.
	 * @return
	 */
	public double getDefaultMileageRate() {
		return defaultMileageRate;
	}

	/**
	 * Sets the default mileage rate.
	 * @param defaultMileageRate
	 */
	public void setDefaultMileageRate(double defaultMileageRate) {
		this.defaultMileageRate = defaultMileageRate;
	}

	/**
	 * Returns the default client.
	 * @return
	 */
	public long getDefaultClientId() {
		return defaultClientId;
	}

	/**
	 * Sets the default client.
	 * @param clientId
	 */
	public void setDefaultClientId(long clientId) {
		this.defaultClientId = clientId;
	}

	/**
	 * Returns the default normal working hours.
	 * @return
	 */
	public double getDefaultNormalWorkingHours() {
		return defaultNormalWorkingHours;
	}

	/**
	 * Sets the default normal working hours.
	 * @param normalWorkingHours
	 */
	public void setDefaultNormalWorkingHours(double normalWorkingHours) {
		this.defaultNormalWorkingHours = normalWorkingHours;
	}

	/**
	 * Returns the default overtime rate.
	 * @return
	 */
	public double getDefaultOvertimeMultiplier() {
		return defaultOvertimeMultiplier;
	}

	/**
	 * Sets the default overtime rate.
	 * @param defaultOvertimeMultiplier
	 */
	public void setDefaultOvertimeMultiplier(double defaultOvertimeMultiplier) {
		this.defaultOvertimeMultiplier = defaultOvertimeMultiplier;
	}

	/**
	 * Creates a {@link Preferences} object.
	 * @param askBeforeRemovingTime
	 * @param defaultTimeFormatId
	 * @param defaultHourlyRate
	 * @param defaultDateFormat
	 * @param normalWorkingHours
	 * @param defaultOvertimeRate
	 * @param defaultClientId
	 * @param defaultMileageUnit
	 * @param defaultMileageRate
	 * @param defaultReportFormatId
	 * @param defaultReportSenderId
	 * @param timesheetReportColumns
	 * @param incomeReportColumns
	 * @param clientListReportColumns
	 * @param taskListReportColumns
	 * @param defaultBackupProviderId
	 */
	public Preferences(long id, boolean askBeforeRemovingTime, int defaultTimeFormatId,
			double defaultHourlyRate, String defaultDateFormat,
			double normalWorkingHours, double defaultOvertimeRate,
			long defaultClientId, String defaultMileageUnit,
			double defaultMileageRate,
			int defaultReportFormatId,
			int defaultReportSenderId,
			ArrayList<String> timesheetReportColumns,
			ArrayList<String> incomeReportColumns,
			ArrayList<String> clientListReportColumns,
			ArrayList<String> taskListReportColumns,
			long defaultBackupProviderId,
			String defaultAccountName,
			String defaultAccountType,
			String defaultCalendarId,
			String defaultCalendarName,
			Date created,
			Date modified,
			String httpPostUrl,
			String reportSenderFtpUsername,
			String reportSenderFtpPassword,
			String reportSenderFtpServerName,
			String reportSenderFtpSubFolder,
			String backupProviderFtpUsername,
			String backupProviderFtpPassword,
			String backupProviderFtpServerName,
			String backupProviderFtpSubFolder) {

		super(id, created, modified);
		this.askBeforeRemovingTime = askBeforeRemovingTime;
		this.defaultTimeFormatId = defaultTimeFormatId;
		this.defaultHourlyRate = defaultHourlyRate;
		this.defaultDateFormat = defaultDateFormat;
		this.defaultNormalWorkingHours = normalWorkingHours;
		this.defaultOvertimeMultiplier = defaultOvertimeRate;
		this.defaultClientId = defaultClientId;
		this.defaultMileageUnit = defaultMileageUnit;
		this.defaultMileageRate = defaultMileageRate;
		this.defaultReportFormatId = defaultReportFormatId;
		this.defaultReportSenderId = defaultReportSenderId;
		this.timesheetReportColumns = timesheetReportColumns;
		this.incomeReportColumns = incomeReportColumns;
		this.clientListReportColumns = clientListReportColumns;
		this.taskListReportColumns = taskListReportColumns;
		this.defaultBackupProviderId = defaultBackupProviderId;
		this.defaultAccountName = defaultAccountName;
		this.defaultAccountType = defaultAccountType;
		this.defaultCalendarId = defaultCalendarId;
		this.defaultCalendarName = defaultCalendarName;
		this.httpPostUrl = httpPostUrl;
		this.reportSenderFtpPassword = reportSenderFtpPassword;
		this.reportSenderFtpServerName = reportSenderFtpServerName;
		this.reportSenderFtpUsername = reportSenderFtpUsername;
		this.reportSenderFtpSubFolder = reportSenderFtpSubFolder;
		this.backupProviderFtpUsername = backupProviderFtpUsername;
		this.backupProviderFtpPassword = backupProviderFtpPassword;
		this.backupProviderFtpServerName = backupProviderFtpServerName;
		this.backupProviderFtpSubFolder = backupProviderFtpSubFolder;
	}

	/**
	 * Creates a {@link Preferences} object.
	 * @param askBeforeRemovingTime
	 * @param defaultTimeFormatId
	 * @param defaultHourlyRate
	 * @param defaultDateFormat
	 * @param normalWorkingHours
	 * @param defaultOvertimeRate
	 * @param defaultClientId
	 * @param defaultMileageUnit
	 * @param defaultMileageRate
	 * @param defaultReportFormatId
	 * @param defaultReportSenderId
	 * @param timesheetReportColumns
	 * @param incomeReportColumns
	 * @param clientListReportColumns
	 * @param taskListReportColumns
	 * @param defaultBackupProviderId
	 */
	public Preferences(long id, int askBeforeRemovingTime, int defaultTimeFormatId,
			double defaultHourlyRate, String defaultDateFormat,
			double normalWorkingHours, double defaultOvertimeRate,
			long defaultClientId, String defaultMileageUnit,
			double defaultMileageRate,
			int defaultReportFormatId,
			int defaultReportSenderId,
			String timesheetReportColumns,
			String incomeReportColumns,
			String clientListReportColumns,
			String taskListReportColumns,
			long defaultBackupProviderId,
			String defaultAccountName,
			String defaultAccountType,
			String defaultCalendarId,
			String defaultCalendarName,
			long created,
			long modified,
			String httpPostUrl,
			String reportSenderFtpUsername,
			String reportSenderFtpPassword,
			String reportSenderFtpServerName,
			String reportSenderFtpSubFolder,
			String backupProviderFtpUsername,
			String backupProviderFtpPassword,
			String backupProviderFtpServerName,
			String backupProviderFtpSubFolder) {
		super(id);

		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(created);
		this.setCreated(cal.getTime());
		cal.setTimeInMillis(modified);
		this.setModified(cal.getTime());
		
		this.askBeforeRemovingTime = askBeforeRemovingTime == 0 ? false : true;
		this.defaultTimeFormatId = defaultTimeFormatId;
		this.defaultHourlyRate = defaultHourlyRate;
		this.defaultDateFormat = defaultDateFormat;
		this.defaultNormalWorkingHours = normalWorkingHours;
		this.defaultOvertimeMultiplier = defaultOvertimeRate;
		this.defaultClientId = defaultClientId;
		this.defaultMileageUnit = defaultMileageUnit;
		this.defaultMileageRate = defaultMileageRate;
		this.defaultReportFormatId = defaultReportFormatId;
		this.defaultReportSenderId = defaultReportSenderId;
		this.timesheetReportColumns = ArrayListUtil.split(timesheetReportColumns);
		this.incomeReportColumns = ArrayListUtil.split(incomeReportColumns);
		this.clientListReportColumns = ArrayListUtil.split(clientListReportColumns);
		this.taskListReportColumns = ArrayListUtil.split(taskListReportColumns);
		this.defaultBackupProviderId = defaultBackupProviderId;
		this.defaultAccountName = defaultAccountName;
		this.defaultAccountType = defaultAccountType;
		this.defaultCalendarId = defaultCalendarId;
		this.defaultCalendarName = defaultCalendarName;
		this.httpPostUrl = httpPostUrl;
		this.reportSenderFtpPassword = reportSenderFtpPassword;
		this.reportSenderFtpServerName = reportSenderFtpServerName;
		this.reportSenderFtpUsername = reportSenderFtpUsername;
		this.reportSenderFtpSubFolder = reportSenderFtpSubFolder;
		this.backupProviderFtpUsername = backupProviderFtpUsername;
		this.backupProviderFtpPassword = backupProviderFtpPassword;
		this.backupProviderFtpServerName = backupProviderFtpServerName;
		this.backupProviderFtpSubFolder = backupProviderFtpSubFolder;
	}

	/**
	 * Returns the default date format.
	 * @return
	 */
	public String getDefaultDateFormat() {
		return defaultDateFormat;
	}
	
	/**
	 * Sets the default date format.
	 * @param value
	 */
	public void setDefaultDateFormat(String value) {
		this.defaultDateFormat = value;
	}

	/**
	 * Returns whether to ask before removing time for a task or not.
	 * @return
	 */
	public boolean getAskBeforeRemovingTime() {
		return askBeforeRemovingTime;
	}
	
	/**
	 * Sets whether to ask before removing time for a task or not.
	 * @param askBeforeRemovingTime
	 */
	public void setAskBeforeRemovingTime(boolean askBeforeRemovingTime) {
		this.askBeforeRemovingTime = askBeforeRemovingTime;
	}

	/**
	 * Returns the default time format.
	 * @return
	 */
	public int getDefaultTimeFormatId() {
		return defaultTimeFormatId;
	}

	/**
	 * Sets the default time format.
	 * @param defaultTimeFormatId
	 */
	public void setDefaultTimeFormatId(int defaultTimeFormatId) {
		this.defaultTimeFormatId = defaultTimeFormatId;
	}

	/**
	 * Returns the default hourly rate.
	 * @return
	 */
	public double getDefaultHourlyRate() {
		return defaultHourlyRate;
	}

	/**
	 * Sets the default hourly rate.
	 * @param defaultHourlyRate
	 */
	public void setDefaultHourlyRate(double defaultHourlyRate) {
		this.defaultHourlyRate = defaultHourlyRate;
	}
}
