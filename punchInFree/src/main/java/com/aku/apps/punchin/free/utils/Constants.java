package com.aku.apps.punchin.free.utils;

public class Constants {
	/**  The static number of pages used in the PagedView **/
	public static final int PAGE_COUNT = 10001;
	
	/** A static number that represents  the middle page, i.e, the current date */
	public static final int PAGE_MIDDLE = 5001;
	
	public static final String FONT_LOCATION = "";
	
	public static final String BACKUP_DATABASE = "BACKUP_DATABASE";
	public static final String BACKUP_FILE_NAME = "BACKUP_FILE_NAME";
	public static final String BACKUP_ERROR_MESSAGE = "BACKUP_ERROR_MESSAGE";
	
	public class WidgetFrequency {
		public final static String _NAME = "frequencyName";
		public final static int EVERY_SECOND = 0;
		public final static int EVERY_MINUTE = 1;
		public final static int EVERY_15_MINUTES = 2;
		public final static int EVERY_HALF_HOUR = 3;
		public final static int EVERY_HOUR = 4;
		public final static int EVERY_DAY = 5;
	}
	
	public class BackupProviders {
		/**
		 * Unique id for the sd card backup provider
		 */
		public final static long SD_CARD = 1;

		/**
		 * Unique id for the ftp backup provider
		 */
		public final static long FTP = 2;

		/**
		 * Unique id for the dropbox backup provider
		 */
		public final static long DROP_BOX = 3;
	}
	
	public class Defaults {
		/**
		 * The default date format
		 */
		public static final String DATE_FORMAT = "EEEE, d MMMM yyyy";
		
		/**
		 * The base punchin folder location on the sd card.
		 */
		public static final String FOLDER_LOCATION_BASE = "/sdcard/punchin free/";
		
		/**
		 * The folder on the sd card used to export timesheets to.
		 */
		public static final String FOLDER_LOCATION_EXPORT = FOLDER_LOCATION_BASE + "export/";
		
		/**
		 * The folder on the sd card used to export sd card backups to.
		 */
		public static final String FOLDER_LOCATION_BACKUP = FOLDER_LOCATION_BASE + "backup/";
		
		/**
		 * The punchin database folder on the android phone that stores the punchin database.
		 */
		public static final String FILE_LOCATION_DATABASE = "/data/data/com.aku.apps.punchin.free/databases/punchin.db";
		
		/**
		 * The folder on the android phone that stores the punchin database.
		 */
		public static final String DATABASE_FILE_NAME = "punchin.db";
	}
	
	public class ReportSenders {
		/**
		 * Represents the sd card report sender.
		 */
		public static final int SD_CARD = 0;
		
		/**
		 * Represents the file transfer report sender.
		 */
		public static final int FILE_TRANSFER = 1;
		
		/**
		 * Represents the http post report sender
		 */
		public static final int HTTP_POST = 2;
		
		/**
		 * Represents the ftp report sender
		 */
		public static final int FTP = 3;
	}
	
	public class TimeFormatters {
		/**
		 * Represents the by seconds time formatter.
		 */
		public static final int BY_SECONDS = 1;
		
		/**
		 * Represents the by minutes time formatter.
		 */
		public static final int BY_MINUTES = 2;
	}
	
	public class ReportFormatters {
		/**
		 * Represents the csv report formatter
		 */
		public static final int CSV = 0;
		
		/**
		 * Represents the xml report formatter
		 */
		public final static int XML = 1;

		/**
		 * Represents the html report formatter.
		 */
		public static final int HTML = 2;

		/**
		 * Represents the json report formatter.
		 */
		public static final int JSON = 3;
	}

	public class Dialogs {
	    
	    /** A unique id for the date dialog. */
		public static final int SHOW_DATE = 0;

		/**
		 * A unique id for the hourly rate dialog
		 */
		public static final int HOURLY_RATE = 1;
		
