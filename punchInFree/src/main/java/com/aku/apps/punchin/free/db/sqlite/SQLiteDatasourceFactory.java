package com.aku.apps.punchin.free.db.sqlite;

import android.content.Context;
import android.os.Handler;

import com.aku.apps.punchin.free.db.BackupProvider;
import com.aku.apps.punchin.free.db.BackupProviderFactory;
import com.aku.apps.punchin.free.db.BaseFactory;
import com.aku.apps.punchin.free.db.CalendarEventFactory;
import com.aku.apps.punchin.free.db.ClientFactory;
import com.aku.apps.punchin.free.db.DatasourceFactory;
import com.aku.apps.punchin.free.db.DayFactory;
import com.aku.apps.punchin.free.db.DefaultTimeFormatFactory;
import com.aku.apps.punchin.free.db.ExpenseFactory;
import com.aku.apps.punchin.free.db.PreferenceFactory;
import com.aku.apps.punchin.free.db.ReportFormatterFactory;
import com.aku.apps.punchin.free.db.ReportSenderFactory;
import com.aku.apps.punchin.free.db.TaskDayFactory;
import com.aku.apps.punchin.free.db.TaskFactory;
import com.aku.apps.punchin.free.db.TimeFormatterFactory;
import com.aku.apps.punchin.free.db.TimeStampFactory;
import com.aku.apps.punchin.free.domain.Client;
import com.aku.apps.punchin.free.domain.Preferences;
import com.aku.apps.punchin.free.domain.TimeFormatter;
import com.aku.apps.punchin.free.reporting.DefaultReportFormatterFactory;
import com.aku.apps.punchin.free.reporting.DefaultReportSenderFactory;
import com.aku.apps.punchin.free.reporting.ReportFormatter;
import com.aku.apps.punchin.free.reporting.ReportSender;

public class SQLiteDatasourceFactory implements DatasourceFactory {
	private TimeFormatterFactory timeFormatFactory = null;
	private PreferenceFactory preferenceFactory = null;
	private TaskFactory taskFactory = null;
	private TimeStampFactory timeStampFactory = null;
	private DayFactory dayFactory = null;
	private Preferences preferences = null;
	private ClientFactory clientFactory = null;
	private ExpenseFactory expenseFactory = null;
	private ReportFormatterFactory formatterFactory = null;
	private ReportSenderFactory senderFactory = null;
	private BackupProviderFactory backupFactory = null;
	private CalendarEventFactory calendarEventFactory = null;
	private TaskDayFactory taskDayFactory = null;
	private DatabaseHelper helper = null;	
	private Context context;
	private Handler handler;
	
	public SQLiteDatasourceFactory(Context ctx) {
		super();

		this.context = ctx;
		this.helper = new DatabaseHelper(ctx);
		this.helper.getWritableDatabase();
		this.helper.close();
		this.handler = new Handler();
	}

	@Override
	public void clearCache() {
		clearCaches(calendarEventFactory, 
				timeFormatFactory, 
				preferenceFactory, 
				taskFactory, 
				timeStampFactory, 
				dayFactory, 
				clientFactory,
				expenseFactory,
				formatterFactory,
				senderFactory,
				taskDayFactory,
				backupFactory);
		
		this.preferences = null;
	}
	
