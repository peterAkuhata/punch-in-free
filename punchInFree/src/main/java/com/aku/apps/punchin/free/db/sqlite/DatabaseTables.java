package com.aku.apps.punchin.free.db.sqlite;

import android.content.Context;

import com.aku.apps.punchin.free.reporting.ClientListReportGenerator;
import com.aku.apps.punchin.free.reporting.IncomeReportGenerator;
import com.aku.apps.punchin.free.reporting.TaskListReportGenerator;
import com.aku.apps.punchin.free.reporting.TimesheetReportGenerator;
import com.aku.apps.punchin.free.utils.Constants;


public class DatabaseTables {
	public static class BaseColumns {
		public static final String _ID = "_id";
		public static final String CREATED = "created";
		public static final String MODIFIED = "modified";
	}
	
	public static class Preferences extends BaseColumns {
		public static final String _TABLE_NAME = "preferences";
		
		public static final String DEFAULT_HOURLY_RATE = "def_hourly_rate";
		public static final String DEFAULT_TIME_FORMAT_ID = "def_time_format_id";
		public static final String DEFAULT_DATE_FORMAT = "def_date_format";
		public static final String DEFAULT_NORMAL_WORKING_HOURS = "def_normal_working_hours";
		public static final String DEFAULT_OVERTIME_MULTIPLIER = "def_overtime_multiplier";
		public static final String DEFAULT_CLIENT_ID = "def_client_id";
		public static final String DEFAULT_MILEAGE_UNIT = "def_mileage_unit";
		public static final String DEFAULT_MILEAGE_RATE = "def_mileage_rate";
		public static final String DEFAULT_REPORT_FORMAT_ID = "def_report_format_id";
		public static final String DEFAULT_REPORT_SENDER_ID = "def_report_sender_id";
		public static final String ASK_BEFORE_REMOVING_TIME = "ask_before_removing_time";
		public static final String COLUMNS_TIMESHEET = "columns_timesheet";
		public static final String COLUMNS_INCOME = "columns_income";
		public static final String COLUMNS_CLIENT_LIST = "columns_client_list";
		public static final String COLUMNS_TASK_LIST = "columns_task_list";
		public static final String DEFAULT_BACKUP_PROVIDER_ID = "def_backup_provider_id";
		public static final String DEFAULT_ACCOUNT_TYPE = "def_account_type";
		public static final String DEFAULT_ACCOUNT_NAME = "def_account_name";
		public static final String DEFAULT_CALENDAR_ID = "def_calendar_id";
		public static final String DEFAULT_CALENDAR_NAME = "def_calendar_name";
		public static final String HTTP_POST_URL = "http_post_url";
		public static final String REPORT_SENDER_FTP_USERNAME = "report_sender_ftp_username";
		public static final String REPORT_SENDER_FTP_PASSWORD = "report_sender_ftp_password";
		public static final String REPORT_SENDER_FTP_SERVERNAME = "report_sender_ftp_servername";
		public static final String REPORT_SENDER_FTP_SUBFOLDER = "report_sender_ftp_subfolder";
		public static final String BACKUP_PROVIDER_FTP_USERNAME = "backup_provider_ftp_username";
		public static final String BACKUP_PROVIDER_FTP_PASSWORD = "backup_provider_ftp_password";
		public static final String BACKUP_PROVIDER_FTP_SERVERNAME = "backup_provider_ftp_servername";
		public static final String BACKUP_PROVIDER_FTP_SUBFOLDER = "backup_provider_ftp_subfolder";
		
