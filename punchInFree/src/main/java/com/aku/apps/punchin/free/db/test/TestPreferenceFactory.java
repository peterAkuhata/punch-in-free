package com.aku.apps.punchin.free.db.test;

import java.util.ArrayList;
import java.util.Date;

import com.aku.apps.punchin.free.db.PreferenceFactory;
import com.aku.apps.punchin.free.domain.Preferences;
import com.aku.apps.punchin.free.utils.Constants;

public class TestPreferenceFactory implements PreferenceFactory {
	private static final Preferences testPrefs = new Preferences(IDGenerator.generate(),
			false, // ask before removing time
			Constants.TimeFormatters.BY_SECONDS, 
			0, 
			Constants.Defaults.DATE_FORMAT, 
			8, 1, -1, "km",
			0.3, 
			Constants.ReportFormatters.HTML, 
			Constants.ReportSenders.SD_CARD,
			new ArrayList<String>(),
			new ArrayList<String>(),
			new ArrayList<String>(),
			new ArrayList<String>(),
			TestBackupProvider.ID,
			"peter.akuhata@gmail.com",
			"com.google", 
			"1", 
			"My Calendar",
			new Date(),
			new Date(),
			"", "", "", "", "",
			"", "", "", "");

	@Override
	public Preferences get() {
		return testPrefs;
	}

	@Override
	public void update(Preferences prefs) {
		// no need to do anything
		prefs.setModified(new Date());
	}

	@Override
	public void clearCache() {
		// no need to do anything
	}
}