	/**
	 * Clears the cache for each specified base factory
	 * @param list
	 */
	private void clearCaches(BaseFactory...list) {		
		if (list != null && list.length > 0) {
			for (BaseFactory item : list) {
				if (item != null) {
					item.clearCache();
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.aku.apps.punchin.free.db.DatabaseFactory#createPreferences()
	 */
	@Override
	public Preferences createPreferences() {
		if (preferences == null) {
			PreferenceFactory f = createPreferenceFactory();
			preferences = f.get();
		}
		
		return preferences;
	}
	
	/* (non-Javadoc)
	 * @see com.aku.apps.punchin.free.db.DatabaseFactory#createDefaultTimeFormat()
	 */
	@Override
	public TimeFormatter createDefaultTimeFormat() {
		TimeFormatter format = null;
		
		PreferenceFactory f = createPreferenceFactory();
		Preferences prefs = f.get();
		TimeFormatterFactory tff = createTimeFormatFactory();
		format = tff.get(prefs);
		
		return format;
	}
	
	/* (non-Javadoc)
	 * @see com.aku.apps.punchin.free.db.DatabaseFactory#createClientFactory()
	 */
	@Override
	public ClientFactory createClientFactory() {
		if (clientFactory == null)
			clientFactory = new SQLiteClientFactory(this.helper);
		
		return clientFactory;
	}
	
	/* (non-Javadoc)
	 * @see com.aku.apps.punchin.free.db.DatabaseFactory#createTimeFormatFactory()
	 */
	@Override
	public TimeFormatterFactory createTimeFormatFactory() {
		if (timeFormatFactory == null)
			timeFormatFactory = new DefaultTimeFormatFactory();
		
		return timeFormatFactory;
	}
	
	/* (non-Javadoc)
	 * @see com.aku.apps.punchin.free.db.DatabaseFactory#createPreferenceFactory()
	 */
	@Override
	public PreferenceFactory createPreferenceFactory() {
		if (preferenceFactory == null)
			preferenceFactory = new SQLitePreferenceFactory(this.context, this.helper);
		
		return preferenceFactory;
	}
	
	/* (non-Javadoc)
	 * @see com.aku.apps.punchin.free.db.DatabaseFactory#createTaskFactory()
	 */
	@Override
	public TaskFactory createTaskFactory() {
		if (taskFactory == null)
			taskFactory = new SQLiteTaskFactory(this.helper);
		
		return taskFactory;
	}
	
	/* (non-Javadoc)
	 * @see com.aku.apps.punchin.free.db.DatabaseFactory#createTimeStampFactory()
	 */
	@Override
	public TimeStampFactory createTimeStampFactory() {
		if (timeStampFactory == null)
			timeStampFactory = new SQLiteTimeStampFactory(this.helper, this);
		
		return timeStampFactory;
	}
	
	/* (non-Javadoc)
	 * @see com.aku.apps.punchin.free.db.DatabaseFactory#createDayFactory()
	 */
	@Override
	public DayFactory createDayFactory() {
		if (dayFactory == null)
			dayFactory = new SQLiteDayFactory(this.helper);
		
		return dayFactory;
	}

	@Override
	public ExpenseFactory createExpenseFactory() {
		if (expenseFactory == null)
			expenseFactory = new SQLiteExpenseFactory(this.helper);
		
		return expenseFactory;
	}

	@Override
	public ReportSenderFactory createReportSenderFactory() {
		if (senderFactory == null)
			senderFactory = new DefaultReportSenderFactory(this.context, this);
		
		return senderFactory;
	}

	@Override
	public ReportFormatterFactory createReportFormatterFactory() {
		if (formatterFactory == null)
			formatterFactory = new DefaultReportFormatterFactory();
		
		return formatterFactory;
	}

	@Override
	public void updatePreferences(Preferences prefs) {
		// no need to do anything here
		this.createPreferenceFactory().update(prefs);
	}

	@Override
	public BackupProviderFactory createBackupProviderFactory() {
		if (backupFactory == null)
			backupFactory = new SQLiteBackupProviderFactory(this.helper, this, this.context, this.handler);
		
		return backupFactory;
	}

	@Override
	public BackupProvider createDefaultBackupProvider() {
		BackupProviderFactory bf = createBackupProviderFactory();
		Preferences prefs = createPreferences();
		
		return bf.get(prefs.getDefaultBackupProviderId());
	}

	@Override
	public Client createDefaultClient() {
		ClientFactory f = createClientFactory();
		Preferences prefs = createPreferences();
		
		return f.get(prefs.getDefaultClientId());
	}

	@Override
	public ReportSender createDefaultReportSender() {
		ReportSenderFactory f = createReportSenderFactory();
		Preferences prefs = createPreferences();
		
		return f.get(prefs.getDefaultReportSenderId());
	}

	@Override
	public ReportFormatter createDefaultReportFormat() {
		ReportFormatterFactory f = createReportFormatterFactory();
		Preferences prefs = createPreferences();
		
		return f.get(prefs.getDefaultReportFormatId());
	}

	@Override
	public CalendarEventFactory createCalendarEventFactory() {
		if (calendarEventFactory == null)
			calendarEventFactory = new SQLiteCalendarEventFactory(this.helper);
		
		return calendarEventFactory;
	}

	@Override
	public TaskDayFactory createTaskDayFactory() {
		if (taskDayFactory == null)
			taskDayFactory = new SQLiteTaskDayFactory(this.helper);
		
		return taskDayFactory;
	}
}