		public static final String[] _COLUMNS = new String[] {
			_ID, DEFAULT_HOURLY_RATE, DEFAULT_TIME_FORMAT_ID, DEFAULT_DATE_FORMAT, DEFAULT_NORMAL_WORKING_HOURS, 
			DEFAULT_OVERTIME_MULTIPLIER, DEFAULT_CLIENT_ID, DEFAULT_MILEAGE_UNIT, DEFAULT_MILEAGE_RATE, 
			DEFAULT_REPORT_FORMAT_ID, DEFAULT_REPORT_SENDER_ID, ASK_BEFORE_REMOVING_TIME, COLUMNS_TIMESHEET, 
			COLUMNS_INCOME, COLUMNS_CLIENT_LIST, COLUMNS_TASK_LIST, DEFAULT_BACKUP_PROVIDER_ID, 
			DEFAULT_ACCOUNT_TYPE, DEFAULT_ACCOUNT_NAME, DEFAULT_CALENDAR_ID, DEFAULT_CALENDAR_NAME, HTTP_POST_URL,
			CREATED, MODIFIED, REPORT_SENDER_FTP_USERNAME, REPORT_SENDER_FTP_PASSWORD, REPORT_SENDER_FTP_SERVERNAME,
			REPORT_SENDER_FTP_SUBFOLDER, BACKUP_PROVIDER_FTP_USERNAME, BACKUP_PROVIDER_FTP_PASSWORD, BACKUP_PROVIDER_FTP_SERVERNAME,
			BACKUP_PROVIDER_FTP_SUBFOLDER
		};

		public static final String _TABLE_CREATE = "CREATE TABLE [preferences] ( "+
					"[_id] INTEGER  PRIMARY KEY AUTOINCREMENT NOT NULL, "+
					"[def_hourly_rate] FLOAT  NOT NULL, "+
					"[def_time_format_id] INTEGER  NOT NULL, "+
					"[def_date_format] VARCHAR(40)  NOT NULL, "+
					"[def_normal_working_hours] FLOAT  NOT NULL, "+
					"[def_overtime_multiplier] FLOAT  NOT NULL, "+
					"[def_client_id] INTEGER  NOT NULL, "+
					"[def_mileage_unit] VARCHAR(10)  NOT NULL, "+
					"[def_mileage_rate] FLOAT  NOT NULL, "+
					"[def_report_format_id] INTEGER  NOT NULL, "+
					"[def_report_sender_id] INTEGER  NOT NULL, "+
					"[ask_before_removing_time] BOOLEAN  NOT NULL, "+
					"[columns_timesheet] TEXT  NOT NULL, "+
					"[columns_income] TEXT  NOT NULL, "+
					"[columns_client_list] TEXT  NOT NULL, "+
					"[columns_task_list] TEXT  NOT NULL, "+
					"[def_backup_provider_id] INTEGER  NOT NULL, "+
					"[def_account_type] VARCHAR(100)  NOT NULL, "+
					"[def_account_name] VARCHAR(100)  NOT NULL, "+
					"[def_calendar_id] VARCHAR(100)  NOT NULL, "+
					"[def_calendar_name] VARCHAR(100)  NOT NULL, "+
					"[http_post_url] VARCHAR(250) NULL," +
					"[report_sender_ftp_username] VARCHAR(100), " +
					"[report_sender_ftp_password] VARCHAR(100), " +
					"[report_sender_ftp_servername] VARCHAR(500), " +
					"[report_sender_ftp_subfolder] VARCHAR(250), " +
					"[backup_provider_ftp_username] VARCHAR(100), " +
					"[backup_provider_ftp_password] VARCHAR(100), " +
					"[backup_provider_ftp_servername] VARCHAR(500), " +
					"[backup_provider_ftp_subfolder] VARCHAR(250), " +
					"[created] DATE  NOT NULL, " +
					"[modified] DATE  NOT NULL " +
				")";
		
		public static String getInsert(Context context) {
			String temp = _TABLE_DEFAULT_PREFERENCES;
			
			temp = temp.replace("[TIMESHEET]", TimesheetReportGenerator.getAvailableColumnsCommaSeparated(context));
			temp = temp.replace("[INCOME]", IncomeReportGenerator.getAvailableColumnsCommaSeparated(context));
			temp = temp.replace("[CLIENT_LIST]", ClientListReportGenerator.getAvailableColumnsCommaSeparated(context));
			temp = temp.replace("[TASK_LIST]", TaskListReportGenerator.getAvailableColumnsCommaSeparated(context));
			return temp;
		}
		
