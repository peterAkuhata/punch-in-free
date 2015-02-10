package com.aku.apps.punchin.free.db;

import com.aku.apps.punchin.free.domain.Client;
import com.aku.apps.punchin.free.domain.Preferences;
import com.aku.apps.punchin.free.domain.TimeFormatter;
import com.aku.apps.punchin.free.reporting.ReportFormatter;
import com.aku.apps.punchin.free.reporting.ReportSender;

/**
 * Represents a factory of factories relevant to a datasource.
 * 
 * @author Peter Akuhata
 *
 */
public interface DatasourceFactory extends BaseFactory {
	/**
	 * Creates and returns a task day factory.
	 */
	public abstract TaskDayFactory createTaskDayFactory();
	
	/**
	 * Creates and returns a calendar event factory.
	 */
	public abstract CalendarEventFactory createCalendarEventFactory();
	
	/**
	 * Creates and returns a backup provider factory.
	 */
	public abstract BackupProviderFactory createBackupProviderFactory();
	
	/**
	 * Creates and returns a report sender factory.
	 * @return
	 */
	public abstract ReportSenderFactory createReportSenderFactory();
	
	/**
	 * Creates and returns a report formatter factory.
	 * @return
	 */
	public abstract ReportFormatterFactory createReportFormatterFactory();

	/**
	 * Creates and returns an expense factory.
	 * @return
	 */
	public abstract ExpenseFactory createExpenseFactory();
	
	/**
	 * Creates a preferences object and returns it.
	 * @return
	 */
	public abstract Preferences createPreferences();
	
	/**
	 * Creates the default backup provider and returns it.
	 * @return
	 */
	public abstract BackupProvider createDefaultBackupProvider();

	/**
	 * Creates and returns the default time format object.
	 * @return
	 */
	public abstract TimeFormatter createDefaultTimeFormat();

	/**
	 * Creates and returns a client factory.
	 * @return
	 */
	public abstract ClientFactory createClientFactory();

	/**
	 * Creates and returns a time format factory.
	 * @return
	 */
	public abstract TimeFormatterFactory createTimeFormatFactory();

	/**
	 * Creates and returns a preference factory object.
	 * @return
	 */
	public abstract PreferenceFactory createPreferenceFactory();

	/**
	 * Creates and returns a task factory object.
	 * @return
	 */
	public abstract TaskFactory createTaskFactory();

	/**
	 * Creates and returns a time stamp factory object.
	 * @return
	 */
	public abstract TimeStampFactory createTimeStampFactory();

	/**
	 * Creates and returns a day factory.
	 * @return
	 */
	public abstract DayFactory createDayFactory();

	/**
	 * Updates the preferences using the specified {@link Preferences} instance.
	 * @param prefs
	 */
	public abstract void updatePreferences(Preferences prefs);

	/**
	 * Looks up the default client id from preferences and creates a client instance for that id.
	 * @return
	 */
	public abstract Client createDefaultClient();

	/**
	 * Looks up the default report sender from prefs and creates a report sender object.
	 * @return
	 */
	public abstract ReportSender createDefaultReportSender();

	/**
	 * Looks up the default report formatter from prefs and creates a report formatter object.
	 * @return
	 */
	public abstract ReportFormatter createDefaultReportFormat();

}