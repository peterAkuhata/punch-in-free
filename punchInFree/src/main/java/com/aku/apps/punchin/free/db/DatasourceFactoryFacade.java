package com.aku.apps.punchin.free.db;

import android.content.Context;

import com.aku.apps.punchin.free.db.sqlite.SQLiteDatasourceFactory;
import com.aku.apps.punchin.free.domain.Client;
import com.aku.apps.punchin.free.domain.Preferences;
import com.aku.apps.punchin.free.domain.TimeFormatter;
import com.aku.apps.punchin.free.reporting.ReportFormatter;
import com.aku.apps.punchin.free.reporting.ReportSender;

/**
 * 
 * @author Peter Akuhata
 * 
 *         This is a factory facade class. It's sole purpose is to create
 *         factory objects for use by the system. It implements
 *         {@link DatasourceFactory} and links to a concrete implementation.
 */
public class DatasourceFactoryFacade implements DatasourceFactory {
	/**
	 * The singleton instance of this facade.
	 */
	private static DatasourceFactory factory = null;

	/**
	 * TODO: Change this to the sqlite database objects on changeover
	 */
	private DatasourceFactory actualFactory = null;

	/**
	 * Returns the singleton instance of this facade.
	 * 
	 * @return
	 */
	public static DatasourceFactory getInstance(Context ctx) {
		if (factory == null)
			factory = new DatasourceFactoryFacade(ctx);
		
		return factory;
	}
	
	/**
	 * Creates a {@link DatasourceFactoryFacade}.
	 * @param ctx
	 */
	public DatasourceFactoryFacade(Context ctx) {
		super();
		
		this.actualFactory = new SQLiteDatasourceFactory(ctx);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.aku.apps.punchin.free.db.DatabaseFactory#createPreferences()
	 */
	@Override
	public Preferences createPreferences() {
		return actualFactory.createPreferences();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.aku.apps.punchin.free.db.DatabaseFactory#createDefaultTimeFormat()
	 */
	@Override
	public TimeFormatter createDefaultTimeFormat() {
		return actualFactory.createDefaultTimeFormat();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.aku.apps.punchin.free.db.DatabaseFactory#createClientFactory()
	 */
	@Override
	public ClientFactory createClientFactory() {
		return actualFactory.createClientFactory();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.aku.apps.punchin.free.db.DatabaseFactory#createTimeFormatFactory()
	 */
	@Override
	public TimeFormatterFactory createTimeFormatFactory() {
		return actualFactory.createTimeFormatFactory();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.aku.apps.punchin.free.db.DatabaseFactory#createPreferenceFactory()
	 */
	@Override
	public PreferenceFactory createPreferenceFactory() {
		return actualFactory.createPreferenceFactory();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.aku.apps.punchin.free.db.DatabaseFactory#createTaskFactory()
	 */
	@Override
	public TaskFactory createTaskFactory() {
		return actualFactory.createTaskFactory();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.aku.apps.punchin.free.db.DatabaseFactory#createTimeStampFactory()
	 */
	@Override
	public TimeStampFactory createTimeStampFactory() {
		return actualFactory.createTimeStampFactory();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.aku.apps.punchin.free.db.DatabaseFactory#createDayFactory()
	 */
	@Override
	public DayFactory createDayFactory() {
		return actualFactory.createDayFactory();
	}

	@Override
	public ExpenseFactory createExpenseFactory() {
		return actualFactory.createExpenseFactory();
	}

	@Override
	public ReportSenderFactory createReportSenderFactory() {
		return actualFactory.createReportSenderFactory();
	}

	@Override
	public ReportFormatterFactory createReportFormatterFactory() {
		return actualFactory.createReportFormatterFactory();
	}

	@Override
	public void updatePreferences(Preferences prefs) {
		actualFactory.updatePreferences(prefs);
	}

	@Override
	public void clearCache() {
		actualFactory.clearCache();
	}

	@Override
	public BackupProviderFactory createBackupProviderFactory() {
		return actualFactory.createBackupProviderFactory();
	}

	@Override
	public BackupProvider createDefaultBackupProvider() {
		return actualFactory.createDefaultBackupProvider();
	}

	@Override
	public Client createDefaultClient() {
		return actualFactory.createDefaultClient();
	}

	@Override
	public ReportSender createDefaultReportSender() {
		return actualFactory.createDefaultReportSender();
	}

	@Override
	public ReportFormatter createDefaultReportFormat() {
		return actualFactory.createDefaultReportFormat();
	}

	@Override
	public CalendarEventFactory createCalendarEventFactory() {
		return actualFactory.createCalendarEventFactory();
	}

	@Override
	public TaskDayFactory createTaskDayFactory() {
		return actualFactory.createTaskDayFactory();
	}
}