		private static final String _TABLE_DEFAULT_PREFERENCES = "INSERT INTO [preferences] (" +
				"[def_hourly_rate], [def_time_format_id], [def_date_format], " +				
				"[def_normal_working_hours], [def_overtime_multiplier], [def_client_id], " +				
				"[def_mileage_unit], [def_mileage_rate], [def_report_format_id], " +				
				"[def_report_sender_id], [ask_before_removing_time], [columns_timesheet], " +				
				"[columns_income], [columns_client_list], [columns_task_list], [def_backup_provider_id], " +				
				"[def_account_type], [def_account_name], [def_calendar_id], [def_calendar_name], " +
				"[created], [modified], [http_post_url], [report_sender_ftp_username], " +
				"[report_sender_ftp_password], [report_sender_ftp_servername], " +
				"[report_sender_ftp_subfolder], [backup_provider_ftp_username], " +
				"[backup_provider_ftp_password], [backup_provider_ftp_servername], " +
				"[backup_provider_ftp_subfolder]) values (" +
				
				"0, " + Constants.TimeFormatters.BY_SECONDS + ", " + "'" + Constants.Defaults.DATE_FORMAT + "', " +
				"8, 1, -1, " +
				"'km', 0, " + Constants.ReportFormatters.HTML + ", " +
				Constants.ReportSenders.SD_CARD + ", 1, '[TIMESHEET]', " + 
				"'[INCOME]', '[CLIENT_LIST]', '[TASK_LIST]', " + Constants.BackupProviders.SD_CARD + "," +
				"'', '', '', '', " +
				"DATETIME('NOW'), DATETIME('NOW'), " +
				"'', '', '', '', '', " +
				"'', '', '', '')";
	}
	
	public static class Checkpoint extends BaseColumns {
		public static final String _TABLE_NAME = "check_point";
		
		public static final String NAME = "name";
		public static final String DATE = "date";
		public static final String EXTRA_DATA = "extra_data";
		public static final String DESCRIPTION = "description";
		
		public static final String[] _COLUMNS = new String[] {
			_ID, NAME, DESCRIPTION, DATE, EXTRA_DATA,
			CREATED, MODIFIED
		};

		public static final String _TABLE_CREATE = "CREATE TABLE [check_point] ( " +
						"[_id] INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT, " +
						"[name] VARCHAR(250)  NOT NULL, " +
						"[description] VARCHAR(250) NULL, " + 
						"[date] DATE  NOT NULL, " +
						"[extra_data] TEXT  NULL, " +
						"[created] DATE  NOT NULL, " +
						"[modified] DATE  NOT NULL " +
					") ";
	}
	
	public static class CalendarEvent extends BaseColumns {
		public static final String _TABLE_NAME = "calendar_event";
		
		public static final String ALL_DAY = "all_day";
		public static final String STATUS = "status";
		public static final String VISIBILITY = "visibility";
		public static final String TRANSPARENCY = "transparency";
		public static final String HAS_ALARM = "has_alarm";
		public static final String ANDROID_EVENT_ID = "android_event_id";
		public static final String START_TIME = "start_time";
		public static final String END_TIME = "end_time";
		public static final String TIME_STAMP_ID_PUNCH_IN = "time_stamp_id_punch_in";
		public static final String TIME_STAMP_ID_PUNCH_OUT = "time_stamp_id_punch_out";
		public static final String TASK_DAY_ID = "task_day_id";
		
		public static final String[] _COLUMNS = new String[] {
			_ID, ALL_DAY, STATUS, VISIBILITY, TRANSPARENCY, HAS_ALARM, 
			ANDROID_EVENT_ID, START_TIME, END_TIME, TIME_STAMP_ID_PUNCH_IN,
			TIME_STAMP_ID_PUNCH_OUT, TASK_DAY_ID,
			CREATED, MODIFIED
		};