		/**
		 * A unique id for a dialog to set the active flag for a task.
		 */
		public static final int ACTIVE_TASK = 2;
		
		/**
		 * A unique id to display a list of clients for the user to select.
		 */
		public static final int SELECT_CLIENT = 5;
		
		/**
		 * A unique id for a yes/no dialog to see if the user wants to create a new client.
		 */
		public static final int YES_NO_CREATE_NEW_CLIENT = 6;
		
		/**
		 * A unique id for a dialog to type in the mobile number (for a client).
		 */
		public static final int MOBILE_NUMBER = 7;
		
		/**
		 * A unique id for the normal working hours dialog
		 */
		public static final int NORMAL_WORKING_HOURS = 8;
		
		/**
		 * A unique id for the overtime rate dialog
		 */
		public static final int OVERTIME_RATE = 9;
		
		/**
		 * A unique id for the clear time dialog
		 */
		public static final int CLEAR_TIME = 10;
		
		/**
		 * A unique id for the mileage rate dialog.
		 */
		public static final int MILEAGE_RATE = 11;
		
		/**
		 * A unique id for the distance travelled dialog.
		 */
		public static final int DISTANCE_TRAVELLED = 12;
		
		/**
		 * A unique id for the costing amount dialog.
		 */
		public static final int COSTING_AMOUNT = 13;
		
		/**
		 * A unique id for the start date dialog.
		 */
		public static final int FROM_DATE = 14;
		
		/**
		 * A unique id for the end date dialog.
		 */
		public static final int TO_DATE = 15;
		
		/**
		 * A unique id for the export format dialog.
		 */
		public static final int EXPORT_FORMAT = 16;
		
		/**
		 * A unique id for the send to dialog.
		 */
		public static final int SEND_TO = 17;
		
		/**
		 * A unique id for the duration dialog.
		 */
		public static final int DURATION = 18;
		
		/**
		 * A unique id for the select columns dialog.
		 */
		public static final int COLUMNS = 19;
		
		/**
		 * A unique id for the email dialog.
		 */
		public static final int EMAIL = 20;
		
		/**
		 * A unique id for the backup provider dialog.
		 */
		public static final int BACKUP_PROVIDER = 21;
		
		/**
		 * A unique id for the select checkpoint dialog.
		 */
		public static final int SELECT_CHECKPOINT = 22;
		
		/**
		 * A unique id for the select time format dialog.
		 */
		public static final int SELECT_TIME_FORMAT = 23;
		
		/**
		 * A unique id for the select date format dialog.
		 */
		public static final int SELECT_DATE_FORMAT = 24;
		
		/**
		 * A unique id for the mileage unit dialog.
		 */
		public static final int MILEAGE_UNIT = 25;
		
		/**
		 * A unique id for the select report format dialog.
		 */
		public static final int SELECT_REPORT_FORMAT = 26;
		
		/**
		 * A unique id for the select report sender dialog.
		 */
		public static final int SELECT_REPORT_SENDER = 27;
		
		/**
		 * A unique id for the 'ask before removing time' dialog.
		 */
		public static final int ASK_BEFORE_REMOVING_TIME = 28;
	    
	    /** 
	     * A unique id for the start date dialog. 
	     */
		public static final int START_DATE = 29;
	    
	    /** 
	     * A unique id for the end date dialog. 
	     */
		public static final int END_DATE = 30;
	    
	    /** 
	     * A unique id for the repeating task dialog. 
	     */
		public static final int REPEATING_TASK = 31;
		
		/**
		 * A unique id for the sync contacts dialog.
		 */
		public static final int SELECT_ACCOUNT = 32;
		
		/**
		 * A unique id for the sync calendar dialog.
		 */
		public static final int SYNC_CALENDAR = 33;
		
		/**
		 * A unique id for the http post url dialog.
		 */
		public static final int HTTP_POST_URL = 34;
		
