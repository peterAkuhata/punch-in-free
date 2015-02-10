package com.aku.apps.punchin.free.db.test;

import android.content.Context;

import com.aku.apps.punchin.free.db.BackupProvider;
import com.aku.apps.punchin.free.db.BackupProviderFactory;
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
import com.aku.apps.punchin.free.db.test.TestClientFactory;
import com.aku.apps.punchin.free.db.test.TestDayFactory;
import com.aku.apps.punchin.free.db.test.TestPreferenceFactory;
import com.aku.apps.punchin.free.db.test.TestTaskFactory;
import com.aku.apps.punchin.free.db.test.TestTimeStampFactory;
import com.aku.apps.punchin.free.domain.Client;
import com.aku.apps.punchin.free.domain.Preferences;
import com.aku.apps.punchin.free.domain.TimeFormatter;
import com.aku.apps.punchin.free.reporting.DefaultReportFormatterFactory;
import com.aku.apps.punchin.free.reporting.DefaultReportSenderFactory;
import com.aku.apps.punchin.free.reporting.ReportFormatter;
import com.aku.apps.punchin.free.reporting.ReportSender;

/**
 * 
 * @author Peter Akuhata
 *
 * This is a factory of factories class.  It's sole purpose is to create factory objects
 * for use by the system. 
 */
public class TestDatasourceFactory implements DatasourceFactory {
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
	private Context context;
	
	
	public TestDatasourceFactory(Context ctx) {
		super();
		this.context = ctx;
	}

	@Override
	public void clearCache() {
		if (calendarEventFactory != null)
			calendarEventFactory.clearCache();
		
		if (timeFormatFactory != null)
			timeFormatFactory.clearCache();
		
		if (preferenceFactory != null)
			preferenceFactory.clearCache();
		
		if (taskFactory != null)
			taskFactory.clearCache();
		
		if (timeStampFactory != null)
			timeStampFactory.clearCache();
		
		if (dayFactory != null)
			dayFactory.clearCache();
		
		if (clientFactory != null)
			clientFactory.clearCache();
		
		if (expenseFactory != null)
			expenseFactory.clearCache();
		
		if (formatterFactory != null)
			formatterFactory.clearCache();
		
		if (senderFactory != null)
			senderFactory.clearCache();
		
		if (taskDayFactory != null)
			taskDayFactory.clearCache();
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
			clientFactory = new TestClientFactory();
		
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
			preferenceFactory = new TestPreferenceFactory();
		
		return preferenceFactory;
	}
	
	/* (non-Javadoc)
	 * @see com.aku.apps.punchin.free.db.DatabaseFactory#createTaskFactory()
	 */
	@Override
	public TaskFactory createTaskFactory() {
		if (taskFactory == null)
			taskFactory = new TestTaskFactory();
		
		return taskFactory;
	}
	
	/* (non-Javadoc)
	 * @see com.aku.apps.punchin.free.db.DatabaseFactory#createTimeStampFactory()
	 */
	@Override
	public TimeStampFactory createTimeStampFactory() {
		if (timeStampFactory == null)
			timeStampFactory = new TestTimeStampFactory(this);
		
		return timeStampFactory;
	}
	
	/* (non-Javadoc)
	 * @see com.aku.apps.punchin.free.db.DatabaseFactory#createDayFactory()
	 */
	@Override
	public DayFactory createDayFactory() {
		if (dayFactory == null)
			dayFactory = new TestDayFactory();
		
		return dayFactory;
	}

	@Override
	public ExpenseFactory createExpenseFactory() {
		if (expenseFactory == null)
			expenseFactory = new TestExpenseFactory();
		
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
			backupFactory = new TestBackupProviderFactory();
		
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
			calendarEventFactory = new TestCalendarEventFactory();
		
		return calendarEventFactory;
	}

	@Override
	public TaskDayFactory createTaskDayFactory() {
		if (taskDayFactory == null)
			taskDayFactory = new TestTaskDayFactory();
		
		return taskDayFactory;
	}
}