		public static final String _TABLE_CREATE = "CREATE TABLE [calendar_event] ( " +
						"[_id] INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT, " +
						"[all_day] BOOLEAN  NOT NULL, " +
						"[status] INTEGER  NOT NULL, " +
						"[visibility] INTEGER  NOT NULL, " +
						"[transparency] INTEGER  NOT NULL, " +
						"[has_alarm] BOOLEAN  NOT NULL, " +
						"[android_event_id] VARCHAR(250)  NOT NULL, " +
						"[start_time] TIME  NULL, " +
						"[end_time] TIME  NULL, " +
						"[time_stamp_id_punch_in] INTEGER  NULL, " +
						"[time_stamp_id_punch_out] INTEGER  NULL, " +
						"[task_day_id] INTEGER  NOT NULL, " +
						"[created] DATE  NOT NULL, " +
						"[modified] DATE  NOT NULL " +
					")";
	}
	
	public static class Expense extends BaseColumns {
		public static final String _TABLE_NAME = "expense";
		
		public static final String TASK_DAY_ID = "task_day_id";
		public static final String TYPE = "type";
		public static final String AMOUNT = "amount";
		public static final String NOTES = "notes";
		
		public static final String[] _COLUMNS = new String[] {
			_ID, TASK_DAY_ID, TYPE, AMOUNT, NOTES,
			CREATED, MODIFIED
		};

		public static final String _TABLE_CREATE = "CREATE TABLE [expense] ( " +
						"[_id] INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT, " +
						"[task_day_id] INTEGER  NOT NULL, " +
						"[type] INTEGER  NOT NULL, " +
						"[amount] FLOAT  NOT NULL, " +
						"[notes] TEXT  NOT NULL, " +
						"[created] DATE  NOT NULL, " +
						"[modified] DATE  NOT NULL " +
					")";
	}
	
	public static class TaskDay extends BaseColumns {
		public static final String _TABLE_NAME = "task_day";
		
		public static final String NOTES = "notes";
		public static final String TASK_ID = "task_id";
		public static final String DAY_ID = "day_id";
		
		public static final String[] _COLUMNS = new String[] {
			_ID, NOTES, TASK_ID, DAY_ID, 
			CREATED, MODIFIED
		};

		public static final String _TABLE_CREATE = "CREATE TABLE [task_day] ( " +
						"[_id] INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT, " +
						"[notes] TEXT  NOT NULL, " +
						"[task_id] INTEGER  NOT NULL, " +
						"[day_id] INTEGER  NOT NULL, " +
						"[created] DATE  NOT NULL, " +
						"[modified] DATE  NOT NULL " +
					")";
	}
	
	public static class TimeStamp extends BaseColumns {
		public static final String _TABLE_NAME = "time_stamp";
		
		public static final String TIME = "time";
		public static final String TYPE = "type";
		public static final String TASK_DAY_ID = "task_day_id";
		
		public static final String[] _COLUMNS = new String[] {
			_ID, TIME, TYPE, TASK_DAY_ID,
			CREATED, MODIFIED
		};

		public static final String _TABLE_CREATE = "CREATE TABLE [time_stamp] ( " +
						"[_id] INTEGER  PRIMARY KEY AUTOINCREMENT NOT NULL, " +
						"[time] TIME  NOT NULL, " +
						"[type] INTEGER  NOT NULL, " +
						"[task_day_id] INTEGER  NOT NULL, " +
						"[created] DATE  NOT NULL, " +
						"[modified] DATE  NOT NULL " +
					")";
	}
	
	public static class Task extends BaseColumns {
		public static final String _TABLE_NAME = "task";
		
		public static final String DESCRIPTION = "description";
		public static final String START_DATE = "start_date";
		public static final String END_DATE = "end_date";
		public static final String REPEATING_TYPE = "repeating_type";
		public static final String CLIENT_ID = "client_id";
		public static final String ACTIVE = "active";
		public static final String SORT = "sort";
		