		/**
		 * A unique id for the report sender ftp server name dialog.
		 */
		public static final int REPORT_SENDER_FTP_SERVER_NAME = 35;
		
		/**
		 * A unique id for the report sender ftp username dialog.
		 */
		public static final int REPORT_SENDER_FTP_USERNAME = 36;
		
		/**
		 * A unique id for the report sender ftp password dialog.
		 */
		public static final int REPORT_SENDER_FTP_PASSWORD = 37;
		
		/**
		 * A unique id for the report sender ftp sub folder dialog.
		 */
		public static final int REPORT_SENDER_FTP_SUB_FOLDER = 38;
		
		/**
		 * A unique id for the checkpoint description dialog.
		 */
		public static final int CHECKPOINT_DESCRIPTION = 39;
		
		/**
		 * A unique id for the widget frequency dialog.
		 */
		public static final int SELECT_WIDGET_FREQUENCY = 40;
				
		/**
		 * A unique id for the ask to select a backup file dialog.
		 */
		public static final int SELECT_BACKUP_FILE = 41;
	}
	
	public class Clients {
		/**
		 * The maximum length that a client name can be.
		 */
		public static final int MAX_LENGTH_NAME = 40;
	}
	
	public class Projects {
		/**
		 * The maximum length that the title field can be.
		 */
		public static final int MAX_LENGTH_TITLE = 40;
	}
	
	public class Tasks {
		/**
		 * The maximum length that the description of a task can be.
		 */
		public static final int MAX_LENGTH_DESCRIPTION = 100;
	}
	
	public class RequestCodes {
		/**
		 * A unique id used to represent the request code when receiving results from an intent (main activity to add task activity).
		 */
		public static final int EDIT_TASK = 0;
		
		/**
		 * A unique id used to represent the request code when receiving results from an intent (main activity to the task list activity).
		 */
		public static final int TASK_LIST = 1;
		
		/**
		 * A unique id used to represent the request code when receiving results from an intent (task activity to project activity).
		 */
		public static final int EDIT_PROJECT = 2;
		
		/**
		 * A unique id used to represent the request code when receiving results from an intent (main activity to the project list activity).
		 */
		public static final int PROJECT_LIST = 3;
		
		/**
		 * A unique id used to represent the request code when editing daily notes/task notes/project description.
		 */
		public static final int NOTES = 4;
		
		/**
		 * A unique id used to represent the request code when receiving results from an intent (project activity to client activity).
		 */
		public static final int EDIT_CLIENT = 5;
		
		/**
		 * A unique id used to represent the request code when receiving results from an intent (main activity to the client list activity).
		 */
		public static final int CLIENT_LIST = 6;
		
		/**
		 * A unique id used to represent the request code when receiving results from an intent (main activity to the expense list activity).
		 */
		public static final int EXPENSE_LIST = 7;
		
		/**
		 * A unique id used to represent the request code when receiving results from an intent (expense list activity to expense activity).
		 */
		public static final int EDIT_EXPENSE = 8;
		
		/**
		 * A unique id used to represent the request code when receiving results from an intent (main activity to add time activity).
		 */
		public static final int ADD_TIME = 9;
		
		/**
		 * A unique id used to represent the request code when receiving results from an intent (main activity to daily events activity).
		 */
		public static final int DAILY_EVENT = 10;
		
		/**
		 * A unique id used to represent the request code when receiving results from an intent (clients activity to client import activity).
		 */
		public static final int IMPORT_CLIENTS = 11;
		
		/**
		 * A result code for preferences activity.
		 */
		public static final int PREFERENCES = 12;
		
		/**
		 * A result code for reporting preferences activity.
		 */
		public static final int PREFERENCES_REPORTING = 13;
		
		/**
		 * A result code for backup provider preferences activity.
		 */
		public static final int PREFERENCES_BACKUP_PROVIDER = 14;
		
		/**
		 * A result code for the 'select file from file system' activity.
		 */
		public static final int SELECT_FILE = 15;
	}
}