		public static final String[] _COLUMNS = new String[] {
			_ID, DESCRIPTION, START_DATE, END_DATE, 
			REPEATING_TYPE, CLIENT_ID,  ACTIVE, SORT, 
			CREATED, MODIFIED
		};

		public static final String _TABLE_CREATE = "CREATE TABLE [task] ( " +
						"[_id] INTEGER  PRIMARY KEY AUTOINCREMENT NOT NULL, " +
						"[description] VARCHAR(100)  NOT NULL, " +
						"[start_date] DATE  NOT NULL, " +
						"[end_date] DATE  NULL, " +
						"[repeating_type] INTEGER  NOT NULL, " +
						"[client_id] INTEGER  NOT NULL, " +
						"[active] BOOLEAN  NOT NULL, " +
						"[sort] INTEGER  NOT NULL, " +
						"[created] DATE  NOT NULL, " +
						"[modified] DATE  NOT NULL " +
					")";
	}
	
	public static class Client extends BaseColumns {
		public static final String _TABLE_NAME = "client";
		
		public static final String NAME = "name";
		public static final String EMAIL = "email";
		public static final String ADDRESS = "address";
		public static final String MOBILE = "mobile";
		public static final String HOURLY_RATE = "hourly_rate";
		public static final String NORMAL_WORKING_HOURS = "normal_working_hours";
		public static final String MILEAGE_RATE = "mileage_rate";
		public static final String OVERTIME_MULTIPLIER = "overtime_multiplier";
		public static final String ACTIVE = "active";
		public static final String SORT = "sort";
		public static final String ANDROID_CONTACT_ID = "android_id";
		public static final String ANDROID_CONTACT_EMAIL_ID = "android_email_id";
		public static final String ANDROID_CONTACT_MOBILE_ID = "android_mobile_id";
		
		public static final String[] _COLUMNS = new String[] {
			_ID, NAME, EMAIL, ADDRESS, MOBILE, HOURLY_RATE,
			NORMAL_WORKING_HOURS, MILEAGE_RATE,  OVERTIME_MULTIPLIER, ACTIVE, SORT,
			ANDROID_CONTACT_ID, ANDROID_CONTACT_EMAIL_ID, ANDROID_CONTACT_MOBILE_ID,
			CREATED, MODIFIED
		};

		public static final String _TABLE_CREATE = "CREATE TABLE [client] ( " +
						"[_id] INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT, " +
						"[name] VARCHAR(100)  NOT NULL, " +
						"[email] VARCHAR(250)  NOT NULL, " +
						"[address] VARCHAR(250)  NULL, " +
						"[mobile] VARCHAR(40)  NULL, " +
						"[hourly_rate] FLOAT  NOT NULL, " +
						"[normal_working_hours] FLOAT  NOT NULL, " +
						"[mileage_rate] FLOAT  NOT NULL, " +
						"[overtime_multiplier] FLOAT  NOT NULL, " +
						"[active] BOOLEAN  NOT NULL, " +
						"[sort] INTEGER  NOT NULL, " +
						"[android_id] VARCHAR(40)  NULL, " +
						"[android_email_id] VARCHAR(40)  NULL, " +
						"[android_mobile_id] VARCHAR(40)  NULL, " +
						"[created] DATE  NOT NULL, " +
						"[modified] DATE  NOT NULL " +
					")";
	}
	
	public static class Day extends BaseColumns {
		public static final String _TABLE_NAME = "day";
		
		public static final String DATE = "date";
		public static final String DAILY_NOTES = "daily_notes";
		
		public static final String[] _COLUMNS = new String[] {
			_ID, DATE, DAILY_NOTES, CREATED, MODIFIED
		};

		public static final String _TABLE_CREATE = "CREATE TABLE [day] ( " +
						"[_id] INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT, " +
						"[date] DATE  NOT NULL, " +
						"[daily_notes] TEXT  NOT NULL, " +
						"[created] DATE  NOT NULL, " +
						"[modified] DATE  NOT NULL " +
					")";
	}
}
